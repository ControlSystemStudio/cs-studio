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
 package org.csstudio.sds.ui.internal.workbench;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Perspective for the <b>Synoptic Display Studio</b>.
 *
 * @author Alexander Will
 * @version $Revision: 1.6 $
 *
 */
public final class DisplayDevelopmentPerspective implements
        IPerspectiveFactory {
    /**
     * The perspective's ID.
     */
    public static final String ID = "org.csstudio.sds.ui.internal.workbench.DisplayDevelopmentPerspective"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    @Override
    public void createInitialLayout(final IPageLayout layout) {
        layout.setEditorAreaVisible(true);

        layout.addView("org.eclipse.ui.views.ResourceNavigator", IPageLayout.LEFT, 0.2f, //$NON-NLS-1$
                IPageLayout.ID_EDITOR_AREA);

        layout.addView("org.eclipse.ui.views.PropertySheet", IPageLayout.BOTTOM, 0.7f,
                       IPageLayout.ID_EDITOR_AREA);

        layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, 0.7f,
                       "org.eclipse.ui.views.PropertySheet");
    }
}
