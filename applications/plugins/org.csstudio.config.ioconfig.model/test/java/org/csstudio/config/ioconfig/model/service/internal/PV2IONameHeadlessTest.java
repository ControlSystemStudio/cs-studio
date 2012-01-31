/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.model.service.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.csstudio.config.ioconfig.model.INode;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.HibernateRepository;
import org.csstudio.config.ioconfig.model.hibernate.HibernateTestManager;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.service.NodeNotFoundException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 11.01.2012
 */
public class PV2IONameHeadlessTest {

    private static Collection<String> _PV_NAMES;
    private static Collection<String> _IO_NAMES;
    private static ProcessVariable2IONameImplemation _PV2IO_NAME_IMPL;
    private static String _PV_NAME_NULL;
    private static String _PV_NAME_VALUE;
    private static String _IO_NAME_NULL;
    private static String _IO_NAME_VALUE;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        final HibernateRepository repository = new HibernateRepository(new HibernateTestManager());
        Repository.injectIRepository(repository);
        _PV_NAME_NULL = "getNull";
        _PV_NAME_VALUE = "42CV111_ao";
        _IO_NAME_NULL = "getIONull";
        _IO_NAME_VALUE = "42CV111";
        _PV2IO_NAME_IMPL = new ProcessVariable2IONameImplemation();
        _PV_NAMES = new ArrayList<String>();
        _PV_NAMES.add(_PV_NAME_VALUE);
        _PV_NAMES.add(_PV_NAME_NULL);
        _IO_NAMES = new ArrayList<String>();
        _IO_NAMES.add(_IO_NAME_VALUE);
        _IO_NAMES.add(_IO_NAME_NULL);
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.service.internal.ProcessVariable2IONameImplemation#getIOName(java.lang.String)}.
     * @throws PersistenceException
     */
    @Test
    public void testGetIOName() throws PersistenceException {
        String ioName = _PV2IO_NAME_IMPL.getIOName(_PV_NAME_NULL);
        assertNull(ioName);
        ioName = _PV2IO_NAME_IMPL.getIOName(_PV_NAME_VALUE);
        assertNotNull(ioName);
        assertEquals(_IO_NAME_VALUE, ioName);
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.service.internal.ProcessVariable2IONameImplemation#getNode(java.lang.String)}.
     * @throws PersistenceException
     */
    @Test
    public void testGetNode() throws PersistenceException {
        INode node = _PV2IO_NAME_IMPL.getNode(_PV_NAME_NULL);
        assertNull(node);
        node = _PV2IO_NAME_IMPL.getNode(_PV_NAME_VALUE);
        assertNotNull(node);
        assertEquals("AO 0", node.getName());
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.service.internal.ProcessVariable2IONameImplemation#getNodes(java.util.Collection)}.
     * @throws NodeNotFoundException
     * @throws PersistenceException
     */
    @Test
    public void testGetNodes() throws PersistenceException, NodeNotFoundException {
        final Map<String, INode> nodes = _PV2IO_NAME_IMPL.getNodes(_PV_NAMES);
        assertNull(nodes.get(_PV_NAME_NULL));
        assertNotNull(nodes.get(_PV_NAME_VALUE));
        assertEquals("AO 0", nodes.get(_PV_NAME_VALUE).getName());
    }

//    /**
//     * Test method for {@link org.csstudio.config.ioconfig.model.service.internal.ProcessVariable2IONameImplemation#getPVNames(java.util.Collection)}.
//     * @throws PersistenceException
//     */
//    @Test
//    public void testGetPVNames() throws PersistenceException {
//        final Map<String, String> pvNames = _PV2IO_NAME_IMPL.getPVNames(_IO_NAMES);
//        assertNotNull(pvNames);
//        assertNull(pvNames.get(_IO_NAME_NULL));
//        assertNotNull(pvNames.get(_IO_NAME_VALUE));
//        assertEquals(_PV_NAME_VALUE, pvNames.get(_IO_NAME_VALUE));
//    }

    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.service.internal.ProcessVariable2IONameImplemation#getPVName(java.lang.String)}.
     * @throws PersistenceException
     */
    @Test
    public void testGetPVName() throws PersistenceException {
        final String pvName = _PV2IO_NAME_IMPL.getPVName(_IO_NAME_VALUE);
        assertNotNull(pvName);
        assertEquals(_PV_NAME_VALUE, pvName);
    }

}
