package com.example.psy_server.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class PsyDto {
    @NotEmpty(message = "Login cannot be empty")
    @Size(min = 6, message = "Login should be bigger 6 characters")
    String login;
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 8, message = "Password should be bigger 8 characters")
    String password;
    @NotEmpty(message = "Name cannot be empty")
    String name;
}
