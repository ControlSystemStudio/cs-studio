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
package org.csstudio.platform.ui.internal.logging;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Common super class for the CSS log appender preference
 * pages.
 * 
 * @author Alexander Will
 */
public abstract class AbstractAppenderPreferencePage extends
		FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Creates a new field editor preference page with the given style, an empty
	 * title, and no image.
	 * 
	 * @param style
	 *            either <code>GRID</code> or <code>FLAT</code>
	 */
	public AbstractAppenderPreferencePage(final int style) {
		super(style);
	}

	/**
	 * Creates a new field editor preference page with the given title, image,
	 * and style.
	 * 
	 * @param title
	 *            the title of this preference page
	 * @param image
	 *            the image for this preference page, or <code>null</code> if
	 *            none
	 * @param style
	 *            either <code>GRID</code> or <code>FLAT</code>
	 */
	public AbstractAppenderPreferencePage(final String title,
			final ImageDescriptor image, final int style) {
		super(title, image, style);
	}

	/**
	 * Creates a new field editor preference page with the given title and
	 * style, but no image.
	 * 
	 * @param title
	 *            the title of this preference page
	 * @param style
	 *            either <code>GRID</code> or <code>FLAT</code>
	 */
	public AbstractAppenderPreferencePage(final String title, final int style) {
		super(title, style);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final IPreferenceStore doGetPreferenceStore() {
		return CSSPlatformUiPlugin.getCorePreferenceStore();
	}

	/**
	 * {@inheritDoc}
	 */
	public void init(final IWorkbench workbench) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean performOk() {
		boolean result = super.performOk();
		CentralLogger.getInstance().configure();
		return result;
	}
}
