package com.example.photoapp.users.shared;

import com.example.photoapp.users.ui.model.AlbumResponseModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String userId;
    private String encryptedPassword;
    private List<AlbumResponseModel> albums;
}
