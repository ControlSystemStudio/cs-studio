/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.ui.properties;

import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.jface.viewers.ICellEditorValidator;

/**
 * This class wraps <code>PropertyValidator</code> with a cell editor validator. It can validate all
 * cell editor values that are of types defined in <code>PropertyTypesEnum</code> or that can be converted
 * into these types.
 *
 * @author Sven Wende
 *
 */
public final class PropertyTypeCellEditorValidator implements
        ICellEditorValidator {
    /**
     * The expected property type.
     */
    private WidgetProperty _property;

    /**
     * Constructor.
     *
     * @param propertyType
     *            the expected property type
     */
    public PropertyTypeCellEditorValidator(final WidgetProperty propertyType) {
        _property = propertyType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String isValid(final Object value) {
        String error = null;

        if(value!=null) {
            if(_property.checkValue(value)==null) {
                error = "Value [" + value + "] cannot not be applied to cell.";
            }
        }

        return error;
    }
}
