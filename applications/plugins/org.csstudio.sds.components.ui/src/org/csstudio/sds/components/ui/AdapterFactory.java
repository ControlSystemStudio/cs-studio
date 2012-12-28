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
package org.csstudio.sds.components.ui;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.data.values.IValue;
import org.csstudio.sds.components.ui.internal.editparts.AdvancedSliderEditPart;
import org.csstudio.sds.components.ui.internal.editparts.MenuButtonEditPart;
import org.csstudio.sds.components.ui.internal.editparts.TextInputEditPart;
import org.csstudio.sds.model.ProcessVariableWithSamples;
import org.eclipse.core.runtime.IAdapterFactory;

/** 
 *  Adapter for AbstractBaseEditPart to ProcessVariable
 *  
 *  @author jhatje
 */
@SuppressWarnings("rawtypes")
public class AdapterFactory implements IAdapterFactory {
    @Override
    public Class[] getAdapterList()	{
        return new Class[] { ProcessVariableWithSamples.class };
    }

    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType) {
    	ProcessVariableWithSamples pv;
        if (adapterType == ProcessVariableWithSamples.class) {
        	if (adaptableObject instanceof AdvancedSliderEditPart) {
        		IValue[] samples = new IValue[1];
        		samples [0] = ((AdvancedSliderEditPart)adaptableObject).getSample(0);
				pv = new ProcessVariableWithSamples(((AdvancedSliderEditPart)adaptableObject).getName(), samples);
				return pv;
        	}
        	if (adaptableObject instanceof MenuButtonEditPart) {
        		IValue[] samples = new IValue[1];
        		samples [0] = ((MenuButtonEditPart)adaptableObject).getSample(0);
        		pv = new ProcessVariableWithSamples(((MenuButtonEditPart)adaptableObject).getName(), samples);
        		return pv;
        	}
        	if (adaptableObject instanceof TextInputEditPart) {
        		IValue[] samples = new IValue[1];
        		samples [0] = ((TextInputEditPart)adaptableObject).getSample(0);
        		pv = new ProcessVariableWithSamples(((TextInputEditPart)adaptableObject).getName(), samples);
        		return pv;
        	}
		}
        return null;
    }
}
