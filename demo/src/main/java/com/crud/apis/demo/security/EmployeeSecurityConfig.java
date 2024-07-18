package com.crud.apis.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class EmployeeSecurityConfig {
    @Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        UserDetails ashu = User.builder()
                .username("ashu")
                .password("{noop}pass1234")
                .roles("EMPLOYEE")
                .build();
        UserDetails praharsh = User.builder()
                .username("praharsh")
                .password("{noop}pass1234")
                .roles("EMPLOYEE", "MANAGER")
                .build();
        UserDetails paytm = User.builder()
                .username("paytm")
                .password("{noop}pass1234")
                .roles("EMPLOYEE", "MANAGER", "ADMIN")
                .build();
        // Spring-boot will now not use the application.properties file's username/pass, instead use these instead
        return new InMemoryUserDetailsManager(ashu, praharsh, paytm);
    }

    // Each role has different levels access of access to various apis
    // GET          - employee
    // POST, PUT    - managers
    // DELETE       - admin
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer->
                configurer
                        .requestMatchers(HttpMethod.GET, "/getemployees").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/getemployeebyid/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/getemployeebyemail/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/addemployee").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.POST, "/addemployees").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/editemployee").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.POST, "/upload").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/delete/**").hasRole("ADMIN")
        );

        // Explicitly mention we need to follow this auth style now instead of default
        http.httpBasic(Customizer.withDefaults());

        // DISABLE CSRF (Cross Site Request Forgery)
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
