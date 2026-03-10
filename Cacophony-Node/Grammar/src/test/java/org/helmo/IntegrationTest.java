package org.helmo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

    @Test
    void testWelcome() {
        String msg = MessageBuilder.buildWelcome("domain.com", "abcdefghijklmnopqrstuv");
        assertTrue(Message.P_CMD_WELCOME.matcher(msg).matches());

        // Test invalid inputs
        String invalidMsg = MessageBuilder.buildWelcome(null, null);
        assertFalse(Message.P_CMD_WELCOME.matcher(invalidMsg).matches(), "Message with null inputs should be invalid");

        String invalidRandomString = MessageBuilder.buildWelcome("domain.com", "short");
        assertFalse(Message.P_CMD_WELCOME.matcher(invalidRandomString).matches(), "Message with short random string should be invalid");
    }

    @Test
    void testUser() {
        String msg = MessageBuilder.buildUser("username");
        assertTrue(Message.P_CMD_USER.matcher(msg).matches());

        // Test invalid inputs
        String shortUser = MessageBuilder.buildUser("user");
        assertFalse(Message.P_CMD_USER.matcher(shortUser).matches(), "Message with short username should be invalid");

        String longUser = MessageBuilder.buildUser("aVeryLongUsernameThatExceedsTheLimit");
        assertFalse(Message.P_CMD_USER.matcher(longUser).matches(), "Message with long username should be invalid");

        String invalidCharUser = MessageBuilder.buildUser("user-name");
        assertFalse(Message.P_CMD_USER.matcher(invalidCharUser).matches(), "Message with invalid characters in username should be invalid");
    }

    @Test
    void testChallenge() {
        String msg = MessageBuilder.buildChallenge("10", "abcdefghijklmnopqrstuv");
        assertTrue(Message.P_CMD_CHALLENGE.matcher(msg).matches());

        // Test invalid inputs
        String invalidCounter = MessageBuilder.buildChallenge("1", "abcdefghijklmnopqrstuv");
        assertFalse(Message.P_CMD_CHALLENGE.matcher(invalidCounter).matches(), "Message with invalid counter should be invalid");

        String invalidRandomString = MessageBuilder.buildChallenge("10", "short");
        assertFalse(Message.P_CMD_CHALLENGE.matcher(invalidRandomString).matches(), "Message with short random string should be invalid");
    }

    @Test
    void testResponse() {
        StringBuilder sha3 = new StringBuilder();
        for(int i=0; i<30; i++) sha3.append("a");
        String msg = MessageBuilder.buildResponse(sha3.toString());
        assertTrue(Message.P_CMD_RESPONSE.matcher(msg).matches());

        // Test invalid inputs
        String shortSha3 = MessageBuilder.buildResponse("short");
        assertFalse(Message.P_CMD_RESPONSE.matcher(shortSha3).matches(), "Message with short sha3 hexstring should be invalid");
    }

    @Test
    void testOnboarding() {
        String msg = MessageBuilder.buildOnboarding("username", "10", "$2b$10$abcdefghijklmnopqrstuvwxyz1234567890");
        assertTrue(Message.P_CMD_ONBOARDING.matcher(msg).matches());

        // Test invalid inputs
        String invalidBcrypt = MessageBuilder.buildOnboarding("username", "10", "invalidBcrypt");
        assertFalse(Message.P_CMD_ONBOARDING.matcher(invalidBcrypt).matches(), "Message with invalid bcrypt should be invalid");
    }

    @Test
    void testSubscribe() {
        String msgUser = MessageBuilder.buildSubscribe("user1@domain.com");
        assertTrue(Message.P_CMD_SUBSCRIBE.matcher(msgUser).matches());

        String msgTag = MessageBuilder.buildSubscribe("#tag12@domain.com");
        assertTrue(Message.P_CMD_SUBSCRIBE.matcher(msgTag).matches());

        // Test invalid inputs
        String invalidTarget = MessageBuilder.buildSubscribe("invalidTarget");
        assertFalse(Message.P_CMD_SUBSCRIBE.matcher(invalidTarget).matches(), "Message with invalid target should be invalid");
    }

    @Test
    void testSend() {
        String msg = MessageBuilder.buildSend("Hello World");
        assertTrue(Message.P_CMD_SEND.matcher(msg).matches());

        // Test invalid message content
        String invalidMsg = MessageBuilder.buildSend("Hello\nWorld");
        assertFalse(Message.P_CMD_SEND.matcher(invalidMsg).matches(), "Message with newline should be invalid");

        String longMsg = MessageBuilder.buildSend("a".repeat(201));
        assertFalse(Message.P_CMD_SEND.matcher(longMsg).matches(), "Message longer than 200 characters should be invalid");
    }

    @Test
    void testMsg() {
        String msg = MessageBuilder.buildMsg("user1@domain.com", "Hello World");
        assertTrue(Message.P_CMD_MSG.matcher(msg).matches());

        // Test invalid inputs
        String invalidTarget = MessageBuilder.buildMsg("invalidTarget", "Hello World");
        assertFalse(Message.P_CMD_MSG.matcher(invalidTarget).matches(), "Message with invalid target should be invalid");

        String longMsg = MessageBuilder.buildMsg("user1@domain.com", "a".repeat(501));
        assertFalse(Message.P_CMD_MSG.matcher(longMsg).matches(), "Message longer than 500 characters should be invalid");
    }

    @Test
    void testOk() {
        String msg1 = MessageBuilder.buildOk();
        assertTrue(Message.P_CMD_OK.matcher(msg1).matches());

        String msg2 = MessageBuilder.buildOk("MessageContent");
        assertTrue(Message.P_CMD_OK.matcher(msg2).matches());
    }

    @Test
    void testError() {
        String msg1 = MessageBuilder.buildError();
        assertTrue(Message.P_CMD_ERROR.matcher(msg1).matches());

        String msg2 = MessageBuilder.buildError("ErrorMessage");
        assertTrue(Message.P_CMD_ERROR.matcher(msg2).matches());
    }

    @Test
    void testLeave() {
        String msg = MessageBuilder.buildLeave();
        assertTrue(Message.P_CMD_LEAVE.matcher(msg).matches());
    }
}
