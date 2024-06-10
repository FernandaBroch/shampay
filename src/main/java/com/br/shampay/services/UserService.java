package com.br.shampay.services;


import com.br.shampay.entities.User;
import com.br.shampay.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    public User save(User user){
        userRepository.save(user);
        return user;
    }
    public Optional<User> findUserById(Long userId){
       return userRepository.findById(userId);
    }
}
