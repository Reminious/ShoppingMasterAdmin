package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.User;
import com.example.demo.Entity.UserDto;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    List<User> findByType(String type);
  
    @Query("SELECT new com.example.demo.Entity.UserDto(u.id, u.username, u.type) FROM User u")
    List<UserDto> findAllUserDtos();//Get all userDto!!
}
