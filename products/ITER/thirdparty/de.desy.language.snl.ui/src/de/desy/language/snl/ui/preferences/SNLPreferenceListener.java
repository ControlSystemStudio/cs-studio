package de.desy.language.snl.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

import de.desy.language.editor.ui.eventing.UIEvent;
import de.desy.language.snl.configurationservice.PreferenceConstants;
import de.desy.language.snl.ui.rules.SNLCodeElementTextAttributeConstants;

/**
 * The property listener for the SNL-preference-page.
 * 
 * @author C1 WPS
 * 
 */
public class SNLPreferenceListener implements IPropertyChangeListener {

	private final IPreferenceStore _preferenceStore;

	public SNLPreferenceListener(final IPreferenceStore preferenceStore) {
		this._preferenceStore = preferenceStore;
	}

	public void propertyChange(final PropertyChangeEvent event) {
		final String preferenceId = event.getProperty();
		final String[] idParts = preferenceId.split("\\.");
		if ((idParts == null) || (idParts.length < 2)) {
			return;
		}
		final String constantId = idParts[0];

		final SNLCodeElementTextAttributeConstants constant = SNLCodeElementTextAttributeConstants
				.findCodeElementOfId(constantId);
		if (constant != null) {

			final RGB old_rgb = constant.getRGB();
			final int old_styleCode = constant.getSwtFontStyleCode();

			final String rgbString = this._preferenceStore.getString(constantId
					+ PreferenceConstants.COLOR_POST_FIX);
			final RGB new_rgb = StringConverter.asRGB(rgbString);
			final int new_styleCode = this
					.getStyleFromPreferenceStore(constantId);

			if (old_rgb.equals(new_rgb) && (old_styleCode == new_styleCode)) {
				// nothing to do
				return;
			}

			constant.setRGBValue(new_rgb);
			constant.setSwtFontStyleCode(new_styleCode);
			UIEvent.TEXT_ATTRIBUTE_CHANGED.triggerEvent();
		}
	}

	private int getStyleFromPreferenceStore(final String constantid) {
		final boolean boldChecked = this._preferenceStore.getBoolean(constantid
				+ PreferenceConstants.BOLD_POST_FIX);
		final boolean italicChecked = this._preferenceStore
				.getBoolean(constantid + PreferenceConstants.ITALIC_POST_FIX);
		final boolean underlineChecked = this._preferenceStore
				.getBoolean(constantid + PreferenceConstants.UNDERLINE_POST_FIX);
		final boolean strikethroughChecked = this._preferenceStore
				.getBoolean(constantid
						+ PreferenceConstants.STRIKETHROUGH_POST_FIX);
		int style = SWT.NORMAL;
		if (boldChecked) {
			style = SWT.BOLD;
			if (italicChecked) {
				style = style | SWT.ITALIC;
			}
			if (underlineChecked) {
				style = style | TextAttribute.UNDERLINE;
			}
			if (strikethroughChecked) {
				style = style | TextAttribute.STRIKETHROUGH;
			}
		} else if (italicChecked) {
			style = SWT.ITALIC;
			if (underlineChecked) {
				style = style | TextAttribute.UNDERLINE;
			}
			if (strikethroughChecked) {
				style = style | TextAttribute.STRIKETHROUGH;
			}
		}
		return style;
	}

}
