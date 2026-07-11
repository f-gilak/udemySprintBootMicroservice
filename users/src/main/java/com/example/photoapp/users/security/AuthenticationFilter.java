package com.example.photoapp.users.security;

import com.example.photoapp.users.service.UserService;
import com.example.photoapp.users.shared.UserDto;
import com.example.photoapp.users.ui.model.LoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env;

//    @Value("${token.expiration_time}")
//    private Long tokenExpirationTime;
//
//    @Value("${token.secret_key}")
//    private String tokenSecret;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment env) {
        super(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestModel model = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);
            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
                    model.getEmail(), model.getPassword(), new ArrayList<>()
            ));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = ((User) authResult.getPrincipal()).getUsername();
        UserDto userDto = userService.findUserDetailByEmail(username);
        long tokenExpirationTime = Long.parseLong(env.getProperty("token.expiration_time"));
        String tokenSecret = env.getProperty("token.secret_key");

        SecretKey key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));

        Instant now = Instant.now();
        String token = Jwts.builder()
                .setSubject(userDto.getUserId())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(tokenExpirationTime)))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
        response.addHeader("Authorization", "Bearer " + token);
        response.addHeader("userId", userDto.getUserId());
    }
}
