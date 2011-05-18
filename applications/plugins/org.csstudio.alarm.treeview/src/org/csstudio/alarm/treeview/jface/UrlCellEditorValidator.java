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
package org.csstudio.alarm.treeview.jface;

import java.net.URL;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.csstudio.alarm.treeview.model.UrlValidator;
import org.csstudio.alarm.treeview.model.UrlValidator.Result;
import org.eclipse.jface.viewers.ICellEditorValidator;

/**
 * JFace cell editor validator for URLs.
 * This is used for validating properties of the alarm tree nodes.
 * @see import org.csstudio.alarm.treeview..model.IAlarmTreeNode
 *
 * The validator allows for strings without a protocol. In this case it silently assumes the file: protocol.
 * The method getInheritedPropertyWithUrlProtocol in the alarm tree node does vice versa and appends
 * the file: protocol if not present.
 *
 * @author bknerr
 * @author jpenning
 * @since 09.12.2010
 */
final class UrlCellEditorValidator implements ICellEditorValidator {
    
    @Override
    @CheckForNull
    public String isValid(@Nullable final Object value) {
        if (value instanceof URL) {
            return null;
        }
        
        if (value instanceof String) {
            if (UrlValidator.checkUrl((String) value) == Result.URL_INVALID) {
                return "Malformed URL! Please enter a valid file or web path.";
            }
            return null;
        }
        return "Entered value is not valid. Please enter a String representation of a URL.";
    }
    
}
