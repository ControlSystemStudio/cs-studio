package org.csstudio.config.ioconfig.config.view.helper;

import javax.annotation.Nonnull;

import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * TODO (hrickens) : 
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 04.08.2011
 */
final class DirectionSelectionAdapter extends SelectionAdapter {
    private final AbstractColumnViewerSorter _abstractColumnViewerSorter;

    /**
     * Constructor.
     */
    public DirectionSelectionAdapter(@Nonnull final AbstractColumnViewerSorter abstractColumnViewerSorter) {
        _abstractColumnViewerSorter = abstractColumnViewerSorter;
    }

    @Override
    public void widgetSelected(@Nonnull final SelectionEvent e) {
        final ViewerComparator comparator = _abstractColumnViewerSorter.getViewer().getComparator();
        SORT_DIRECTION nextDirection = SORT_DIRECTION.ASC;
        if (comparator != null && comparator == _abstractColumnViewerSorter) {
            final SORT_DIRECTION tdirection = _abstractColumnViewerSorter.getDirection();
            nextDirection = tdirection.getNextDirection();
        } 
        _abstractColumnViewerSorter.setSorter(_abstractColumnViewerSorter, nextDirection);
    }
}
