package com.naukma.services;

import com.naukma.db.UserRepository;
import com.naukma.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User register(@Valid @NotNull User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        return userRepository.saveUser(user);
    }

    public boolean emailIsUsed(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).get();
    }
}
