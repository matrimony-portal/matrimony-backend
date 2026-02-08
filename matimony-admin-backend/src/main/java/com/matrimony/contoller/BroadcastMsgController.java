package com.matrimony.contoller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.matrimony.dtos.BroadcastMsgDTO;
import com.matrimony.service.BroadcastMsgService;

@RestController
@RequestMapping("/broadcast-messages")
@CrossOrigin(origins = "*")
public class BroadcastMsgController {
    
    @Autowired
    private BroadcastMsgService broadcastMsgService;
    
    @PostMapping
    public ResponseEntity<BroadcastMsgDTO> createMessage(@RequestBody BroadcastMsgDTO messageDTO) {
        BroadcastMsgDTO createdMessage = broadcastMsgService.createMessage(messageDTO);
        return ResponseEntity.ok(createdMessage);
    }
    
    @GetMapping
    public ResponseEntity<List<BroadcastMsgDTO>> getAllMessages() {
        List<BroadcastMsgDTO> messages = broadcastMsgService.getAllMessages();
        return ResponseEntity.ok(messages);
    }
}