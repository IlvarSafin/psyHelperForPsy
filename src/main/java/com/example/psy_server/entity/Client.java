package com.example.psy_server.entity;

import com.example.psy_server.entity.enums.ERole;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "client")
public class Client{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @NotEmpty(message = "Name should not be empty")
    @Column(name = "name")
    private String name;
    @NotEmpty(message = "Email should not be empty")
    //@Size(min = 6, message = "Login should be bigger 8 characters")

    @Email
    @Column(name = "email")
    private String email;
    @Column(name = "activation_code")
    private String activationCode;
    @NotEmpty(message = "Password should not be empty")
    @Size(min = 8, message = "Password should be bigger 8 characters")
    @Column(name = "password")
    private String password;
    @Column(name = "status")
    private boolean status;
    @Column(name = "money")
    private double money;
    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    @Column(name = "photo")
    private byte[] photo;

    @ElementCollection(targetClass = ERole.class)
    @CollectionTable(name = "client_role",
            joinColumns = @JoinColumn(name = "client_id"))
    private Set<ERole> roles = new HashSet<>();

    @OneToMany(mappedBy = "client")
    private List<Appointment> appointments;
}
