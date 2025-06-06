package by.base.main.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		 	response.setContentType("application/json");
	        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	        response.getWriter().write("{\"message\": \"Unauthorized access. Please log in.\","
	        		+ "\"status\": \"403\"}");
		
	}

}