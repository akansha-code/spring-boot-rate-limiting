package com.example.spring_boot_rate_limiting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootRateLimitingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRateLimitingApplication.class, args);
	}

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilterFilterRegistrationBean()
    {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitingFilter());
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}
