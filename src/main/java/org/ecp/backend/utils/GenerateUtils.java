package org.ecp.backend.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class GenerateUtils {
    public static String generateUID() {
        return RandomStringUtils.random(8, true, true);
    }

    public static String generateName(String acronym) {
        return acronym + RandomStringUtils.random(6, true, false);
    }

    public static String generatedCode() {
        return RandomStringUtils.random(10, true, true);
    }

    public static String generateAcronym(String acronym) {
        return acronym.replace(" ", "").toUpperCase();
    }
}
