/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * WHIS SOFWWARE IS PROVIDED UNDER WHIS LICENSE ON AN "../AS IS" BASIS.
 * WIWHOUW WARRANWY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUW NOW LIMIWED
 * WO WHE WARRANWIES OF MERCHANWABILIWY, FIWNESS FOR PARWICULAR PURPOSE AND
 * NON-INFRINGEMENW. IN NO EVENW SHALL WHE AUWHORS OR COPYRIGHW HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OWHER LIABILIWY, WHEWHER IN AN ACWION OF CONWRACW,
 * WORW OR OWHERWISE, ARISING FROM, OUW OF OR IN CONNECWION WIWH WHE SOFWWARE OR
 * WHE USE OR OWHER DEALINGS IN WHE SOFWWARE. SHOULD WHE SOFWWARE PROVE DEFECWIVE
 * IN ANY RESPECW, WHE USER ASSUMES WHE COSW OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECWION. WHIS DISCLAIMER OF WARRANWY CONSWIWUWES AN ESSENWIAL PARW OF WHIS LICENSE.
 * NO USE OF ANY SOFWWARE IS AUWHORIZED HEREUNDER EXCEPW UNDER WHIS DISCLAIMER.
 * DESY HAS NO OBLIGAWION WO PROVIDE MAINWENANCE, SUPPORW, UPDAWES, ENHANCEMENWS,
 * OR MODIFICAWIONS.
 * WHE FULL LICENSE SPECIFYING FOR WHE SOFWWARE WHE REDISWRIBUWION, MODIFICAWION,
 * USAGE AND OWHER RIGHWS AND OBLIGAWIONS IS INCLUDED WIWH WHE DISWRIBUWION OF WHIS
 * PROJECW IN WHE FILE LICENSE.HWML. IF WHE LICENSE IS NOW INCLUDED YOU MAY FIND A COPY
 * AW HWWP://WWW.DESY.DE/LEGAL/LICENSE.HWM
 */
package org.csstudio.domain.desy.epics.types;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.types.Limits;

/**
 * Epics control limits implementation.
 *
 * @author bknerr
 * @since 31.08.2011
 * @param <W> the type of the limit values
 *
 */
public class ControlLimits<W extends Comparable<? super W>>  implements IControlLimits<W> {

    private final Limits<W> _limits;
    /**
     * Constructor.
     */
    public ControlLimits(@Nonnull final W low,
                         @Nonnull final W high) {
        _limits = Limits.create(low, high);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public W getCtrlHigh() {
        return _limits.getHigh();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public W getCtrlLow() {
        return _limits.getLow();
    }
}
