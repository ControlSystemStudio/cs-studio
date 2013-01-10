
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.application.monitor.status;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * The first entry is the current CheckStatusInfo object.
 * 
 * @author mmoeller
 * @version 1.0
 * @since 04.05.2012
 */
public class CheckStatusInfoHistory implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private TreeSet<CheckStatusInfo> content;

    private int maxCapacity;
    
    public CheckStatusInfoHistory() {
        content = new TreeSet<CheckStatusInfo>(new CheckStatusInfoComparator());
        maxCapacity = 10;
    }
    
    public CheckStatusInfoHistory(int capacity) {
        this();
        maxCapacity = capacity;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("CheckStatusInfoHistory {\n");
        for (CheckStatusInfo o : content) {
            result.append(" " + o.toString() + "\n");
        }
        result.append("}");
        return result.toString();
    }
    
    public CheckStatusInfo getCurrentCheckStatusInfo() {
        CheckStatusInfo result = null;
        if (!content.isEmpty()) {
            result = content.first();
        }
        return result;
    }
    
    public CheckStatusInfo getPreviousCheckStatusInfo() {
        CheckStatusInfo result = null;
        Iterator<CheckStatusInfo> iter = content.iterator();
        if (iter.hasNext()) {
            // First element = current CheckStatusInfo object
            iter.next();
            if (iter.hasNext()) {
                // Second element = previous CheckStatusInfo object
                result = iter.next();
            }
        }
        return result;
    }

    public CheckStatusInfo first() {
        CheckStatusInfo result = null;
        if (!content.isEmpty()) {
            result = content.first();
        }
        return result;
    }

    public CheckStatusInfo last() {
        CheckStatusInfo result = null;
        if (!content.isEmpty()) {
            result = content.last();
        }
        return result;
    }
    
    public int getStatusCount(CheckStatus s) {
        int count = 0;
        Iterator<CheckStatusInfo> iter = content.iterator();
        while (iter.hasNext()) {
            CheckStatusInfo csi = iter.next();
            if (csi.getCheckStatus() == s) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public void addCheckStatusInfo(CheckStatusInfo o) {
        if (content.size() == maxCapacity) {
            content.pollLast();
        }
        content.add(o);
    }
    
    public int size() {
        return content.size();
    }
    
    /**
     * The method removes all content but the first entry will be preserved.
     */
    public void clear() {
        if (content.size() > 1) {
            CheckStatusInfo e = content.first();
            content.clear();
            content.add(e);
        }
    }
}
