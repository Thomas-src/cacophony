package org.helmo.dto;

import java.util.List;

public record UserDto(
        String login,
        String bcryptHash,
        int bcryptRound,
        String bcryptSalt,
        List<String> subscribers,
        List<String> userTags
) {}