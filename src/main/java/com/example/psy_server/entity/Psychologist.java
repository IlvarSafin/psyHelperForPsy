package com.example.psy_server.entity;

import com.example.psy_server.entity.enums.ERole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "psychologist")
public class Psychologist{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "estimation")
    private double estimation;
    @Column(name = "status")
    private boolean status;
    @Column(name = "email")
    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;
    @NotEmpty(message = "Password should not be empty")
    @Size(min = 8, message = "Password should be bigger 8 characters")
    @Column(name = "password")
    private String password;
    @Column(name = "description")
    private String description;
    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    @Column(name="photo")
    private byte[] photo;
    @Column(name="activation_code")
    private String activationCode;
    @Column(name = "registered")
    private boolean registered;
    @Column(name = "sex")
    @NotEmpty(message = "Sex should not be empty")
    private String sex;
    @Column(name = "price")
    @NotNull(message = "Price cannot be null")
    private Double price;

    @JsonIgnore
    @OneToMany(mappedBy = "psychologist")
    private List<Review> reviews;

    @JsonIgnore
    @OneToMany(mappedBy = "psychologist")
    private List<Appointment> appointments;

    @JsonIgnore
    @ElementCollection(targetClass = ERole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "psy_role",
            joinColumns = @JoinColumn(name = "psy_id"))
    private Set<ERole> roles = new HashSet<>();

    @OneToMany(mappedBy = "psychologist")
    private List<Certificate> certificates;
}
