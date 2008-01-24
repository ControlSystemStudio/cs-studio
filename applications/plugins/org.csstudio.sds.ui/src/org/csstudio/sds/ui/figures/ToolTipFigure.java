package org.csstudio.sds.ui.figures;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.properties.OptionProperty;
import org.csstudio.sds.util.AbstractToolTipConverter;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.ToolbarLayout;

/**
 * The figure for the tooltip.
 * 
 * @author Kai Meyer
 * 
 */
public final class ToolTipFigure extends Panel {

	/**
	 * The label, which contains the text.
	 */
	private Label _label;
	/**
	 * The {@link AbstractWidgetModel} for the tooltip.
	 */
	private AbstractWidgetModel _widgetModel;
	/**
	 * The text, which contains the parameters.
	 */
	private String _toolTip;
	/**
	 * The properties of the {@link AbstractWidgetModel} and their labels to
	 * display in the view.
	 */
	private Map<String, String> _displayNames;

	/**
	 * The tooltip converter.
	 */
	private AbstractToolTipConverter _converter = new AbstractToolTipConverter() {
		@Override
		protected String getReplacementForParameter(final String parameter) {
			String propertyName = null;
			if (_widgetModel.hasProperty(parameter)) {
				propertyName = parameter;
			} else {
				if (_displayNames.containsKey(parameter)) {
					propertyName = _displayNames.get(parameter);
				}
			}
			if (propertyName != null) {
				WidgetProperty property = _widgetModel
						.getProperty(propertyName);
				if (property != null) {
					if (propertyName
							.equals(AbstractWidgetModel.PROP_PRIMARY_PV)) {
						try {
							return ChannelReferenceValidationUtil
									.createCanonicalName(property
											.getPropertyValue().toString(),
											_widgetModel.getAliases());
						} catch (ChannelReferenceValidationException e) {
							return property.getPropertyValue().toString();
						}
					}
					if (property instanceof OptionProperty) {
						return ((OptionProperty) property).getOptions()[property
								.getPropertyValue()];
					}
					if (propertyName
							.equals(AbstractWidgetModel.PROP_ACTIONDATA)) {
						return _widgetModel.getActionData().toString();
					}
					return property.getPropertyValue().toString();
				}
			}
			return parameter;
		}
	};

	/**
	 * Constructor.
	 * 
	 * @param widgetModel
	 *            The {@link AbstractWidgetModel} for the tooltip
	 */
	public ToolTipFigure(final AbstractWidgetModel widgetModel) {
		super();
		_widgetModel = widgetModel;
		this.fillDisplayNames(widgetModel);
		_toolTip = widgetModel.getToolTipText();
		this.setLayoutManager(new ToolbarLayout(false));
		this.setBackgroundColor(ColorConstants.tooltipBackground);
		_label = new Label();
		this.add(_label);
		this.refresh();
	}

	/**
	 * Fetches all properties and their display names.
	 * 
	 * @param widgetModel
	 *            The {@link AbstractWidgetModel} of the properties
	 */
	private void fillDisplayNames(final AbstractWidgetModel widgetModel) {
		_displayNames = new HashMap<String, String>();
		for (String propertyName : widgetModel.getPropertyNames()) {
			String description = widgetModel.getProperty(propertyName)
					.getDescription();
			_displayNames.put(description, propertyName);
		}
	}

	/**
	 * Sets the tooltip-text, which can contain parameters.
	 * 
	 * @param toolTip
	 *            The tooltip-text
	 */
	public void setToolTipText(final String toolTip) {
		_toolTip = toolTip;
		this.refresh();
	}

	/**
	 * Refreshes the whole tooltip and gets the current values.
	 */
	public void refresh() {
		String text = _converter.convertToolTip(_toolTip);
		// FIXME: swende: Inperformat -> besser eine einzige Regexp verwenden
		// (Vorraussetzung: die Separatoren bestehen jeweils nur aus 1 Zeichen)
		text = text.replace(AbstractToolTipConverter.START_SEPARATOR, "");
		text = text.replace(AbstractToolTipConverter.END_SEPARATOR, "");
		_label.setText(text);
	}

}
