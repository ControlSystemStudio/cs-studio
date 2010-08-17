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
 /**
 * 
 */
package org.csstudio.platform.libs.dcf.actions;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.libs.dcf.actions.internal.ActionEnumerator;
import org.csstudio.platform.libs.dcf.actions.internal.ActionProxyBase;
import org.csstudio.platform.libs.dcf.actions.internal.DynamicParamValueActionProxy;
import org.csstudio.platform.libs.dcf.actions.internal.EnumeratedActionProxy;
import org.csstudio.platform.libs.dcf.actions.internal.ParametrizedActionProxy;
import org.csstudio.platform.libs.dcf.actions.internal.FileTransferActionProxy;

/**
 * Queries this instance for actions that are registered
 * with the extension point and returns a corresponding 
 * ActionDescriptor object for each one of them wrapped
 * inside a List object.
 * 
 * @author avodovnik
 *
 */
public class ActionQueryActions implements IAction {

	/**
	 * The action ID used to register for the extension point.
	 */
	public static final String ACTION_ID = "org.csstudio.platform.libs.dcf.queryActions";
	
	/* (non-Javadoc)
	 * @see org.csstudio.platform.libs.dcf.actions.IAction#run(java.lang.Object)
	 */
	public Object run(Object param) {
		List<ActionDescriptor> descriptors = new ArrayList<ActionDescriptor>();
		for(ActionProxyBase apb : ActionEnumerator.getActions()) {
			
			if(apb.isVisible()){	
				if(apb instanceof ParametrizedActionProxy
						&& !(apb instanceof DynamicParamValueActionProxy))
					descriptors.add(new ParametrizedActionDescriptor(apb.getId(),
							apb.getName(), apb.getType(),
							((ParametrizedActionProxy)apb).getParameters()));
				else if(apb instanceof EnumeratedActionProxy)
					descriptors.add(new EnumeratedActionDescriptor(apb.getId(),
							apb.getName(), apb.getType(),
							((EnumeratedActionProxy)apb).getValues()));
				else if(apb instanceof DynamicParamValueActionProxy)
					descriptors.add(new DynamicParamValueActionDescriptor(apb.getId(),
							apb.getName(), apb.getType(),
							((DynamicParamValueActionProxy)apb).getParameters(),
							((DynamicParamValueActionProxy)apb).getLoaderActionId()));
				else if(apb instanceof FileTransferActionProxy)
					descriptors.add(new FileTransferActionDescriptor(apb.getId(),
							apb.getName(), apb.getType(),
							((FileTransferActionProxy)apb).getParameters()));
				else
					descriptors.add(new ActionDescriptor(apb.getId(), apb.getName(), apb.getType()));
			}
			
		}
	
		System.out.println("Management queried your actions.");
		
		return descriptors.toArray(new ActionDescriptor[0]);
	}

}
