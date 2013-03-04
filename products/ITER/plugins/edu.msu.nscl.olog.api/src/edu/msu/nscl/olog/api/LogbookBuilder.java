/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog.api;

/**
 *
 * @author berryman
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

		XmlLogbook toXml() {
			return new XmlLogbook(name, owner);
		}

		Logbook build() {
			return new Logbook(this.toXml());
		}

}
