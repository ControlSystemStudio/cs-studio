package org.csstudio.logbook;

/**
 * A builder for a default implementation of the {@link Logbook} interface.
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
     *            - name of the logbook
     * @return LogbookBuilder
     */
    public static LogbookBuilder logbook(String name) {
	if (name == null) {
	    throw new NullPointerException("logbook name cannot be null");
	} else {

	    LogbookBuilder logbookBuilder = new LogbookBuilder();
	    logbookBuilder.name = name;
	    return logbookBuilder;
	}
    }

    /**
     * Create a Builder object with parameters initialized with the same values
     * as the given logbook object
     * 
     * @param logbook
     * @return LogbookBuilder
     */
    public static LogbookBuilder logbook(Logbook logbook) {
	if (logbook.getName() == null) {
	    throw new NullPointerException("logbook name cannot be null");
	} else {
	    LogbookBuilder logbookBuilder = new LogbookBuilder();
	    logbookBuilder.name = logbook.getName();
	    logbookBuilder.owner = logbook.getOwner();
	    return logbookBuilder;
	}
    }

    /**
     * Set owner
     * 
     * @param owner
     * @return LogbookBuilder
     */
    public LogbookBuilder owner(String owner) {
	this.owner = owner;
	return this;
    }

    /**
     * Build an object implementing the {@link Logbook}.
     * 
     * @return Logbook - concerete immutable instance of a Logbook
     */
    Logbook build() {
	return new LogbookImpl(name, owner);
    }

    /**
     * A Default implementation of the {@link Logbook} interface
     * 
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
