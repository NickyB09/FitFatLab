package com.fitfatlab.fitfatlab_backend.security;

import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("userResolver")
@RequiredArgsConstructor
public class UserResolver {

    private final UserRepository userRepository;

    public UUID resolve(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow()
                .getId();
    }
}
