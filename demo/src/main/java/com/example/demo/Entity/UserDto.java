package com.example.demo.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Integer id;
    private String username;

    private String role;
    public UserDto(Integer id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}

/* This is for send all user id and name to the admin dashboard,so the admin can view all user and manage them!*/