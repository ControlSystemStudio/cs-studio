/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.util.Map;

import org.csstudio.opibuilder.util.MacrosInput;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.widgets.Menu;

/**
 * The contribution item that creates the <code>File/Top Files</code> submenu.
 *
 * @author Xihui Chen
 *
 */
public class TopFilesContributionItem extends ContributionItem {

    public TopFilesContributionItem() {
    }

    public TopFilesContributionItem(String id) {
        super(id);
    }

    @Override
    public void fill(Menu menu, int index) {
        Map<IPath, MacrosInput> topOPIs = OpenTopOPIsAction.loadTopOPIs();
        if (topOPIs == null)
            return;
        OpenTopOPIsAction.fillMenu(topOPIs, menu);
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
