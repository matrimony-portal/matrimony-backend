package com.matrimony.service;

public interface UserManagementService {
    boolean blockUser(String email);
    boolean unblockUser(String email);
}