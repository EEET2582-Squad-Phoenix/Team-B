package com.teamb.backend.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("accounts")
public class Account implements UserDetails{
    @Id
    private String id;
    private String email;
    private String password;
    private Role role; 
    private Boolean emailVerified;
    private Boolean adminCreated;
    private Instant createdAt;
    private Instant updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert role to a GrantedAuthority
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email; // Email used as username
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Always active
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Not locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Always valid
    }

    @Override
    public boolean isEnabled() {
        return emailVerified != null ? emailVerified : false;
    }
}

enum Role {
    DONOR, CHARITY, ADMIN
}