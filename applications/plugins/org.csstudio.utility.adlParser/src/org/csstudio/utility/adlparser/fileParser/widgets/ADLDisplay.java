package org.csstudio.utility.adlparser.fileParser.widgets;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.csstudio.utility.adlparser.internationalization.Messages;

public class ADLDisplay extends ADLAbstractWidget {
	private String _clr;
	private String _bclr;
	private boolean _snapToGrid = false;
	private boolean _gridOn = false;
	private int _gridSpacing = 5;
	
	public ADLDisplay(ADLWidget adlWidget) {
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
	        for (FileLine fileLine : adlWidget.getBody()) {
	            String parameter = fileLine.getLine();
	            if(parameter.trim().startsWith("//")){ //$NON-NLS-1$
	                continue;
	            }
	            String[] row = parameter.split("="); //$NON-NLS-1$
	            if(row.length!=2){
	                throw new WrongADLFormatException(Messages.ADLMonitor_WrongADLFormatException_Begin+parameter+Messages.ADLMonitor_WrongADLFormatException_End);
	            }
	            if(row[0].trim().toLowerCase().equals("clr")){ //$NON-NLS-1$
	                set_clr(row[1].trim());
	            }else if(row[0].trim().toLowerCase().equals("bclr")){ //$NON-NLS-1$
	                set_bclr(row[1].trim());
	            }else if(row[0].trim().toLowerCase().equals("gridSpacing")){ //$NON-NLS-1$
	                set_gridSpacing(Integer.parseInt(row[1].trim()));
	            }else if(row[0].trim().toLowerCase().equals("gridOn")){ //$NON-NLS-1$
	                set_gridOn(Boolean.parseBoolean(row[1].trim()));
	            }else if(row[0].trim().toLowerCase().equals("snapToGrid")){ //$NON-NLS-1$
	                set_snapToGrid(Boolean.parseBoolean(row[1].trim()));
	            }else {
	                throw new WrongADLFormatException(Messages.ADLMonitor_WrongADLFormatException_Parameter_Begin+row[0]+Messages.ADLMonitor_WrongADLFormatException_Parameter_End+parameter);
	            }
	        }
		}
		
		catch (WrongADLFormatException ex) {
			
		}
	}

	
	/**
	 * @param _clr the _clr to set
	 */
	public void set_clr(String _clr) {
		this._clr = _clr;
	}

	/**
	 * @return the _clr
	 */
	public String get_clr() {
		return _clr;
	}

	/**
	 * @param _bclr the _bclr to set
	 */
	public void set_bclr(String _bclr) {
		this._bclr = _bclr;
	}

	/**
	 * @return the _bclr
	 */
	public String get_bclr() {
		return _bclr;
	}

	/**
	 * @param _snapToGrid the _snapToGrid to set
	 */
	public void set_snapToGrid(boolean _snapToGrid) {
		this._snapToGrid = _snapToGrid;
	}

	/**
	 * @return the _snapToGrid
	 */
	public boolean is_snapToGrid() {
		return _snapToGrid;
	}

	/**
	 * @param _gridOn the _gridOn to set
	 */
	public void set_gridOn(boolean _gridOn) {
		this._gridOn = _gridOn;
	}

	/**
	 * @return the _gridOn
	 */
	public boolean is_gridOn() {
		return _gridOn;
	}

	/**
	 * @param _gridSpacing the _gridSpacing to set
	 */
	public void set_gridSpacing(int _gridSpacing) {
		this._gridSpacing = _gridSpacing;
	}

	/**
	 * @return the _gridSpacing
	 */
	public int get_gridSpacing() {
		return _gridSpacing;
	}

	
}
