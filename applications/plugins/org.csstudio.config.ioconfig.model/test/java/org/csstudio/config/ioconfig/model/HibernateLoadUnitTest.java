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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.model;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.hibernate.HibernateRepository;
import org.csstudio.config.ioconfig.model.hibernate.HibernateTestManager;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 11.05.2011
 */
//CHECKSTYLE:OFF
public class HibernateLoadUnitTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        final HibernateRepository repository = new HibernateRepository(new HibernateTestManager());
        Repository.injectIRepository(repository);
    }

    @AfterClass
    public static void tearDownAfterClass() {
        Repository.close();
    }

    
    @SuppressWarnings("rawtypes")
    @Test
    public void loadFromHibernate() throws Exception {
        final List<FacilityDBO> load = Repository.load(FacilityDBO.class);
        assertNotNull(load);
        assertFalse(load.isEmpty());
        testChildren(new HashSet<AbstractNodeSharedImpl>(load));
        List<DocumentDBO> loadDocument = Repository.loadDocument(false);
        assertNotNull(loadDocument);
        assertFalse(loadDocument.isEmpty());
        loadDocument = Repository.loadDocument(true);
        assertNotNull(loadDocument);
        assertFalse(loadDocument.isEmpty());
        loadDocument = Repository.loadDocument(false);
        assertNotNull(loadDocument);
        assertFalse(loadDocument.isEmpty());


    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void testChildren(@Nonnull final Set<AbstractNodeSharedImpl> nodes) {
        for (AbstractNodeSharedImpl node : nodes) {
            final Set<DocumentDBO> doc = node.getDocuments();
            assertNotNull(doc);
            testChildren(node.getChildren());
        }
    }

    @Test
    public void loadDocumentWithFalseFromHibernate() throws Exception {
        List<DocumentDBO> loadDocument = Repository.loadDocument(false);
        assertNotNull(loadDocument);
        assertFalse(loadDocument.isEmpty());
        loadDocument = Repository.loadDocument(true);
        assertNotNull(loadDocument);
        assertFalse(loadDocument.isEmpty());
        loadDocument = Repository.loadDocument(false);
        assertNotNull(loadDocument);
        assertFalse(loadDocument.isEmpty());
    }


}
