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
 * @author awill
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
