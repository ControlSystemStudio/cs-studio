
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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.csstudio.archive.sdds.server.command.CommandExecutor;
import org.csstudio.archive.sdds.server.command.ServerCommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */
public class Server extends Thread
{
    /** The logger of this class */
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    
    /** The server socket */
    private ServerSocket serverSocket;
    
    /** Thread pool for client request handling */
    private final ExecutorService threadExecutor;
    
    /** The class that holds all possible server commands */
    private CommandExecutor commandExecutor;
    
    /** The port of the server */
    private final int serverPort;
    
    /** The specified timeout for the server socket */
    private final int timeout;
    
    /** Flag that indicates if the server is running */
    private boolean running;

    /**
     * @throws ServerException 
     * 
     */
    public Server(int port, int timeOut) throws ServerException
    {
        serverSocket = null;
        this.serverPort = port;
        this.timeout = timeOut;
        this.running = true;
        int numberofReadThreads = 25;
        
        this.threadExecutor = Executors.newFixedThreadPool(numberofReadThreads);
        LOG.info("Created client request thread pool with {} threads", numberofReadThreads); 
        
        try
        {
            commandExecutor = new CommandExecutor();
        }
        catch(ServerCommandException sce)
        {
            LOG.error("[*** ServerCommandException ***]: ", sce);
            throw new ServerException("Cannot create instance of CommandExecutor: " + sce.getMessage());
        }
        
        try
        {
            initSocketConnection(port, timeout);
        }
        catch(IOException ioe)
        {
            LOG.error("[*** IOException ***]: ", ioe);
            throw new ServerException("Cannot create socket: " + ioe.getMessage());
        }
    }

    /**
     * 
     * @param port
     * @throws ServerException
     */
    public Server(int port) throws ServerException
    {
        this(port, 0);
    }

    /**
     * @throws IOException 
     * 
     */
    private void initSocketConnection(int port, int timeOut) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(timeOut);
    }
    
    /**
     * 
     */
    @Override
	public void run()
    {
        Socket socket = null;
        ClientRequest request = null;
        
        LOG.info("Server is running.");
        
        while(running) {
            
        	LOG.info("Server is waiting for a request.");
            
            try {
                
            	socket = serverSocket.accept();
                LOG.info("Anfrage empfangen...");
                request = new ClientRequest(socket, commandExecutor);
                threadExecutor.execute(request);
                
                // Socket will be closed in class ClientRequest
                socket = null;
            
            } catch (IOException ioe) {
                LOG.info("[*** IOException ***]: " + ioe.getMessage());
            }
        }
        
        try {
            
        	LOG.info("Closing server socket.");
            if(serverSocket.isClosed() == false) {
                serverSocket.close();
            } else {
                LOG.info("Server socket was closed already.");
            }
        } catch(IOException ioe) {
            LOG.error("[*** IOException ***]: ", ioe);
        }
        
        LOG.info("Leaving Server.");
    }
    
    /**
     * 
     * @return The port that is used by the server
     */
    public int getServerPort() {
        return serverPort;
    }
    
    /**
     * 
     * @return The timeout for the socket
     */
    public int getTimeOut() {
        return this.timeout;
    }
    
    /**
     * Mark to stop the server.
     * 
     */
    public void stopServer() {
        
    	running = false;
        
        synchronized(this) {
            
        	this.notify();
            
        	try {
                LOG.info("Server is forced to be stopped. Closing server socket.");
                serverSocket.close();
            } catch (IOException ioe) {
                LOG.warn("stopServer(): [*** IOException ***]: ", ioe);
            }
        }
    }
}
