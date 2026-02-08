package com.matrimony.service;

import java.util.List;
import com.matrimony.dtos.UserListDTO;

public interface UserListService {
    List<UserListDTO> getAllUsers();
    List<UserListDTO> getRegularUsers();
}