package org.csstudio.utility.adlparser.fileParser.widgets;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLBasicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLControl;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLDynamicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLMonitor;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLPoints;
import org.csstudio.utility.adlparser.fileParser.widgetParts.RelatedDisplayItem;

public abstract class ADLAbstractWidget {
	protected ADLObject _adlObject= null;
	protected ADLBasicAttribute _adlBasicAttribute= null;
	protected ADLControl _adlControl= null;
	protected ADLMonitor _adlMonitor= null;
	protected ADLPoints _adlPoints= null;
	protected RelatedDisplayItem[] _relatedDisplayItem = null;
	protected ADLDynamicAttribute _adlDynamicAttribute = null;

	protected boolean _hasBasicAttribute = false;
	protected boolean _hasObject = false;
	protected boolean _hasControl = false;
	protected boolean _hasMonitor = false;
	protected boolean _hasPoints = false;
	protected boolean _hasRelatedDisplayItem = false;
	protected boolean _hasDynamicAttribute = false;
	
	public ADLAbstractWidget(final ADLWidget adlWidget){
		
	}
	
	boolean hasADLObject(){
		return _hasObject;
	}
	
	boolean hasADLBasicAttribute(){
		return _hasBasicAttribute;
	}

	boolean hasADLControl(){
		return _hasControl;
	}
	
	boolean hasADLMonitor(){
		return _hasMonitor;
	}
	
	boolean hasADLPoints(){
		return _hasPoints;
	}
	
	boolean hasRelatedDisplayItem(){
		return _hasRelatedDisplayItem;
	}
	
	boolean hasADLDynamicAttribute(){
		return _hasDynamicAttribute;
	}
	

	/**
	 * @return ADLObject
	 */
	public ADLObject getAdlObject(){
		return _adlObject;
	}

	/**
	 * @return ADLBasicAttribute
	 */
	public ADLBasicAttribute getAdlBasicAttribute(){
		return _adlBasicAttribute;
	}

	/**
	 * @return ADLControl
	 */
	public ADLControl getAdlControl(){
		return _adlControl;
	}

	/**
	 * @return ADLMonitor
	 */
	public ADLMonitor getAdlMonitor(){
		return _adlMonitor;
	}

	/**
	 * @return ADLPoints
	 */
	public ADLPoints getAdlPoints(){
		return _adlPoints;
	}

	/**
	 * @return RelatedDisplayItem[]
	 */
	public RelatedDisplayItem[] getRelatedDisplayItem(){
		return _relatedDisplayItem;
	}

	/**
	 * @return ADLDynamicAttribute
	 */
	public ADLDynamicAttribute getAdlDynamicAttribute(){
		return _adlDynamicAttribute;
	}

}	
