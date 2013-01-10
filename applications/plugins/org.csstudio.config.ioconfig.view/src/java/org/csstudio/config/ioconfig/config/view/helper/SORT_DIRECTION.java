package org.csstudio.config.ioconfig.config.view.helper;

import javax.annotation.Nonnull;

import org.eclipse.swt.SWT;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 04.08.2011
 */
enum SORT_DIRECTION{
    ASC(1,SWT.DOWN),
    DESC(-1,SWT.UP),
    NONE(0,SWT.NONE);
    private final int _multi;
    private final int _swtDirection;

    /**
     * Constructor.
     */
    private SORT_DIRECTION(final int multi, final int swtDirection) {
        _multi = multi;
        _swtDirection = swtDirection;
    }

    public int getSwtDirection() {
        return _swtDirection;
    }

    public int calculateDirection(final int doCompare) {
        return _multi * doCompare;
    }

    /**
     * @return
     */
    @Nonnull
    public SORT_DIRECTION getNextDirection() {
        final SORT_DIRECTION[] values = SORT_DIRECTION.values();
        int ordinal = ordinal()+1;
        if(ordinal>values.length) {
         ordinal=0;
        }
        return values[ordinal];
    }
}
