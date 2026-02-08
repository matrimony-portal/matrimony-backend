package com.matrimony.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserStatsDTO {
    private Long activeUsers;
    private Long totalUsers;
    private Long activeRegularUsers;
    private Long activeAdmins;
}