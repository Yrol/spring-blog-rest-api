package com.yrol.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * A configuration class for handling API security
 * */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain authorizationManager(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                .anyRequest()
                .authenticated()
                .and().httpBasic();

        http.csrf().disable();
        return http.build();
    }

    /**
     * User Authentication - In-memory
     * Using the encrypted(BCrypt) passwords generated via: https://bcrypt-generator.com/
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .inMemoryAuthentication()
                .passwordEncoder(new BCryptPasswordEncoder())
                .withUser("yrol")
                .password("$2a$12$jvnz5av00xZNOmVwcaFcF.lXJCgtwLWbGys/rOExgcnjJwgpbWvz6") // "password"
                .roles("ADMIN").and().and().build();
    }
}
