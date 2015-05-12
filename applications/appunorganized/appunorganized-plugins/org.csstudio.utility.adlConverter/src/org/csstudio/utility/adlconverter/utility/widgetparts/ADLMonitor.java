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
package org.csstudio.utility.adlconverter.utility.widgetparts;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.DebugHelper;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 12.09.2007
 */
public class ADLMonitor extends WidgetPart{

    /**
     * The foreground Color (also Font color).
     */
    private String _clr;
    /**
     * The background Color.
     */
    private String _bclr;
    /**
     * The Channel.
     */
    private String[] _chan;
    /**
     * The Record property/Feldname.
     */
    private final String _postfix="";

    /**
     * The default constructor.
     *
     * @param monitor An ADLWidget that correspond a ADL Monitor.
     * @param parentWidgetModel The Widget that set the parameter from ADLWidget.
     * @throws WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public ADLMonitor(final ADLWidget monitor, final AbstractWidgetModel parentWidgetModel) throws WrongADLFormatException {
        super(monitor, parentWidgetModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void init() {
        /* Not to initialization*/
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
            if(row[0].trim().toLowerCase().equals("clr")){ //$NON-NLS-1$
                _clr=row[1].trim();
            }else if(row[0].trim().toLowerCase().equals("bclr")){ //$NON-NLS-1$
                _bclr=row[1].trim();
            }else if(row[0].trim().toLowerCase().equals("chan")){   // chan and rdbk means both the same. Readback channel. //$NON-NLS-1$
                DebugHelper.add(this, row[1]);
                _chan=ADLHelper.cleanString(row[1]);
                if(_chan[0].contains("[")) {
                    uninit();
                }
            }else if(row[0].trim().toLowerCase().equals("rdbk")){ //$NON-NLS-1$
                DebugHelper.add(this, row[1]);
                _chan=ADLHelper.cleanString(row[1]);
            }else {
                throw new WrongADLFormatException(Messages.ADLMonitor_WrongADLFormatException_Parameter_Begin+row[0]+Messages.ADLMonitor_WrongADLFormatException_Parameter_End+parameter);
            }
        }
    }


    /**
     * Generate all Elements from ADL Monitor Attributes.
     */
    @Override
    final void generateElements() {
        if(_clr!=null){
            _widgetModel.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, ADLHelper.getRGB(_clr));
        }
        if(_bclr!=null){
            _widgetModel.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, ADLHelper.getRGB(_bclr));
        }
        if(_chan!=null){
            /*
             * EDIT: Helge Rickens 21.11.08
             * Im alias ist der Postfix enthalten und wurde hier noch mal weiter gegeben.
             * Dadurch kam es das der Postfix doppelt gesetzt wurde.
             */
//            _postfix = ADLHelper.setChan(_widgetModel,_chan);
            ADLHelper.setChan(_widgetModel,_chan);
        }
    }

    /**
     *
     * @return the postfix (property/Feldname) of the record.
     */
    public final String getPostfix() {
        return _postfix;
    }

    /**
     *
     * @return the backgoundcolor.
     */
    public final String getBclr() {
        return _bclr;
    }

    /**
     *
     * @param bclr set the background color.
     */
    public final void setBclr(final String bclr) {
        _bclr = bclr;
    }

    /**
     *
     * @return get the foreground color.
     */
    public final String getClr() {
        return _clr;
    }

    /**
     *
     * @param clr set the foreground color.
     */
    public final void setClr(final String clr) {
        _clr = clr;
    }

    /**
     *
     * @return get the Channel.
     */
    public final String[] getChan() {
        return _chan;
    }

}

