package com.ibrahim.task.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDto {
    private long id;
    private String userId;
    private String email;
    private String password;
    private String role;

    public void setAccessToken(String s) {
    }
}


