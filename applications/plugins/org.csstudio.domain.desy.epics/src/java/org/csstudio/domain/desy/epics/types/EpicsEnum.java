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
 */
package org.csstudio.domain.desy.epics.types;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.types.AbstractTriple;

import com.google.common.base.Joiner;

/**
 * The enum type for epics.
 * Example epics record definition
 *
   field(ZRVL, "0x01")<br>
   field(ONVL, "0x80")<br>
   field(TWVL, "0xA1")<br>
   field(THVL, "0x00")<br>
   field(ZRST, "val of first")<br>
   field(ONST, "val of second")<br>
   field(TWST, "val of third")<br>
   field(THST, "val of fourth")<br>
   <br>

   Careful! Depending on whether
   field(DTYP, "Soft Channel")
   or
   field(DTYP, "Raw Soft Channel")
   is defined, the record behaves differently:

   As device type 'Soft Channel' the mapping between the bit muster 0x?? and the corresponding
   enum string is NOT performed, but the incoming value is either directly copied into the VAL field,
   (e.g. myRecord.VAL == 0x80) or it is interpreted as 'index' of the number of states (when 0x02 is
   the value input, the state with index 2 is copied into VAL="val of third").

   Only when the DTYP is set to 'Raw Soft Channel' and a constant INP link is provided the INP bit
   muster is copied into RVAL, the mapping into VAL is performed, leading to e.g.:
   .RVAL==0x01, .VAL="val of first"

   Hence, we store simply that what we get as string.
   The raw value can be stored as registered channel by itself.

   Resulting EpicsEnums:<br>

   (0, "val of 33", 33)<br>
   (1, "val of 21", 21)<br>
   (2, "val of 12", 12)<br>
   (3, "val of 45", 45)<br>
 *
 * @author bknerr
 * @since 15.12.2010
 */
public class EpicsEnum extends AbstractTriple<Integer, String, Integer> {

    private static final long serialVersionUID = -3340079923729173798L;
    public static final String UNKNOWN_STATE = "UNKNOWN";
    public static final String UNSET_STATE = "NOT_SET";

    @Nonnull
    public static final EpicsEnum create(@Nonnull final Integer index,
                                         @Nonnull final String state,
                                         @Nullable final Integer raw) {
        return new EpicsEnum(index, state, raw);
    }

    /**
     * Constructor.
     */
    protected EpicsEnum(@Nonnull final Integer index,
                        @Nonnull final String state,
                        @Nullable final Integer raw) {
        super(index, state, raw);
    }
    @Nonnull
    public Integer getIndex() {
        return super.getFirst();
    }
    @Nonnull
    public String getState() {
        return super.getSecond();
    }
    @CheckForNull
    public Integer getRaw() {
        return super.getThird();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String toString() {
        final Integer raw = getRaw();
        String rawStr = "null";
        if (raw != null) {
            rawStr = raw.toString();
        }
        return "(" + Joiner.on(",").join(getIndex(), getState(), rawStr) + ")";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getState().hashCode();
        final Integer raw = getRaw();
        if (raw != null) {
            result = 31 * result + getRaw().hashCode();
        }
        return result;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@CheckForNull final Object obj) {
        if (!(obj instanceof EpicsEnum)) {
            return false;
        }
        final EpicsEnum other = (EpicsEnum) obj;
        if (!getState().equals(other.getState())) {
            return false;
        }
        if (getRaw() != other.getRaw()) {
            return false;
        }
        return true;
    }
}
