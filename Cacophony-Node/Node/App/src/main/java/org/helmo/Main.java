package org.helmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();


    public static void main(String[] args) {

        var user = new User("name", new HashPassword("tegfd"), null);

        LOGGER.info(user.getName());
    }
}