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

import com.google.common.base.Predicate;


/**
 * Filter decorator that checks for the files' suffix and their directory depth.
 *
 * @author bknerr
 * @since 28.04.2011
 */
public class SuffixBasedFileFilterDecorator extends AbstractFilePathParserFilterDecorator {

    private final String _suffix;
    private final int _finalDepth;

    /**
     * Constructor.
     */
    public SuffixBasedFileFilterDecorator(@Nonnull final String fileNameSuffix,
                                          final int depth) {
        this(null, fileNameSuffix, depth);
    }
    /**
     * Constructor.
     */
    public SuffixBasedFileFilterDecorator(@Nullable final Predicate<File> baseDecorator,
                                          @Nonnull final String fileNameSuffix,
                                          final int depth) {
        super(baseDecorator);
        _suffix = fileNameSuffix;
        _finalDepth = depth;
    }

    /**
     * {@inheritDoc}
     *
     * Filters directories deeper than final depth and
     * files ending on the specified suffix.
     */
    @Override
    public boolean apply(@Nonnull final File input,
                                  final int currentDepth) {
        if (currentDepth > _finalDepth) {
            return true;
        }
        if (input.isDirectory()) {
            return true;
        }
        return apply(input);
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
        if (input.getName().endsWith(_suffix)) {
            return false;
        }
        return true;
    }
}
