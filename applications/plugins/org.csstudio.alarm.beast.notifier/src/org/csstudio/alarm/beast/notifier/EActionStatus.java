package org.csstudio.alarm.beast.notifier;

/**
 * Status for automated actions.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public enum EActionStatus {
	
    OK(Messages.Status_OK, 0),
    FORCED(Messages.Status_FORCED, 1),
    STOPPED(Messages.Status_STOPPED, 2),
    CANCELED(Messages.Status_CANCELED, 3);

	final private String display_name;
    final private int priority;
	
	EActionStatus(final String display_name, final int priority) {
		this.display_name = display_name;
		this.priority = priority;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public int getPriority() {
		return priority;
	}

	@Override
	public String toString() {
		return "EActionStatus " + name() + " (" + display_name + ",  " + ordinal()
				+ ")";
	}
}
