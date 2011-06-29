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
package org.csstudio.domain.desy.epics.name;

import static org.csstudio.domain.desy.epics.name.EpicsChannelName.FIELD_REGEX;
import static org.csstudio.domain.desy.epics.name.EpicsChannelName.FIELD_SEP_FOR_SPLIT;
import static org.csstudio.domain.desy.epics.name.EpicsChannelName.FIELD_SEP_REGEX;
import static org.csstudio.domain.desy.epics.name.EpicsChannelName.FULLNAME_REGEX;

import javax.annotation.Nonnull;

/**
 * EPICS specific name service.
 *
 * @author bknerr
 * @since 24.06.2011
 */
public final class EpicsNameSupport {

    /**
     * Don't instantiate.
     */
    private EpicsNameSupport() {
        // Empty
    }

    @Nonnull
    public static String parseBaseName(@Nonnull final String rawName) {

        if (rawName.matches("^" + FULLNAME_REGEX + "$")) {
            return rawName.replaceAll(FIELD_SEP_REGEX + FIELD_REGEX + "$", "");
        }
        throw new IllegalArgumentException(rawName + " does match channel name regex: " + "^" + FULLNAME_REGEX + "$");
    }

    /**
     * Parses the given string for {@link EpicsChannelName#FIELD_SEP} and returns
     * the field type (if already known) {@link RecordField}, and a new instance of
     * {@link UnknownRecordField} with the specified fieldName set to the given suffix.
     */
    @Nonnull
    public static IRecordField parseField(@Nonnull final String rawName) {
        if (rawName.matches("^" + FULLNAME_REGEX + "$")) {

            final String[] fields = rawName.split(FIELD_SEP_FOR_SPLIT);
            if (fields.length == 1) {
                return RecordField.VAL;
            }
            if (fields.length == 2) {
                try {
                    return RecordField.valueOf(fields[1]);
                } catch (final IllegalArgumentException e) {
                    return new UnknownRecordField(fields[1]);
                }
            }
        }
        throw new IllegalArgumentException(rawName + " does match channel name regex: " + "^" + FULLNAME_REGEX + "$");
    }


}
