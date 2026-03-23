package org.helmo;

import org.helmo.domain.Domain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SpeakerDomainServiceTest {

    private SpeakerDomainService domainService;
    private Map<String, Domain> domains;
    private Domain domain1;
    private Domain domain2;

    @BeforeEach
    void setUp() {
        domain1 = new Domain("domain1", new byte[]{1,2,3});
        domain2 = new Domain("domain2", new byte[]{4,5,6});
        domains = new HashMap<>();
        domains.put("domain1", domain1);
        domains.put("domain2", domain2);
        domainService = new SpeakerDomainService(domains);
    }

    @Test
    void testRoutePostMessageWithNonExistentFromDomain() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            domainService.routePostMessage("nonexistent", "domain2", new byte[]{});
        });
        assertEquals("Le domaine source n'existe pas: nonexistent", exception.getMessage());
    }

    @Test
    void testRoutePostMessageWithNonExistentToDomain() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            domainService.routePostMessage("domain1", "nonexistent", new byte[]{});
        });
        assertEquals("Le domaine destinataire n'existe pas: nonexistent", exception.getMessage());
    }

    @Test
    void testRoutePostMessageWithInactiveFromDomain() {
        domain1.setActive(false);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            domainService.routePostMessage("domain1", "domain2", new byte[]{});
        });
        assertEquals("Le domaine source n'est pas actif: domain1", exception.getMessage());
    }

    @Test
    void testRoutePostMessageWithInactiveToDomain() {
        domain2.setActive(false);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            domainService.routePostMessage("domain1", "domain2", new byte[]{});
        });
        assertEquals("Le domaine destinataire n'est pas actif: domain2", exception.getMessage());
    }
}
