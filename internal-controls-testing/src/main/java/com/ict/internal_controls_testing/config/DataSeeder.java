package com.ict.internal_controls_testing.config;

import com.ict.internal_controls_testing.entity.Control;
import com.ict.internal_controls_testing.entity.User;
import com.ict.internal_controls_testing.repository.ControlRepository;
import com.ict.internal_controls_testing.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository, ControlRepository controlRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role("ROLE_ADMIN")
                        .build();

                User user = User.builder()
                        .username("user")
                        .email("user@example.com")
                        .password(passwordEncoder.encode("user123"))
                        .role("ROLE_USER")
                        .build();

                userRepository.save(admin);
                userRepository.save(user);

                if (controlRepository.count() == 0) {
                    List<Control> controls = new ArrayList<>();
                    for (int i = 1; i <= 30; i++) {
                        controls.add(Control.builder()
                                .title("Internal Control Test " + i)
                                .description("Description for internal control test " + i + ". This verifies compliance standard " + (i % 5 + 1) + ".")
                                .status(i % 3 == 0 ? "PASSED" : (i % 2 == 0 ? "FAILED" : "PENDING"))
                                .riskLevel(i % 4 == 0 ? "HIGH" : (i % 3 == 0 ? "MEDIUM" : "LOW"))
                                .deadline(LocalDateTime.now().plusDays(i))
                                .assignee(i % 2 == 0 ? admin : user)
                                .build());
                    }
                    controlRepository.saveAll(controls);
                }
            }
        };
    }
}
