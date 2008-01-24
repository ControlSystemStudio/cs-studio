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
package org.csstudio.sds.model.properties;

import org.csstudio.sds.model.properties.actions.CommitValueActionFactory;
import org.csstudio.sds.model.properties.actions.OpenShellActionFactory;
import org.csstudio.sds.model.properties.actions.OpenViewActionFactory;
import org.csstudio.sds.model.properties.actions.WidgetActionFactory;

/**
 * The types that can be used for the property ActionData.
 * @author Kai Meyer
 *
 */
public enum ActionType {
	/**
	 * Open a Display in a shell.
	 */
	OPEN_SHELL("OPEN SHELL", new OpenShellActionFactory()),
	/**
	 * Open a Display in a view.
	 */
	OPEN_VIEW("OPEN VIEW", new OpenViewActionFactory()),
	/**
	 * Commit a value.
	 */
	COMMIT_VALUE("SEND", new CommitValueActionFactory());
	
	/**
	 * The title of this {@link ActionType}.
	 */
	private String _title;
	/**
	 * The {@link WidgetActionFactory} of this {@link ActionType}.
	 */
	private WidgetActionFactory _actionFactory;
	
	/**
	 * Constructor.
	 * @param title The title of this {@link ActionType}
	 * @param factory The {@link WidgetActionFactory} for the WidgetAction.
	 */
	private ActionType(final String title, final WidgetActionFactory factory) {
		assert factory!=null;
		_title = title;
		_actionFactory = factory;
	}
	
	/**
	 * Returns the title of the {@link ActionType}.
	 * @return The title of the {@link ActionType}
	 */
	public String getTitle() {
		return _title;
	}
	
	/**
	 * Returns the {@link WidgetActionFactory}.
	 * @return The factory
	 */
	public WidgetActionFactory getActionFactory() {
		return _actionFactory;
	}

}
