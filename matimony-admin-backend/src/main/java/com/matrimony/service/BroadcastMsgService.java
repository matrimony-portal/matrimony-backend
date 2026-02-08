package com.matrimony.service;

import java.util.List;
import com.matrimony.dtos.BroadcastMsgDTO;

public interface BroadcastMsgService {
    BroadcastMsgDTO createMessage(BroadcastMsgDTO messageDTO);
    List<BroadcastMsgDTO> getAllMessages();
}