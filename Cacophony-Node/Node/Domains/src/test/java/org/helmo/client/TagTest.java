package org.helmo.client;

import org.helmo.ISubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    private Tag tag;
    private ISubscriber subscriber1;
    private ISubscriber subscriber2;

    @BeforeEach
    void setUp() {
        subscriber1 = Mockito.mock(ISubscriber.class);
        subscriber2 = Mockito.mock(ISubscriber.class);
        List<ISubscriber> subscribers = new ArrayList<>();
        subscribers.add(subscriber1);
        tag = new Tag("testTag@domain.com", subscribers);
    }

    @Test
    void testGetName() {
        assertEquals("testTag", tag.getName());
    }

    @Test
    void testGetFullName() {
        assertEquals("testTag@domain.com", tag.getFullName());
    }

    @Test
    void testSubscribe() {
        tag.subscribe(subscriber2);
        assertTrue(tag.isSubscribed(subscriber2));
    }

    @Test
    void testIsSubscribed() {
        assertTrue(tag.isSubscribed(subscriber1));
        assertFalse(tag.isSubscribed(subscriber2));
    }

    @Test
    void testGetSubscribers() {
        List<ISubscriber> subscribers = tag.getSubscribers();
        assertEquals(1, subscribers.size());
        assertTrue(subscribers.contains(subscriber1));
    }

    @Test
    void testGetSubscribersIsACopy() {
        List<ISubscriber> subscribers = tag.getSubscribers();
        assertThrows(UnsupportedOperationException.class, () -> subscribers.add(subscriber2));
        assertEquals(1, tag.getSubscribers().size());
    }

    @Test
    void testConstructorWithNullSubscribers() {
        Tag tagWithNullSubscribers = new Tag("test@domain.com", null);
        assertNotNull(tagWithNullSubscribers.getSubscribers());
        assertEquals(0, tagWithNullSubscribers.getSubscribers().size());
    }
}