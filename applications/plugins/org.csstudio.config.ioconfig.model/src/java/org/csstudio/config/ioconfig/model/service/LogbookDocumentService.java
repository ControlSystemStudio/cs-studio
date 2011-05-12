/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id: LogbookDocumentService.java,v 1.3 2010/08/20 13:33:08 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.service;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.IDocument;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.tools.Helper;
import org.csstudio.platform.logging.CentralLogger;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.3 $
 * @since 27.08.2009
 */
public class LogbookDocumentService implements DocumentService {
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    public void openDocument(@Nonnull final String id) throws PersistenceException {
        DocumentDBO firstElement = Repository.load(DocumentDBO.class, id);
        if(firstElement != null) {
            File createTempFile = null;
            try {
                String mimeType = firstElement.getMimeType();
                if(mimeType == null) {
                    mimeType = "tmp";
                }
                createTempFile = File.createTempFile("ddbDoc", "." + mimeType);
                Helper.writeDocumentFile(createTempFile, firstElement);
                if( (createTempFile != null) && createTempFile.isFile()) {
                    if(Desktop.isDesktopSupported()) {
                        if(Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                            CentralLogger.getInstance().debug(this, "Desktop unterstützt Open!");
                            Desktop.getDesktop().open(createTempFile);
                        }
                    }
                }
            } catch (IOException e) {
                throw new PersistenceException(e);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveDocumentAs(@Nonnull final String id, @Nonnull final File file) {
        // TODO Implement save Document as File!
    }
    
    /**
     * Get all Document from a Node.
     * @throws PersistenceException 
     */
    @Nonnull
    List<IDocument> getAllDocumentsFromNode(final int nodeId) throws PersistenceException {
        List<IDocument> docList = new ArrayList<IDocument>();
        AbstractNodeDBO load = Repository.load(AbstractNodeDBO.class, nodeId);
        while (load != null) {
            if(load.getDocuments() != null) {
                docList.addAll(load.getDocuments());
            }
            load = load.getParent();
        }
        return docList;
    }
}
