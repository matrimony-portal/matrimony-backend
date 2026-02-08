package com.matrimony.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matrimony.dtos.UserStatsDTO;
import com.matrimony.service.UserStatsService;

@RestController
@RequestMapping("/api/user-stats")
@CrossOrigin(origins = "*")
public class UserStatsController {
    
    @Autowired
    private UserStatsService userStatsService;
    
    @GetMapping("/active-count")
    public ResponseEntity<Long> getActiveUserCount() {
        Long activeCount = userStatsService.getActiveUserCount();
        return ResponseEntity.ok(activeCount);
    }
    
    @GetMapping
    public ResponseEntity<UserStatsDTO> getUserStats() {
        UserStatsDTO stats = userStatsService.getUserStats();
        return ResponseEntity.ok(stats);
    }
}