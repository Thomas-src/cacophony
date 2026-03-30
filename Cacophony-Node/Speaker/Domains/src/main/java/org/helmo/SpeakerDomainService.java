//package org.helmo;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonParseException;
//import org.helmo.crypto.CryptoProvider;
//import org.helmo.domain.Domain;
//import org.helmo.domain.MessageRouter;
//import org.helmo.dto.DecryptedMessageDto;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Map;
//import java.util.Optional;
//
///**
// * Service métier principal du Speaker : gestion des domaines, routage, logique de traitement des messages.
// * À implémenter : méthodes pour router, valider, transformer les messages, gestion des domaines et clés.
// */
//public class SpeakerDomainService implements MessageRouter {
//    private final Map<String, Domain> domains;
//    private final CryptoProvider cryptoService;
//    private static final Gson gson = new Gson();
//
//    public SpeakerDomainService(Map<String, Domain> domains, CryptoProvider cryptoService) {
//        this.domains = domains;
//        this.cryptoService = cryptoService;
//    }
//
//    /**
//     * Retourne le domaine correspondant au nom, s'il existe.
//     */
//    public Optional<Domain> getDomain(String name) {
//        return Optional.ofNullable(domains.get(name));
//    }
//
//    /**
//     * Exemple de squelette pour router un message POST.
//     */
//    @Override
//    public void routePostMessage(String fromDomainName, String toDomainName, byte[] encryptedMessage) {
//        //1. Récupérer les objets Domain pour 'fromDomain' et 'toDomain' via la Map 'domains'
//        Domain fromDomain = getDomain(fromDomainName).orElseThrow(() -> new IllegalArgumentException("Le domaine source n'existe pas: " + fromDomainName));
//        Domain toDomain = getDomain(toDomainName).orElseThrow(() -> new IllegalArgumentException("Le domaine destinataire n'existe pas: " + toDomainName));
//
//        // 2. Valider l'existence et l'état des domaines (ex: sont-ils actifs ?)
//        if (!fromDomain.isActive()) {
//            throw new IllegalStateException("Le domaine source n'est pas actif: " + fromDomainName);
//        }
//        if (!toDomain.isActive()) {
//            throw new IllegalStateException("Le domaine destinataire n'est pas actif: " + toDomainName);
//        }
//
//        // 3. Déchiffrer 'encryptedMessage' en utilisant la clé AES du domaine source
//        byte[] decryptedMessage = cryptoService.decrypt(encryptedMessage, fromDomain.getAesKey());
//
//
//        // 4. Valider le contenu du message déchiffré (format, intégrité)
//        validateMessageContent(decryptedMessage);
//
//        // 5. Transformer ou logger le message si nécessaire pour le routage
//        // TODO: La transformation du message doit être définie. Pour l'instant, c'est une simple concaténation.
//        String transformedMessage = "[Transformed] " + new String(decryptedMessage, StandardCharsets.UTF_8);
//
//        // 6. Chiffrer le message avec la clé AES du domaine destinataire (chiffrement symétrique efficace)
//
//        // String encryptedTransformedMessage = cryptoService.encrypt(transformedMessage);
//
//
//
//        // 7. Identifier l'adresse du Node destinataire
//        String destinationAddress = toDomain.getName();
//        // TODO: Gérer le cas où l'adresse du nœud n'est pas encore connue (DEFAULT_NODE_ADDRESS).
//        // Le message pourrait être mis en file d'attente jusqu'à ce que l'adresse soit découverte via un message ADVERT.
//
//        // 8. Envoyer le message via le client réseau approprié
//        sendMessage(destinationAddress, encryptedTransformedMessage);
//    }
//
//    private void sendMessage(String destinationAddress, byte[] encryptedTransformedMessage) {
//        // TODO: Implémenter un client HTTP pour envoyer le message chiffré au Node destinataire.
//        // Utiliser une bibliothèque comme OkHttp ou le client HTTP de Java 11.
//        // Gérer les erreurs réseau (ex: connexion refusée, timeout).
//        System.out.println("Envoi du message à l'adresse : " + destinationAddress);
//        System.out.println("Message chiffré : [REDACTED] (" + encryptedTransformedMessage.length + " bytes)");
//    }
//
//    @Override
//    public void validateDecryptedMessage(byte[] decryptedMessage) {
//        validateMessageContent(decryptedMessage);
//    }
//
//    /**
//     * Valide le contenu d'un message déchiffré.
//     * Vérifie que le message est un JSON valide et contient les champs requis.
//     *
//     * @param decryptedMessage Le message déchiffré en bytes.
//     * @throws IllegalArgumentException si le message est invalide.
//     */
//    private void validateMessageContent(byte[] decryptedMessage) {
//        try {
//            String messageContent = new String(decryptedMessage, StandardCharsets.UTF_8);
//            DecryptedMessageDto messageDto = gson.fromJson(messageContent, DecryptedMessageDto.class);
//
//            if (messageDto == null || messageDto.getType() == null || messageDto.getPayload() == null) {
//                throw new IllegalArgumentException("Le message déchiffré est invalide : champs requis manquants ou format JSON incorrect.");
//            }
//            // TODO: Ajouter une validation plus poussée du payload en fonction du type de message.
//        } catch (JsonParseException e) {
//            throw new IllegalArgumentException("Le message déchiffré n'est pas un JSON valide.", e);
//        }
//    }
//
//    // TODO: Ajouter une méthode pour mettre à jour l'adresse d'un nœud (appelée après la réception d'un message ADVERT).
//    // public void updateNodeAddress(String domainName, String nodeAddress) { ... }
//}
