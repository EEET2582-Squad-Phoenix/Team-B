package com.teamb.authentication.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

// import com.teamb.authentication.services.AuthenticateService;
// import com.teamb.common.configurations.PasswordEncoding;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // @Autowired
    // private AuthenticateService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    // @Autowired
    // private PasswordEncoding passwordEncoding;

    // public SecurityConfig(AuthenticateService userDetailsService) {
    //     this.userDetailsService = userDetailsService;
    // }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return httpSecurity
                .csrf(csrf-> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth-> auth
                .requestMatchers("/auth/**", "/error").permitAll()
                .requestMatchers("/account/**", "admin/charity/**", "admin/donor/**").hasRole("ADMIN")
                .requestMatchers("/account/**", "donor/**").hasRole("DONOR")
                .requestMatchers("/account/**", "charity/**").hasRole("CHARITY")
                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // @Bean
    // public AuthenticationProvider authenticationProvider(){
    //     DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    //     provider.setPasswordEncoder(passwordEncoding.passwordEncoder());
    //     provider.setUserDetailsService(userDetailsService);
    //     return provider;
    // }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}