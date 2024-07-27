package br.com.fean.apigateway.infrastructure.filters;

import br.com.fean.apigateway.infrastructure.provider.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

        @Autowired
        private JwtTokenProvider tokenProvider;

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
                try {
                        String jwt = getJwtFromRequest(request);

                        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                                String username = tokenProvider.getUsernameFromJWT(jwt);

                                UserDetails userDetails = tokenProvider.getUserDetails(username);
                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                                SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                } catch (Exception ex) {
                        // log the exception
                }

                filterChain.doFilter(request, response);
        }

        private String getJwtFromRequest(HttpServletRequest request) {
                String bearerToken = request.getHeader("Authorization");
                if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                        return bearerToken.substring(7);
                }
                return null;
        }

        public Mono<Void> doFilter(ServerWebExchange exchange, FilterChain chain) {
                HttpServletRequest request = (HttpServletRequest) exchange.getRequest();
                HttpServletResponse response = (HttpServletResponse) exchange.getResponse();
                return Mono.fromRunnable(() -> {
                        try {
                                doFilterInternal(request, response, chain);
                        } catch (ServletException | IOException e) {
                                // log the exception
                        }
                });
        }

}
