package org.csstudio.logbook;

/**
 * A builder for a default implementation of the Logbook interface.
 * 
 * @author shroffk
 * 
 */
public class LogbookBuilder {

	// required
	private String name;
	// optional
	private String owner;

	/**
	 * Create a Builder for Logbook with the name _name_
	 * 
	 * @param name
	 */
	public static LogbookBuilder logbook(String name) {
		LogbookBuilder logbookBuilder = new LogbookBuilder();
		logbookBuilder.name = name;
		return logbookBuilder;
	}

	/**
	 * Create a Builder object with parameters initialized with the same values
	 * as the given Logbook object
	 * 
	 * @param logbook
	 * @return
	 */
	public static LogbookBuilder logbook(Logbook logbook) {
		LogbookBuilder logbookBuilder = new LogbookBuilder();
		logbookBuilder.name = logbook.getName();
		logbookBuilder.owner = logbook.getOwner();
		return logbookBuilder;
	}

	/**
	 * Set owner
	 * 
	 * @param owner
	 * @return
	 */
	public LogbookBuilder owner(String owner) {
		this.owner = owner;
		return this;
	}

	/**
	 * Build an object implementing the Logbook.
	 * 
	 * @return
	 */
	Logbook build() {
		return new LogbookImpl(name, owner);
	}

	/**
	 * A Default implementation of the Logbook interface
	 * @author shroffk
	 *
	 */
	private class LogbookImpl implements Logbook {

		private final String name;
		private final String owner;

		public LogbookImpl(String name, String owner) {
			this.name = name;
			this.owner = owner;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getOwner() {
			return owner;
		}

	}

}
