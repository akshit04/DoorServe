package com.doorserve.dto;

import com.doorserve.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private UserType userType;
    private String email;
    private String firstName;
    private String lastName;
}
