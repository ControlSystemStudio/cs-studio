/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.sds.ui.internal.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TreeItem;

/**
 * tree.addSelectionListener(new TreeCheckSelectionListener());.
 * @author Toasta
 */
public class TreeCheckSelectionListener extends SelectionAdapter {

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected(SelectionEvent event) {
        if (event.detail == SWT.CHECK) {
            this.handleCheckedItem((TreeItem) event.item);
        }
    }

    /**
     * Updates the check-status of every affected <code>TreeItem</code> if the given item has been checked or unchecked.
     * @param item the <code>TreeItem</code>
     */
    private void handleCheckedItem(TreeItem item) {
        //somebody selected the item, so it can't be grayed
        item.setGrayed(false);
        //check all children of the selected item
        this.checkAllChildren(item);
        //check all parents of the selected item
        this.checkAllParents(item);
    }

    /**
     * Checks all children of the given <code>TreeItem</code> like that item (checked or unchecked).
     * <code>Grayed</code> is set to <code>false</code> at all children in any case, because every child
     * just can be checked or unchecked.
     * @param parent the <code>TreeItem</code>
     */
    private void checkAllChildren(TreeItem parent) {
        for (TreeItem child : parent.getItems()) {
            child.setChecked(parent.getChecked());
            child.setGrayed(false);
            this.checkAllChildren(child);
        }
    }

    /**
     * Checks all parents of the given <code>TreeItem</code> depend on the check-status of their cildren.
     * @param item the <code>TreeItem</code>
     */
    private void checkAllParents(TreeItem item) {
        TreeItem parent = item.getParentItem();
        if (parent!=null) {
            if (allDirectChildrenChecked(parent)) {
                parent.setGrayed(false);
                parent.setChecked(true);
            } else {
                if (allDirectChildrenUnchecked(parent)) {
                    parent.setGrayed(false);
                    parent.setChecked(false);
                } else {
                    parent.setGrayed(true);
                    parent.setChecked(true);
                }
            }
            this.checkAllParents(parent);
        }
    }

    /**
     * Returns whether all direct children of the given parent are really checked or not.
     * Really checked means, that all children are checked without being grayed.
     * @param parent the <code>TreeItem</code>
     * @return <code>true</code>, if all direct children of the given parent are really checked, <code>false</code> otherwise
     */
    private boolean allDirectChildrenChecked(TreeItem parent) {
        TreeItem[] children = parent.getItems();
        for (TreeItem child : children) {
            if (!child.getChecked() || (child.getChecked() && child.getGrayed())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether all direct children of the given parent are really unchecked or not.
     * Really unchecked means, that all children are unchecked never mind being grayed or not.
     * @param parent the <code>TreeItem</code>
     * @return <code>true</code>, if all direct children of the given parent are unchecked, <code>false</code> otherwise
     */
    private boolean allDirectChildrenUnchecked(TreeItem parent) {
        TreeItem[] children = parent.getItems();
        for (TreeItem child : children) {
            if (child.getChecked()) {
                return false;
            }
        }
        return true;
    }

}
