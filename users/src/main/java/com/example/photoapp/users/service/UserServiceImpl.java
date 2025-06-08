package com.example.photoapp.users.service;

import com.example.photoapp.users.data.UserEntity;
import com.example.photoapp.users.data.UsersRepositiory;
import com.example.photoapp.users.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private UsersRepositiory usersRepositiory;

    public UserServiceImpl(UsersRepositiory usersRepositiory) {
        this.usersRepositiory = usersRepositiory;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPassword("test");
        usersRepositiory.save(userEntity);
        UserDto createdUser = modelMapper.map(userEntity, UserDto.class);
        return createdUser;
    }
}
