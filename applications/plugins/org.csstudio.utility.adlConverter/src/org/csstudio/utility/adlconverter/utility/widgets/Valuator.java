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

import org.csstudio.sds.components.model.SimpleSliderModel;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLWidget;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 19.10.2007
 */
public class Valuator extends Widget {

    /**
     * @param valuator ADLWidget that describe the Valuator.
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     */
    public Valuator(final ADLWidget valuator, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute) {
        super(valuator, storedBasicAttribute, storedDynamicAttribute);
        // The Adl valuator have an empty top area (approx 10px).
        if(getObject()!=null){
            getObject().setY(getObject().getY());
            getObject().setHeight(getObject().getHeight()-10);
        }
//        <property type="sds.double" id="max" value="100.0">
//            <dynamicsDescriptor ruleId="directConnection">
//              <inputChannel name="$channel$.HOPR" type="java.lang.Object" />
//          </dynamicsDescriptor>
//        </property>
        _widget.setPropertyValue(SimpleSliderModel.PROP_MAX, 100);
        DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$[graphMax]", "")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(SimpleSliderModel.PROP_MAX, dynamicsDescriptor );

        _widget.setPropertyValue(SimpleSliderModel.PROP_MIN, 0);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$[graphMin]", "")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(SimpleSliderModel.PROP_MIN, dynamicsDescriptor );
//        <property type="sds.double" id="increment" value="1.0" />
        _widget.setPropertyValue(SimpleSliderModel.PROP_INCREMENT, 1.0); //TODO: Prüfen ob man den Wert aus dem ADLfile bekommt
//        <property type="sds.double" id="value" value="50.0">
//            <dynamicsDescriptor ruleId="directConnection">
//                <inputChannel name="$channel$" type="java.lang.Object" />
//                <outputChannel name="$channel$" type="java.lang.Object" />
//            </dynamicsDescriptor>
//        </property>
        String postfix =""; //$NON-NLS-1$
        if(getControl()!=null&&getControl().getPostfix()!=null){
            postfix=getControl().getPostfix();
        }

        _widget.setPropertyValue(SimpleSliderModel.PROP_VALUE, 50);
        dynamicsDescriptor = new DynamicsDescriptor("directConnection"); //$NON-NLS-1$
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor("$channel$"+postfix, "")); //$NON-NLS-1$
        dynamicsDescriptor.setOutputChannel(new ParameterDescriptor("$channel$"+postfix, "")); //$NON-NLS-1$
        _widget.setDynamicsDescriptor(SimpleSliderModel.PROP_VALUE, dynamicsDescriptor );
        _widget.setLayer(Messages.ADLDisplayImporter_ADLActionLayerName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(SimpleSliderModel.ID);
    }
}
