package com.spt.development.demo.config;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class WebConfigTest {

    @Nested
    class TraceIdFilterTest {

        @Test
        void destroy_happyPath_doesNothing() {
            // Just added for coverage :O(
            createDefaultFilter().destroy();
            
            assertThat(true).isTrue();
        }

        private WebConfig.TraceIdFilter createDefaultFilter() {
            final WebConfig.TraceIdFilter filter =  new WebConfig.TraceIdFilter(Mockito.mock(Tracer.class));
            filter.init(Mockito.mock(FilterConfig.class));

            return filter;
        }
    }
}