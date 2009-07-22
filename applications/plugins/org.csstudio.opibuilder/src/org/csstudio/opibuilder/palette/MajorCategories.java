package org.csstudio.opibuilder.palette;

/**The Major categories of widgets on the palette.
 * @author Xihui Chen
 *
 */
public enum MajorCategories {
	
	GRAPHICS("Graphics"),
	
	MONITORS("Monitors"),
	
	CONTROLS("Controls"),
	
	OTHERS("Others");
	
	private String description;
	
	private MajorCategories(String description){
		this.description = description;
	}
	
	@Override
	public String toString() {
		return description;
	}
}
