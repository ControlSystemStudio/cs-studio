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
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 15.04.2011
 */
//CHECKSTYLE:OFF
public class UserPrmDataUnitTest {
    
    private GSDFileDBO _bIMF5861;
    private GSDFileDBO _b756P33;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _b756P33 = GSDTestFiles.B756_P33.getFileAsGSDFileDBO();
    }
    
    @Test
    public void userPrmBIMF5861DataTest() throws Exception {
        final List<Integer> expected = Arrays.asList(0,1,0,55,0,19,0,0,0,1,1);
        _bIMF5861 = GSDTestFiles.BIMF5861.getFileAsGSDFileDBO();
        final AbstractGsdPropertyModel parsedGsdFileModel = _bIMF5861.getParsedGsdFileModel();
        final List<Integer> out = parsedGsdFileModel.getExtUserPrmDataConst();
        assertNotNull(out);
        assertEquals(expected.size(), out.size());
        
        for (int i = 0; i < out.size(); i++) {
            final Integer intOut = out.get(i);
            final Integer intExp = expected.get(i);
            assertEquals("UserPrmData not equal at position "+i, intExp, intOut);
        }
    }
    
    @Test
    public void userPrmDataB756P33Test() throws Exception {
        final List<Integer> expected = Arrays.asList(0,0,0,0,0,0,0,2,0,203,67,195,127,0,1,0,0,0,0);
        _b756P33 = GSDTestFiles.B756_P33.getFileAsGSDFileDBO();
        final AbstractGsdPropertyModel parsedGsdFileModel = _b756P33.getParsedGsdFileModel();
        final List<Integer> out = parsedGsdFileModel.getExtUserPrmDataConst();
        assertNotNull(out);
        assertEquals(expected.size(), out.size());
        
        for (int i = 0; i < out.size(); i++) {
            final Integer intOut = out.get(i);
            final Integer intExp = expected.get(i);
            assertEquals("UserPrmData not equal at position "+i, intExp, intOut);
        }
    }
    
    
}
//CHECKSTYLE:ON
