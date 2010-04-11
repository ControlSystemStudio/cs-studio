package org.csstudio.utility.adlparser.fileParser.widgets;

import java.util.ArrayList;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.csstudio.utility.adlparser.fileParser.widgetParts.RelatedDisplayItem;
import org.csstudio.utility.adlparser.internationalization.Messages;

public class RelatedDisplay extends ADLAbstractWidget {
	private String label;
	private String bclr;
	private String clr;
	private ArrayList<RelatedDisplayItem> rdItems = new ArrayList<RelatedDisplayItem>();
	
	public RelatedDisplay(ADLWidget adlWidget) {
		super(adlWidget);
		try {
			for (ADLWidget childWidget : adlWidget.getObjects()) {
	        	if (childWidget.getType().equals("object")){
	        		_adlObject = new ADLObject(childWidget);
	        		if (_adlObject != null){
	        			_hasObject = true;
	        		}
	        		
	        	}
	        }
			for (FileLine fileLine : adlWidget.getBody()){
				String bodyPart = fileLine.getLine();
				String[] row = bodyPart.trim().split("=");
				if (row.length < 2){
					throw new WrongADLFormatException(Messages.Label_WrongADLFormatException_Parameter_Begin + bodyPart + Messages.Label_WrongADLFormatException_Parameter_End);
				}
				if (row[0].equals("label")){
					setLabel(row[1].replaceAll("\"", "").trim());
				}
				else if (row[0].equals("clr")){
					setClr(row[1].replaceAll("\"", ""));
				}
				else if (row[0].equals("bclr")){
					setBclr(row[1].replaceAll("\"", ""));
				}
			}
			for (ADLWidget item : adlWidget.getObjects()){
				if (item.getType().startsWith("display[")){
					rdItems.add(new RelatedDisplayItem(item));
				}
			}
		}
		catch (WrongADLFormatException ex) {
			
		}
	}


	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param clr the foreground color
	 */
	public void setClr(String clr) {
		this.clr = clr;
	}

	/**
	 * @return the foreground color
	 */
	public String getClr() {
		return clr;
	}

	/**
	 * @param bclr the background color
	 */
	public void setBclr(String bclr) {
		this.bclr = bclr;
	}

	/**
	 * @return the background color
	 */
	public String getBclr() {
		return bclr;
	}
	/**
	 * @return array of RelatedDisplayItem
	 */
	public RelatedDisplayItem[] getRelatedDisplayItems(){
		if ( rdItems.size() > 0 ){
			RelatedDisplayItem[] retItems = new RelatedDisplayItem[rdItems.size()];
			for (int ii=0; ii<rdItems.size(); ii++){
				retItems[ii] = rdItems.get(ii);
			}
			return retItems;
		}
		return null;
	}
}

