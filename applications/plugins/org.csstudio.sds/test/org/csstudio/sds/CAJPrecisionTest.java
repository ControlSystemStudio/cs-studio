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
 */
package org.csstudio.sds;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.csstudio.domain.desy.junit.ConditionalClassRunner;
import org.csstudio.domain.desy.junit.OsCondition;
import org.csstudio.domain.desy.junit.RunIf;
import org.csstudio.domain.desy.softioc.BasicSoftIocConfigurator;
import org.csstudio.domain.desy.softioc.Caget;
import org.csstudio.domain.desy.softioc.DBR;
import org.csstudio.domain.desy.softioc.ISoftIocConfigurator;
import org.csstudio.domain.desy.softioc.SoftIoc;
import org.eclipse.core.runtime.FileLocator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 25.11.2011
 */
@RunWith(ConditionalClassRunner.class)
@RunIf(conditionClass = OsCondition.class, arguments = {OsCondition.WIN})
public class CAJPrecisionTest {

    private SoftIoc _softIoc;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        final URL dbBundleResourceUrl = CAJPrecisionTest.class.getClassLoader().getResource("resources/db/CAJPrecisionTest.db");
        final URL dbFileUrl = FileLocator.toFileURL(dbBundleResourceUrl);

        final ISoftIocConfigurator cfg = new BasicSoftIocConfigurator().with(new File(dbFileUrl.getFile()));
        _softIoc = new SoftIoc(cfg);
        _softIoc.start();
    }

    @Test
    public void test() throws IOException, URISyntaxException {
        final Caget caget = new Caget();
        caget.setDbr(DBR.DBR_TIME_STRING);
        caget.setWaitTime(3);
        final ArrayList<String> result = caget.caget("DALPrecisionTest1");
        assertEquals(8, result.size());
        final String[] split = result.get(4).trim().split(":");
        assertEquals(2, split.length);
        final String value = split[1].trim();
        assertEquals("12345.6789", value);
    }

}
