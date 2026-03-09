package org.helmo;

import java.util.List;

public record ConfigDto(String currentDomain,
                        int saltSizeInBytes,
                        String multicastAddress,
                        int multicastPort,
                        int unicastPort,
                        int relayPort,
                        String networkInterface,
                        String base64AES,
                        boolean tls
) {
};
