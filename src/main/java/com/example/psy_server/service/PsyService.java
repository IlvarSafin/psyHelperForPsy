package com.example.psy_server.service;

import com.example.psy_server.entity.Appointment;
import com.example.psy_server.entity.Psychologist;
import com.example.psy_server.entity.enums.ERole;
import com.example.psy_server.repository.PsyRepository;
import com.example.psy_server.security.JwtPsyDetailsService;
import com.example.psy_server.security.jwt.JwtPsy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PsyService {
    private final PsyRepository psyRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public PsyService(PsyRepository psyRepository,
                      BCryptPasswordEncoder bCryptPasswordEncoder){
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.psyRepository = psyRepository;
    }

    public Psychologist register(Psychologist psychologist){
        Set<ERole> roles = new HashSet<>();
        roles.add(ERole.ROLE_PSY);

        psychologist.setPassword(bCryptPasswordEncoder.encode(psychologist.getPassword()));
        psychologist.setRoles(roles);
        psychologist.setStatus(false);

        Psychologist registerPsy = psyRepository.save(psychologist);
        log.info("IN register - psychologist: {} successfully registered", registerPsy);
        return registerPsy;
    }

    public List<Psychologist> notConfirmedPsys(){
        List<Psychologist> psys = psyRepository.findAll()
                .stream().filter(p -> !p.isStatus())
                .collect(Collectors.toList());
        return psys;
    }

    public List<Psychologist> confirmedPsys(){
        List<Psychologist> psys = psyRepository.findAll()
                .stream().filter(p -> p.isStatus())
                .collect(Collectors.toList());
        return psys;
    }

    public Psychologist findPsyById(int id){
        return psyRepository.findById(id)
                .orElseThrow(()->new UsernameNotFoundException("Psychologist with id: " + id + " not found"));
    }

    public byte[] uploadPhoto(MultipartFile file) throws IOException {
        Psychologist psychologist = getPsychologist();

        psychologist.setPhoto(file.getBytes());
        psyRepository.save(psychologist);

        return file.getBytes();
    }

    public Psychologist deletePhoto(){
        Psychologist psychologist = getPsychologist();
        psychologist.setPhoto(null);
        return psyRepository.save(psychologist);
    }

    public List<Appointment> activeAppointments(Psychologist psychologist){
        return psychologist.getAppointments().stream()
                .filter(appointment -> appointment.isStatus())
                .collect(Collectors.toList());
    }

    public Psychologist getPsychologist(){
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        JwtPsy jwtPsy = (JwtPsy) authentication.getPrincipal();
        return psyRepository.findByLogin(jwtPsy.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("No auth psy"));
    }

    public Psychologist saveEditPsy(Psychologist editPsy){
        Psychologist psychologist = getPsychologist();
        psychologist.setName(editPsy.getName());
        psychologist.setDescription(editPsy.getDescription());
        return psyRepository.save(psychologist);
    }
}
