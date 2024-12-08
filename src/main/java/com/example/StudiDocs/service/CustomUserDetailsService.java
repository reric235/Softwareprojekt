// src/main/java/com/example/StudiDocs/service/CustomUserDetailsService.java

package com.example.StudiDocs.service;

import com.example.StudiDocs.model.Student;
import com.example.StudiDocs.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student student = studentRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden"));

        return User.builder()
                .username(student.getEmail())
                .password(student.getPasswort())
                .authorities("ROLE_" + student.getRolle().name()) // Rollen mit Präfix
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!student.getVerifiziert())
                .build();
    }
}
