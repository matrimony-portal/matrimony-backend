package com.matrimony.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.matrimony.entites.BroadcastMsg;

@Repository
public interface BroadcastMsgRepository extends JpaRepository<BroadcastMsg, Long> {
    List<BroadcastMsg> findAllByOrderByDateDescTimeDesc();
}