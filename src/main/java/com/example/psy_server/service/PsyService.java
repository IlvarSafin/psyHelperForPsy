package com.example.psy_server.service;

import com.example.psy_server.entity.Appointment;
import com.example.psy_server.entity.Certificate;
import com.example.psy_server.entity.Psychologist;
import com.example.psy_server.entity.enums.ERole;
import com.example.psy_server.payload.request.RegisterRequest;
import com.example.psy_server.repository.CertificateRepository;
import com.example.psy_server.repository.PsyRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PsyService {
    private final PsyRepository psyRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CertificateRepository certificateRepository;
    private final CertificateService certificateService;
    private MailSender mailSender;

    @Autowired
    public PsyService(PsyRepository psyRepository,
                      BCryptPasswordEncoder bCryptPasswordEncoder,
                      CertificateRepository certificateRepository,
                      CertificateService certificateService,
                      MailSender mailSender){
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.psyRepository = psyRepository;
        this.certificateRepository = certificateRepository;
        this.certificateService = certificateService;
        this.mailSender = mailSender;
    }

    public Psychologist register(RegisterRequest registerRequest){
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmedPassword())){
            throw new RuntimeException("Password not confirmed");
        }

        Set<ERole> roles = new HashSet<>();
        roles.add(ERole.ROLE_PSY);

        List<Certificate> certificates = new ArrayList<>();
        if (!registerRequest.getCertificates().isEmpty()) {
            registerRequest.getCertificates()
                    .forEach(e -> certificates.add(certificateRepository.findById(e)
                            .orElseThrow(() -> new UsernameNotFoundException("Certificate with id: " + e + " not found"))));
        }

        Psychologist psychologist = new Psychologist();
        psychologist.setEmail(registerRequest.getLogin());
        psychologist.setName(registerRequest.getName());
        psychologist.setPassword(bCryptPasswordEncoder.encode(registerRequest.getPassword()));
        psychologist.setDescription(registerRequest.getDescription());
        psychologist.setRoles(roles);
        psychologist.setStatus(false);
        psychologist.setCertificates(certificates);
        psychologist.setActivationCode(UUID.randomUUID().toString());

        Psychologist registerPsy = psyRepository.save(psychologist);
        certificateService.addPsyToCertificate(registerRequest.getCertificates(), registerPsy);

        String message = String.format(
                "Hello, %s! \n" +
                        "Welcome to psyHelper. Please, visit next link: http://localhost:8080/api/auth/activate/%s",
                psychologist.getEmail(), psychologist.getActivationCode()
        );
        mailSender.send(psychologist.getEmail(), "Activation code", message);

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
        return psyRepository.findByEmail(jwtPsy.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("No auth psy"));
    }

    public Psychologist saveEditPsy(Psychologist editPsy){
        Psychologist psychologist = getPsychologist();
        psychologist.setName(editPsy.getName());
        psychologist.setDescription(editPsy.getDescription());
        return psyRepository.save(psychologist);
    }

    public boolean activatePsy(String code) {
        Psychologist psychologist = psyRepository.findByActivationCode(code)
                .orElseThrow(() -> new UsernameNotFoundException("NOT Psychologist with code"));
        psychologist.setActivationCode(null);
        psychologist.setRegistered(true);

        psyRepository.save(psychologist);
        return psychologist.isRegistered();
    }
}
