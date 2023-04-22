package com.example.psy_server.controller;

import com.example.psy_server.dto.AuthReqDto;
import com.example.psy_server.entity.Certificate;
import com.example.psy_server.entity.Psychologist;
import com.example.psy_server.payload.request.RegisterRequest;
import com.example.psy_server.repository.PsyRepository;
import com.example.psy_server.security.jwt.JwtTokenProvider;
import com.example.psy_server.service.CertificateService;
import com.example.psy_server.service.PsyService;
import com.example.psy_server.service.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/auth/")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PsyService psyService;
    private final PsyRepository psyRepository;
    private final ResponseErrorValidation responseErrorValidation;
    private final CertificateService certificateService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          PsyService psyService,
                          PsyRepository psyRepository,
                          ResponseErrorValidation responseErrorValidation,
                          CertificateService certificateService){
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.psyService = psyService;
        this.psyRepository = psyRepository;
        this.responseErrorValidation = responseErrorValidation;
        this.certificateService = certificateService;
    }

    @PostMapping("reg")
    public ResponseEntity reg(@RequestBody @Valid RegisterRequest registerRequest,
                              BindingResult bindingResult){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)){
            return errors;
        }

        Psychologist psychologist = psyService.register(registerRequest);

        return ResponseEntity.ok(psychologist);
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody @Valid AuthReqDto authReqDto,
                                BindingResult bindingResult){
        try{
            System.out.println("1111111111");
            ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
            if (!ObjectUtils.isEmpty(errors)){
                return errors;
            }

            String login = authReqDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, authReqDto.getPassword()));
            System.out.println("222222222");
            Psychologist psychologist = psyRepository.findByEmail(login)
                    .orElseThrow(() -> new UsernameNotFoundException("User with login: " + login + " not found"));
            System.out.println("333333333");
            String token = jwtTokenProvider.createToken(login, psychologist.getRoles());

            Map<Object, Object> response = new HashMap<>();
            response.put("login", login);
            response.put("token", "Bearer " + token);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("createCertificate")
    public ResponseEntity<Certificate> createCertificate(@RequestParam("certificate") MultipartFile file) throws IOException {
        Certificate certificate = certificateService.createCertificate(file);
        return new ResponseEntity<>(certificate, HttpStatus.OK);
    }
}
