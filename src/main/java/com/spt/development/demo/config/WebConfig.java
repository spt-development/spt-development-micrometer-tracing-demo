package com.spt.development.demo.config;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Configuration
public class WebConfig {
    public static final String TRACE_ID_HEADER = "X-B3-TraceId";

    private static final List<String> URL_PATTERNS = Collections.singletonList("/*");

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilter(Tracer tracer) {
        final FilterRegistrationBean<TraceIdFilter> filterRegBean = new FilterRegistrationBean<>(
            new TraceIdFilter(tracer)
        );

        filterRegBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        filterRegBean.setUrlPatterns(URL_PATTERNS);

        return filterRegBean;
    }

    @AllArgsConstructor
    static class TraceIdFilter implements Filter {
        private final Tracer tracer;

        @Override
        public void init(FilterConfig filterConfig) {
            // NO-OP
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
            final TraceContext context = tracer.currentTraceContext().context();

            if (context != null) {
                ((HttpServletResponse) response).setHeader(TRACE_ID_HEADER, context.traceId());
            }
            filterChain.doFilter(request, response);
        }

        @Override
        public void destroy() {
            // NO-OP
        }
    }
}
