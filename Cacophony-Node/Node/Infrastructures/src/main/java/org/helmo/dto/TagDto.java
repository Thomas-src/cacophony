package org.helmo.dto;

import java.util.List;

public record TagDto(
        String tag,
        List<String> subscribers
) {}