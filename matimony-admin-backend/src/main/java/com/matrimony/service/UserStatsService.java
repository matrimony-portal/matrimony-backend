package com.matrimony.service;

import com.matrimony.dtos.UserStatsDTO;

public interface UserStatsService {
    UserStatsDTO getUserStats();
    Long getActiveUserCount();
}