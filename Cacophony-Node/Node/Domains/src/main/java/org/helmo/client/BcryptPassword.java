package org.helmo.client;

import java.util.Arrays;

public class BcryptPassword {
    private String bcrypt;
    private int rotation;
    private String salt;
    private int saltLength;
    private String psw;


    public BcryptPassword(String rawPassword, int saltlength) {
        String[] split = rawPassword.split("\\$");

        bcrypt = split[1];
        rotation = Integer.parseInt(split[2]);
        this.saltLength = saltlength;
        this.salt = split[3].substring(0, this.saltLength);
        this.psw = split[3].substring(this.saltLength);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("$").append(bcrypt);
        builder.append("$").append(rotation);
        builder.append("$").append(salt).append(psw);

        return builder.toString();
    }

    public String getHash() {
        return psw;
    }

    public int getRotation() {
        return rotation;
    }

    public String getSalt() {
        return salt;
    }
}
