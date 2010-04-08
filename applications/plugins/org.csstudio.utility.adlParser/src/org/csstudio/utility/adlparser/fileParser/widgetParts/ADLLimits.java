package org.csstudio.utility.adlparser.fileParser.widgetParts;

import org.csstudio.utility.adlparser.fileParser.ADLResource;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.internationalization.Messages;

public class ADLLimits extends WidgetPart {
	/**Source of LOPR */
	private String _loprSrc;
	/**Default value for LOPR */
	private float _loprDefault;
	/**Source of HOPR */
	private String _hoprSrc;
	/**Default value for HOPR */
	private float _hoprDefault;
	/**Source of PREC */
	private String _precSrc;
	/**Default value for PREC */
	private float _precDefault;
	
	public ADLLimits(ADLWidget widgetPart) throws WrongADLFormatException {
		super(widgetPart);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object[] getChildren() {
    	Object[] ret = new Object[6];
    	ret[0] = new ADLResource(ADLResource.LOPR_SRC, new String(_loprSrc));
    	ret[1] = new ADLResource(ADLResource.LOPR_DEFAULT, new Float(_loprDefault));
    	ret[2] = new ADLResource(ADLResource.HOPR_SRC, new String(_hoprSrc));
    	ret[3] = new ADLResource(ADLResource.HOPR_DEFAULT, new Float(_hoprDefault));
    	ret[4] = new ADLResource(ADLResource.PREC_SRC, new String(_precSrc));
    	ret[5] = new ADLResource(ADLResource.PREC_DEFAULT, new Float(_precDefault));
    	
    	return ret;
	}

	@Override
	void init() {
		_loprSrc = new String("Channel");
		_hoprSrc = new String("Channel");
		_precSrc = new String("Channel");
	}

	@Override
	void parseWidgetPart(ADLWidget adlObject) throws WrongADLFormatException {
        assert adlObject.isType("object") : Messages.ADLObject_AssertError_Begin+adlObject.getType()+Messages.ADLObject_AssertError_End+"\r\n"+adlObject; //$NON-NLS-1$
        for (FileLine fileLine : adlObject.getBody()) {
            String parameter = fileLine.getLine();
            if(parameter.trim().startsWith("//")){ //$NON-NLS-1$
                continue;
            }
            String[] row = parameter.split("=");
            if (row.length !=2){
                throw new WrongADLFormatException(Messages.ADLObject_WrongADLFormatException_Begin+parameter+Messages.ADLObject_WrongADLFormatException_End);
            }
            row[1] = row[1].replaceAll("\"", "").trim();
            System.out.println(row[0] + ", " + row[1]);
            if (row[0].trim().toLowerCase().equals("loprsrc")){
            	if (row[1].startsWith("$")){
            		//TODO ADLLimits Figure out what to do with macro in loprSrc
            		_loprSrc = "Channel";
            	}
            	else {
            		_loprSrc = row[1].toString();
            	}
            }
            if (row[0].trim().toLowerCase().equals("loprdefault")){
            	if (row[1].startsWith("$")){
            		//TODO ADLLimits Figure out what to do with macro in loprSrc
            		_loprDefault = 0;
            	}
            	else {
            		_loprDefault = Float.parseFloat(row[1]);
            	}
            }
            if (row[0].trim().toLowerCase().equals("hoprsrc")){
            	if (row[1].startsWith("$")){
            		//TODO ADLLimits Figure out what to do with macro in hoprSrc
            		_hoprSrc = "Channel";
            	}
            	else {
            		_hoprSrc = row[1].toString();
            	}
            }
            if (row[0].trim().toLowerCase().equals("hoprdefault")){
            	if (row[1].startsWith("$")){
            		//TODO ADLLimits Figure out what to do with macro in hoprSrc
            		_hoprDefault = 0;
            	}
            	else {
            		_hoprDefault = Float.parseFloat(row[1]);
            	}
            }
            if (row[0].trim().toLowerCase().equals("precsrc")){
            	if (row[1].startsWith("$")){
            		//TODO ADLLimits Figure out what to do with macro in precSrc
            		_precSrc = "Channel";
            	}
            	else {
            		_precSrc = row[1].toString();
            	}
            }
            if (row[0].trim().toLowerCase().equals("precdefault")){
            	if (row[1].startsWith("$")){
            		//TODO Figure out what to do with macro in precDefault
            		_precDefault = 0;
            	}
            	else {
            		_precDefault = Float.parseFloat(row[1]);
            	}
            }
        }
	}

}
