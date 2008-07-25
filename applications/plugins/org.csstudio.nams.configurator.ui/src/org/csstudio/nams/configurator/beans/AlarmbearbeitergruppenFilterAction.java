package org.csstudio.nams.configurator.beans;

/**
 * (2,'SMS an Gruppe',NULL)
 * (3,'SMS an Gruppe Best.',NULL)
 * (5,'VMail an Gruppe',NULL)
 * (6,'VMail an Gruppe Best.',NULL)
 * (8,'EMail an Gruppe',NULL)
 * (9,'EMail an Gruppe Best.',NULL)
 *
 */
public class AlarmbearbeitergruppenFilterAction extends AbstractFilterAction<AlarmbearbeitergruppenFilterAction.AlarmbearbeitergruppenFilterActionType> {

	public static enum AlarmbearbeitergruppenFilterActionType implements FilterActionType {
		SMS(2,"SMS an Gruppe"), 
		SMS_Best(3,"SMS an Gruppe Best."), 
		VMAIL(5,"VMail an Gruppe"), 
		VAMAIL_Best(6, "VMail an Gruppe Best."), 
		EMAIL(8, "EMail an Gruppe"), 
		EMAIL_Best(9, "EMail an Gruppe Best.");

		private final int key;
		private final String description;

		private AlarmbearbeitergruppenFilterActionType(int key, String description) {
			this.key = key;
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
		
	}
	
	public AlarmbearbeitergruppenFilterAction() {
		super(AlarmbearbeitergruppenFilterActionType.class);
	}

	public FilterActionType[] getFilterActionTypeValues() {
		return AlarmbearbeitergruppenFilterActionType.values();
	}

}
