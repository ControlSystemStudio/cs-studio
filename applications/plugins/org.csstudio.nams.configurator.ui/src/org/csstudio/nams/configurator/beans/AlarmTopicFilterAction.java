package org.csstudio.nams.configurator.beans;

/**
 * (10,'Message an Topic',NULL)
 *
 */
public class AlarmTopicFilterAction extends AbstractFilterAction<AlarmTopicFilterAction.AlarmTopicFilterActionType> {

	public static enum AlarmTopicFilterActionType implements FilterActionType {
		TOPIC(10, "Message an Topic");

		private final int id;
		private final String description;

		private AlarmTopicFilterActionType(int id, String description) {
			this.id = id;
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
		
	}
	
	protected AlarmTopicFilterAction() {
		super(AlarmTopicFilterActionType.class);
		setType(AlarmTopicFilterActionType.TOPIC);
	}

	public FilterActionType[] getFilterActionTypeValues() {
		return AlarmTopicFilterActionType.values();
	}

}
