package com.chat_application.AuthFilter;

import com.chat_application.entity.User;
import com.chat_application.services.JwtService;
import com.chat_application.services.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService ;
    private final UserService userService ;
    private  final HandlerExceptionResolver handlerExceptionResolver ;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthFilter processing: {}", request.getServletPath());
        final String requestTokenHeader = request.getHeader("Authorization");
        log.info("Authorization header: {}", requestTokenHeader);
        if(requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")){
            log.info("No Bearer token, continuing filter chain");
            filterChain.doFilter(request,response);
            return;
        }
        try{
            String token = requestTokenHeader.substring(7).trim() ;
            Long userId = jwtService.getUserIdFromToken(token);
            log.info("Extracted userId from token: {}", userId);
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.getUserById(userId);
                log.info("Loaded user: {}", user.getEmail());
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        }catch(JwtException e){
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }
}
