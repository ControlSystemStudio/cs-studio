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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;

import com.google.common.base.Predicate;

/**
 * Filter decorator that filters all files with last modification times before the given time
 * threshold.
 * Or in other words, files older than the threshold are rejected.
 *
 * @author bknerr
 * @since 03.08.2011
 */
public class LastModificationTimeFileFilterDecorator extends AbstractFilePathParserFilterDecorator {

    private final TimeInstant _timeThreshold;

    /**
     * Constructor.
     */
    public LastModificationTimeFileFilterDecorator(@Nonnull final TimeInstant timeThreshold) {
        this(null, timeThreshold);
    }
    /**
     * Constructor.
     */
    public LastModificationTimeFileFilterDecorator(@Nullable final Predicate<File> baseDecorator,
                                                   @Nonnull final TimeInstant timeThreshold) {
        super(baseDecorator);
        _timeThreshold = timeThreshold;
    }

    /**
     * {@inheritDoc}
     *
     * Filters anything but files ending on the specified suffix.
     */
    @Override
    public boolean apply(@Nonnull final File input) {
        if (baseDecoratorApply(input)) {
            return true;
        }

        if (!input.isFile()) {
            return true;
        }
        final TimeInstant lastModified = TimeInstantBuilder.fromMillis(input.lastModified());
        // don't filter the file, if the last modification time lies after or exactly on the threshold
        if (!_timeThreshold.isAfter(lastModified)) {
            return false;
        }
        return true;
    }
}
