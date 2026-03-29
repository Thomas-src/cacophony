package org.helmo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageBuilderTest {

    @Test
    void testBuildWelcome() {
        String expected = "WELCOME domain.com randomString\r\n";
        assertEquals(expected, MessageBuilder.buildWelcome("domain.com", "randomString"));
    }

    @Test
    void testBuildUser() {
        String expected = "USER username\r\n";
        assertEquals(expected, MessageBuilder.buildUser("username"));
    }

    @Test
    void testBuildChallenge() {
        String expected = "CHALLENGE 10 randomString\r\n";
        assertEquals(expected, MessageBuilder.buildChallenge("10", "randomString"));
    }

    @Test
    void testBuildResponse() {
        String expected = "RESPONSE hexstring\r\n";
        assertEquals(expected, MessageBuilder.buildResponse("hexstring"));
    }

    @Test
    void testBuildOnboarding() {
        String expected = "ONBOARDING user 10 $2b$10$salt\r\n";
        assertEquals(expected, MessageBuilder.buildOnboarding("user", "10", "$2b$10$salt"));
    }

    @Test
    void testBuildSubscribe() {
        String expected = "SUBSCRIBE user@domain.com\r\n";
        assertEquals(expected, MessageBuilder.buildSubscribe("user@domain.com"));
    }

    @Test
    void testBuildSend() {
        String expected = "SEND Hello World\r\n";
        assertEquals(expected, MessageBuilder.buildSend("Hello World"));
    }

    @Test
    void testBuildMsg() {
        String expected = "MSG user@domain.com Hello World\r\n";
        assertEquals(expected, MessageBuilder.buildMsg("user@domain.com", "Hello World"));
    }

    @Test
    void testBuildOk() {
        String expected1 = "OK\r\n";
        assertEquals(expected1, MessageBuilder.buildOk());

        String expected2 = "OK Message\r\n";
        assertEquals(expected2, MessageBuilder.buildOk("Message"));
    }

    @Test
    void testBuildError() {
        String expected1 = "ERROR\r\n";
        assertEquals(expected1, MessageBuilder.buildError());

        String expected2 = "ERROR Message\r\n";
        assertEquals(expected2, MessageBuilder.buildError("Message"));
    }

    @Test
    void testBuildLeave() {
        String expected = "LEAVE\r\n";
        assertEquals(expected, MessageBuilder.buildLeave());
    }

    @Test
    void testBuildPostValidUserToUser() {
        String post = MessageBuilder.buildPost(
                "1234@node1.swilabus.xyz",
                "alice@node1.swilabus.xyz",
                "bob@node4.swilabus.xyz",
                "SUBSCRIBE bob@node4.swilabus.xyz"
        );

        assertEquals(
                "POST 1234@node1.swilabus.xyz alice@node1.swilabus.xyz bob@node4.swilabus.xyz SUBSCRIBE bob@node4.swilabus.xyz\r\n",
                post
        );
    }
}
