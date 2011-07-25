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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

    
    @Ignore // TODO (hrickens) : infinite loop ??? does not terminate
    @Test
    public void loadFromHibernate() throws Exception {
        final List<FacilityDBO> load = Repository.load(FacilityDBO.class);
        assertNotNull(load);
        assertFalse(load.isEmpty());

        final Iterator<FacilityDBO> iterator = load.iterator();
        while (iterator.hasNext()) {
            final FacilityDBO next = iterator.next();
            assertNotNull(next);
            final Set<DocumentDBO> documents = next.getDocuments();
            assertNotNull(documents);
            testIocs(next.getChildren());
        }

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


    /**
     * @param next
     */
    private void testIocs(final Set<IocDBO> iocs) {
        if(!iocs.isEmpty()) {
            final Iterator<IocDBO> iocIterator = iocs.iterator();
            while (iocIterator.hasNext()) {
                final IocDBO iocDBO = iocIterator.next();
                final Set<DocumentDBO> doc = iocDBO.getDocuments();
                assertNotNull(doc);
                testSubnets(iocDBO.getChildren());
            }

        }
    }

    /**
     * @param children
     */
    private void testSubnets(final Set<ProfibusSubnetDBO> children) {
        if(!children.isEmpty()) {
            final Iterator<ProfibusSubnetDBO> subnetIterator = children.iterator();
            while (subnetIterator.hasNext()) {
                final ProfibusSubnetDBO subnetDBO = subnetIterator.next();
                final Set<DocumentDBO> doc = subnetDBO.getDocuments();
                assertNotNull(doc);
                testMaster(subnetDBO.getChildren());
            }

        }
    }


    /**
     * @param children
     */
    private void testMaster(final Set<MasterDBO> children) {
        if(!children.isEmpty()) {
            final Iterator<MasterDBO> masterIterator = children.iterator();
            while (masterIterator.hasNext()) {
                final MasterDBO masterDBO = masterIterator.next();
                final Set<DocumentDBO> doc = masterDBO.getDocuments();
                assertNotNull(doc);
                testSlave(masterDBO.getChildren());
            }

        }
    }


    /**
     * @param children
     */
    private void testSlave(final Set<SlaveDBO> children) {
        if(!children.isEmpty()) {
            final Iterator<SlaveDBO> iterator = children.iterator();
            while (iterator.hasNext()) {
                final SlaveDBO dbo = iterator.next();
                final Set<DocumentDBO> doc = dbo.getDocuments();
                assertNotNull(doc);
                testModule(dbo.getChildren());
            }
        }
    }


    /**
     * @param children
     */
    private void testModule(final Set<ModuleDBO> children) {
        if(!children.isEmpty()) {
            final Iterator<ModuleDBO> iterator = children.iterator();
            while (iterator.hasNext()) {
                final ModuleDBO dbo = iterator.next();
                final Set<DocumentDBO> doc = dbo.getDocuments();
                assertNotNull(doc);
                testChannelStructure(dbo.getChildren());
            }
        }

    }


    /**
     * @param children
     */
    private void testChannelStructure(final Set<ChannelStructureDBO> children) {
        if(!children.isEmpty()) {
            final Iterator<ChannelStructureDBO> iterator = children.iterator();
            while (iterator.hasNext()) {
                final ChannelStructureDBO dbo = iterator.next();
                final Set<DocumentDBO> doc = dbo.getDocuments();
                assertNotNull(doc);
            }
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
//CHECKSTYLE:ON
