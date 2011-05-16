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

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.google.common.base.Strings;

/**
 * The enum type for epics.
 * Example epics record definition from e.g. vxBoot/ioc/kryo/kryoVBox/dbd/Epics3-14-11.dbd
 * for recordtypes digLog, mbbo, mbbi, bo, bi -> VAL field is DBF_ENUM
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

   Careful, depending on whether
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

   Hence, we store that what we get either as string if it is known from the meta data of the first
   connection or we store the value we get as integer.
   The raw value can be stored as registered channel by itself (just register the .RVAL field to
   be archived)

   Resulting EpicsEnums:<br>

   (Integer, String)
   (null, "val of first")<br>
   (null, "val of second")<br>
   (4711, null)<br>

 *
 * @author bknerr
 * @since 15.12.2010
 */
public final class EpicsEnum implements Serializable {

    public static final String SEP = ":";
    public static final String STATE = "STATE";
    public static final String RAW = "RAW";

    public static final Pattern EPICS_ENUM_STATE_REGEX =
        Pattern.compile(STATE + "\\(([\\d+])\\)" + SEP + "([^\\s]+)");
    public static final Pattern EPICS_ENUM_RAW_REGEX =
        Pattern.compile(RAW + SEP + "([-+]?[0-9]*([eE][-+]?[0-9]+)?)");

    public static final String UNKNOWN_STATE = "STATE(0):UNKNOWN";
    public static final String UNSET_STATE = "STATE(0):UNSET";

    private static final long serialVersionUID = -3340079923729173798L;

    @Nonnull
    public static EpicsEnum createFromRaw(@Nonnull final Integer raw) {
        return new EpicsEnum(raw);
    }
    @Nonnull
    public static EpicsEnum createFromStateName(@Nonnull final String state) {
        return new EpicsEnum(state, 0);
    }
    @Nonnull
    public static EpicsEnum createFromState(@Nonnull final String state,
                                            @Nonnull final Integer index) {
        return new EpicsEnum(state, index);
    }
    @Nonnull
    public static EpicsEnum createFromString(@Nonnull final String string) {
        Matcher matcher = EPICS_ENUM_RAW_REGEX.matcher(string);
        if (matcher.matches()) {
            final String eGroup = matcher.group(2);
            final String number = matcher.group(1);
            if (Strings.isNullOrEmpty(eGroup)) {
                return EpicsEnum.createFromRaw(Integer.valueOf(number));
            } else {
                return EpicsEnum.createFromRaw(Double.valueOf(number).intValue());
            }
        }
        matcher = EPICS_ENUM_STATE_REGEX.matcher(string);
        if (matcher.matches()) {
            return EpicsEnum.createFromState(matcher.group(2), Integer.valueOf(matcher.group(1)));
        }
        throw new IllegalArgumentException("String " + string + " cannot be converted to " +
                                           EpicsEnum.class.getSimpleName() + ".");
    }

    private final Integer _raw;
    private final Integer _stateIndex;
    private final String _state;


    private EpicsEnum(@Nonnull final String state,
                      @Nonnull final Integer index) {
        this(null, index, state);
    }

    private EpicsEnum(@Nonnull final Integer raw) {
        this(raw, null, null);
    }

    private EpicsEnum(@CheckForNull final Integer raw,
                      @CheckForNull final Integer index,
                      @CheckForNull final String state) {
        _raw = raw;
        _stateIndex = index;
        _state = state;
        if (isRaw() && isState() ||
            !isRaw() && !isState()) {
            throw new IllegalArgumentException("Exactly one out of both fields has to be set to null.");
        }

    }

    /**
     * @throws IllegalStateException if the epics enum does not hold a raw value but a state
     * @return the raw value as integer
     */
    @Nonnull
    public Integer getRaw() {
        if (isRaw()) {
            return _raw;
        }
        throw new IllegalStateException("This " + getClass().getSimpleName() + " object holds a state, not a raw value.");
    }

    /**
     * @throws IllegalStateException if the epics enum does not hold a state but a raw value
     * @return the state value as string
     */
    @Nonnull
    public String getState() {
        if (isState()) {
            return _state;
        }
        throw new IllegalStateException("This " + getClass().getSimpleName() + " object holds a state, not a raw value.");
    }

    @Nonnull
    public Integer getStateIndex() {
        if (isState()) {
            return _stateIndex;
        }
        throw new IllegalStateException(EpicsEnum.class.getName() + " is 'raw' instance not a 'state' instance.");
    }

    public boolean isRaw() {
        return _raw != null && _state == null && _stateIndex == null;
    }

    public boolean isState() {
        return _state != null && _stateIndex != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String toString() {
        if (isRaw()) {
            return RAW + SEP + _raw.toString();
        } else if (isState()) {
            return STATE + "(" + _stateIndex.toString() + ")" + SEP + _state;
        }
        throw new IllegalStateException("Exactly one out of both fields has to be set to null.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        if (isState()) {
            final int result = 31 + _state.hashCode();
            return result*31 + _raw.hashCode();
        }
        if (isRaw()) {
            return _raw.hashCode();
        }
        throw new IllegalStateException("All object's fields have been initialized to null.");
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
        if (other.isRaw()) {
            return other.getRaw().equals(_raw);
        }
        if (other.isState()) {
            return other.getState().equals(_state) && other.getStateIndex().equals(_stateIndex);
        }
        throw new IllegalStateException("Other's fields are both set to null, which is not allowed for this object: " + obj.toString());
    }
}
