package org.csstudio.sds.model.properties.actions;

import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.ActionType;
import org.csstudio.sds.model.properties.StringProperty;

/**
 * A {@link WidgetAction}, which commits a value.
 * @author Kai Meyer
 */
public final class CommitValueWidgetAction extends WidgetAction {
	/**
	 * The ID for the <i>value</i> property. 
	 */
	public static final String PROP_VALUE = "value";
	/**
	 * The ID for the <i>description</i> property. 
	 */
	public static final String PROP_DESCRIPTION = "description";
	
	/**
	 * Constructor.
	 */
	public CommitValueWidgetAction() {
		super("SEND", ActionType.COMMIT_VALUE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createProperties() {
		addProperty(PROP_VALUE, new StringProperty("Value", WidgetPropertyCategory.Behaviour, ""));
		addProperty(PROP_DESCRIPTION, new StringProperty("Description", WidgetPropertyCategory.Behaviour, ""));
	}
	
	/**
	 * Returns the value to commit.
	 * @return The value
	 */
	public String getValue() {
		return getProperty(PROP_VALUE).getPropertyValue();
	}
	
	/**
	 * Returns the description.
	 * @return The description
	 */
	public String getDescription() {
		return getProperty(PROP_DESCRIPTION).getPropertyValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getActionLabel() {
		if (getDescription()==null || getDescription().trim().length()==0) {
			StringBuffer buffer = new StringBuffer(getName());
			buffer.append(" '");
			if (getValue()==null || getValue().trim().length()==0) {
				buffer.append("unspecified");
			} else {
				buffer.append(getValue());
			}
			buffer.append("'");
			return buffer.toString();
		}
		return getDescription();
	}

}
