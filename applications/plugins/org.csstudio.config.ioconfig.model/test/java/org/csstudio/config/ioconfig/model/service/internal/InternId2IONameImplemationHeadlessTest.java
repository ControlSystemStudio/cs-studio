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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.csstudio.config.ioconfig.model.IDocument;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.HibernateRepository;
import org.csstudio.config.ioconfig.model.hibernate.HibernateTestManager;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 26.01.2012
 */
public class InternId2IONameImplemationHeadlessTest {

    private static String _IO_NAME_NULL;
    private static String _IO_NAME_VALUE;
    private static String _INTERN_ID_NULL;
    private static String _INTERN_ID_VALUE;

    private InternId2IONameImplemation _internId2IONameImplemation;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        final HibernateRepository repository = new HibernateRepository(new HibernateTestManager());
        Repository.injectIRepository(repository);
        _IO_NAME_NULL = "getIONull";
        _IO_NAME_VALUE = "42CV111";
        _INTERN_ID_NULL = "00000000";
        _INTERN_ID_VALUE = "12345678";
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _internId2IONameImplemation = new InternId2IONameImplemation();

    }

    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.service.internal.InternId2IONameImplemation#getIOName(java.lang.String)}.
     */
    @Test
    public void testGetIOName() {
        String ioName = _internId2IONameImplemation.getIOName(_INTERN_ID_NULL);
        assertNull(ioName);
        ioName = _internId2IONameImplemation.getIOName(_INTERN_ID_VALUE);
        assertEquals(_IO_NAME_VALUE, ioName);
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.service.internal.InternId2IONameImplemation#getDocuments(java.lang.String)}.
     * @throws 1
     */
    @Test
    public void testGetDocuments() throws PersistenceException {
        Set<IDocument> documents = _internId2IONameImplemation.getDocuments(_INTERN_ID_NULL);
        assertNotNull(documents);
        assertTrue(documents.isEmpty());
        documents = _internId2IONameImplemation.getDocuments(_INTERN_ID_VALUE);
        assertNotNull(documents);
        assertFalse(documents.isEmpty());
        assertEquals("Testbild", documents.iterator().next().getSubject());
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.service.internal.InternId2IONameImplemation#getProcessVariables(String)}.
     * @throws PersistenceException
     */
    @Test
    public void testGetProcessVariable() throws PersistenceException {
        String processVariable = _internId2IONameImplemation.getProcessVariables(_INTERN_ID_NULL);
        assertNull(processVariable);
        processVariable = _internId2IONameImplemation.getProcessVariables(_INTERN_ID_VALUE);
        assertNotNull(processVariable);
        assertEquals("42CV111_ao",processVariable);
    }

}
