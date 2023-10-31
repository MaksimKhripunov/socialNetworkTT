package ru.khripunov.socialnetworktt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class Person implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="pwd")
    private String pwd;
    @Column(name="firstname")
    private String firstname;
    @Column(name="lastname")
    private String lastname;
    @Column(name="username")
    private String username;
    @Column(name="email")
    private String email;

    @Column(name = "delete_time")
    private Date deleteTime=null;

    @Column(name = "message_only_friends")
    private Boolean messageOnlyFriends=false;

    @Column(name = "hide_friends_list")
    private Boolean hideFriendsList=false;



    @ManyToMany
    @JoinTable(name = "friends",
            joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "id", nullable = false))
    private List<Person> friends;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of();
    }

    @Override
    public String getPassword() {
        return pwd;
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

    @Override
    public String toString(){
        return "Person [id="+id+", firstname="+firstname+", lastname="+lastname+", username="+username+", password="+pwd+", email="+email+"]";
    }
}
