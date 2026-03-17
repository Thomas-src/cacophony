package org.helmo.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ClientTest {
/*
    private Client client;
    private HashPassword hashPassword;
    private ISubscriber subscriber1;
    private ISubscriber subscriber2;

    @BeforeEach
    void setUp() {
        hashPassword = Mockito.mock(HashPassword.class);
        subscriber1 = Mockito.mock(ISubscriber.class);
        subscriber2 = Mockito.mock(ISubscriber.class);
        List<ISubscriber> subscribers = new ArrayList<>();
        subscribers.add(subscriber1);
        client = new Client("testUser@domain.com", hashPassword, subscribers);
    }

    @Test
    void testNewClientFromOnboarding() {
        String onboardingMessage = "ONBOARDING testUser@domain.com 22 $2b$10$N9qo8uLOickgx2ZMRZoMye.aA.w.s.vQv2v.vX.vX.vX.vX.vX";
        Client newClient = Client.newClientFromOnboarding(onboardingMessage);

        assertNotNull(newClient);
        assertEquals("testUser@domain.com", newClient.getFullName());
        assertEquals("testUser", newClient.getName());
        assertNotNull(newClient.getHashPassword());
        assertEquals(10, newClient.getHashPassword().bcryptRound());
        assertEquals("N9qo8uLOickgx2ZMRZoMye", newClient.getHashPassword().bcryptSalt());
        assertEquals(".aA.w.s.vQv2v.vX.vX.vX.vX.vX", newClient.getHashPassword().storedBcryptHash());
        assertTrue(newClient.getSubscribers().isEmpty());
    }

    @Test
    void testNewClientFromOnboardingWithNullMessage() {
        assertNull(Client.newClientFromOnboarding(null));
    }

    @Test
    void testNewClientFromOnboardingWithInvalidMessage() {
        assertNull(Client.newClientFromOnboarding("INVALID MESSAGE"));
    }

    @Test
    void testGetName() {
        assertEquals("testUser", client.getName());
    }

    @Test
    void testGetFullName() {
        assertEquals("testUser@domain.com", client.getFullName());
    }

    @Test
    void testGetHashPassword() {
        assertEquals(hashPassword, client.getHashPassword());
    }

    @Test
    void testVerifyChallengeResponse() {
        String clientResponse = "response";
        String challenge = "challenge";
        when(hashPassword.checkChallenge(clientResponse, challenge)).thenReturn(true);

        assertTrue(client.verifyChallengeResponse(clientResponse, challenge));
    }

    @Test
    void testIsSubscribed() {
        assertTrue(client.isSubscribed(subscriber1));
        assertFalse(client.isSubscribed(subscriber2));
    }

    @Test
    void testSubscribe() {
        client.subscribe(subscriber2);
        assertTrue(client.isSubscribed(subscriber2));
    }

    @Test
    void testSubscribeNull() {
        assertThrows(NullPointerException.class, () -> client.subscribe(null));
    }

    @Test
    void testGetSubscribers() {
        List<ISubscriber> subscribers = client.getSubscribers();
        assertEquals(1, subscribers.size());
        assertTrue(subscribers.contains(subscriber1));
    }

    @Test
    void testGetSubscribersIsImmutable() {
        List<ISubscriber> subscribers = client.getSubscribers();
        assertThrows(UnsupportedOperationException.class, () -> subscribers.add(subscriber2));
    }

    @Test
    void testEquals() {
        Client sameClient = new Client("testUser@domain.com", null, null);
        Client differentClient = new Client("anotherUser@domain.com", null, null);

        assertEquals(client, sameClient);
        assertNotEquals(client, differentClient);
        assertNotEquals(client, null);
        assertNotEquals(client, new Object());
    }

    @Test
    void testHashCode() {
        Client sameClient = new Client("testUser@domain.com", null, null);
        assertEquals(client.hashCode(), sameClient.hashCode());
    }

 */
}