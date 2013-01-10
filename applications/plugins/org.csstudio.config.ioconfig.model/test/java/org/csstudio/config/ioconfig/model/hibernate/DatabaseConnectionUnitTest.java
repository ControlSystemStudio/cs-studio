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
package org.csstudio.config.ioconfig.model.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.junit.Assert;
import org.junit.Test;

/**
 * TODO (hrickens) : 
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 22.07.2011
 */
public class DatabaseConnectionUnitTest {
    
    @Test
    public void krykmantDBTestConnectionTest() throws Exception {
        final HibernateTestManager hibernateManager = new HibernateTestManager();
        Assert.assertNotNull(hibernateManager);
        hibernateManager.buildConfig();
        final AnnotationConfiguration cfg = hibernateManager.getCfg();
        Assert.assertNotNull(cfg);
        final SessionFactory buildSessionFactory = cfg.buildSessionFactory();
        Assert.assertNotNull(buildSessionFactory);
        final Session openSession = buildSessionFactory.openSession();
        Assert.assertNotNull(openSession);
        Assert.assertTrue("Test Database Session is not open", openSession.isOpen());
        Assert.assertTrue("Test Database Session is not connected", openSession.isConnected());
        openSession.disconnect();
    }
    
    @Test
    public void kryklogtDBTestConnectionTest() throws Exception {
        final HibernateTestManager hibernateManager = new HibernateTestManager();
        Assert.assertNotNull(hibernateManager);
        hibernateManager.buildConfig();
        final AnnotationConfiguration cfg = hibernateManager.getCfg();
        Assert.assertNotNull(cfg);
        cfg.setProperty("hibernate.connection.username", "KRYKLOGT")
                .setProperty("hibernate.connection.password", "KRYKLOGT");
        final SessionFactory buildSessionFactory = cfg.buildSessionFactory();
        Assert.assertNotNull(buildSessionFactory);
        final Session openSession = buildSessionFactory.openSession();
        Assert.assertNotNull(openSession);
        Assert.assertTrue("Test Database Session is not open", openSession.isOpen());
        Assert.assertTrue("Test Database Session is not connected", openSession.isConnected());
        openSession.disconnect();
    }
    
}
