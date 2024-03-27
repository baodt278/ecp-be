package org.ecp.backend.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class GenerateUtils {
    public static String generateContract(String acronym) {
        return acronym + RandomStringUtils.random(6, true, true).toLowerCase();
    }

    public static String generatedCode() {
        return RandomStringUtils.random(10, true, true);
    }

    public static String generateAcronym(String acronym) {
        return acronym.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }
}
