
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

import org.csstudio.archive.sdds.server.command.CommandExecutor;
import org.csstudio.archive.sdds.server.command.CommandNotImplementedException;
import org.csstudio.archive.sdds.server.command.ServerCommandException;
import org.csstudio.archive.sdds.server.util.IntegerValue;
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
    private Socket socket;

    /** The class that holds and executes the server commands. */
    private final CommandExecutor commandExecutor;

    /**
     *
     * @param socket
     */
    public ClientRequest(final Socket socket, final CommandExecutor commandExecutor)
    {
        this.socket = socket;
        this.commandExecutor = commandExecutor;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
	public void run()
    {
        InputStream in = null;
        final IntegerValue resultLength = new IntegerValue();
        final RawData resultData = new RawData();
        RawData requestData = null;

        LOG.info("Handle request from socket " + socket.toString());

        try
        {
            in = socket.getInputStream();

            while(socket.isClosed() == false)
            {
                final CommandHeader header = readHeader(in);
                requestData = readData(in);

                if(header != null) {

                	LOG.info(header.toString());

                    try {
                        commandExecutor.executeCommand(header.getCommandTag(), requestData,
                                                       resultData, resultLength);
                    } catch (final ServerCommandException sce) {
                        LOG.error("[*** ServerCommandException ***]: " + sce.getMessage());
                        header.setError(sce.getErrorNumber());
                        resultData.setData(sce.getMessage().getBytes());
                        resultLength.setIntegerValue(resultData.getData().length + 2);
                    }
                    catch(final CommandNotImplementedException cnie)
                    {
                        LOG.error("[*** CommandNotImplementedException ***]: " + cnie.getMessage());
                        header.setError(AapiServerError.BAD_CMD.getErrorNumber());
                        resultData.setData(cnie.getMessage().getBytes());
                        resultLength.setIntegerValue(resultData.getData().length + 2);
                    }

                    resultLength.setIntegerValue(resultData.getData().length);
                    writeAnswer(socket.getOutputStream(), header, resultData, resultLength);
                }
            }
        } catch (final IOException ioe) {
            if(ioe instanceof EOFException) {
                LOG.info("End of data stream reached.");
            } else {
                LOG.error(ioe.getMessage());
            }
        } finally {
            in = null;
            if(socket!=null) {
            	try{socket.close();}catch(final Exception e){/* Can be ignored */}
            	socket = null;
            }
        }

        LOG.info("Request finished.");
    }

    /**
     *
     * @param stream
     * @return
     * @throws IOException
     */
    private CommandHeader readHeader(final InputStream stream) throws IOException
    {
        DataInputStream dis = null;
        CommandHeader result = null;

        dis = new DataInputStream(stream);
        result = new CommandHeader();
        result.setPacketSize(dis.readInt());
        result.setCommandTag(dis.readInt());
        result.setError(dis.readInt());
        result.setAapiVersion(dis.readInt());

        return result;
    }

    /**
     *
     * @param stream
     * @return
     * @throws IOException
     */
    private RawData readData(final InputStream stream)
    {
        DataInputStream dis = null;
        RawData result = null;
        byte[] data = null;
        int dataLength;

        try
        {
            dataLength = stream.available();
        }
        catch(final IOException ioe)
        {
            dataLength = 0;
        }

        if(dataLength > 0)
        {
            dis = new DataInputStream(stream);
            data = new byte[dataLength];

            try
            {
                dis.read(data);
                result = new RawData();
                result.setData(data);
            }
            catch(final IOException ioe)
            {
                result = null;
            }
        }

        return result;
    }

    /**
     *
     * @param out
     * @param header
     * @param data
     * @param dataLength
     * @throws IOException
     */
    private void writeAnswer(final OutputStream out, final CommandHeader header, final RawData data, final IntegerValue dataLength) throws IOException
    {
        final ByteArrayOutputStream outData = new ByteArrayOutputStream(AAPI.HEADER_LENGTH + dataLength.getIntegerValue());
        DataOutputStream dos = new DataOutputStream(outData);

        // Write header
        dos.writeInt(AAPI.HEADER_LENGTH + dataLength.getIntegerValue());
        dos.writeInt(header.getCommandTag());
        dos.writeInt(data.getErrorValue());
        dos.writeInt(AAPI.AAPI_VERSION);

        // Write data
        dos.write(data.getData());

        // Write to socket output stream
        out.write(outData.toByteArray());
//        if(header.getError() != 0)
//        {
//            out.write(0);
//            out.write(0);
//        }

        if(dos!=null) {
        	try{dos.close();}catch(final Exception e){/* Can be ignored */}
        	dos=null;
        }
    }
}
