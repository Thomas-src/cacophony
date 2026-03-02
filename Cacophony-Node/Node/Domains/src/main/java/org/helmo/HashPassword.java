package org.helmo;

public class HashPassword {
    private final String hashpassword;
    public HashPassword(String hashPassword) {
        this.hashpassword = hashPassword;
    }

    public boolean checkPassword(String hashPassword) {
        return this.hashpassword.equals(hashPassword); // todo remplacer par le challenge
    }
}
