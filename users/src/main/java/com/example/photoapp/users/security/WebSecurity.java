package com.example.photoapp.users.security;

import com.example.photoapp.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {

    private final Environment env;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder managerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        managerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
        AuthenticationManager manager = managerBuilder.build();
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(manager, userService, env);
        authenticationFilter.setFilterProcessesUrl(env.getProperty("login.url.path"));

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
//                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/users/**").permitAll()
                                .requestMatchers("/users/**").permitAll()
//                                .requestMatchers("/users/**").access(
//                                        new WebExpressionAuthorizationManager("hasIpAddress('" +
//                                                env.getProperty("gateway.ip") + "')"))
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                                .anyRequest().authenticated()
                )
                .addFilter(authenticationFilter)
                .authenticationManager(manager)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(header -> header.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()));

        return http.build();
    }
}
