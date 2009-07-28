package org.csstudio.opibuilder.visualparts;


/**The style of border.
 * @author Xihui Chen
 *
 */
public enum BorderStyle {

	/**
	 * No border.
	 */
	NONE("None"),
	/**
	 * A line border.
	 */
	LINE("Line Style"),
	/**
	 * A raised border.
	 */
	RAISED("Raised Style"),
	/**
	 * A lowered border.
	 */
	LOWERED("Lowered Style"),
	
	/**
	 * A etched border.
	 */
	ETCHED("Etched Style"),
	
	/**
	 * A ridged border.
	 */
	RIDGED("Ridged Style"),
	
	BUTTON_RAISED("Button Raised Style"),
	
	BUTTON_PRESSED("Button Pressed Style"),	
	/**
	 * A dotted border.
	 */
	DOTTED("Dot Style"),
	
    /**
     * A dashed border.
     */
    DASHED("Dash Style"),
    /**
     * A dashed dotted border.
     */
    DASH_DOT("Dash Dot Style"),
	
	/**
     * A dashed dot dotted border.
     */
    DASH_DOT_DOT("Dash Dot Dot Style"), 
	
	TITLE_BAR("Title Bar Style"),
	
    GROUP_BOX("Group Box Style");
   
	
	private String description;
	
	private BorderStyle(String description){
		this.description = description;
	}
	
	@Override
	public String toString() {
		return description;
	}
	
	public static String[] stringValues(){
		String[] sv = new String[values().length];
		int i=0;
		for(BorderStyle p : values())
			sv[i++] = p.toString();
		return sv;
	}
	
	
}
