package com.matrimony.dtos;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserListDTO {
    private String userName;
    private String email;
    private String contactNo;
    private String status;
    private String subscription;
    private LocalDateTime joinDate;
}