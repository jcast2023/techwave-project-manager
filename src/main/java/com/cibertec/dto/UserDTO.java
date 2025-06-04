package com.cibertec.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean active;
    private LocalDateTime createdAt;
    private RoleDTO role;
}
