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

import com.google.common.base.Predicate;

/**
 * Test for the decorator pattern of file filters.
 *
 * @author bknerr
 * @since 03.08.2011
 */
public class FileFilterDecoratorTest {

    // CHECKSTYLE OFF: VisibilityModifier
    @Rule
    public TemporaryFolder _tempFolder = new TemporaryFolder();
    // CHECKSTYLE ON: VisibilityModifier

    @Test
    public void combineDecoratorTest() throws InterruptedException, IOException {
        final File file1 = _tempFolder.newFile("early.rightSuffix");
        Assert.assertTrue(file1.exists());
        final File file1a = _tempFolder.newFile("early.wrongSuffix");
        Assert.assertTrue(file1a.exists());

        Thread.sleep(10);
        final TimeInstant threshold = TimeInstantBuilder.fromNow();
        Thread.sleep(10);

        final File file2 = _tempFolder.newFile("late.wrongSuffix");
        Assert.assertTrue(file2.exists());
        final File file2a = _tempFolder.newFile("late.rightSuffix");
        Assert.assertTrue(file2a.exists());


        final Predicate<File> timeFilter =
            new LastModificationTimeFileFilterDecorator(threshold);
        Assert.assertTrue(timeFilter.apply(file1));
        Assert.assertTrue(timeFilter.apply(file1a));
        Assert.assertFalse(timeFilter.apply(file2));
        Assert.assertFalse(timeFilter.apply(file2a));

        final Predicate<File> suffixFilter =
            new SuffixBasedFileFilterDecorator(".rightSuffix", 0);
        Assert.assertFalse(suffixFilter.apply(file1));
        Assert.assertTrue(suffixFilter.apply(file2));
        Assert.assertTrue(suffixFilter.apply(file1a));
        Assert.assertFalse(suffixFilter.apply(file2a));

        final Predicate<File> suffixAndTimeFilter =
            new SuffixBasedFileFilterDecorator(new LastModificationTimeFileFilterDecorator(threshold),
                                               ".rightSuffix",
                                               0);
        Assert.assertTrue(suffixAndTimeFilter.apply(file1));
        Assert.assertTrue(suffixAndTimeFilter.apply(file1a));
        Assert.assertTrue(suffixAndTimeFilter.apply(file2));
        Assert.assertFalse(suffixAndTimeFilter.apply(file2a));

        final Predicate<File> timeAndSuffixFilter =
            new LastModificationTimeFileFilterDecorator(new SuffixBasedFileFilterDecorator(".rightSuffix", 0),
                                                        threshold);
        Assert.assertTrue(timeAndSuffixFilter.apply(file1));
        Assert.assertTrue(timeAndSuffixFilter.apply(file1a));
        Assert.assertTrue(timeAndSuffixFilter.apply(file2));
        Assert.assertFalse(timeAndSuffixFilter.apply(file2a));
    }
}
