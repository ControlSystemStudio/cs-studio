
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.alarm.jms2ora.management;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IManagementCommand;

/**
 * @author mmoeller
 * @since 01.02.2012
 */
public class GetVersionMgmtCommand implements IManagementCommand {
    
    /** The path to the file that contains the product version */
    private static String VERSION_FILE;

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(final CommandParameters parameters) {

        CommandResult result = null;

        if (VERSION_FILE != null) {

            final Properties prop = new Properties();

            try {
                prop.load(new FileInputStream(VERSION_FILE));
                if (prop.containsKey("version")) {
                    result = CommandResult.createMessageResult("Jms2Oracle\n\nVersion: " + prop.getProperty("version"));
                } else {
                    result = CommandResult.createMessageResult("Cannot find version in file.");
                }
            } catch (final FileNotFoundException fnfe) {
                result = CommandResult.createMessageResult("Version file not found.\n\n" + fnfe.getMessage());
            } catch (final IOException ioe) {
                result = CommandResult.createMessageResult("Cannot read version file.\n\n" + ioe.getMessage());
            }
        } else {
            result = CommandResult.createMessageResult("Path to version file is not defined.");
        }

        return result;
    }

    public static void injectStaticObject(final String filePath) {
        VERSION_FILE = filePath;
    }
}
