package com.example.demo.Service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;

@Service
	public class UserService {
	    @Autowired
	    private UserRepository userRepository;

	    public User getUserById(Integer id) {
	        return userRepository.findById(id).orElse(null);
	    }

	    public User getUserByUsername(String username) {
	        return userRepository.findByUsername(username);
	    }

	    public List<User> getAllUsers() {
	        return userRepository.findAll();
	    }

	    public void saveUser(User user) {
	        userRepository.save(user);
	    }

	    public void deleteUser(Integer id) {
	        userRepository.deleteById(id);
	    }
	}
	

