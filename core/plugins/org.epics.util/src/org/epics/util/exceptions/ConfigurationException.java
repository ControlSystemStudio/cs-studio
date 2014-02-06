/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.util.exceptions;

/**
 * Signals that the method is failing due to a misconfiguration of the
 * library which should be fixed by the user of such library.
 *
 * @author carcassi
 */
public class ConfigurationException extends RuntimeException {

    public ConfigurationException() {
    }
    
    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
