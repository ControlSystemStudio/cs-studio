package org.csstudio.config.ioconfig.view.serachview;

import java.util.Date;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.SearchNodeDBO;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * 
 * Sorter for each column of the Search Dialog Table.
 * 
 * @author hrickens
 * @author $Author: $
 * @since 23.09.2010
 */
final class SearchViewerSorterExtension extends ViewerSorter {
    private int _state;
    private boolean _asc;
    private Viewer _viewer;
    
    public SearchViewerSorterExtension() {
        // Default Constructor.
    }
    
    @Override
    public int compare(@Nonnull final Viewer viewer, @Nullable final Object e1,
                       @Nullable final Object e2) {
        _viewer = viewer;
        if (e1 instanceof SearchNodeDBO && e2 instanceof SearchNodeDBO) {
            final SearchNodeDBO node1 = (SearchNodeDBO) e1;
            final SearchNodeDBO node2 = (SearchNodeDBO) e2;
            return compareSearchNodeDBO(node1, node2);
        }
        return super.compare(viewer, e1, e2);
    }
    
    private int compareDate(@CheckForNull final Date date1,
                            @CheckForNull final Date date2, final int asc) {
        if (date1 == null && date2 == null) {
            return 0;
        }
        if (date1 == null) {
            return -asc;
        }
        if (date2 == null) {
            return asc;
        }
        
        if (date1.before(date2)) {
            return asc;
        }
        return -asc;
    }
    
    // CHECKSTYLE ON: CyclomaticComplexity
    
    private int compareNumber(@Nullable final Number parentId,
                              @Nullable final Number parentId2, final int asc) {
        int compareTo = 0;
        if (parentId == null) {
            compareTo = -1;
        } else if (parentId2 == null) {
            compareTo = 1;
        } else {
            compareTo = parentId.intValue() - parentId2.intValue();
        }
        return asc * compareTo;
    }
    
    /**
     */
    // CHECKSTYLE OFF: CyclomaticComplexity
    private int compareSearchNodeDBO(@Nonnull final SearchNodeDBO node1,
                                     @Nonnull final SearchNodeDBO node2) {
        int asc = 1;
        if (_asc) {
            asc = -1;
        }
        switch (_state) {
            case 0:
                return compareString(node1.getName(), node2.getName(), asc);
            case 1:
                return compareString(node1.getIoName(), node2.getIoName(), asc);
            case 2:
                return compareString(node1.getEpicsAddressString(),
                                     node2.getEpicsAddressString(), asc);
            case 3:
                return compareString(node1.getCreatedBy(),
                                     node2.getCreatedBy(), asc);
            case 4:
                return compareDate(node1.getCreatedOn(), node2.getCreatedOn(),
                                   asc);
            case 5:
                return compareString(node1.getUpdatedBy(),
                                     node2.getUpdatedBy(), asc);
            case 6:
                return compareDate(node1.getUpdatedOn(), node2.getUpdatedOn(),
                                   asc);
            case 7:
                return asc * (node1.getId() - node2.getId());
            case 8:
                return compareNumber(node1.getParentId(), node2.getParentId(),
                                     asc);
            default:
        }
        return asc;
    }
    
    private int compareString(@CheckForNull final String string1,
                              @CheckForNull final String string2, final int asc) {
        
        if (string1 == null && string2 == null) {
            return 0;
        }
        if (string1 == null) {
            return asc;
        }
        if (string2 == null) {
            return -asc;
        }
        return asc * string1.compareTo(string2);
    }
    
    public void setState(final int state) {
        if (_state == state) {
            _asc = !_asc;
        } else {
            _asc = true;
            _state = state;
        }
        if (_viewer != null) {
            _viewer.refresh();
        }
    }
}
