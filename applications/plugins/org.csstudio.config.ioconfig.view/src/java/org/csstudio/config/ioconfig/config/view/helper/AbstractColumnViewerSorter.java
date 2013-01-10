package org.csstudio.config.ioconfig.config.view.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 17.08.2010
 */
/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 17.08.2010
 */
abstract class AbstractColumnViewerSorter extends ViewerComparator {
    private SORT_DIRECTION _direction;
    
    private final TableViewerColumn _column;
    
    private final ColumnViewer _viewer;
    
    public AbstractColumnViewerSorter(@Nonnull final ColumnViewer viewer, @Nonnull final TableViewerColumn column) {
        _column = column;
        _viewer = viewer;
        _column.getColumn().addSelectionListener(new DirectionSelectionAdapter(this));
    }
    
    @Override
    public int compare(@Nonnull final Viewer viewer, @Nullable final Object e1, @Nullable final Object e2) {
        return getDirection().calculateDirection(doCompare(viewer, e1, e2));
    }
    
    public void setSorter(@Nonnull final AbstractColumnViewerSorter sorter, @Nonnull final SORT_DIRECTION direction) {
        sorter.setDirection(direction);
        _column.getColumn().getParent().setSortDirection(direction.getSwtDirection());
        switch (direction) {
            case NONE:
                _column.getColumn().getParent().setSortColumn(null);
                getViewer().setComparator(null);
                break;
            default:
                _column.getColumn().getParent().setSortColumn(_column.getColumn());
                if( getViewer().getComparator() == sorter ) {
                    getViewer().refresh();
                } else {
                    getViewer().setComparator(sorter);
                }
                break;
        }
    }
    
    protected abstract int doCompare(@Nonnull Viewer viewer, @Nullable Object e1, @Nullable Object e2);

    public void setDirection(@Nonnull final SORT_DIRECTION direction) {
        _direction = direction;
    }

    @Nonnull
    public SORT_DIRECTION getDirection() {
        return _direction;
    }

    @Nonnull
    public ColumnViewer getViewer() {
        return _viewer;
    }
}
