package com.example.hello_sring_boot.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import com.example.hello_sring_boot.exception.RateLimitException;

@Component
@Order(1)
public class RateLimitFilter implements Filter {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    Supplier<BucketConfiguration> bucketConfiguration;

    @Autowired
    ProxyManager<String> proxyManager;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String key = httpRequest.getRemoteAddr();
        Bucket bucket = proxyManager.builder().build(key, bucketConfiguration);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        LOG.debug(">>>>>>>>remainingTokens: {}", probe.getRemainingTokens());
        if (probe.isConsumed()) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            long retryAfterSeconds = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());
            resolver.resolveException(httpRequest, (HttpServletResponse) servletResponse, null,
                    new RateLimitException("error.rate_limit", retryAfterSeconds));
        }
    }
}