package com.dagytech.simreg.security;

import com.dagytech.simreg.model.StaffUser;
import com.dagytech.simreg.repository.StaffUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final StaffUserRepository staffUserRepository;

    @Autowired
    public CustomUserDetailsService(StaffUserRepository staffUserRepository) {
        this.staffUserRepository = staffUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        StaffUser staff = staffUserRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Mtumiaji hajapatikana: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(staff.getUsername())
                .password(staff.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + staff.getRole()))
                .build();
    }
}
