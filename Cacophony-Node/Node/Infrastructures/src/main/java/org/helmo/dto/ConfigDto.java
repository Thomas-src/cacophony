package org.helmo.dto;


public record ConfigDto(String currentDomain,
                        int saltSizeInBytes,
                        String multicastAddress,
                        int multicastPort,
                        int unicastPort,
                        int relayPort,
                        String networkInterface,
                        String base64AES,
                        boolean tls,
                        String certificateName,
                        String certificatePassword
                        ) {
};
