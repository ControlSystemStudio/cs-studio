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
 * $Id$
 */
package org.csstudio.config.ioconfig.model.service;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.config.ioconfig.model.Document;
import org.csstudio.config.ioconfig.model.IDocument;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.tools.Helper;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.documentservice.service.DocumentService;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 27.08.2009
 */
public class LogbookDocumentService implements DocumentService {

    /**
     * {@inheritDoc} 
     */
    @Override
    public void openDocument(String id) {
        Document firstElement = Repository.load(Document.class, id);
        File createTempFile = null;
        try {
            createTempFile = File.createTempFile("ddbDoc", "."
                    + firstElement.getMimeType());
            Helper.writeDocumentFile(createTempFile, firstElement);
            if(createTempFile!=null&&createTempFile.isFile()) {
                if(Desktop.isDesktopSupported()) {
                    if(Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        CentralLogger.getInstance().debug(this,"Desktop unterstützt Open!");
                        Desktop.getDesktop().open(createTempFile);
                    }
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void saveDocumentAs(String id, File file) {
        // TODO Auto-generated method stub

    }

    
    List<IDocument> getAllDocumentsFromNode(int id){
        List<IDocument> docList = new ArrayList<IDocument>();
        Node load = Repository.load(Node.class, id);
        while(load!=null) {
            if(load.getDocuments()!=null) {
                docList.addAll(load.getDocuments());
            }
            load = load.getParent();
        }
        return docList;
    }
    
//    @Override
//    public IDocument getDocumentFromNode(int id) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public IDocument getDocumentFromNode(String EpicsAddressString) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public IDocument getDocumentFromPV(String pv) {
//        // TODO Auto-generated method stub
//        return null;
//    }

}
