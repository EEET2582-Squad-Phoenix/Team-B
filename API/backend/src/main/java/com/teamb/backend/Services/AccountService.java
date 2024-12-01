package com.teamb.backend.Services;

import java.util.List;
import java.util.UUID;
import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.teamb.backend.Models.Account;
import com.teamb.backend.Models.Charity;
import com.teamb.backend.Models.Donor;
import com.teamb.backend.Models.Registration;
import com.teamb.backend.Models.Role;
import com.teamb.backend.Repositories.AccountRepository;
import com.teamb.backend.Repositories.CharityRepository;
import com.teamb.backend.Repositories.DonorRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CharityRepository charityRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    public String authenticateUser(String email, String password) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(email);
        } else {
            return "fail";
        }
    }

   public Account registerUser(Registration registration) {
        // Validate email uniqueness
        if (accountRepository.findByEmail(registration.getEmail()) != null) {
            throw new IllegalArgumentException("Email already taken");
        }

        // Create and save the Account
        String sharedId = UUID.randomUUID().toString().split("-")[0];
        Account account = new Account();
        account.setId(sharedId);
        account.setEmail(registration.getEmail());
        account.setPassword(passwordEncoder.encode(registration.getPassword()));
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

    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    public Account getAccountByAccountId(String accountId){
        return accountRepository.findById(accountId).get();
    }

   
    public Account updateAccount(Account account, String id){
        Account existingAccount = accountRepository.findById(id).orElseThrow(() -> 
            new RuntimeException("Account not found with id: " + account.getId())
        );

        // Update existing fields
        existingAccount.setRole(account.getRole());
        existingAccount.setEmailVerified(account.getEmailVerified());
        existingAccount.setAdminCreated(account.getAdminCreated());

        // Update the updatedAt field
        existingAccount.setUpdatedAt(Instant.now());

        // Save the updated account
        return accountRepository.save(existingAccount);
    }

    public String deleteAccount(String accountId){
        accountRepository.deleteById(accountId);
        return "Account with id " + accountId + " deleted";
    }
}
