package org.helmo;

public record ConfigDto(String name, String token) {
    public ConfigDto(String name, String token) {
        this.name = name;
        this.token = token;
    }
};
