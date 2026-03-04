package org.helmo;

import java.util.List;

public record TagDto(
        String tag,
        List<String> subscribers
) {}