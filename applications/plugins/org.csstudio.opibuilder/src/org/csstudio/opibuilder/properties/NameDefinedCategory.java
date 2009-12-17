package org.csstudio.opibuilder.properties;

/** A property category whose name is specified from the constructor input.
 * @author Xihui Chen
 *
 */
public class NameDefinedCategory implements WidgetPropertyCategory{
		private String name;
		
		public NameDefinedCategory(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
	}