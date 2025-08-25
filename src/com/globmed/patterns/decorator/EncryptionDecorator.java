package com.globmed.patterns.decorator;

import java.util.Base64;

/**
 *
 * @author Hansana
 */
public class EncryptionDecorator extends ServiceDecorator {

    public EncryptionDecorator(Service service) {
        super(service);
    }

    @Override
    public String execute(String input) {
        String encrypted = Base64.getEncoder().encodeToString(input.getBytes());
        return super.execute(encrypted);
    }
}
