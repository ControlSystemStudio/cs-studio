/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.common.softioc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.eclipse.core.runtime.FileLocator;

/**
 * Give a EPICS caget on Windows systems!
 *
 * @author hrickens
 * @since 25.11.2011
 */
public class Caget {

    static final double DEFAULT_WAIT_TIME = 1.000000d;
    private double _waitTime = DEFAULT_WAIT_TIME;
    private boolean _terseMode;
    private DBR _dbr = DBR.DBR_AUTO;

    /**
     * Constructor<br>.
     * Can only use on WIN!!!
     */
    public Caget() {
        final String os = System.getProperty("os.name").toLowerCase();
        if (!(os.indexOf("win") >= 0 || os.indexOf("Win") >= 0)) {
            throw new IllegalStateException("Wrong OS! Run only on Windos");
        }
    }

    /**
     * Wait time in sec.
     */
    public void setWaitTime(final double waitTimeInSec) {
        _waitTime = waitTimeInSec;
    }

    @Nonnull
    private String getWaitTimeOptionString() {
        if (_waitTime == DEFAULT_WAIT_TIME) {
            return "";
        }
        return String.format("-w %f", _waitTime);
    }

    /**
     * Wait time in sec.
     */
    public void setTerseMode(final boolean terseMode) {
        _terseMode = terseMode;
    }

    @Nonnull
    private String getTerseModeOptionString() {
        if (_terseMode) {
            return "-t";
        }
        return "";
    }

    @Nonnull
    private String getDBROptionString() {
        return _dbr.getAsOptionString();
    }

    public void setDbr(@Nonnull final DBR dbr) {
        _dbr = dbr;
    }




    @Nonnull
    public ArrayList<String> caget(@Nonnull final String recodName) throws IOException, URISyntaxException {
        final ArrayList<String> lines = new ArrayList<String>();
        final URL camExeUrl = FileLocator.toFileURL(Caget.class.getClassLoader()
                .getResource("win/caget.exe"));

        Process cam = null;
        final String[] commands = buildCommand(new File(camExeUrl.toURI()).toString(), recodName);
        try {
            cam = new ProcessBuilder(commands).start();
            final BufferedReader input = new BufferedReader(new InputStreamReader(cam.getInputStream()));
            String line = input.readLine();
            while(line != null) {
                lines.add(line);
                line = input.readLine();
            }
            input.close();
        } finally {
            if (cam != null) {
                cam.destroy();
            }
        }
        return lines;
    }

    /**
     * @param string
     * @param recodName
     * @return
     */
    @Nonnull
    private String[] buildCommand(@Nonnull final String exe, @Nonnull final String recodName) {
        final ArrayList<String> commands = new ArrayList<String>();
        commands.add(exe);
        addNotEmpty(commands, getWaitTimeOptionString());
        addNotEmpty(commands, getTerseModeOptionString());
        addNotEmpty(commands, getDBROptionString());

        commands.add(recodName);
        return commands.toArray(new String[commands.size()]);
    }

    /**
     * @param commands
     * @param command
     */
    private void addNotEmpty(@Nonnull final ArrayList<String> commands,
                             @CheckForNull final String command) {
        if (command != null && !command.isEmpty()) {
            commands.add(command);
        }
    }

}
