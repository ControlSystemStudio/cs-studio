/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.sds.ui.internal.runmode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.internal.runmode.RunModeBoxInput;
import org.csstudio.sds.internal.runmode.RunModeType;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RunModeBoxInputTest {
    private RunModeBoxInput _input;

    private Path _path1;
    private Path _path2;

    private Map<String, String> _aliases1;
    private Map<String, String> _aliases2;

    @Before
    public void setUp() throws Exception {
        _aliases1 = new HashMap<String, String>();
        _aliases1.put("channel", "local://myvalue1");

        _aliases2 = new HashMap<String, String>();
        _aliases2.put("channel", "local://myvalue2");

        _path1 = new Path("/SDS/display1.css-sds");
        _path2 = new Path("/SDS/display2.css-sds");

        _input = new RunModeBoxInput(_path1, _aliases1,
                RunModeType.SHELL);
    }

    @After
    public void tearDown() throws Exception {


    }

    @Test
    public void testEquality() {
        // same path, same alias
        assertEquals(new RunModeBoxInput(_path1, _aliases1,
                RunModeType.SHELL), new RunModeBoxInput(_path1, _aliases1,
                RunModeType.SHELL));

        // same path, different alias
        assertNotSame(new RunModeBoxInput(_path1, _aliases1,
                RunModeType.SHELL), new RunModeBoxInput(_path1, _aliases2,
                RunModeType.SHELL));

        // different path, same alias
        assertNotSame(new RunModeBoxInput(_path1, _aliases1,
                RunModeType.SHELL), new RunModeBoxInput(_path2, _aliases1,
                RunModeType.SHELL));
    }


    @Test
    public void testSerialization() {
        ByteArrayOutputStream bot = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bot);
            oos.writeObject(_input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
