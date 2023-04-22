package com.example.psy_server.repository;

import com.example.psy_server.entity.Psychologist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PsyRepository extends JpaRepository<Psychologist, Integer> {
    Optional<Psychologist> findByEmail(String email);
    Optional<Psychologist> findByActivationCode(String code);
}
