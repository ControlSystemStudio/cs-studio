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
package org.csstudio.domain.desy.file;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test for {@link LastModificationTimeFileFilterDecorator}.
 *
 * @author bknerr
 * @since 03.08.2011
 */
public class LastModificationTimeFileFilterDecoratorUnitTest {

    // CHECKSTYLE OFF: VisibilityModifier
    @Rule
    public TemporaryFolder _tempFolder = new TemporaryFolder();
    // CHECKSTYLE ON: VisibilityModifier

    @Test
    public void testFilter() throws IOException {
        final File validFile = _tempFolder.newFile("a.test");
        Assert.assertTrue(validFile.exists());

        final TimeInstant lastModified =
            TimeInstantBuilder.fromMillis(validFile.lastModified());

        TimeInstant threshold = lastModified.plusMillis(10L);
        LastModificationTimeFileFilterDecorator filter =
            new LastModificationTimeFileFilterDecorator(threshold);
        // filter if file is older than threshold
        Assert.assertTrue(filter.apply(validFile));

        threshold = lastModified.minusMillis(10L);
        filter =
            new LastModificationTimeFileFilterDecorator(threshold);
        // don't filter if file is younger than threshold
        Assert.assertFalse(filter.apply(validFile));

        filter =
            new LastModificationTimeFileFilterDecorator(lastModified);
        // don't filter if file is exactly on threshold
        Assert.assertFalse(filter.apply(validFile));
    }
}
