package org.helmo;

import java.util.List;

/**
 * Classe de configuration centrale pour l'application Speaker.
 * Gère le chargement du fichier config-speaker.json, l'accès aux domaines et clés AES.
 */
public class SpeakerAppConfig {

    // TODO: Ce chemin est relatif et peut être fragile. Envisager une méthode de localisation plus robuste.
    private static final String CONFIG_FILE = "./Speaker/config-speaker.json";
    // TODO: Le DEFAULT_NODE_ADDRESS devrait être remplacé par une adresse de node découverte dynamiquement.
    private static final String DEFAULT_NODE_ADDRESS = "";
    // TODO: Le DEFAULT_BYTE_ARRAY pour la clé publique devrait être remplacé par une vraie clé publique si la vérification de signature est implémentée.
    private static final byte[] DEFAULT_BYTE_ARRAY = new byte[0];

    private List<ConfiguredDomain> configuredDomains;
    private String multicastAddress;
    private int multicastPort;
    private String networkInterface;

    public List<ConfiguredDomain> getConfiguredDomains() {
        // TODO: La conversion de ConfiguredDomain à Domain se fait à la volée.
        // Envisager de la faire une seule fois lors du chargement de la configuration pour de meilleures performances.
        return java.util.Collections.unmodifiableList(configuredDomains);
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
            if (base64AES == null || base64AES.isEmpty()) {
                throw new IllegalStateException("La clé AES Base64 pour le domaine '" + domain + "' est absente ou vide.");
            }
            final byte[] aesKeyBytes;
            try {
                aesKeyBytes = java.util.Base64.getDecoder().decode(base64AES);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("La clé AES Base64 pour le domaine '" + domain + "' est invalide.", e);
            }
            // TODO: La création du domaine utilise des valeurs par défaut pour l'adresse du nœud et la clé publique.
            // Ces valeurs devraient être mises à jour dynamiquement.
            return new org.helmo.domain.Domain(domain, aesKeyBytes, DEFAULT_NODE_ADDRESS, DEFAULT_BYTE_ARRAY);
        }
    }
}
