package com.matrimony.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matrimony.service.UserManagementService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserManagementController {
    
    @Autowired
    private UserManagementService userManagementService;
    
    @PutMapping("/{email}/status")
    public ResponseEntity<String> blockUser(@PathVariable String email) {
        boolean blocked = userManagementService.blockUser(email);
        if (blocked) {
            return ResponseEntity.ok("User blocked successfully");
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{email}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable String email) {
        boolean unblocked = userManagementService.unblockUser(email);
        if (unblocked) {
            return ResponseEntity.ok("User unblocked successfully");
        }
        return ResponseEntity.notFound().build();
    }
}