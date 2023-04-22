package com.example.psy_server.payload.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class RegisterRequest {
    @NotEmpty(message = "Login cannot be empty")
    @Size(min = 6, message = "Login should be bigger 6 characters")
    private String login;
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 8, message = "Password should be bigger 8 characters")
    private String password;
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 8, message = "Password should be bigger 8 characters")
    private String confirmedPassword;
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    @NotEmpty(message = "Description cannot be empty")
    private String description;

    private List<Integer> certificates;
}
