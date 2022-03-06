package com.ick.kalambury.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "bucket4j-config")
public class RateLimitersConfig {

    private Boolean enabled;
    private List<RateLimiter> rateLimiters;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<RateLimiter> getRateLimiters() {
        return rateLimiters;
    }

    public void setRateLimiters(List<RateLimiter> rateLimiters) {
        this.rateLimiters = rateLimiters;
    }

    public static class RateLimiter {

        private String name;
        private Pattern pattern;
        private int bucketSize;
        private RefillType refillType;
        private Duration period;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public void setPattern(Pattern pattern) {
            this.pattern = pattern;
        }

        public int getBucketSize() {
            return bucketSize;
        }

        public void setBucketSize(int bucketSize) {
            this.bucketSize = bucketSize;
        }

        public RefillType getRefillType() {
            return refillType;
        }

        public void setRefillType(RefillType refillType) {
            this.refillType = refillType;
        }

        public Duration getPeriod() {
            return period;
        }

        public void setPeriod(Duration period) {
            this.period = period;
        }

    }

    public enum RefillType {
        GREEDY, INTERVAL
    }
}
