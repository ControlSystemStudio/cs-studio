/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id$
 */
package org.csstudio.utility.adlparser.fileParser.widgetParts;

//**import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.utility.adlparser.internationalization.Messages;
import org.csstudio.utility.adlparser.fileParser.ADLResource;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 12.09.2007
 */
public class ADLMonitor extends WidgetPart{

    // The foreground Color (also Font color).
    private int _clr;
    // The background Color.
    private int _bclr;
    // The Channel.
    private String _chan;
    private boolean _isBackColorDefined;
    private boolean _isForeColorDefined;

    /**
     * The default constructor.
     * 
     * @param monitor An ADLWidget that correspond a ADL Monitor. 
     * @param parentWidgetModel The Widget that set the parameter from ADLWidget.
     * @throws WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public ADLMonitor(final ADLWidget monitor) throws WrongADLFormatException {
        super(monitor);
    }

    /**
     * Default constructor
     */
    public ADLMonitor(){
    	super();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void init() {
        name = new String("monitor");
        set_isBackColorDefined(false);
        set_isForeColorDefined(false);
        _chan = String.valueOf("");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    final void parseWidgetPart(final ADLWidget monitor) throws WrongADLFormatException {
        assert monitor.isType("monitor") : Messages.ADLMonitor_assertError_Begin+monitor.getType()+Messages.ADLMonitor_assertError_End; //$NON-NLS-1$

        for (FileLine fileLine : monitor.getBody()) {
            String parameter = fileLine.getLine();
            if(parameter.trim().startsWith("//")){ //$NON-NLS-1$
                continue;
            }
            String[] row = parameter.split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.ADLMonitor_WrongADLFormatException_Begin+parameter+Messages.ADLMonitor_WrongADLFormatException_End);
            }
            if(FileLine.argEquals(row[0], "clr")){ //$NON-NLS-1$
                _clr=FileLine.getIntValue(row[1]);
                set_isForeColorDefined(true);
            }else if(FileLine.argEquals(row[0], "bclr")){ //$NON-NLS-1$
                _bclr=FileLine.getIntValue(row[1]);
                set_isBackColorDefined(true);
            }else if(FileLine.argEquals(row[0], "chan")){   // chan and rdbk means both the same. Readback channel. //$NON-NLS-1$
            	_chan = FileLine.getTrimmedValue(row[1]);
            }else if(FileLine.argEquals(row[0], "rdbk")){ //$NON-NLS-1$
            	_chan = FileLine.getTrimmedValue(row[1]);
            }else {
                throw new WrongADLFormatException(Messages.ADLMonitor_WrongADLFormatException_Parameter_Begin+row[0]+Messages.ADLMonitor_WrongADLFormatException_Parameter_End+parameter);
            }
        }
    }

    

    /**
     * 
     * @return get the Channel.
     */
    public final String getChan() {
        return _chan;
    }

	@Override
	public Object[] getChildren() {
		Object[] ret = new Object[3];
		ret[0] = new ADLResource(ADLResource.FOREGROUND_COLOR, _clr);
		ret[1] = new ADLResource(ADLResource.BACKGROUND_COLOR, _bclr);
		ret[2] = new ADLResource(ADLResource.CHANNEL, _chan);
		return ret;
	}

    /** 
     * @return background Color
     */
    public int getBackgroundColor(){
    	return _bclr;
    }

    /** 
     * @return background Color
     */
    public int getForegroundColor(){
    	return _clr;
    }

	/**
	 * @param _isBackColorDefined the _isBackColorDefined to set
	 */
	private void set_isBackColorDefined(boolean _isBackColorDefined) {
		this._isBackColorDefined = _isBackColorDefined;
	}

	/**
	 * @return the _isBackColorDefined
	 */
	public boolean isBackColorDefined() {
		return _isBackColorDefined;
	}

	/**
	 * @param _isForeColorDefined the _isForeColorDefined to set
	 */
	private void set_isForeColorDefined(boolean _isForeColorDefined) {
		this._isForeColorDefined = _isForeColorDefined;
	}

	/**
	 * @return the _isForeColorDefined
	 */
	public boolean isForeColorDefined() {
		return _isForeColorDefined;
	}

}

