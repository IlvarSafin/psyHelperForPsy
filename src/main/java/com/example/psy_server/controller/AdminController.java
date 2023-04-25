package com.example.psy_server.controller;

import com.example.psy_server.entity.Psychologist;
import com.example.psy_server.repository.PsyRepository;
import com.example.psy_server.service.PsyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin/")
public class AdminController {
    private PsyService psyService;
    private PsyRepository psyRepository;

    @Autowired
    public AdminController(PsyService psyService,
                           PsyRepository psyRepository){
        this.psyService = psyService;
        this.psyRepository = psyRepository;
    }

    @GetMapping("notConfirmedPsys")
    public ResponseEntity showAllNotConfirmedPsys(){
        List<Psychologist> psys = psyService.notConfirmedPsys();
        return ResponseEntity.ok(psys);
    }

    @GetMapping("confirmedPsys")
    public ResponseEntity showAllConfirmedPsys(){
        List<Psychologist> psys = psyService.confirmedPsys();
        return ResponseEntity.ok(psys);
    }

    @GetMapping("psy/{id}")
    public ResponseEntity showPsy(@PathVariable("id") int id){
        Psychologist psychologist = psyService.findPsyById(id);
        System.out.println("11111111111111  " + psychologist.getCertificates());
        return ResponseEntity.ok(psychologist);
    }

    @PostMapping("psy/{id}")
    public ResponseEntity changeStatus(@RequestBody Psychologist psychologist){
        psychologist.setStatus(psychologist.isStatus());
        psyRepository.save(psychologist);
        return ResponseEntity.ok(psychologist);
    }
}
