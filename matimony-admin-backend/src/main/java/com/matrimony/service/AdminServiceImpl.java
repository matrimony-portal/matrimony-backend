package com.matrimony.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matrimony.dtos.AdminDTO;
import com.matrimony.entites.Role;
import com.matrimony.entites.User;
import com.matrimony.repository.UserRepository;

@Service
public class AdminServiceImpl implements AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public List<AdminDTO> getAllAdmins() {
        return userRepository.findByRole(Role.ROLE_ADMIN)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public AdminDTO getAdminById(Long id) {
        return userRepository.findByIdAndRole(id, Role.ROLE_ADMIN)
                .map(this::convertToDTO)
                .orElse(null);
    }
    
    @Override
    public AdminDTO getAdminByEmail(String email) {
        return userRepository.findByEmailAndRole(email, Role.ROLE_ADMIN)
                .map(this::convertToDTO)
                .orElse(null);
    }
    
    private AdminDTO convertToDTO(User user) {
        AdminDTO dto = new AdminDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus().toString());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}