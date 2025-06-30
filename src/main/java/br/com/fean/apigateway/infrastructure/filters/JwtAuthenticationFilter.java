package br.com.fean.apigateway.infrastructure.filters;

import br.com.fean.apigateway.infrastructure.provider.JwtTokenProvider;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

        @Autowired
        private JwtTokenProvider tokenProvider;



        @Override
        public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

            String path = exchange.getRequest().getPath().toString();
            if (path.startsWith("/api/v1/auth/")) {
                return chain.filter(exchange);
            }

            return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                    .filter(bearerToken -> StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
                    .map(token -> token.substring(7))
                    .filter(tokenProvider::validateToken)
                    .flatMap(token -> {
                        String username = tokenProvider.getUsernameFromJWT(token);
                        UserDetails userDetails = tokenProvider.getUserDetails(username);
                        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        return ReactiveSecurityContextHolder.getContext()
                                .map(securityContext -> {
                                    securityContext.setAuthentication(authentication);
                                    return securityContext;
                                })
                                .then(chain.filter(exchange));
                    })
                    .switchIfEmpty(chain.filter(exchange));
        }
}

//        @Autowired
//        private JwtTokenProvider tokenProvider;
//
//        @Override
//        protected void doFilterInternal(HttpServletRequest request,
//                                        HttpServletResponse response,
//                                        FilterChain filterChain) throws ServletException, IOException {
//                try {
//                        String jwt = getJwtFromRequest(request);
//
//                        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
//                                String username = tokenProvider.getUsernameFromJWT(jwt);
//
//                                UserDetails userDetails = tokenProvider.getUserDetails(username);
//                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                                SecurityContextHolder.getContext().setAuthentication(authentication);
//                        }
//                } catch (Exception ex) {
//                        // log the exception
//                }
//
//                filterChain.doFilter(request, response);
//        }
//
//        private String getJwtFromRequest(HttpServletRequest request) {
//                String bearerToken = request.getHeader("Authorization");
//                if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//                        return bearerToken.substring(7);
//                }
//                return null;
//        }
//
//
//        public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
//                HttpServletRequest request = (HttpServletRequest) exchange.getRequest();
//                HttpServletResponse response = (HttpServletResponse) exchange.getResponse();
//                return Mono.fromRunnable(() -> {
//                        try {
//                                doFilterInternal(request, response, chain);
//                        } catch (ServletException | IOException e) {
//                                // log the exception
//                        }
//                });
//        }
//
//}
