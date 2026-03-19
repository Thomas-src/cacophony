package org.helmo;

import java.util.List;

/**
 * Classe de configuration centrale pour l'application Speaker.
 * Gère le chargement du fichier config-speaker.json, l'accès aux domaines et clés AES.
 */
public class SpeakerAppConfig {

    private static final String CONFIG_FILE = "Speaker/config-speaker.json";

    private List<ConfiguredDomain> configuredDomains;
    private String multicastAddress;
    private int multicastPort;
    private String networkInterface;

    public List<ConfiguredDomain> getConfiguredDomains() {
        return configuredDomains;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    public String getNetworkInterface() {
        return networkInterface;
    }

    @SuppressWarnings("unused")
    public static class ConfiguredDomain {
        private String domain;
        private String base64AES;

        public String getDomain() {
            return domain;
        }

        public String getBase64AES() {
            return base64AES;
        }

        public org.helmo.domain.Domain toDomain() {
            // Implémentation de la conversion en org.helmo.domain.Domain
            return new org.helmo.domain.Domain(domain, java.util.Base64.getDecoder().decode(base64AES));
        }
    }
}
