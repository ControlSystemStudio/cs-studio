package org.csstudio.opibuilder.properties;

/**
 * Categories of widget properties.
 * 
 * @author Xihui Chen
 * 
 */
public interface WidgetPropertyCategory {
	
	/**
	 * Image category.
	 */
	public final static WidgetPropertyCategory Image = new WidgetPropertyCategory(){
		public String toString() {
			return "Image"; 
		}
	};

	/**
	 * Behavior category.
	 */
	public final static WidgetPropertyCategory Behavior = new WidgetPropertyCategory(){
		public String toString() {
			return "Behavior";
		}
	};

	/**
	 * Display category.
	 */
	public final static WidgetPropertyCategory Display = new WidgetPropertyCategory(){
		public String toString() {		
			return "Display";
		}
	};
	
	/**
	 * Position category.
	 */
	public final static WidgetPropertyCategory Position = new WidgetPropertyCategory(){
		public String toString() {			
			return "Position";
		}
	};
	
	
	/**
	 * Misc category.
	 */
	public final static WidgetPropertyCategory Misc = new WidgetPropertyCategory(){
		public String toString() {			
			return "Misc";
		}
	};
	
	/**
	 * Border category.
	 */
	public final static WidgetPropertyCategory Border = new WidgetPropertyCategory(){
		public String toString() {
			return "Border";
		}
	};
}
