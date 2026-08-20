package com.example.photoapp.users.ui.controllers;

import com.example.photoapp.users.service.UserService;
import com.example.photoapp.users.shared.UserDto;
import com.example.photoapp.users.ui.model.CreateUserRequestModel;
import com.example.photoapp.users.ui.model.CreateUserResponseModel;
import com.example.photoapp.users.ui.model.UserResponseModel;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    @GetMapping("/status/check")
    public String status() {
        return "Working on port: " + environment.getProperty("local.server.port") + ", with token=" +
                environment.getProperty("token.secret_key");
    }

    @PostMapping
    public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel userRequestModel) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(userRequestModel, UserDto.class);
        UserDto createUser = userService.createUser(userDto);
        CreateUserResponseModel responseModel = modelMapper.map(createUser, CreateUserResponseModel.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseModel> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);
        UserResponseModel returnValue = new ModelMapper().map(userDto, UserResponseModel.class);
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }
}
