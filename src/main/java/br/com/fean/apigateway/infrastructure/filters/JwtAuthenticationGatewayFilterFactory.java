package br.com.fean.apigateway.infrastructure.filters;


import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Component
public class JwtAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public JwtAuthenticationGatewayFilterFactory(JwtAuthenticationFilter jwtAuthenticationFilter) {
        super(Config.class);
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> jwtAuthenticationFilter.filter(exchange, new WebFilterChain() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange) {
                return chain.filter(exchange);
            }
        });
    }

    public static class Config {
    }
}
