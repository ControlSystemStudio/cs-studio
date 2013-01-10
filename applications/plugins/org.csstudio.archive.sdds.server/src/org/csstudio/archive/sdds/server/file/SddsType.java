
/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.archive.sdds.server.file;

import javax.annotation.Nonnull;

/**
 * @author Markus Moeller
 *
 */
public enum SddsType {

    NOT_SET("Not set"),
    SDDS_DOUBLE("Double"),
    SDDS_FLOAT("Float"),
    SDDS_LONG("Long"),
    SDDS_SHORT("Short"),
    SDDS_STRING("String"),
    SDDS_CHARACTER("Character");

    /**  */
    private String typeName;

    /**
     *
     * @param name
     */
    private SddsType(@Nonnull final String name) {
        typeName = name;
    }

    /**
     *
     * @param typeName
     * @return The SDDS type matching the given name
     */
    @Nonnull
    public static SddsType getByTypeName(@Nonnull final String typeName) {

        SddsType result = SddsType.NOT_SET;

        for(final SddsType o : SddsType.values()) {
            if(o.toString().compareTo(typeName) == 0) {
                result = o;
                break;
            }
        }

        return result;
    }

    /**
     *
     * @param ordinal
     * @return The SDDS type with the given ordinal number.
     */
    @Nonnull
    public static SddsType getByOrdinal(final int ordinal) {

        SddsType result = SddsType.NOT_SET;

        for(final SddsType o : SddsType.values()) {
            if(o.ordinal() == ordinal) {
                result = o;
                break;
            }
        }

        return result;
    }


    /**
     *
     */
    @Override
    @Nonnull
    public String toString() {
        return typeName;
    }
}
