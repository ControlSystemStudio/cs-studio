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
import org.junit.Before;
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
    @Before
    public void setUp() throws Exception {
//        IPreferencesService prefs = Platform.getPreferencesService();
//        prefs.getString("org.csstudio.platform", "log4j.appender.css_console.Threshold", "", null))
        HibernateRepository repository = new HibernateRepository(new HibernateTestManager());
        Repository.injectIRepository(repository);
    }
    
    
    @Test
    public void loadFromHibernate() throws Exception {
        Repository.close();
        List<FacilityDBO> load = Repository.load(FacilityDBO.class);
        assertNotNull(load);
        assertFalse(load.isEmpty());
        
        Iterator<FacilityDBO> iterator = load.iterator();
        while (iterator.hasNext()) {
            FacilityDBO next = iterator.next();
            assertNotNull(next);
            Set<DocumentDBO> documents = next.getDocuments();
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
    private void testIocs(Set<IocDBO> iocs) {
        if(!iocs.isEmpty()) {
            Iterator<IocDBO> iocIterator = iocs.iterator();
            while (iocIterator.hasNext()) {
                IocDBO iocDBO = iocIterator.next();
                Set<DocumentDBO> doc = iocDBO.getDocuments();
                assertNotNull(doc);
                testSubnets(iocDBO.getChildren());
            }
            
        }
    }
    
    /**
     * @param children
     */
    private void testSubnets(Set<ProfibusSubnetDBO> children) {
        if(!children.isEmpty()) {
            Iterator<ProfibusSubnetDBO> subnetIterator = children.iterator();
            while (subnetIterator.hasNext()) {
                ProfibusSubnetDBO subnetDBO = subnetIterator.next();
                Set<DocumentDBO> doc = subnetDBO.getDocuments();
                assertNotNull(doc);
                testMaster(subnetDBO.getChildren());
            }
            
        }
    }


    /**
     * @param children
     */
    private void testMaster(Set<MasterDBO> children) {
        if(!children.isEmpty()) {
            Iterator<MasterDBO> masterIterator = children.iterator();
            while (masterIterator.hasNext()) {
                MasterDBO masterDBO = masterIterator.next();
                Set<DocumentDBO> doc = masterDBO.getDocuments();
                assertNotNull(doc);
                testSlave(masterDBO.getChildren());
            }
            
        }
    }


    /**
     * @param children
     */
    private void testSlave(Set<SlaveDBO> children) {
        if(!children.isEmpty()) {
            Iterator<SlaveDBO> iterator = children.iterator();
            while (iterator.hasNext()) {
                SlaveDBO dbo = iterator.next();
                Set<DocumentDBO> doc = dbo.getDocuments();
                assertNotNull(doc);
                testModule(dbo.getChildren());
            }
        }
    }


    /**
     * @param children
     */
    private void testModule(Set<ModuleDBO> children) {
        if(!children.isEmpty()) {
            Iterator<ModuleDBO> iterator = children.iterator();
            while (iterator.hasNext()) {
                ModuleDBO dbo = iterator.next();
                Set<DocumentDBO> doc = dbo.getDocuments();
                assertNotNull(doc);
                testChannelStructure(dbo.getChildren());
            }
        }
        
    }


    /**
     * @param children
     */
    private void testChannelStructure(Set<ChannelStructureDBO> children) {
        if(!children.isEmpty()) {
            Iterator<ChannelStructureDBO> iterator = children.iterator();
            while (iterator.hasNext()) {
                ChannelStructureDBO dbo = iterator.next();
                Set<DocumentDBO> doc = dbo.getDocuments();
                assertNotNull(doc);
            }
        }
        
    }


    @Test
    public void loadDocumentWithFalseFromHibernate() throws Exception {
        Repository.close();
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
