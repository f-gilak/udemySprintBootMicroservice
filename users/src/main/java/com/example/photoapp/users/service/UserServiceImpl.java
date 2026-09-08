package com.example.photoapp.users.service;

import com.example.photoapp.users.data.AlbumServiceClient;
import com.example.photoapp.users.data.UserEntity;
import com.example.photoapp.users.data.UsersRepository;
import com.example.photoapp.users.shared.UserDto;
import com.example.photoapp.users.ui.model.AlbumResponseModel;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RestTemplate restTemplate;
    private final Environment env;
    private final AlbumServiceClient albumServiceClient;

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        userDto.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        usersRepository.save(userEntity);
        UserDto createdUser = modelMapper.map(userEntity, UserDto.class);
        return createdUser;
    }

    @Override
    public UserDto findUserDetailByEmail(String username) {
        UserEntity userEntity = usersRepository.findByEmail(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }
        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = usersRepository.findByUserId(userId);
        if (userEntity == null) {
            throw new UsernameNotFoundException("user not found");
        }
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
//        List<AlbumResponseModel> albums=getAlbumByUserId(userId);
        log.debug("Befrore calling ablums Microservice");
        List<AlbumResponseModel> albums = getAlbumByUserIdWitFeign(userId);
        log.debug("After calling ablums Microservice");
        userDto.setAlbums(albums);
        return userDto;
    }

    private List<AlbumResponseModel> getAlbumByUserIdWitFeign(String userId) {
        try {
            return albumServiceClient.getAlbums(userId);
        } catch (FeignException e) {
            log.error(e.getLocalizedMessage());
        }
        return null;
    }

    private List<AlbumResponseModel> getAlbumByUserId(String userId) {
        String url = String.format(Objects.requireNonNull(env.getProperty("album.url")), userId);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List<AlbumResponseModel>> albumsResponse = restTemplate.exchange(url, HttpMethod.GET, entity,
                new ParameterizedTypeReference<List<AlbumResponseModel>>() {
                });
        return albumsResponse.getBody();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = usersRepository.findByEmail(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }
        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getEncryptedPassword())
                .disabled(false)
                .build();
    }
}
