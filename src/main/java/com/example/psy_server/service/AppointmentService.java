package com.example.psy_server.service;

import com.example.psy_server.dto.AppointmentPsyDto;
import com.example.psy_server.entity.Appointment;
import com.example.psy_server.entity.Psychologist;
import com.example.psy_server.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PsyService psyService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PsyService psyService){
        this.appointmentRepository = appointmentRepository;
        this.psyService = psyService;
    }

    public AppointmentPsyDto createAppDto(Appointment appointment){
        return new AppointmentPsyDto(
                appointment.getId(),
                appointment.getDate(),
                appointment.isStatus(),
                appointment.getClient().getId(),
                appointment.getLink()
        );
    }

    public List<AppointmentPsyDto> getAllAppointments(){
        Psychologist psychologist = psyService.getPsychologist();

        return psychologist.getAppointments().stream()
                .map(appointment -> createAppDto(appointment))
                .collect(Collectors.toList());
    }
}
