package com.devos.backend.auth.service;

import com.devos.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsernameGenerator {

    private final UserRepository userRepository;

    public String generate(String firstName, String lastName) {

        String baseUsername = buildBaseUsername(firstName, lastName);

        String username = baseUsername;

        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    private String buildBaseUsername(
            String firstName,
            String lastName
    ) {

        String first = firstName
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");

        String last = lastName == null
                ? ""
                : lastName
                    .trim()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "");

        if (last.isEmpty()) {
            return first;
        }

        return first + "_" + last;
    }
}