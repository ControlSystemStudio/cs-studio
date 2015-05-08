/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.nameSpaceSearch.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class TableSorter extends ViewerSorter {
    private int spalte;
    private boolean backward;
    private int lastSort;
    private boolean lastSortBackward;

    public TableSorter(int spalte, boolean backward, int lastSort, boolean lastSortBackward) {
        this.spalte = spalte;
        this.backward = backward;
        this.lastSort = lastSort;
        this.lastSortBackward = lastSortBackward;
    }
    // Sort a table at the last two selected tableheader
    public int compare(Viewer viewer, Object o1, Object o2) {
        if (o1 instanceof ProcessVariableItem&&o2 instanceof ProcessVariableItem) {
            ProcessVariableItem vp1 = (ProcessVariableItem) o1;
            ProcessVariableItem vp2 = (ProcessVariableItem) o2;
            int multi =-1;
            if(backward)
                multi=1;
            int erg=multi*vp1.getPath()[spalte].compareTo(vp2.getPath()[spalte]);
            if(erg==0){
                int multi2 =-1;
                if(lastSortBackward)
                    multi2=1;
                erg=multi2*vp1.getPath()[lastSort].compareTo(vp2.getPath()[lastSort]);
            }
//            for(int i=0;erg==0&&i<lastSort.length;i++){
//                erg=vp1.getPath()[lastSort[0]].compareTo(vp2.getPath()[lastSort[0]]);
//            }
            return erg;
        }else
            return 0;
    }

}
