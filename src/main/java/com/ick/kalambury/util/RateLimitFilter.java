package com.ick.kalambury.util;

import com.ick.kalambury.config.RateLimitersConfig;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.cache.CacheManager;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Component
@ConditionalOnProperty(value = "bucket4j-config.enabled")
@Order(2)
public class RateLimitFilter extends OncePerRequestFilter {

    private final CacheManager cacheManager;
    private final RateLimitersConfig limitersConfig;

    private final Map<Pattern, String> limiters = new LinkedHashMap<>();
    private final Map<String, BucketConfiguration> bucketConfigurations = new HashMap<>();
    private final Map<String, ProxyManager<String>> managers = new ConcurrentHashMap<>();

    public RateLimitFilter(RateLimitersConfig limitersConfig, CacheManager cacheManager) {
        this.limitersConfig = limitersConfig;
        this.cacheManager = cacheManager;
        initLimiters();
    }

    private void initLimiters() {
        limitersConfig.getRateLimiters().forEach(rateLimiter -> {
            limiters.put(rateLimiter.getPattern(), rateLimiter.getName());
            bucketConfigurations.put(rateLimiter.getName(), getBucketConfiguration(rateLimiter));
        });
    }

    private BucketConfiguration getBucketConfiguration(RateLimitersConfig.RateLimiter limiter) {
        Refill refill = null;
        switch (limiter.getRefillType()) {
            case GREEDY:
                refill = Refill.greedy(limiter.getBucketSize(), limiter.getPeriod());
                break;
            case INTERVAL:
                refill = Refill.intervally(limiter.getBucketSize(), limiter.getPeriod());
        }

        return BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(limiter.getBucketSize(), refill))
                .build();
    }

    @Override
    protected void doFilterInternal(@NonNull  HttpServletRequest request, @NonNull
            HttpServletResponse response, @NonNull  FilterChain filterChain) throws ServletException, IOException
    {
        String limiterName = null;
        for (Map.Entry<Pattern, String> entry : limiters.entrySet()) {
            Pattern key = entry.getKey();
            String name = entry.getValue();
            if (key.matcher(request.getRequestURI()).matches()) {
                limiterName = name;
                break;
            }
        }

        if (limiterName == null) {
            filterChain.doFilter(request, response);
            return;
        }

        ProxyManager<String> manager = getProxyManager(limiterName);
        BucketConfiguration config = bucketConfigurations.get(limiterName);
        Bucket bucket = manager.builder().build(request.getRemoteAddr(), config);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
            response.setContentType("text/plain");
            response.sendError(status.value(), status.getReasonPhrase());
        }
    }

    //Create manager when it's used first time to ensure that proper cache was autoconfigured.
    private ProxyManager<String> getProxyManager(String limiterName) {
        ProxyManager<String> manager = managers.get(limiterName);
        if (manager == null) {
            manager = new JCacheProxyManager<>(cacheManager.getCache(limiterName));
            managers.put(limiterName, manager);
        }
        return manager;
    }

}
