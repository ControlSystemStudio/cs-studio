package de.desy.language.snl.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

import de.desy.language.editor.ui.eventing.UIEvent;
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
			System.out.println("Trigger Refreshing");
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

	/*
	 * Object newValue = event.getNewValue();
	 * 
	 * if ("color".equals(valueToSet)) { /*- Cause of the stupidity of the
	 * eclipse team, we will receive string values from the initializer by
	 * pressing "restore defaults" but RGB by pushing "Apply" ;-)
	 * 
	 * RGB color; if (newValue instanceof RGB) { color = (RGB) newValue; } else {
	 * String newValueAsString = (String) newValue;
	 * System.out.println(".propertyChange() " + newValueAsString); if
	 * (newValueAsString != null && newValueAsString.trim().length() > 0) {
	 * color = StringConverter .asRGB(newValueAsString); } else { return; } }
	 * constant.setRGBValue(color);
	 * UIEvent.TEXT_ATTRIBUTE_CHANGED.triggerEvent(); } else if
	 * ("bold".equals(valueToSet)) { boolean bold; if (newValue instanceof
	 * Boolean) { bold = ((Boolean) newValue).booleanValue(); } else { String
	 * newValueAsString = (String) newValue;
	 * 
	 * if (newValueAsString != null && newValueAsString.trim().length() > 0) {
	 * bold = StringConverter .asBoolean(newValueAsString); } else { return; } }
	 * int swtFontStyleCode = constant .getSwtFontStyleCode(); if (bold) {
	 * swtFontStyleCode = swtFontStyleCode | SWT.BOLD; } else { swtFontStyleCode =
	 * swtFontStyleCode ^ SWT.BOLD; }
	 * constant.setSwtFontStyleCode(swtFontStyleCode);
	 * UIEvent.TEXT_ATTRIBUTE_CHANGED.triggerEvent(); } else if
	 * ("italic".equals(valueToSet)) { boolean italic; if (newValue instanceof
	 * Boolean) { italic = ((Boolean) newValue) .booleanValue(); } else { String
	 * newValueAsString = (String) newValue;
	 * 
	 * if (newValueAsString != null && newValueAsString.trim().length() > 0) {
	 * italic = StringConverter .asBoolean(newValueAsString); } else { return; } }
	 * int swtFontStyleCode = constant .getSwtFontStyleCode(); if (italic) {
	 * swtFontStyleCode = swtFontStyleCode | SWT.ITALIC; } else {
	 * swtFontStyleCode = swtFontStyleCode ^ SWT.ITALIC; }
	 * constant.setSwtFontStyleCode(swtFontStyleCode);
	 * UIEvent.TEXT_ATTRIBUTE_CHANGED.triggerEvent(); }
	 */

}
