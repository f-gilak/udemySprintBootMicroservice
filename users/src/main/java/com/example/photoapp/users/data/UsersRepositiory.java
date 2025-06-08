package com.example.photoapp.users.data;

import org.springframework.data.repository.CrudRepository;

public interface UsersRepositiory extends CrudRepository<UserEntity, Long> {
}
