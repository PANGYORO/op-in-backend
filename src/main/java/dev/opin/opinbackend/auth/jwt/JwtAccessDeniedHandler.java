package dev.opin.opinbackend.auth.jwt;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import dev.opin.opinbackend.exception.auth.AuthExceptionEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	/**
	 * 권한 체크 후 인가 실패 동작하는 코드
	 * status 403
	 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException exception) throws IOException {
		response.setStatus(AuthExceptionEnum.AUTH_AUTHENTICATION_EXCEPTION.getHttpStatus().value());
		try (OutputStream os = response.getOutputStream()) {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(os, AuthExceptionEnum.convertMap(AuthExceptionEnum.AUTH_AUTHENTICATION_EXCEPTION));
			os.flush();
		}
	}

}

