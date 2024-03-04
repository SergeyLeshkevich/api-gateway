package ru.clevertec.gateway.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Data
public class JwtResponse implements Serializable {
    private UUID uuid;
    private String username;
    private Set<String> roles;
    private String accessToken;
    private String refreshToken;
}
