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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import javax.annotation.Nonnull;

import com.google.common.io.Files;

/**
 * Wraps access via Runtime to the configurable Soft IOC.
 *
 * @author bknerr
 * @since 27.05.2011
 */
public class SoftIoc {

    private final ISoftIocConfigurator _cfg;
    private Process _process;

    /**
     * Constructor.
     * @throws URISyntaxException
     * @throws IOException
     */
    public SoftIoc() throws URISyntaxException, IOException {
        this(new BasicSoftIocConfigurator());
    }

    /**
     * Constructor.
     */
    public SoftIoc(@Nonnull final ISoftIocConfigurator cfg) {
        final String os = System.getProperty("os.name");
        if (!os.startsWith("Windows")) {
            throw new IllegalArgumentException("Soft IOC can only be used on windows systems.");
        }
        _cfg = cfg;
        _process = null;
    }

    public void start() throws IOException {

        final File softIocCmdFile = createCmdFile(_cfg);

        final ProcessBuilder builder = new ProcessBuilder().command(_cfg.getDemoExecutableFilePath(), softIocCmdFile.getName())
                                                     .directory(softIocCmdFile.getParentFile());

        _process = builder.start();
    }

    @Nonnull
    private File createCmdFile(@Nonnull final ISoftIocConfigurator cfg) throws IOException {

        final File tmpCopy = createTmpCmdFileCopy(cfg);
        insertDbFilesAndInitCommands(cfg, tmpCopy);

        return tmpCopy;
    }

    private void insertDbFilesAndInitCommands(@Nonnull final ISoftIocConfigurator cfg,
                                              @Nonnull final File tmpCopy) throws IOException {
        final FileWriter writer = new FileWriter(tmpCopy, true);
        for (final File dbFile : cfg.getDbFileSet()) {
            writer.append("dbLoadRecords(\"").append(dbFile.getAbsolutePath()).append("\")\n");
        }
        writer.append("iocInit\n");
        writer.append("dbpf \"UnitTestIoc:valid\",\"Enabled\"   # from demo\n\n\n");
        writer.close();
    }

    public boolean isStartUpDone() {
        boolean done = false;
        final InputStream inputStream = _process.getInputStream();
        try {
            BufferedReader input;
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            input = new BufferedReader(inputStreamReader);
            String line;
            line = input.readLine();
            while (line != null ) {
                done = line.startsWith("DBR_STRING:          \"Enabled\"");
                if (done) {
                    return true;
                }
                // TODO (hrickens): hier muss nach einem Timeout Abgebrochen werden. Sonst kann es sein das man endlos Wartet!
                line = input.readLine();
            }
            if(input.ready()) {
                input.close();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return done;
    }


    @Nonnull
    private File createTmpCmdFileCopy(@Nonnull final ISoftIocConfigurator cfg) throws IOException {
        final File softIocCmdFile = cfg.getSoftIocCmdFile();

        final File tmpCopy = File.createTempFile(softIocCmdFile.getName(), null, softIocCmdFile.getParentFile());
        tmpCopy.deleteOnExit();

        Files.copy(softIocCmdFile, tmpCopy);
        return tmpCopy;
    }

    public void stop() throws IOException {
        if (_process != null) {
            _process.destroy();
        }
    }
}
