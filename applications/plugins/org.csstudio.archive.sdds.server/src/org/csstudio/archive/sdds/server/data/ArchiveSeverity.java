
/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 */

package org.csstudio.archive.sdds.server.data;

import javax.annotation.Nonnull;

/**
 * TODO (mmoeller) :
 *
 * @author mmoeller
 * @version
 * @since 04.11.2010
 */
public enum ArchiveSeverity {

    NO_ALARM(0),
    MINOR(1),
    MAJOR(2),
    INVALID(3),
    ARCHIVE_DISABLED(3848),
    REPEAT(3856),
    ARCHIVE_OFF(3872),
    DISCONNECTED(3904),
    EST_REPEAT(3968),
    UNDEFINED(9999);

    /** */
    private long severityValue;

    /**
     *
     * @param statusValue
     */
    private ArchiveSeverity(final long value) {
        this.severityValue = value;
    }

    /**
     *
     * @return The value of this severity
     */
    public long getSeverityValue() {
        return this.severityValue;
    }

    @Nonnull
    public static ArchiveSeverity getByArchiveValue(final long v) {

        ArchiveSeverity result = ArchiveSeverity.UNDEFINED;

        for(final ArchiveSeverity o : ArchiveSeverity.values()) {

            if(o.getSeverityValue() == v) {

                result = o;
                break;
            }
        }

        return result;
    }
}
