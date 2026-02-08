package com.matrimony.service;

import java.util.List;
import com.matrimony.dtos.AdminDTO;

public interface AdminService {
    List<AdminDTO> getAllAdmins();
    AdminDTO getAdminById(Long id);
    AdminDTO getAdminByEmail(String email);
}