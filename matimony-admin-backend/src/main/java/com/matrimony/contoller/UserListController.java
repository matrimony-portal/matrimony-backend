package com.matrimony.contoller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matrimony.dtos.UserListDTO;
import com.matrimony.service.UserListService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserListController {
    
    @Autowired
    private UserListService userListService;
    
    @GetMapping
    public ResponseEntity<List<UserListDTO>> getAllUsers() {
        List<UserListDTO> users = userListService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/regular")
    public ResponseEntity<List<UserListDTO>> getRegularUsers() {
        List<UserListDTO> users = userListService.getRegularUsers();
        return ResponseEntity.ok(users);
    }
}