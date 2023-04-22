package com.example.psy_server.service;

import com.example.psy_server.entity.Certificate;
import com.example.psy_server.entity.Psychologist;
import com.example.psy_server.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CertificateService {
    private CertificateRepository certificateRepository;

    @Autowired
    public CertificateService(CertificateRepository certificateRepository){
        this.certificateRepository = certificateRepository;
    }

    public Certificate createCertificate(MultipartFile file) throws IOException {
        Certificate certificate = new Certificate();
        certificate.setImage(file.getBytes());
        Certificate savedCertificate = certificateRepository.save(certificate);

        return savedCertificate;
    }

    public void addPsyToCertificate(List<Integer> certificates, Psychologist registerPsy) {
        certificates.forEach(e -> {
            Certificate certificate = certificateRepository.findById(e)
                    .orElseThrow(() -> new UsernameNotFoundException("certificate with id: " + e + " not found"));
            certificate.setPsychologist(registerPsy);
            certificateRepository.save(certificate);
        });
    }
}
