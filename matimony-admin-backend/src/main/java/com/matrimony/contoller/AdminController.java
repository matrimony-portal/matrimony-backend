package com.matrimony.contoller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.matrimony.dtos.AdminDTO;
import com.matrimony.service.AdminService;

@RestController
@RequestMapping("/admins")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping
    public ResponseEntity<List<AdminDTO>> getAllAdmins() {
        List<AdminDTO> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AdminDTO> getAdminById(@PathVariable Long id) {
        AdminDTO admin = adminService.getAdminById(id);
        return admin != null ? ResponseEntity.ok(admin) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/by-email")
    public ResponseEntity<AdminDTO> getAdminByEmail(@RequestParam String email) {
        AdminDTO admin = adminService.getAdminByEmail(email);
        return admin != null ? ResponseEntity.ok(admin) : ResponseEntity.notFound().build();
    }
}
