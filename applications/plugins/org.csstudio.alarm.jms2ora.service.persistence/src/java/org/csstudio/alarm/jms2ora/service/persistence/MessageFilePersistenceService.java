
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
 *
 */

package org.csstudio.alarm.jms2ora.service.persistence;

import java.util.Vector;

import org.csstudio.alarm.jms2ora.service.IPersistenceHandler;
import org.csstudio.alarm.jms2ora.service.MessageContent;

/**
 * TODO (mmoeller) : The methods should throw an exception
 * 
 * @author mmoeller
 * @version 1.0
 * @since 22.08.2011
 */
public class MessageFilePersistenceService implements IPersistenceHandler {
    
    /** The class that handles the file access for persistence storage */
    private MessageFileHandler fileHandler;
    
    /**
     * Constructor. Oh, really?
     */
    public MessageFilePersistenceService() {
        fileHandler = new MessageFileHandler();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteAllMessageFiles() {
        return fileHandler.deleteAllMessageFiles();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMessageFiles() {
        return fileHandler.getMessageFilesNumber();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void writeMessageContent(MessageContent content) {
        fileHandler.writeMessageContentToFile(content);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void writeMessages(Vector<MessageContent> messages) {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MessageContent readMessageContent(String name) {
        return fileHandler.readMessageContent(name);
    }
}
