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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.persister.internal;

import java.io.IOException;

import org.csstudio.persister.declaration.IPersistableService;
import org.csstudio.persister.declaration.IPersistenceService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

/**
 * Test of the persistence service
 * 
 * @author jpenning
 * @since 30.03.2012
 */
public class PersistenceServiceUnitTest {
    private static final String FILENAME = "test.ser";

    private IPersistableService _persistableService;
    
    @Before
    public void setUp() throws Exception {
        _persistableService = mock(IPersistableService.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInitNeedsService() throws Exception {
        @SuppressWarnings("unused")
        IPersistenceService serviceUnderTest = new PersistenceService().init(null, FILENAME);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testInitMayNotBeCalledTwice() throws Exception {
        IPersistenceService serviceUnderTest = new PersistenceService().init(_persistableService, FILENAME);
        serviceUnderTest.init(_persistableService, FILENAME);
    }
    
    @Test
    public void testSaveAndRead() throws Exception {
        when(_persistableService.getMemento()).thenReturn("Serializable");
        
        PersistenceService serviceUnderTest = new PersistenceService();
        serviceUnderTest.init(_persistableService, FILENAME);
        serviceUnderTest.saveMemento();

        serviceUnderTest.restoreMemento();
        verify(_persistableService).restoreMemento("Serializable");
    }
    
    @Test
    public void testSaveAndReadMany() throws Exception {
        final int COUNT = 100000; // ca. 800 kByte
        
        double[] data = new double[COUNT];
        for (int i = 0; i < data.length; i++) {
            data[i] = i;
        }
        when(_persistableService.getMemento()).thenReturn(data);
        
        PersistenceService serviceUnderTest = new PersistenceService();
        serviceUnderTest.init(_persistableService, FILENAME);
        serviceUnderTest.saveMemento();
        
        // we only want to get hold of the internal data, there is probably a more suitable method
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                double[] storedData = (double[]) args[0];
                for (int i = 0; i < COUNT; i++) {
                    assertEquals(i, storedData[i], 0.01);
                }
                return null;
            }}).when(_persistableService).restoreMemento(data);
        
        serviceUnderTest.restoreMemento();
    }
    
    @Test
    public void testExecutor() throws Exception {
        PersistenceService serviceUnderTest = new PersistenceService();
        serviceUnderTest.init(_persistableService, FILENAME);
        serviceUnderTest.runPersister(1);

        checkMemento(serviceUnderTest, "Blabla 1");
        checkMemento(serviceUnderTest, "Blabla 2");
        checkMemento(serviceUnderTest, "Blabla 3");
        checkMemento(serviceUnderTest, "Blabla 4");
    }

    private void checkMemento(PersistenceService serviceUnderTest, String value) throws InterruptedException,
                                                                  IOException,
                                                                  ClassNotFoundException {
        when(_persistableService.getMemento()).thenReturn(value);
        Thread.sleep(1500);
        serviceUnderTest.restoreMemento();
        verify(_persistableService).restoreMemento(value);
    }
}
