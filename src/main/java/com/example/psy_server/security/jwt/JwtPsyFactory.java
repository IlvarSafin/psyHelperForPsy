package com.example.psy_server.security.jwt;

import com.example.psy_server.entity.Psychologist;
import com.example.psy_server.entity.enums.ERole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.management.relation.Role;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtPsyFactory {
    public JwtPsyFactory(){

    }

    public static JwtPsy create(Psychologist psychologist){
        return new JwtPsy(
                psychologist.getId(),
                psychologist.getName(),
                psychologist.getLogin(),
                psychologist.getPassword(),
                psychologist.isStatus(),
                mapToGrantedAuthorities(psychologist.getRoles())
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Set<ERole> roles){
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.name()))
                .collect(Collectors.toList());
    }
}
