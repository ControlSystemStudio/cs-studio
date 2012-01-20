package edu.msu.nscl.olog.api;

public class Tag {
	private final String name;
	private final String state;

	/**
	 * @author berryman from shroffk
	 *
	 */

	Tag(XmlTag xml) {
		this.name = xml.getName();
		this.state = xml.getState();
	}

	public String getName() {
		return name;
	}

	public String getState() {
		return state;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Tag))
			return false;
		Tag other = (Tag) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
}
