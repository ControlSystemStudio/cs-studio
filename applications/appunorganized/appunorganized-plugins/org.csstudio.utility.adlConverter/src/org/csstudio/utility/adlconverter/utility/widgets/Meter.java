/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.utility.adlconverter.utility.widgets;

import org.csstudio.sds.components.model.MeterModel;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.utility.adlconverter.utility.ADLWidget;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 19.10.2007
 */
public class Meter extends Widget {

    /**
     * @param meter ADLWidget that describe the Meter.
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     */
    public Meter(final ADLWidget meter, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute) {
        super(meter, storedBasicAttribute, storedDynamicAttribute);
//      <property type="sds.double" id="bound.hihi" value="10.0">
//          <dynamicsDescriptor ruleId="directConnection">
//              <inputChannel name="$channel$.HIHI" type="java.lang.Object" />
//          </dynamicsDescriptor>
//      </property>
        _widget.setPropertyValue(MeterModel.PROP_MAXVAL, 10.0);
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$[graphMax]","")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(MeterModel.PROP_MAXVAL, dynamicsDescriptor);

        _widget.setPropertyValue(MeterModel.PROP_MINVAL, 0.0);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$[graphMin]","")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(MeterModel.PROP_MINVAL, dynamicsDescriptor);

        _widget.setPropertyValue(MeterModel.PROP_HIHIBOUND, 8.0);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$[alarmMax]","")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(MeterModel.PROP_HIHIBOUND, dynamicsDescriptor);

        _widget.setPropertyValue(MeterModel.PROP_HIBOUND, 6.0);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$[warningMax]","")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(MeterModel.PROP_HIBOUND, dynamicsDescriptor);

        _widget.setPropertyValue(MeterModel.PROP_LOBOUND, 4.0);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$[warningMin]","")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(MeterModel.PROP_LOBOUND, dynamicsDescriptor);

        _widget.setPropertyValue(MeterModel.PROP_LOLOBOUND, 2.0);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$[alarmMin]","")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(MeterModel.PROP_LOLOBOUND, dynamicsDescriptor);

        _widget.setPropertyValue(MeterModel.PROP_VALUE, 5.0);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$","")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(MeterModel.PROP_VALUE, dynamicsDescriptor);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(MeterModel.ID);
    }
}
