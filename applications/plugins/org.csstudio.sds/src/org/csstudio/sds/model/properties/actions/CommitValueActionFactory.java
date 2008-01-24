package org.csstudio.sds.model.properties.actions;


/**
 * Creates a {@link WidgetAction} for committing a value.
 * @author Kai Meyer
 *
 */
public final class CommitValueActionFactory extends WidgetActionFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WidgetAction createWidgetAction() {
		return new CommitValueWidgetAction();
	}

}
