package org.csstudio.sds.ui.internal.properties.view;

import java.text.MessageFormat;
import java.util.Map;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.gef.commands.Command;

/**
 * Command, which sets the alias descriptors of a widget model.
 * 
 * @author Alexander Will
 * 
 */
final class SetAliasDescriptorsCommand extends Command {

	/**
	 * The new property value.
	 */
	private Map<String, String> _aliases;

	/**
	 * The old property value.
	 */
	private Map<String, String> _undoValue;

	/**
	 * The property source.
	 */
	private IPropertySource _propertySource;

	/**
	 * Constructor.
	 * 
	 * @param propLabel
	 *            a label for the property, that is beeing set
	 * @param aliases
	 *            the new aliases
	 * @param propertySource
	 *            the property source
	 */
	public SetAliasDescriptorsCommand(final String propLabel,
			final Map<String, String> aliases,
			final IPropertySource propertySource) {
		super(MessageFormat.format("Set {0} Property",
				new Object[] { propLabel }).trim());
		_aliases = aliases;
		_propertySource = propertySource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		_undoValue = (Map<String, String>) _propertySource.getPropertyValue(AbstractWidgetModel.PROP_ALIASES);
		_propertySource.setPropertyValue(AbstractWidgetModel.PROP_ALIASES, _aliases);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_propertySource.setPropertyValue(AbstractWidgetModel.PROP_ALIASES, _undoValue);
	}

}
