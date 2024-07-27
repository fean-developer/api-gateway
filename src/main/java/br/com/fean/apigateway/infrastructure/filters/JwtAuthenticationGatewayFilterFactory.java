package br.com.fean.apigateway.infrastructure.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
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
        return (exchange, chain) -> {
            return Mono.fromRunnable(() -> {
                try {
                    jwtAuthenticationFilter.doFilter((ServletRequest) exchange.getRequest(), (ServletResponse) exchange.getResponse(), (FilterChain) chain);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).then(chain.filter(exchange));
        };
    }

    public static class Config {
    }
}
