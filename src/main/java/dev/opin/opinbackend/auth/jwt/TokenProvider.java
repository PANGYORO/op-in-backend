package dev.opin.opinbackend.auth.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import dev.opin.opinbackend.auth.model.TokenDto;
import dev.opin.opinbackend.exception.auth.AuthRuntimeException;
import dev.opin.opinbackend.exception.member.MemberExceptionEnum;
import dev.opin.opinbackend.exception.member.MemberRuntimeException;
import dev.opin.opinbackend.persistence.entity.Member;
import dev.opin.opinbackend.persistence.repository.MemberRepository;

import dev.opin.opinbackend.exception.auth.AuthExceptionEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenProvider implements InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

	private static final String AUTHORITIES_KEY = "auth";

	private final String secret;
	private final long accessTokenValidityInMilliseconds;
	private final long refreshTokenValidityInMilliseconds;

	MemberRepository memberRepository;

	private Key key;

	public TokenProvider(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
		@Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds,
		MemberRepository memberRepository) {
		this.secret = secret;
		this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
		this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
		this.memberRepository = memberRepository;
	}

	/*
	 * ????????? ??? ??????
	 */
	@Override
	public void afterPropertiesSet() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	/*
	 * ????????? ???????????? ?????? ????????? ???????????? ?????????
	 * AccessToken??? Claim????????? email??? nickname??? ????????????.
	 */
	public TokenDto createToken(String email,
		String authorities) {

		Member member = memberRepository.findByEmail(email).orElse(null);
		if (member == null) {
			throw new MemberRuntimeException(MemberExceptionEnum.MEMBER_NOT_EXIST_EXCEPTION);
		}

		long now = (new Date()).getTime();

		String accessToken = createAccessToken(email, authorities);

		String refreshToken = Jwts.builder()
			.claim(AUTHORITIES_KEY, authorities)
			.claim("email", member.getEmail())
			.claim("nickname", member.getNickname())
			.setExpiration(new Date(now + refreshTokenValidityInMilliseconds))
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();

		return new TokenDto(accessToken, refreshToken);
	}

	/*
	 * accessToken ?????????
	 * */
	public String createAccessToken(String email, String authorities) {

		Member member = memberRepository.findByEmail(email).orElse(null);
		if (member == null) {
			throw new MemberRuntimeException(MemberExceptionEnum.MEMBER_NOT_EXIST_EXCEPTION);
		}

		long now = (new Date()).getTime();

		String accessToken = Jwts.builder()
			.claim("email", member.getEmail())
			.claim("nickname", member.getNickname())
			.claim(AUTHORITIES_KEY, authorities)
			.setExpiration(new Date(now + accessTokenValidityInMilliseconds))
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();

		return accessToken;
	}

	/*
	 * ?????? ???????????? ?????????
	 */
	public Authentication getAuthentication(String token) {
		Claims claims = getClaims(token);

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
		return new UsernamePasswordAuthenticationToken(claims.get("email"), null, authorities);
	}

	/*
	 * ?????? ????????? ???????????? ?????????
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(key).parseClaimsJws(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.info("????????? JWT ???????????????.");
			throw new AuthRuntimeException(AuthExceptionEnum.AUTH_JWT_SIGNATURE_EXCEPTION);
		} catch (ExpiredJwtException e) {
			log.info("????????? JWT ???????????????.");
			throw new AuthRuntimeException(AuthExceptionEnum.AUTH_JWT_EXPIRED_EXCEPTION);
		} catch (UnsupportedJwtException e) {
			log.info("???????????? ?????? JWT???????????????.");
			throw new AuthRuntimeException(AuthExceptionEnum.AUTH_JWT_SUPPORT_EXCEPTION);
		} catch (IllegalArgumentException e) {
			log.info("JWT????????? ?????????????????????.");
			throw new AuthRuntimeException(AuthExceptionEnum.AUTH_JWT_SIGNATURE_EXCEPTION);
		}
	}

	/*
	 * ???????????? Claim ???????????? ?????????
	 */
	public Claims getClaims(String token) {
		try {
			return Jwts
				.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

}
