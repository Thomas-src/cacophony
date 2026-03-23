package org.helmo;

import org.helmo.domain.CryptoProvider;
import org.helmo.domain.Domain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpeakerDomainServiceTest {

    private SpeakerDomainService domainService;

    @BeforeEach
    void setUp() {
        Domain domain1 = new Domain("domain1", new byte[]{1, 2, 3}, "nodeAddress1", new byte[0]);
        Domain domain2 = new Domain("domain2", new byte[]{4, 5, 6}, "nodeAddress2", new byte[0]);
        Map<String, Domain> domains = new HashMap<>();
        domains.put("domain1", domain1);
        domains.put("domain2", domain2);

        CryptoProvider cryptoProvider = mock(CryptoProvider.class);
        when(cryptoProvider.decrypt(any(byte[].class), any(byte[].class))).thenReturn("{}".getBytes());
        when(cryptoProvider.encrypt(any(byte[].class), any(byte[].class))).thenReturn("encrypted".getBytes());

        domainService = new SpeakerDomainService(domains, cryptoProvider);
    }

    @Test
    void testRoutePostMessageWithNonExistentFromDomain() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            domainService.routePostMessage("nonexistent", "domain2", new byte[]{})
        );
        assertEquals("Le domaine source n'existe pas: nonexistent", exception.getMessage());
    }

    @Test
    void testRoutePostMessageWithNonExistentToDomain() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            domainService.routePostMessage("domain1", "nonexistent", new byte[]{})
        );
        assertEquals("Le domaine destinataire n'existe pas: nonexistent", exception.getMessage());
    }

    @Test
    void testRoutePostMessageWithInactiveFromDomain() {
        Domain domain1 = domainService.getDomain("domain1").orElseThrow();
        domain1.setActive(false);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
            domainService.routePostMessage("domain1", "domain2", new byte[]{})
        );
        assertEquals("Le domaine source n'est pas actif: domain1", exception.getMessage());
    }

    @Test
    void testValidateDecryptedMessage_ValidMessage() {
        String validJson = "{\"type\":\"example\",\"payload\":\"data\"}";
        byte[] decryptedMessage = validJson.getBytes();

        assertDoesNotThrow(() ->
            domainService.validateDecryptedMessage(decryptedMessage)
        );
    }
}
