package com.example.photoapp.users.service;

import com.example.photoapp.users.shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDto createUser(UserDto userDto);

    UserDto findUserDetailByEmail(String username);

    UserDto getUserByUserId(String userId);
}
