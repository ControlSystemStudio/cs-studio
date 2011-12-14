package edu.msu.nscl.olog.api;

/**
 *
 * @author Eric Berryman
 * @deprecated
 */
@Deprecated public class Level {
	private final String name;

	@Deprecated public static class Builder {
		// required
		private String name;

		/**
		 * @param name
		 */
		public static Builder level(String name) {
			Builder levelBuilder = new Builder();
			levelBuilder.name = name;
			return levelBuilder;
		}

		public static Builder level(Level level) {
			Builder levelBuilder = new Builder();
			levelBuilder.name = level.getName();
			return levelBuilder;
		}

		XmlLevel toXml() {
			return new XmlLevel(name);
		}

		Level build() {
			return new Level(this);
		}
	}

	/**
	 * @param xmlLevel
	 */
	Level(XmlLevel xmlLevel) {
		this.name = xmlLevel.getName();
	}

	private Level(Builder builder) {
		this.name = builder.name;
	}

	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Level))
			return false;
		Level other = (Level) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
