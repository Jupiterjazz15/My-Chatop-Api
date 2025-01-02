package com.chatop.My_Chatop_Api.services;

import com.chatop.My_Chatop_Api.dtos.User.RegisterRequest;
import com.chatop.My_Chatop_Api.dtos.User.UserDto;
import com.chatop.My_Chatop_Api.models.User;
import com.chatop.My_Chatop_Api.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User saveUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return null;
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedAt(LocalDate.now());
        user.setUpdatedAt(LocalDate.now());
        return userRepository.save(user);
    }

    public UserDto getUserByEmail(String email) {
        // convert User to UserDto
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }

        // Convertir l'entité User en DTO UserDto
        return modelMapper.map(user, UserDto.class);
    }

    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElse(null);

        return modelMapper.map(user, UserDto.class);
    }
}
