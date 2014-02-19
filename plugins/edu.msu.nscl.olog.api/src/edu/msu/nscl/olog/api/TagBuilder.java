/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog.api;

/**
 *
 * @author berryman
 */
public class TagBuilder {
		// Required
		private String name;
		// Optional
		private String state = null;

		public static TagBuilder tag(Tag tag) {
			TagBuilder builder = new TagBuilder();
			builder.name = tag.getName();
			builder.state = tag.getState();
			return builder;
		}

		public static TagBuilder tag(String name) {
			TagBuilder builder = new TagBuilder();
			builder.name = name;
			return builder;
		}

		public static TagBuilder tag(String name, String state) {
			TagBuilder builder = new TagBuilder();
			builder.name = name;
			builder.state = state;
			return builder;
		}

		public TagBuilder state(String state) {
			this.state = state;
			return this;
		}

		XmlTag toXml() {
			XmlTag xml = new XmlTag();
			xml.setName(name);
			xml.setState(state);
			return xml;
		}

		Tag build(){
			return new Tag(this.toXml());
		}


}
