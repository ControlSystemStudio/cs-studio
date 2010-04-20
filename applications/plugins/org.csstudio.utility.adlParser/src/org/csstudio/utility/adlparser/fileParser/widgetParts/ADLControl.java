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

//**import org.csstudio.sds.internal.rules.ParameterDescriptor;
//**import org.csstudio.sds.model.AbstractWidgetModel;
//**import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.utility.adlparser.internationalization.Messages;
//**import org.csstudio.utility.adlparser.fileParser.ADLHelper;
import org.csstudio.utility.adlparser.fileParser.ADLResource;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 20.09.2007
 */
public class ADLControl extends WidgetPart{
	//TODO Strip out old code lines that refer to SDS implementations
    /**
     * The foreground color.
     */
    private int _clr;
    /**
     * The background color.
     */
    private int _bclr;
    /**
     * The channel.
     */
    private String _chan;
    /**
     * If true backgroundColor get the ConnectionState.
     * Default is false.
     */
    private boolean _connectionState;
  //**    /**
  //**     * The Record property/Feldname.
  //**     */
  //**    private String _postfix;
    private boolean _isBackColorDefined;
    private boolean _isForeColorDefined;
     

    /**
     * The default constructor.
     * 
     * @param adlControl An ADLWidget that correspond a ADL Control. 
     * @param parentWidgetModel The Widget that set the parameter from ADLWidget.
     * @throws WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public ADLControl(final ADLWidget adlControl) throws WrongADLFormatException {
        super(adlControl);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    final void init() {
        _connectionState = false;
        _chan = new String();
        set_isForeColorDefined(false);
        set_isBackColorDefined(false);
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    final void parseWidgetPart(final ADLWidget adlControl) throws WrongADLFormatException {

        assert adlControl.isType("control") : Messages.ADLControl_AssertError_Begin+adlControl.getType()+Messages.ADLControl_AssertError_End; //$NON-NLS-1$

        for (FileLine parameter : adlControl.getBody()) {
            if(parameter.getLine().trim().startsWith("//")){ //$NON-NLS-1$
                continue;
            }
            String[] row = parameter.getLine().split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.ADLControl_WrongADLFormatException_Begin+parameter+Messages.ADLControl_WrongADLFormatException_End);
            }
            if(FileLine.argEquals(row[0], "clr")){ //$NON-NLS-1$
                _clr=FileLine.getIntValue(row[1]);
                set_isForeColorDefined(true);
            }else if(FileLine.argEquals(row[0], "bclr")){ //$NON-NLS-1$
                _bclr=FileLine.getIntValue(row[1]);
                set_isBackColorDefined(true);
            }else if(FileLine.argEquals(row[0], "chan")){ // chan and ctrl means both the same.  //$NON-NLS-1$
            	_chan = FileLine.getTrimmedValue(row[1]);
            	//**                _chan=ADLHelper.cleanString(row[1]);
            }else if(FileLine.argEquals(row[0], "ctrl")){ //$NON-NLS-1$
            	_chan = FileLine.getTrimmedValue(row[1]);
//**                _chan=ADLHelper.cleanString(row[1]);
            }else {
                throw new WrongADLFormatException(Messages.ADLControl_WrongADLFormatException_Parameter_Begin+parameter+Messages.ADLControl_WrongADLFormatException_Parameter_End);
            }
        }
    }

//**    /**
  //**     * Generate all Elements from ADL Control Attributes.
  //**     */
  //**    @Override
  //**    final void generateElements() {
  //**        if(_clr!=null){
  //**            _widgetModel.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, ADLHelper.getRGB(_clr));
  //**        }
  //**        if(_bclr!=null){
  //**            _widgetModel.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, ADLHelper.getRGB(_bclr));
  //**            if(_connectionState){
  //**                DynamicsDescriptor dynDis = new DynamicsDescriptor("rule.null"); //$NON-NLS-1$
  //**                dynDis.addInputChannel(new ParameterDescriptor("$channel$","")); //$NON-NLS-1$
  //**                _widgetModel.setDynamicsDescriptor("color.background", dynDis); //$NON-NLS-1$
  //**            }
  //**        }
  //**        if(_chan!=null){
  //**            _postfix = ADLHelper.setChan(_widgetModel,_chan);
  //**        }
  //**    }
    
    /**
     * @return the state of background color display the ConnectionState.
     */
    public final boolean isConnectionState() {
        return _connectionState;
    }

//**    /**
  //**     * @param connectionState set background color display the ConnectionState.
  //**     */
  //**    public final void setConnectionState(final boolean connectionState) {
  //**        _connectionState = connectionState;
  //**    }

//**    /**
  //**     * @return the postfix (property/Feldname) of the record.
  //**    */
  //**    public final String getPostfix() {
  //**        return _postfix;
  //**    }

    /**
     * @return child objects
     */
    public Object[] getChildren(){
    	Object[] ret = new Object[4];
		ret[0] = new ADLResource(ADLResource.FOREGROUND_COLOR, _clr);
		ret[1] = new ADLResource(ADLResource.BACKGROUND_COLOR, _bclr);
		ret[2] = new ADLResource(ADLResource.CHANNEL, _chan);
		ret[3] = new ADLResource(ADLResource.CONNECTION_STATE, _connectionState);
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
     * @return control channel
     */
    public String getChan(){
    	return _chan;
    }

	/**
	 * @param _isBackColorDefined the _isBackColorDefined to set
	 */
	public void set_isBackColorDefined(boolean _isBackColorDefined) {
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
	public void set_isForeColorDefined(boolean _isForeColorDefined) {
		this._isForeColorDefined = _isForeColorDefined;
	}

	/**
	 * @return the _isForeColorDefined
	 */
	public boolean isForeColorDefined() {
		return _isForeColorDefined;
	}
}
