package org.csstudio.sds.internal.statistics;

/**
 * The types that can be used as categories in the course of performance
 * tracking. The {@link StatisticUtil} can be used with these type to track
 * certain performance data for each specific category.
 * 
 * @author Sven Wende, Stefan Hofer
 * @version $Revision$
 * 
 */
public enum MeasureCategoriesEnum {
	/**
	 * Category for sync executions.
	 */
	SYNC_EXEC_CATEGORY("Synchroneous Executions"),

	/**
	 * Category for property events.
	 */
	PROPERTY_EVENT_CATEGORY("Property Events"),
	
	/**
	 * Category for the execution of rules.
	 */
	RULE_EXEC_CATEGORY("Rule Executions");

	/**
	 * The description of the category type.
	 */
	private String _description;

	/**
	 * Constructor.
	 * 
	 * @param description
	 *            the description
	 */
	private MeasureCategoriesEnum(final String description) {
		_description = description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return _description;
	}
}
