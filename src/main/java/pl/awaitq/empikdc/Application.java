package pl.awaitq.empikdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.ForwardedHeaderFilter;


@Configuration
@EnableCaching
class BasicConfig {

    private final Logger logger = LoggerFactory.getLogger(BasicConfig.class);

    @Value("${app.cache.enabled}")
    private Boolean cacheEnabled;

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        // We should pass the traffic through a proxy which correctly handles IP spoofing.
        // Usually the proxy overrides X-Forwarded* headers, so spoofing doesn't work.
        // Setting X-Forwarded-For header allows us testing different IP addresses.
        FilterRegistrationBean<ForwardedHeaderFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ForwardedHeaderFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public CacheManager cacheManager() {
        if (!cacheEnabled) {
            logger.info("Cache is disabled.");
            return new NoOpCacheManager();
        }
        logger.info("Cache is enabled.");
        return new ConcurrentMapCacheManager();
    }
}


@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
