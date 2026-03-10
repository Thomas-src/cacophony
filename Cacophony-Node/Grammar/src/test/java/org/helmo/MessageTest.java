package org.helmo;

import org.junit.jupiter.api.Test;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void testWelcome() {
        Pattern p = Message.P_CMD_WELCOME;
        // WELCOME sp domain sp random_string newline
        String valid = "WELCOME domain.com abcdefghijklmnopqrstuv\r\n";
        assertTrue(p.matcher(valid).matches());

        String invalid = "WELCOME domain.com abcdefghijklmnopqrstuv"; // Missing newline
        assertFalse(p.matcher(invalid).matches());
    }

    @Test
    void testUser() {
        Pattern p = Message.P_CMD_USER;
        // USER sp user newline
        assertTrue(p.matcher("USER username\r\n").matches());
        assertFalse(p.matcher("USER usr\r\n").matches()); // User too short
    }

    @Test
    void testChallenge() {
        Pattern p = Message.P_CMD_CHALLENGE;
        // CHALLENGE sp salt_length sp random_string newline
        assertTrue(p.matcher("CHALLENGE 10 abcdefghijklmnopqrstuv\r\n").matches());
        assertFalse(p.matcher("CHALLENGE 5 abcdefghijklmnopqrstuv\r\n").matches()); // Salt length too small
    }

    @Test
    void testResponse() {
        Pattern p = Message.P_CMD_RESPONSE;
        // RESPONSE sp sha3_hexstring newline
        StringBuilder sb = new StringBuilder("RESPONSE ");
        for(int i=0; i<30; i++) sb.append("a");
        sb.append("\r\n");
        assertTrue(p.matcher(sb.toString()).matches());
    }

    @Test
    void testOnboarding() {
        Pattern p = Message.P_CMD_ONBOARDING;
        // ONBOARDING sp user sp salt_length sp bcrypt_hash newline
        assertTrue(p.matcher("ONBOARDING user1 10 $2b$10$abcdefghijklmnopqrstuvwxyz1234567890\r\n").matches());
    }

    @Test
    void testSubscribe() {
        Pattern p = Message.P_CMD_SUBSCRIBE;
        // SUBSCRIBE sp (user_at_domain / tag_at_domain) newline
        String validUser = "SUBSCRIBE user1@domain.com\r\n";
        String validTag = "SUBSCRIBE #tag12@domain.com\r\n";

        assertTrue(p.matcher(validUser).matches());
        assertTrue(p.matcher(validTag).matches());

        String invalid = "SUBSCRIBE user@domain.com\r\n"; // User too short
        assertFalse(p.matcher(invalid).matches());
    }

    @Test
    void testSend() {
        Pattern p = Message.P_CMD_SEND;
        // SEND sp message newline
        assertTrue(p.matcher("SEND Hello World\r\n").matches());
        assertFalse(p.matcher("SEND \r\n").matches()); // Message empty
    }

    @Test
    void testMsg() {
        Pattern p = Message.P_CMD_MSG;
        // MSG sp (user_at_domain / tag_at_domain) sp message newline
        assertTrue(p.matcher("MSG user1@domain.com Hello World\r\n").matches());
        assertTrue(p.matcher("MSG #tag12@domain.com Hello World\r\n").matches());
    }

    @Test
    void testOk() {
        Pattern p = Message.P_CMD_OK;
        // OK [sp message] newline
        String valid1 = "OK\r\n";
        String valid2 = "OK MessageContent\r\n";
        
        assertTrue(p.matcher(valid1).matches());
        assertTrue(p.matcher(valid2).matches());

        String invalid = "OK "; // Missing newline
        assertFalse(p.matcher(invalid).matches());
    }

    @Test
    void testError() {
        Pattern p = Message.P_CMD_ERROR;
        // ERROR [sp message] newline
        assertTrue(p.matcher("ERROR\r\n").matches());
        assertTrue(p.matcher("ERROR Something went wrong\r\n").matches());
    }

    @Test
    void testLeave() {
        Pattern p = Message.P_CMD_LEAVE;
        // LEAVE newline
        assertTrue(p.matcher("LEAVE\r\n").matches());
        assertFalse(p.matcher("LEAVE \r\n").matches()); // Space not allowed
    }
}
