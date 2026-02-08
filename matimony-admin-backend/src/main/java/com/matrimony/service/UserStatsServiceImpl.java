package com.matrimony.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matrimony.dtos.UserStatsDTO;
import com.matrimony.entites.Role;
import com.matrimony.entites.Status;
import com.matrimony.repository.UserRepository;

@Service
public class UserStatsServiceImpl implements UserStatsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserStatsDTO getUserStats() {
        Long activeUsers = userRepository.countByStatus(Status.ACTIVE);
        Long totalUsers = userRepository.count();
        Long activeRegularUsers = userRepository.countByStatusAndRole(Status.ACTIVE, Role.Role_user);
        Long activeAdmins = userRepository.countByStatusAndRole(Status.ACTIVE, Role.ROLE_ADMIN);
        
        return new UserStatsDTO(activeUsers, totalUsers, activeRegularUsers, activeAdmins);
    }
    
    @Override
    public Long getActiveUserCount() {
        return userRepository.countByStatus(Status.ACTIVE);
    }
}