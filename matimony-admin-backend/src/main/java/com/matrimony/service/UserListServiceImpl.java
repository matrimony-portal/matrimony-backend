package com.matrimony.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matrimony.dtos.UserListDTO;
import com.matrimony.entites.Role;
import com.matrimony.entites.User;
import com.matrimony.repository.UserRepository;

@Service
public class UserListServiceImpl implements UserListService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public List<UserListDTO> getAllUsers() {
        return userRepository.findByRoleOrderByCreatedAtDesc(Role.Role_user)
                .stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserListDTO> getRegularUsers() {
        return userRepository.findByRoleOrderByCreatedAtDesc(Role.Role_user)
                .stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());
    }
    
    private UserListDTO convertToListDTO(User user) {
        UserListDTO dto = new UserListDTO();
        dto.setUserName(user.getFirstName() + " " + user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setContactNo(user.getPhone());
        dto.setStatus(user.getStatus().toString());
        dto.setSubscription(user.getSubscription().toString());
        dto.setJoinDate(user.getCreatedAt());
        return dto;
    }
}