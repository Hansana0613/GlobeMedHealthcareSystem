package com.globmed.service;

import com.globmed.patterns.decorator.BaseService;
import com.globmed.patterns.decorator.EncryptionDecorator;
import com.globmed.patterns.decorator.LoggingDecorator;
import com.globmed.patterns.decorator.Service;

/**
 *
 * @author Hansana
 */
public class SecurityService {

    private Service service = new LoggingDecorator(new EncryptionDecorator(new BaseService()));

    public String secureExecute(String input) {
        return service.execute(input);
    }
}
