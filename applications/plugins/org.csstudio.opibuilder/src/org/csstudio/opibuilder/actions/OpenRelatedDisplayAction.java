/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.eclipse.jface.action.Action;

/** The action open default related display in different target.
 * 
 * @author Xihui Chen
 *
 */
public class OpenRelatedDisplayAction extends Action {

	public enum OpenDisplayTarget{
		DEFAULT("Default"),
		NEW_TAB("Open in workbench tab"),
		NEW_WINDOW("Open in new workbench"),
		NEW_SHELL("Open in standalone window");

		private String description;
		private OpenDisplayTarget(String desc) {
			this.description = desc;
		}

		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(OpenDisplayTarget p : values())
				sv[i++] = p.description;
			return sv;
		}
	}

	private OpenDisplayAction openDisplayAction;
	
	private OpenDisplayTarget  target;

	public OpenRelatedDisplayAction(OpenDisplayAction openDisplayAction,
			OpenDisplayTarget target) {
		super();
		this.openDisplayAction = openDisplayAction;
		this.target = target;
		switch (target) {
		case NEW_TAB:
			setText("Open in Workbench Tab");
			break;
		case NEW_WINDOW:
			setText("Open in New Workbench");
			break;
		case NEW_SHELL:
			setText("Open in Standalone Window");
			break;
		default:
			setText("Open");
			break;
		}
	}
	
	@Override
	public void run() {
        switch (target)
        {
        case NEW_TAB:
            openDisplayAction.runWithModifiers(true, false);
            break;
        case NEW_WINDOW:
            openDisplayAction.runWithModifiers(false, true);
            break;
        case NEW_SHELL:
            openDisplayAction.runWithModifiers(true, true);
            break;
        default:
            openDisplayAction.run();
            break;
        }
	}
	
}
