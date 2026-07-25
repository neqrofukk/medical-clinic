package com.neqrofukk.medicalclinic.validators;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class EmailValidator implements Validator {
    private static final Pattern LOCAL_CHARS = Pattern.compile("[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+");
    private static final Pattern DOMAIN_CHARS = Pattern.compile("[a-zA-Z0-9.-]+");
    private static final int MAX_LOCAL_LENGTH = 64;
    private static final int MAX_DOMAIN_LENGTH = 255;

    @Override
    public boolean validate(@NonNull String email) {
        String normalized = normalize(email);
        if (normalized.isBlank()) return false;
        if (!hasNoWhitespace(normalized)) {
            return false;
        }
        if (!hasExactlyOneAt(normalized)) {
            return false;
        }
        if (!hasValidTotalLength(normalized)) {
            return false;
        }
        int atIndex = normalized.indexOf('@');
        String local = normalized.substring(0, atIndex);
        String domain = normalized.substring(atIndex + 1);

        return isValidLocal(local) && isValidDomain(domain);
    }

    private boolean hasNoWhitespace(String email) {
        for (int i = 0; i < email.length(); i++) {
            if(Character.isWhitespace(email.charAt(i))){
                return false;
            }
        }
        return true;
    }

    private boolean hasExactlyOneAt(String email) {
        int counter = 0;
        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@') {
                counter++;
            }
        }
        return counter == 1;
    }

    private boolean hasValidTotalLength(String email) {
        return email.length() < 255;
    }

    private boolean isValidLocal(String local) {
        if (local.isEmpty()) {
            return false;
        }
        if (local.length() > MAX_LOCAL_LENGTH) {
            return false;
        }
        if (local.startsWith(".") || local.endsWith(".")) {
            return false;
        }
        if (local.contains("..")) {
            return false;
        }
        return LOCAL_CHARS.matcher(local).matches();
    }

    private boolean isValidDomain(String domain) {
        if (domain.isEmpty()) {
            return false;
        }
        if (domain.length() > MAX_DOMAIN_LENGTH) {
            return false;
        }
        if (!domain.contains(".")) {
            return false;
        }
        if (domain.startsWith(".") || domain.endsWith(".")) {
            return false;
        }
        if (domain.contains("..")) {
            return false;
        }
        if (domain.startsWith("-") || domain.endsWith("-")) {
            return false;
        }
        return DOMAIN_CHARS.matcher(domain).matches();
    }

    public static String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
