package com.teamb.authentication.models;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.teamb.account.models.Account;

public class AccountAuth implements UserDetails{
    private Account account;

    public AccountAuth(Account account){
        this.account = account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert role to a GrantedAuthority
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()));
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
        return account.getEmailVerified() != null ? account.getEmailVerified() : false;
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getEmail();
    }
}
