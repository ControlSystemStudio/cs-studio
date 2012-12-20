
/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: CommandLine.java,v 1.1 2008/04/07 15:29:47 mmoeller Exp $
 *
 * Date         Author  Changes
 * 1/6/2000     jima    Created
 * 1/9/2000     jima    changed package name from com.comware to org.exolab
 * 7/1/2000     jima    removed the CW prefix from the class name
 * 1/8/2001     jima    Removed it from jtf library and imported it into
 *                      the openjms library
 */

package org.csstudio.ams.systemmonitor.util;

import java.util.Hashtable;
import java.util.Vector;

/**
 * This core class is responsible for processing the command line and
 * storing away the list of options and parameters specified. The
 * difference between an option and a command line is that an option
 * is a boolean value (true if it is specified and false otherwise)
 * and a parameter always has an associated value.
 *
 * @version     $version$
 * @author      jima
 **/
public class CommandLine {

    /**
     * A list of option of switches on the command line. A switch
     * is either set or not
     */
    private final Vector<String> _switches = new Vector<String>();

    /**
     * A dictionary of all the options and their associated values
     */
    private final Hashtable<String, String> _options = new Hashtable<String, String>();

    /**
     * Construct an instance of this class with the specified string
     * array.
     *
     * @param       args        command line argument
     */
    public CommandLine(final String[] args) {
        processCommandLine(args);
    }

    /**
     * Default constructor which simply initialised the class
     */
    public CommandLine() {
        // Nothing to do here
    }

    /**
     * Check if the following option or command has been specified
     *
     * @param       name        name of option or command
     * @return      boolean     true if it has been specified
     */
    public final boolean exists(final String name) {
        return _switches.contains(name) || _options.containsKey(name);
    }

    /**
     * Check if the following option has been specified.
     *
     * @param       name        name of the option
     * @return      boolean     true if it has been specified
     */
    public final boolean isSwitch(final String name) {
        return _switches.contains(name);
    }

    /**
     * Check if the following parameter has been specified.
     *
     * @param       name        name of the parameter
     * @return      boolean     true if it has been specified
     */
    public boolean isParameter(final String name) {
        return _options.containsKey(name);
    }

    /**
     * Return the value of the parameter or option. If the string nominates
     * an option then return null
     *
     * @param       name        name of option or parameter
     * @return      String      value of parameter or null
     */
    public String value(final String name) {
        String result = null;

        if (_options.containsKey(name)) {
            result = _options.get(name);
        }

        return result;
    }

    /**
     * Return the value of the parameter or option, returning a default
     * value if none is specified
     *
     * @param       name        name of option or parameter
     * @param       defaultValue the default value
     * @return      String      value of parameter
     */
    public final String value(final String name, final String defaultValue) {
        final String result = value(name);
        return result != null ? result : defaultValue;
    }

    /**
     * Add the following option or parameter to the list. An option will
     * have a null value, whereas a parameter will have a non-null value.
     * <p>
     * This will automatically overwrite the previous value, if one has been
     * specified.
     *
     * @param       name        name of option or parameter
     * @param       value       value of name
     * @return      boolean     true if it was successfully added
     */
    public boolean add(final String name, final String value) {
        return add(name, value, true);
    }

    /**
     * Add the following option or parameter to the list. An option will
     * have a null value, whereas a parameter will have a non-null value.
     * <p>
     * If the overwrite flag is true then this value will overwrite the
     * previous value. If the overwrite flag is false and the name already
     * exists then it will not overwrite it and the function will return
     * false. In all other circumstances it will return true.
     *
     * @param       name        name of option or parameter
     * @param       value       value of name
     * @param       overwrite   true to overwrite previous value
     * @return      boolean     true if it was successfully added
     */
    public final boolean add(final String name, final String value, final boolean overwrite) {
        boolean result = false;

        if (value == null) {
            // it is an option
            if (_switches.contains(name) &&
                overwrite) {
                _switches.addElement(name);
                result = true;
            } else if (!_switches.contains(name)) {
                _switches.addElement(name);
                result = true;
            }
        } else {
            // parameter
            if (_options.containsKey(name) &&
                overwrite) {
                _options.put(name, value);
                result = true;
            } else if (!_options.containsKey(name)) {
                _options.put(name, value);
                result = true;
            }
        }

        return result;
    }

    /**
     * This method processes the command line and extracts the list of
     * options and command lines. It doesn't intepret the meaning of the
     * entities, which is left to the application.
     *
     * @param       args        command line as a collection of tokens
     */
    private void processCommandLine(final String[] args) {
        boolean prevWasHyphen = false;
        String prevKey = null;

        for (int index = 0; index < args.length; index++) {
            if (args[index].startsWith("-")) {
                // if the previous string started with a hyphen then
                // it was an option store store it, without the hyphen
                // in the _switches vector. Otherwise if the previous was
                // not a hyphen then store key and value in the _options
                // hashtable
                if (prevWasHyphen) {
                    add(prevKey, null);
                }

                prevKey = args[index].substring(1);
                prevWasHyphen = true;

                // check to see whether it is the last element in the
                // arg list. If it is then assume it is an option and
                // break the processing
                if (index == args.length - 1) {
                    add(prevKey, null);
                    break;
                }
            } else {
                // it does not start with a hyphen. If the prev_key is
                // not null then set the value to the prev_value.
                if (prevKey != null) {
                    add(prevKey, args[index]);
                    prevKey = null;
                }
                prevWasHyphen = false;
            }
        }
    }

}
