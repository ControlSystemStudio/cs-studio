package de.desy.language.snl.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;

import de.desy.language.snl.configurationservice.PreferenceConstants;
import de.desy.language.snl.ui.SNLUiActivator;
import de.desy.language.snl.ui.rules.SNLCodeElementTextAttributeConstants;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IEclipsePreferences node = new DefaultScope()
				.getNode(SNLUiActivator.PLUGIN_ID);
		for (final SNLCodeElementTextAttributeConstants constant : SNLCodeElementTextAttributeConstants
				.values()) {
			final String value = StringConverter.asString(constant.getRGB());
			node.put(
					constant.asStringId() + PreferenceConstants.COLOR_POST_FIX,
					value);
			node.putBoolean(constant.asStringId()
					+ PreferenceConstants.BOLD_POST_FIX, (constant
					.getSwtFontStyleCode() & SWT.BOLD) > 0);
			node.putBoolean(constant.asStringId()
					+ PreferenceConstants.ITALIC_POST_FIX, (constant
					.getSwtFontStyleCode() & SWT.ITALIC) > 0);
			node.putBoolean(constant.asStringId()
					+ PreferenceConstants.UNDERLINE_POST_FIX, (constant
					.getSwtFontStyleCode() & TextAttribute.UNDERLINE) > 0);
			node.putBoolean(constant.asStringId()
					+ PreferenceConstants.STRIKETHROUGH_POST_FIX, (constant
					.getSwtFontStyleCode() & TextAttribute.STRIKETHROUGH) > 0);
		}
		node.putBoolean(SNLUiActivator.PLUGIN_ID + PreferenceConstants.SAVE_AND_COMPILE_POST_FIX, false);
		node.put(SNLUiActivator.PLUGIN_ID + PreferenceConstants.TARGET_PLATFORM, "none");
	}

}
