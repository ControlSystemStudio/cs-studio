package org.csstudio.nams.configurator.beans;

/**
 * (1,'SMS an Person',NULL)
 * (4,'VMail an Person',NULL)
 * (7,'EMail an Person',NULL)
 * @param <AlarmbearbeiterFilterActionType>
 *  
 */
public class AlarmbearbeiterFilterAction extends AbstractFilterAction<AlarmbearbeiterFilterAction.AlarmbearbeiterFilterActionType> {

	public AlarmbearbeiterFilterAction() {
		super(AlarmbearbeiterFilterActionType.class);
	}

	public static enum AlarmbearbeiterFilterActionType implements FilterActionType {
		SMS(1, "SMS an Person"),VMAIL(4, "VMail an Person"),EMAIL(7,"EMail an Person");
		
		private final int id;
		private final String description;

		private AlarmbearbeiterFilterActionType(int id, String description) {
			this.id = id;
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}
	
	public FilterActionType[] getFilterActionTypeValues() {
		return AlarmbearbeiterFilterActionType.values();
	}	
}
