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
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 04.09.2007
 */
public class ADLObject extends WidgetPart {

    private static final Logger LOG = LoggerFactory.getLogger(ADLObject.class);

    /** The x-coordinate of the Object.*/
    private int _x;
    /** The y-coordinate of the Object.*/
    private int _y;
    /** The width of the Object.*/
    private int _width;
    /** The height of the Object.*/
    private int _height;

    /**
     * The default constructor.
     *
     * @param adlObject An ADLWidget that correspond a ADL Object.
     * @param parentWidgetModel The Widget that set the parameter from ADLWidget.
     * @throws WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public ADLObject(final ADLWidget adlObject, final AbstractWidgetModel parentWidgetModel) throws WrongADLFormatException {
       super(adlObject, parentWidgetModel);
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
    final void parseWidgetPart(final ADLWidget adlObject) throws WrongADLFormatException {
        assert adlObject.isType("object") : Messages.ADLObject_AssertError_Begin + adlObject.getType() + Messages.ADLObject_AssertError_End + "\r\n" + adlObject; //$NON-NLS-1$

        for (final FileLine fileLine : adlObject.getBody()) {
            final String parameter = fileLine.getLine();
            if(parameter.trim().startsWith("//")){ //$NON-NLS-1$
                continue;
            }
            final String[] row = parameter.split("="); //$NON-NLS-1$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.ADLObject_WrongADLFormatException_Begin+parameter+Messages.ADLObject_WrongADLFormatException_End);
            }
            row[1] = row[1].replaceAll("\"", "").trim();
            if(row[0].trim().toLowerCase().equals("x")){ //$NON-NLS-1$
                if(row[1].startsWith("$")){
                    //TODO: ADLObject --> Dynamic x coordinate
                    _x=0;
                }else{
                    _x=Integer.parseInt(row[1]);
                }
            }else if(row[0].trim().toLowerCase().equals("y")){ //$NON-NLS-1$
                if(row[1].startsWith("$")){
                    //TODO: ADLObject --> Dynamic y coordinate
                    _y=0;
                }else {
                    _y=Integer.parseInt(row[1]);
                }
            }else if(row[0].trim().toLowerCase().equals("width")){ //$NON-NLS-1$
                _width=Integer.parseInt(row[1]);
            }else if(row[0].trim().toLowerCase().equals("height")){ //$NON-NLS-1$
                _height=Integer.parseInt(row[1]);
            }else if(row[0].trim().toLowerCase().equals("groupid")){ //$NON-NLS-1$
                // TODO: ADLObject->groupid
                LOG.info("Unhandled Parameter: {}",fileLine);
            }else {
                throw new WrongADLFormatException(Messages.ADLObject_WrongADLFormatException_Parameter_Begin+fileLine+Messages.ADLObject_WrongADLFormatException_Parameter_End);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void generateElements() {
            setX(_x);
            setY(_y);
            setWidth(_width);
            setHeight(_height);

    }

    /**
     * @return the x-coordinate.
     */
    public final int getX() {
        return _x;
    }
    /**
     * @return the y-coordinate.
     */
    public final int getY() {
        return _y;
    }
    /**
     * @return the width.
     */
    public final int getWidth() {
        return _width;
    }

    /**
     * @return the height.
     */
    public final int getHeight() {
        return _height;
    }

    /**
     * @param x set the x-coordinate.
     */
    public final void setX(final int x) {
        _x = x;
        _widgetModel.setX(_x);
    }

    /**
     * @param y set the y-coordinate
     */
    public final void setY(final int y) {
        _y = y;
        _widgetModel.setY(_y);
    }

    /**
     * @param width set the width-coordinate
     */
    public final void setWidth(final int width) {
        _width = width;
        _widgetModel.setWidth(_width);
    }

    /**
     * @param height set the height-coordinate.
     */
    public final void setHeight(final int height) {
        _height = height;
        _widgetModel.setHeight(_height);
    }
}
