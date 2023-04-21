package com.example.psy_server.security;

import com.example.psy_server.entity.Psychologist;
import com.example.psy_server.repository.PsyRepository;
import com.example.psy_server.security.jwt.JwtPsy;
import com.example.psy_server.security.jwt.JwtPsyFactory;
import com.example.psy_server.service.PsyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtPsyDetailsService implements UserDetailsService {

    private final PsyRepository psyRepository;

    @Autowired
    public JwtPsyDetailsService(PsyRepository psyRepository){
        this.psyRepository = psyRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Psychologist psychologist = psyRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));

        JwtPsy jwtPsy = JwtPsyFactory.create(psychologist);
        log.info("IN loadUserByUsername user with username: {} successfully loaded", username);
        return jwtPsy;
    }
}
