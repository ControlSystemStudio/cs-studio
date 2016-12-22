/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Utility methods to handle exceptions.
 *
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 21 Dec 2016
 */
public class ExceptionUtilities {

    /**
     * Return a reduced version of the stack frame string, containing the given
     * {@code pattern} string.
     *
     * @param ex The exception.
     * @param pattern The string to be matched.
     * @return The reduced version of the stack frame string, containing the
     *         given {@code pattern} string
     */
    public static String reducedStackTrace ( Throwable ex, String pattern ) {

        StringBuilder builder = new StringBuilder();
        String[] stackFrames = ExceptionUtils.getStackFrames(ex);

        builder.append(stackFrames[0]);

        boolean patternFound = false;

        for ( int i = 1; i < stackFrames.length; i++ ) {

            String frame = stackFrames[i];

            builder.append("\n").append(frame);

            boolean found = frame.contains(pattern);

            if ( !patternFound || found ) {
                if ( found )  {
                    patternFound = true;
                }
            } else if ( patternFound && !found ) {
                builder.append("\n\t...");
                break;
            }

        }

        return builder.toString();

    }

    private ExceptionUtilities ( ) {
    }

}
