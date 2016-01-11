/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import org.eclipse.ui.IPageLayout;

/**
 *
 * <code>OPIPerspective</code> is an override of the original OPI Runtime perspective,
 * which does not use the editor area.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class OPIPerspective extends OPIRunnerPerspective {

    @Override
    public void createInitialLayout(IPageLayout layout) {
        super.createInitialLayout(layout);
        layout.setEditorAreaVisible(true);
    }
}
