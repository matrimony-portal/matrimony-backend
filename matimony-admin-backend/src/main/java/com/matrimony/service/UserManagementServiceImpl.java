package com.matrimony.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matrimony.entites.Status;
import com.matrimony.entites.User;
import com.matrimony.repository.UserRepository;

@Service
public class UserManagementServiceImpl implements UserManagementService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public boolean blockUser(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(Status.BLOCKED);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean unblockUser(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(Status.ACTIVE);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}