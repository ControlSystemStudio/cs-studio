
/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 */

package org.csstudio.archive.sdds.server.io;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.sdds.server.command.CommandExecutor;
import org.csstudio.archive.sdds.server.command.CommandNotImplementedException;
import org.csstudio.archive.sdds.server.command.ServerCommandException;
import org.csstudio.archive.sdds.server.util.RawData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.desy.aapi.AAPI;
import de.desy.aapi.AapiServerError;

/**
 * This class handles a request to the server.
 * It closes the socket.
 *
 * @author Markus Moeller
 */
public class ClientRequest implements Runnable {

    /** The logger of this class */
    private static final Logger LOG = LoggerFactory.getLogger(ClientRequest.class);

    /** The socket of this request. */
    private final Socket socket;

    /** The class that holds and executes the server commands. */
    private final CommandExecutor commandExecutor;

    /**
     *
     * @param socket
     */
    public ClientRequest(@Nonnull final Socket socket,
                         @Nonnull final CommandExecutor commandExecutor) {
        this.socket = socket;
        this.commandExecutor = commandExecutor;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        LOG.info("Handle request from socket " + socket.toString());

        InputStream in = null;
        try {
            in = socket.getInputStream();

            while (!socket.isClosed()) {
                final CommandHeader header = readHeader(in);
                final RawData requestData = readData(in);

                if (header != null) {
                    LOG.info(header.toString());
                    RawData cmdResult;
                    try {
                        cmdResult =
                            commandExecutor.executeCommand(header.getCommandTag(),
                                                           requestData);

                    } catch (final ServerCommandException sce) {
                        LOG.error("[*** ServerCommandException ***]: " + sce.getMessage());
                        header.setError(sce.getErrorNumber());
                        cmdResult = new RawData(sce.getMessage().getBytes(),
                                                sce.getErrorNumber());
                    } catch(final CommandNotImplementedException cnie) {
                        LOG.error("[*** CommandNotImplementedException ***]: " + cnie.getMessage());
                        header.setError(AapiServerError.BAD_CMD.getErrorNumber());
                        cmdResult = new RawData(cnie.getMessage().getBytes(),
                                                AapiServerError.BAD_CMD.getErrorNumber());
                    }

                    writeAnswer(socket.getOutputStream(), header, cmdResult);
                }
            }
        } catch (final IOException ioe) {
            if (ioe instanceof EOFException) {
                LOG.info("End of data stream reached.");
            } else {
                LOG.error(ioe.getMessage());
            }
        } finally {
            in = null;
            if (socket != null) {
                try {
                    socket.close();
                } catch (final Exception e) {/* Can be ignored */}
            }
        }
        LOG.info("Request finished.");
    }

    @Nonnull
    private CommandHeader readHeader(@Nonnull final InputStream stream) throws IOException {

        final DataInputStream dis = new DataInputStream(stream);

        final CommandHeader result = new CommandHeader();
        result.setPacketSize(dis.readInt());
        result.setCommandTag(dis.readInt());
        result.setError(dis.readInt());
        result.setAapiVersion(dis.readInt());
        return result;
    }

    @CheckForNull
    private RawData readData(@Nonnull final InputStream stream) {
        final int dataLength = getDataLengthFromStream(stream);

        if(dataLength > 0) {
            final DataInputStream dis = new DataInputStream(stream);
            final byte[] data = new byte[dataLength];
            try {
                dis.read(data);
                return new RawData(data);
            } catch(final IOException ioe) {
                // Ignore
            }
        }
        return null;
    }

    private int getDataLengthFromStream(@Nonnull final InputStream stream) {
        int dataLength;
        try {
            dataLength = stream.available();
        } catch(final IOException ioe) {
            dataLength = 0;
        }
        return dataLength;
    }

    private void writeAnswer(@Nonnull final OutputStream out,
                             @Nonnull final CommandHeader header,
                             @Nonnull final RawData resultData) throws IOException {

        final int length = resultData.getData().length;
        final ByteArrayOutputStream outData =
            new ByteArrayOutputStream(AAPI.HEADER_LENGTH + length);
        final DataOutputStream dos = new DataOutputStream(outData);

        // Write header
        dos.writeInt(AAPI.HEADER_LENGTH + length);
        dos.writeInt(header.getCommandTag());
        dos.writeInt(resultData.getErrorValue());
        dos.writeInt(AAPI.AAPI_VERSION);

        // Write data
        dos.write(resultData.getData());

        // Write to socket output stream
        out.write(outData.toByteArray());

        try {
            dos.close();
        } catch (final Exception e) {/* Can be ignored */}
    }
}
