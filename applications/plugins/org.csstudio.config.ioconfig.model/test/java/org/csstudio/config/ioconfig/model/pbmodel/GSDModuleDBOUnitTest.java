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
package org.csstudio.config.ioconfig.model.pbmodel;

import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.HibernateRepository;
import org.csstudio.config.ioconfig.model.hibernate.HibernateTestManager;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 22.07.2011
 */
public class GSDModuleDBOUnitTest {
    
    private static HibernateRepository _REPOSITORY;
    
    private int _id;
    private GSDModuleDBO _gsdModuleDBO;
    
    @BeforeClass
    public static void setUpBeforeClass(){
        _REPOSITORY = new HibernateRepository(new HibernateTestManager());
        Repository.injectIRepository(_REPOSITORY);
    }
    
    @AfterClass
    public static void tearDownAfterClass() {
        Repository.close();
    }
    
    @After
    public void tearDown() throws PersistenceException{
        if(_gsdModuleDBO != null) {
            Repository.removeNode(_gsdModuleDBO);
        }
    }
    
    @Test
    public void testGSDModuleDBO() throws Exception {
        _gsdModuleDBO = new GSDModuleDBO("JUnitTest");
        _gsdModuleDBO.save();
        _id = _gsdModuleDBO.getId();
        Assert.assertTrue(_id>0);
        
        //test load
        final GSDModuleDBO load = Repository.load(GSDModuleDBO.class, _id);
        Assert.assertNotNull(load);
        Assert.assertEquals(_gsdModuleDBO, load);
        
        //test update
        load.setName("new JUnitTest");
        load.save();
        
        final GSDModuleDBO update = Repository.load(GSDModuleDBO.class, _id);
        Assert.assertNotNull(update);
        Assert.assertEquals(load, update);
    }
    
    
}

