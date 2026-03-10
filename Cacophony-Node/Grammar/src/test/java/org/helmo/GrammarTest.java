package org.helmo;

import org.junit.jupiter.api.Test;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

class GrammarTest {

    @Test
    void testUserAtDomain() {
        Pattern p = Grammar.P_USER_AT_DOMAIN;
        assertTrue(p.matcher("user1@domain.com").matches());
        assertTrue(p.matcher("12345@example.org").matches());
        assertTrue(p.matcher("Alice@my.domain.net").matches());

        assertFalse(p.matcher("user@").matches());
        assertFalse(p.matcher("@domain.com").matches());
        assertFalse(p.matcher("usr@domain.com").matches());
        assertFalse(p.matcher("user1@dom").matches());
        assertFalse(p.matcher("user with space@domain.com").matches());
    }

    @Test
    void testTagAtDomain() {
        Pattern p = Grammar.P_TAG_AT_DOMAIN;
        assertTrue(p.matcher("#tag12@domain.com").matches());
        assertTrue(p.matcher("#MyTag@example.org").matches());

        assertFalse(p.matcher("tag12@domain.com").matches());
        assertFalse(p.matcher("#tag@domain.com").matches());
        assertFalse(p.matcher("#tag12@dom").matches());
    }

    @Test
    void testBcrypt() {
        Pattern p = Grammar.P_BCRYPT;
        // $2b$ + 2 digits + $ + 1-70 chars
        assertTrue(p.matcher("$2b$10$abcdefghijklmnopqrstuvwxyz1234567890").matches());
        assertTrue(p.matcher("$2b$05$ShortSalt").matches());
        assertTrue(p.matcher("$2b$12$ValidSaltWithSpecialChars!@#").matches());

        assertFalse(p.matcher("$2a$10$abcdefghijklmnopqrstuvwxyz").matches());
        assertFalse(p.matcher("$2b$1$abcdefghijklmnopqrstuvwxyz").matches());
        assertFalse(p.matcher("$2b$100$abcdefghijklmnopqrstuvwxyz").matches());
        assertFalse(p.matcher("$2b$10$").matches());
    }

    @Test
    void testMessage() {
        Pattern p = Grammar.P_MESSAGE;
        assertTrue(p.matcher("Hello World").matches());
        assertTrue(p.matcher("A").matches());
        
        StringBuilder longMsg = new StringBuilder();
        for(int i=0; i<200; i++) longMsg.append("a");
        assertTrue(p.matcher(longMsg.toString()).matches());

        assertFalse(p.matcher("").matches());
        
        StringBuilder tooLongMsg = new StringBuilder();
        for(int i=0; i<201; i++) tooLongMsg.append("a");
        assertFalse(p.matcher(tooLongMsg.toString()).matches());
    }

    @Test
    void testInnerMessage() {
        Pattern p = Pattern.compile("^" + Grammar.INNER_MESSAGE + "$");
        assertTrue(p.matcher("Hello World").matches());
        
        StringBuilder longMsg = new StringBuilder();
        longMsg.append("a".repeat(500));
        assertTrue(p.matcher(longMsg.toString()).matches());

        assertFalse(p.matcher("").matches());
        
        StringBuilder tooLongMsg = new StringBuilder();
        for(int i=0; i<501; i++) tooLongMsg.append("a");
        assertFalse(p.matcher(tooLongMsg.toString()).matches());
    }

    @Test
    void testPort() {
        Pattern p = Pattern.compile("^" + Grammar.PORT + "$");
        assertTrue(p.matcher("80").matches());
        assertTrue(p.matcher("8080").matches());
        assertTrue(p.matcher("12345").matches());
        assertTrue(p.matcher("0").matches());

        assertFalse(p.matcher("").matches());
        assertFalse(p.matcher("123456").matches());
        assertFalse(p.matcher("port").matches());
    }

    @Test
    void testCounter() {
        Pattern p = Pattern.compile("^" + Grammar.COUNTER + "$");
        assertTrue(p.matcher("00").matches());
        assertTrue(p.matcher("10").matches());
        assertTrue(p.matcher("99").matches());

        assertFalse(p.matcher("1").matches());
        assertFalse(p.matcher("100").matches());
        assertFalse(p.matcher("aa").matches());
    }

    @Test
    void testSaltLength() {
        Pattern p = Pattern.compile("^" + Grammar.SALT_LENGTH + "$");
        assertTrue(p.matcher("10").matches());
        assertTrue(p.matcher("32").matches());

        assertFalse(p.matcher("5").matches());
        assertFalse(p.matcher("100").matches());
    }

    @Test
    void testUser() {
        Pattern p = Pattern.compile("^" + Grammar.USER + "$");
        assertTrue(p.matcher("user1").matches());
        assertTrue(p.matcher("Alice").matches());
        assertTrue(p.matcher("12345").matches());
        
        StringBuilder longUser = new StringBuilder();
        for(int i=0; i<20; i++) longUser.append("a");
        assertTrue(p.matcher(longUser.toString()).matches());

        assertFalse(p.matcher("user").matches()); // Too short (<5)
        
        StringBuilder tooLongUser = new StringBuilder();
        for(int i=0; i<21; i++) tooLongUser.append("a");
        assertFalse(p.matcher(tooLongUser.toString()).matches());
        
        assertFalse(p.matcher("user_name").matches()); // Underscore not allowed
    }

    @Test
    void testTag() {
        Pattern p = Pattern.compile("^" + Grammar.TAG + "$");
        assertTrue(p.matcher("#tag12").matches());
        assertTrue(p.matcher("#MyTag").matches());

        assertFalse(p.matcher("tag12").matches()); // Missing #
        assertFalse(p.matcher("#tag").matches()); // Too short
    }

    @Test
    void testDomain() {
        Pattern p = Pattern.compile("^" + Grammar.DOMAIN + "$");
        assertTrue(p.matcher("domain.com").matches());
        assertTrue(p.matcher("my.domain.net").matches());
        assertTrue(p.matcher("localhost").matches());

        assertFalse(p.matcher("dom").matches()); // Too short (<5)
        assertFalse(p.matcher("domain with space").matches());
    }

    @Test
    void testSha3Hexstring() {
        Pattern p = Pattern.compile("^" + Grammar.SHA3_HEXSTRING + "$");
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<30; i++) sb.append("a");
        assertTrue(p.matcher(sb.toString()).matches());
        
        for(int i=0; i<170; i++) sb.append("a");
        assertTrue(p.matcher(sb.toString()).matches()); // 200 chars

        assertFalse(p.matcher("short").matches()); // Too short
    }

    @Test
    void testRandomString() {
        Pattern p = Pattern.compile("^" + Grammar.RANDOM_STRING + "$");
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<22; i++) sb.append("a");
        assertTrue(p.matcher(sb.toString()).matches());

        assertFalse(p.matcher("short").matches());
        assertFalse(p.matcher("toolongstringtoolongstring").matches());
    }
}
