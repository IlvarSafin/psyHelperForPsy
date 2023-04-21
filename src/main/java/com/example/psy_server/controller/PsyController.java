package com.example.psy_server.controller;

import com.example.psy_server.entity.Appointment;
import com.example.psy_server.entity.Psychologist;
import com.example.psy_server.repository.AppointmentRepository;
import com.example.psy_server.repository.PsyRepository;
import com.example.psy_server.service.AppointmentService;
import com.example.psy_server.service.PsyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/api/psy/")
public class PsyController {
    private final PsyService psyService;
    private final PsyRepository psyRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;

    @Autowired
    public PsyController(PsyService psyService,
                         PsyRepository psyRepository,
                         AppointmentRepository appointmentRepository,
                         AppointmentService appointmentService){
        this.psyRepository = psyRepository;
        this.psyService = psyService;
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
    }

    @GetMapping("appointments")
    public ResponseEntity getActiveAppointments(){
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("appointment/{id}")
    public ResponseEntity getAppointment(@PathVariable("id") int id){
        return ResponseEntity.ok(appointmentService.createAppDto(appointmentRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Appointment with id : " + id + " not found"))));
    }

    @PostMapping("appointment/{id}/addLink")
    public ResponseEntity addLink(@PathVariable("id") int id, @RequestBody String link){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("appointment with id: " + id + " not found"));
        appointment.setLink(link);
        appointmentRepository.save(appointment);
        return ResponseEntity.ok(appointmentService.createAppDto(appointment));
    }

    @PostMapping("appointment/{id}/deleteLink")
    public ResponseEntity deleteLink(@PathVariable("id") int id, String link){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("appointment with id: " + id + " not found"));
        appointment.setLink(null);
        appointmentRepository.save(appointment);
        return ResponseEntity.ok(appointmentService.createAppDto(appointment));
    }

    @PostMapping("appointment/{id}/cancel")
    public ResponseEntity cancelAppointment(@RequestBody Appointment appointment){
        appointment.setStatus(false);
        Appointment appointmentRes = appointmentRepository.save(appointment);
        return ResponseEntity.ok(appointmentRes);
    }

    @GetMapping("myProfile")
    public ResponseEntity getInfo(){
        return ResponseEntity.ok(psyService.getPsychologist());
    }

    @GetMapping("editProfile")
    public ResponseEntity forEditPage(){
        return ResponseEntity.ok(psyService.getPsychologist());
    }

    @PostMapping("editProfile")
    public ResponseEntity saveEditPsy(@RequestBody Psychologist psychologist){
        return ResponseEntity.ok(psyService.saveEditPsy(psychologist));
    }

    @PostMapping("uploadPhoto")
    public ResponseEntity<Object> uploadPhotoToPsy(@RequestParam("photo")MultipartFile file) throws IOException {
        psyService.uploadPhoto(file);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @PostMapping("deletePhoto")
    public ResponseEntity<Psychologist> deleteProfilePhoto(){
        Psychologist psychologist = psyService.deletePhoto();
        return new ResponseEntity<>(psychologist, HttpStatus.OK);
    }
}
