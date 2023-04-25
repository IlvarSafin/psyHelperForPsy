package com.example.psy_server.service;

import com.example.psy_server.dto.AppointmentPsyDto;
import com.example.psy_server.entity.Appointment;
import com.example.psy_server.entity.Psychologist;
import com.example.psy_server.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        List<Appointment> appointments = psychologist.getAppointments();
        long twoHoursInMillieSeconds = 7200000;

        appointments.forEach(e -> {
            if ((e.getDate().getTime() + twoHoursInMillieSeconds) <= System.currentTimeMillis()){
                e.setStatus(false);
                appointmentRepository.save(e);
            }
        });

        return psychologist.getAppointments().stream()
                .filter(Appointment::isStatus)
                .map(this::createAppDto)
                .collect(Collectors.toList());
    }

    public boolean finishingAppointment(int id) {
        long twoHoursInMillieSeconds = 7200000;

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("appointment with id: " + id + " not found"));
        if ((appointment.getDate().getTime() + twoHoursInMillieSeconds) <= System.currentTimeMillis()) {
            appointment.setStatus(false);
            Appointment appointmentRes = appointmentRepository.save(appointment);
            return true;
        }

        return false;
    }
}
