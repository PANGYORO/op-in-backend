package dev.opin.opinbackend.auth.service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import dev.opin.opinbackend.auth.jwt.TokenProvider;
import dev.opin.opinbackend.auth.model.MemberDto;
import dev.opin.opinbackend.auth.model.TokenDto;
import dev.opin.opinbackend.auth.model.response.OAuthAccessTokenResponse;
import dev.opin.opinbackend.batch.step.Action;
import dev.opin.opinbackend.constant.GitHub;
import dev.opin.opinbackend.persistence.entity.Member;
import dev.opin.opinbackend.persistence.entity.Role;
import dev.opin.opinbackend.persistence.repository.MemberRepository;
import dev.opin.opinbackend.util.RandomString;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {
	private final TokenProvider tokenProvider;
	private final Action action;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final MemberRepository memberRepository;
	private final AsyncService asyncService;
	@Value("${security.oauth.github.client-id}")
	private String clientId;
	@Value("${security.oauth.github.client-secret}")
	private String clientSecret;
	private final PasswordEncoder passwordEncoder;

	/**
	 * github OAuth??? ?????? Redirect ????????? ???????????????.
	 *
	 * @return
	 */
	@Override
	public String getRedirectUrl(String redirectUri) {

		return GitHub.AUTHORIZE_URL + "?client_id=" + clientId
			+ (redirectUri != null ? "&redirect_uri=" + redirectUri : "");
	}

	@Override
	public TokenDto login(String code, String redirectUri) {
		OAuthAccessTokenResponse tokenResponse = getToken(code, redirectUri);
		MemberDto memberDto = getUserProfile(tokenResponse);
		Member member = saveOrUpdate(memberDto);
		final CompletableFuture<String> certResult = asyncService.process(member);
		certResult.thenAccept(result -> {
			log.info("GITHUB REPOSITORY UPDATE STATUS: {}",result);
		});
		TokenDto token = authorize(member);
		return token;
	}

	public TokenDto authorize(Member member) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			member.getEmail(), "");
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String authorities = getAuthorities(authentication);

		return tokenProvider.createToken(member.getEmail(), authorities);
	}

	public String getAuthorities(Authentication authentication) {
		return authentication.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));
	}

	/**
	 * ?????? ???????????? ?????? ???????????? ?????????, ????????? ????????? ???????????? ???????????????.
	 *
	 * @param memberDto
	 * @return member
	 */
	@Transactional
	public Member saveOrUpdate(MemberDto memberDto) {
		Member member = memberRepository.findByGithubId(memberDto.getGithubId())
			.map(entity ->
				entity.fetch(memberDto.getGithubToken(),
					memberDto.getGithubUserName()
				))
			.orElseGet(memberDto::toMember);

		return memberRepository.save(member);
	}

	/**
	 * Github???????????? Member??? ????????? ????????? MemberDto??? ???????????????.
	 * email??? {github_id}@github.io ??? ??????????????? -> ???????????? unique?????? ?????????!
	 * nickname??? {github_id}.{random?????? 6??????}??? ???????????????.
	 *
	 * @param tokenResponse
	 * @return MemberDto
	 */
	private MemberDto getUserProfile(OAuthAccessTokenResponse tokenResponse) {
		Map<String, Object> userAttributes = getUserAttributes(tokenResponse);
		return MemberDto.builder()
			.githubId(String.valueOf(userAttributes.get("id")))
			.githubToken(tokenResponse.getAccessToken())
			.githubUserName(String.valueOf(userAttributes.get("login")))
			.githubSyncFl(true)
			.password(new BCryptPasswordEncoder().encode(""))
			.email(userAttributes.get("id") + "@github.io")
			.nickname(userAttributes.get("login") + "." + RandomString.generateNumber())
			.avatarUrl((String)userAttributes.get("avatar_url"))
			.role(Role.ROLE_USER)
			.build();
	}

	private Map<String, Object> getUserAttributes(OAuthAccessTokenResponse response) {
		return WebClient.create()
			.get()
			.uri(GitHub.USER_INFO_URL)
			.headers(header -> header.setBearerAuth(response.getAccessToken()))
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
			})

			.block();
	}

	private OAuthAccessTokenResponse getToken(String code, String redirectUri) {
		return WebClient.create()
			.post()
			.uri(GitHub.ACCESS_TOKEN_URL)
			.headers(header -> {
				header.setBasicAuth(clientId, clientSecret);
				header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
				header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
			}).bodyValue(tokenRequest(code, redirectUri))
			.retrieve()
			.bodyToMono(OAuthAccessTokenResponse.class)
			.block();
	}

	/**
	 * OAuth ?????? ???????????? token??? ????????? ???, formData??? ???????????????.
	 *
	 * @param code
	 * @return
	 */
	private MultiValueMap<String, String> tokenRequest(String code, String redirectUri) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("code", code);
		// if(redirectUri != null) {
		// 	formData.add("redirect_uri", redirectUri);
		// }
		return formData;
	}
}
