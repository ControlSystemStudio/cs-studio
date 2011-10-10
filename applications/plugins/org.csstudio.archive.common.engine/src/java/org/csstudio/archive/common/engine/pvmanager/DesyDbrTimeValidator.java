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
package org.csstudio.archive.common.engine.pvmanager;

import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * Predicate for {@link DBR} types for being of type {@link TIME} and having a timestamp greater
 * than 1ms since epoch.
 *
 * @author bknerr
 * @since 07.10.2011
 */
public class DesyDbrTimeValidator implements Predicate<DBR> {

    private static final Logger LOG = LoggerFactory.getLogger(DesyDbrTimeValidator.class);

    private final Predicate<DBR> _validator;

    /**
     * Constructor.
     */
    public DesyDbrTimeValidator() {
        _validator = null;
    }

    /**
     * Constructor.
     * For decorator pattern.
     */
    public DesyDbrTimeValidator(@Nonnull final Predicate<DBR> validator) {
        _validator = validator;
    }

    @Override
    public boolean apply(@Nonnull final DBR rawDBR) {
        if (_validator != null && !_validator.apply(rawDBR)) {
            return false;
        }

        return applyForTime(rawDBR);
    }

    private boolean applyForTime(@Nonnull final DBR rawDBR) {
        if (!(rawDBR instanceof TIME)) {
            LOG.debug("DBR is not of type {}. Not valid for creation.",
                      TIME.class.getSimpleName());
            return false;
        }
        final TimeStamp ts = ((TIME) rawDBR).getTimeStamp();
        return ts.secPastEpoch() != 0L || ts.nsec() >= 1000000L;
    }
}
