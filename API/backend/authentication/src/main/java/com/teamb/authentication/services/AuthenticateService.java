package com.teamb.authentication.services;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.teamb.account.models.Account;
import com.teamb.account.repositories.AccountRepository;
import com.teamb.authentication.models.AccountAuth;
import com.teamb.authentication.models.Registration;
import com.teamb.charity.models.Charity;
import com.teamb.charity.repositories.CharityRepository;
import com.teamb.common.configurations.PasswordEncoding;
import com.teamb.common.exception.EntityNotFound;
import com.teamb.common.models.Role;
import com.teamb.common.services.MailService;
import com.teamb.donor.models.Donor;
import com.teamb.donor.repositories.DonorRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthenticateService implements UserDetailsService{

    @Autowired
    private CharityRepository charityRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private PasswordEncoding passwordEncoding;

    @Autowired
    private MailService mailService;
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JWTService jwtService;


   public Account registerUser(Registration registration) {
        // Validate email uniqueness
        if (checkEmail(registration.getEmail())) {
            throw new IllegalArgumentException("Email already taken");
        }

        // Create and save the Account
        String sharedId = UUID.randomUUID().toString().split("-")[0];
        Account account = new Account();
        account.setId(sharedId);
        account.setEmail(registration.getEmail());
        account.setPassword(passwordEncoding.passwordEncoder().encode(registration.getPassword()));
        account.setRole(registration.getRole());
        if(registration.getRole() == Role.ADMIN){
            account.setEmailVerified(true);
        }else{
            account.setEmailVerified(registration.getEmailVerified());
        }
        account.setCreatedAt(Instant.now());
        account.setAdminCreated(false); // Default
        accountRepository.save(account);

        // Generate email verification token
        String verificationToken = UUID.randomUUID().toString();

        // Save the token to the account
        account.setVerificationToken(verificationToken);
        accountRepository.save(account);

        // Send the verification email
        mailService.sendVerificationEmail(registration.getEmail(), verificationToken);

        // Save Charity or Donor based on role
        if (registration.getRole() == Role.CHARITY) {
            if (registration.getName() == null || registration.getName().isEmpty() ||
                registration.getAddress() == null || registration.getAddress().isEmpty() ||
                registration.getTaxCode() == null || registration.getTaxCode().isEmpty() ) {
                throw new IllegalArgumentException("Missing required fields for Charity");
            }else{
                Charity charity = new Charity();
                charity.setId(sharedId);
                charity.setName(registration.getName());
                charity.setLogoUrl(registration.getLogoUrl());
                charity.setIntroVidUrl(registration.getIntroVidUrl());
                charity.setAddress(registration.getAddress());
                charity.setTaxCode(registration.getTaxCode());
                charity.setType(registration.getCharityType());
                charityRepository.save(charity);
            }
        } else if (registration.getRole() == Role.DONOR) {
            if (registration.getFirstName() == null || registration.getFirstName().isEmpty() ||
                registration.getLastName() == null || registration.getLastName().isEmpty()) {
                throw new IllegalArgumentException("Missing required fields for Charity");
            }else{
                Donor donor = new Donor();
                donor.setId(sharedId);
                donor.setAvatarUrl(registration.getAvatarUrl());
                donor.setIntroVidUrl(registration.getDonorIntroVidUrl());
                donor.setFirstName(registration.getFirstName());
                donor.setLastName(registration.getLastName());
                donor.setAddress(registration.getDonorAddress());
                donorRepository.save(donor);
            }
        }

        return account;
    }

    public Boolean checkEmail(String email){
        return (accountRepository.findByEmail(email) != null);      
    }

    public String verifyEmail(String token){
        Account account = accountRepository.findByVerificationToken(token);
        if (account != null && !account.getEmailVerified()) {
            account.setEmailVerified(true);  // Mark the account as verified
            account.setVerificationToken(null); // Remove the verification token
            accountRepository.save(account);
            return "Verified successfully"; // Return confirmation message
        } else {
            return "Invalid or already verified token"; // Return an error message if token is invalid
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account user = accountRepository.findByEmail(email);
        if (user == null) {
            System.out.println("User Not Found");
            throw new UsernameNotFoundException("user not found");
        }
        
        return new AccountAuth(user);
    }

    public ResponseEntity<?> getMe(HttpServletRequest request){
            // Retrieve cookies from the request
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) { // Look for the 'jwt' cookie
                    String token = cookie.getValue(); // Get the token value
                    String id = (jwtService.extractAccountId(token));
                    String role = (jwtService.extractRole(token));
                    String email = (jwtService.extractEmail(token));

                    Map<String, Object> response = new HashMap<>();
                    response.put("role", role);
                    response.put("email", email);
                    if("DONOR".equals(role)){
                        Donor donor = donorRepository.findById(id).orElseThrow(() -> new EntityNotFound("Donor not found for ID: " + id));
                        response.put("data", donor);
                    }else if("CHARITY".equals(role)){
                        Charity charity = charityRepository.findById(id).orElseThrow(() -> new EntityNotFound("Charity not found for ID: " + id));
                        response.put("data", charity);
                    }

                    return ResponseEntity.ok(response);
                }
            }
            
        }
        throw new IllegalStateException("JWT cookie not found or invalid");
      
    }


    public String deleteAccount(String id) {
        boolean isCharity = charityRepository.existsById(id) && accountRepository.existsById(id);
        boolean isDonor =  donorRepository.existsById(id) && accountRepository.existsById(id);
        if (isCharity) {
            accountRepository.deleteById(id);
            charityRepository.deleteById(id);
            return "Account with id " + id + " deleted";
        }
        if (isDonor) {
            accountRepository.deleteById(id);
            donorRepository.deleteById(id);
            return "Account with id " + id + " deleted";
        }
        throw new EntityNotFound("Id", id);
    }
}
