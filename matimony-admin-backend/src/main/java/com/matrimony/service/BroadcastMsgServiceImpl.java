package com.matrimony.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.matrimony.dtos.BroadcastMsgDTO;
import com.matrimony.entites.BroadcastMsg;
import com.matrimony.repository.BroadcastMsgRepository;

@Service
public class BroadcastMsgServiceImpl implements BroadcastMsgService {
    
    @Autowired
    private BroadcastMsgRepository broadcastMsgRepository;
    
    @Override
    public BroadcastMsgDTO createMessage(BroadcastMsgDTO messageDTO) {
        BroadcastMsg message = new BroadcastMsg();
        message.setAdminName(messageDTO.getAdminName());
        message.setMessage(messageDTO.getMessage());
        message.setDate(LocalDate.now());
        message.setTime(LocalTime.now());
        
        BroadcastMsg savedMessage = broadcastMsgRepository.save(message);
        return convertToDTO(savedMessage);
    }
    
    @Override
    public List<BroadcastMsgDTO> getAllMessages() {
        return broadcastMsgRepository.findAllByOrderByDateDescTimeDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private BroadcastMsgDTO convertToDTO(BroadcastMsg message) {
        BroadcastMsgDTO dto = new BroadcastMsgDTO();
        dto.setId(message.getId());
        dto.setAdminName(message.getAdminName());
        dto.setMessage(message.getMessage());
        dto.setDate(message.getDate());
        dto.setTime(message.getTime());
        return dto;
    }
}