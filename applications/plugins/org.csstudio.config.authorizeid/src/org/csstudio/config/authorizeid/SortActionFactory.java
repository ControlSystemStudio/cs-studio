/* 
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.config.authorizeid;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Factory singleton to create actions for sorting columns and registering them at the resp. column headers
 * 
 * @author jpenning
 * @since 21.01.2011
 */
enum SortActionFactory {
    
    INSTANCE;
    
    /**
     * You have to implement the comparison for your table entry items
     */
    public static interface TypedComparator<T> {
        /**
         * @return -1, 0, +1 dependent on the result of the comparison
         */
        public int compare(T entry1, T entry2);
    }

    /**
     * Creates a sort action based on the given comparator and registers it at the column header.
     * Defines the default sort column.
     * 
     * @param <T> type of the items to compare
     * @param viewer
     * @param column 
     * @param comparator
     * @param isDefault
     */
    public static <T> void connectSortActionToColumn(@Nonnull final TableViewer viewer,
                                                     @Nonnull final TableColumn column,
                                                     @Nonnull final TypedComparator<T> comparator,
                                                     final boolean isDefault) {
        final InvertableViewerComparator invertableComparator = SortActionFactory.INSTANCE
                .createComparator(comparator);
        final Action action = createSortAction(viewer, column, invertableComparator);
        if (isDefault) {
            action.run();
        }
        final SelectionListener listener = createColumnHeaderSortListener(action);
        column.addSelectionListener(listener);
    }
    
    private <T> InvertableViewerComparator createComparator(final TypedComparator<T> comparator) {
        return new InvertableViewerComparator() {
            @SuppressWarnings("unchecked")
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                int result = 0;
                // catching the class cast exception guards against wrong element types
                try {
                    result = comparator.compare((T) e1, (T) e2);
                    result = getSortDirection() ? result : -result;
                } catch (ClassCastException e) {
                    // not comparable yields 0
                }
                
                return result;
            }
        };
    }

    /**
     * Compare string which may be null
     * 
     * @return 0 if strings are null or equal, else -1 or +1 resp.
     */
    public static int robustStringCompare(@CheckForNull final String string1, @CheckForNull final String string2) {
        int result = 0;
        if ( (string1 == null) && (string2 == null)) {
            result = 0;
        } else if (string1 == null) {
            result = -1;
        } else if (string2 == null) {
            result = 1;
        } else {
            result = string1.compareTo(string2);
        }
        return result;
    }

    private static Action createSortAction(final TableViewer viewer,
                                           final TableColumn column,
                                           final InvertableViewerComparator comparator) {
        return new Action() {
            @Override
            public void run() {
                viewer.getTable().setSortColumn(column);
                comparator.invertSortDirection();
                viewer.getTable().setSortDirection(comparator.getSortDirection() ? SWT.UP
                        : SWT.DOWN);
                viewer.setComparator(comparator);
                viewer.refresh();
            }
        };
    }
    
    private static SelectionAdapter createColumnHeaderSortListener(final Action action) {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                action.run();
            }
        };
    }
    
    private static class InvertableViewerComparator extends ViewerComparator {
        private boolean up = false;
        
        InvertableViewerComparator() {
            // ok
        }
        
        void invertSortDirection() {
            up = !up;
        }
        
        public boolean getSortDirection() {
            return up;
        }
    }
    
}