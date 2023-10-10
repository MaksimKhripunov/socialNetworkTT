package ru.khripunov.socialnetworktt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "users")
public class Person implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="pwd")
    @NotBlank(message = "not empty password")
    @Size(min=5, message = "should be longer than 5")
    private String pwd;
    @Column(name="firstname")
    @NotBlank(message = "not empty firstname")
    private String firstname;
    @Column(name="lastname")
    @NotBlank(message = "not empty lastname")
    private String lastname;
    @Column(name="username")
    @NotBlank(message = "not empty username")
    private String username;
    @Column(name="email")
    @NotBlank(message = "not empty email")
    @Email
    private String email;

    @Column(name = "delete_time")
    private Date deleteTime;


    public Person(){
        this.deleteTime=null;
    }

    public Person(String pwd, String firstname, String lastname, String username, String email){
        this.pwd=pwd;
        this.firstname=firstname;
        this.lastname=lastname;
        this.email=email;
        this.username=username;
        this.deleteTime=null;
    }


    public Long getId() {
        return id;
    }


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of();
    }


    public String getPassword() {
        return pwd;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public void setPassword(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String toString(){
        return "Person [id="+id+", firstname="+firstname+", lastname="+lastname+", username="+username+", password="+pwd+", email="+email+"]";
    }


    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }
}
