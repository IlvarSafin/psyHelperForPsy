package com.example.psy_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentPsyDto {
    private int id;
    private Date date;
    private boolean status;
    private int clientId;
    private String link;
}
