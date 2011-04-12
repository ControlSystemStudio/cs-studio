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
   field(ZRVL, "33")<br>
   field(ONVL, "21")<br>
   field(TWVL, "12")<br>
   field(THVL, "45")<br>
   field(ZRST, "val of 33")<br>
   field(ONST, "val of 21")<br>
   field(TWST, "val of 12")<br>
   field(THST, "val of 45")<br>
   <br>
   Resulting EpicsEnumTriples:<br>
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
}
