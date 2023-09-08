package com.ibrahim.task.service;


import com.ibrahim.task.exception.ResourceNotFoundException;
import com.ibrahim.task.model.UserDto;

public interface UserService {
    UserDto createUser(UserDto user) throws ResourceNotFoundException;
    UserDto getUser(String email);

    UserDto getUserByUserId(String id) throws Exception;

}