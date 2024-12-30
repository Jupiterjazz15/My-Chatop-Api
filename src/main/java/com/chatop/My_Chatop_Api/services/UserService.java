package com.chatop.My_Chatop_Api.services;

import com.chatop.My_Chatop_Api.dtos.User.SignUpRequest;
import com.chatop.My_Chatop_Api.dtos.User.UserResponse;
import com.chatop.My_Chatop_Api.models.User;
import com.chatop.My_Chatop_Api.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User saveUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return null;
        }

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setCreatedAt(LocalDate.now());
        user.setUpdatedAt(LocalDate.now());
        return userRepository.save(user);
    }

    public UserResponse getUserByEmail(String email) {
        // convert User to UserDto
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }

        // Mapper l'entité User vers le DTO UserResponse
        return modelMapper.map(user, UserResponse.class);
    }

    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id).orElse(null);

        return modelMapper.map(user, UserResponse.class);
    }
}
