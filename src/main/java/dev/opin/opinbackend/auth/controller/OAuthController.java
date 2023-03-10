package dev.opin.opinbackend.auth.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.opin.opinbackend.auth.jwt.JwtFilter;
import dev.opin.opinbackend.auth.model.TokenDto;
import dev.opin.opinbackend.auth.service.OAuthService;
import dev.opin.opinbackend.auth.service.OAuthServiceImpl;
import dev.opin.opinbackend.exception.member.MemberRuntimeException;
import dev.opin.opinbackend.exception.member.MemberExceptionEnum;

@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

	private static final Logger logger = LoggerFactory.getLogger(OAuthController.class);
	OAuthService oAuthService;
	@Value("${jwt.access-token-validity-in-seconds}")
	private int accessTokenValidityInSeconds;

	@Value("${jwt.refresh-token-validity-in-seconds}")
	private int refreshTokenValidityInSeconds;

	@Autowired
	public OAuthController(OAuthServiceImpl oAuthService) {
		this.oAuthService = oAuthService;
	}

	@GetMapping("/redirect/github")
	public void redirectGithub(HttpServletResponse response,
		@RequestParam(value = "redirect_uri", required = false) String redirectUri) throws IOException {
		response.sendRedirect(oAuthService.getRedirectUrl(redirectUri));
	}

	@GetMapping("/login/github")
	public void getUserInfo(@RequestParam String code,
		@RequestParam(value = "redirect_uri", required = false) String redirectUri, HttpServletResponse response) {
		try {
			TokenDto token = oAuthService.login(code, redirectUri);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + token.getAccessToken());

			Cookie accessTokenCookie = new Cookie("accessToken", token.getAccessToken());
			Cookie refreshTokenCookie = new Cookie("refreshToken", token.getRefreshToken());
			Cookie typeCookie = new Cookie("type", token.getType());

			accessTokenCookie.setPath("/");
			accessTokenCookie.setMaxAge(accessTokenValidityInSeconds);
			refreshTokenCookie.setPath("/");
			refreshTokenCookie.setMaxAge(refreshTokenValidityInSeconds);
			typeCookie.setPath("/");
			typeCookie.setMaxAge(refreshTokenValidityInSeconds);

			response.addCookie(accessTokenCookie);
			response.addCookie(refreshTokenCookie);
			response.addCookie(typeCookie);
			response.sendRedirect(redirectUri);
		} catch (Exception e) {
			throw new MemberRuntimeException(MemberExceptionEnum.MEMBER_WRONG_EXCEPTION);
		}

	}
}
