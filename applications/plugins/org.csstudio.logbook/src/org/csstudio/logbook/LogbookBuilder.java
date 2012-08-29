package org.csstudio.logbook;

/**
 * @author shroffk
 * 
 */
public class LogbookBuilder {

	// required
	private String name;
	// optional
	private String owner;

	/**
	 * @param name
	 */
	public static LogbookBuilder logbook(String name) {
		LogbookBuilder logbookBuilder = new LogbookBuilder();
		logbookBuilder.name = name;
		return logbookBuilder;
	}

	public static LogbookBuilder logbook(Logbook logbook) {
		LogbookBuilder logbookBuilder = new LogbookBuilder();
		logbookBuilder.name = logbook.getName();
		logbookBuilder.owner = logbook.getOwner();
		return logbookBuilder;
	}

	public LogbookBuilder owner(String owner) {
		this.owner = owner;
		return this;
	}

	Logbook build() {
		return new LogbookImpl(name, owner);
	}
	
	private class LogbookImpl implements Logbook{

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
