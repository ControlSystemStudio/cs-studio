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
 * $Id: CheckNumFocusListener.java,v 1.1 2009/08/26 07:09:21 hrickens Exp $
 */
package org.csstudio.config.ioconfig.config.view.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.pbmodel.Ranges.Value;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;

/**
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 15.08.2007
 */
class CheckNumFocusListener implements FocusListener{

    /**
     *  The min range.
     */
    private long _min;
    /**
     *  The max range.
     */
    private long _max;
    
    /**
     * @param ranges The min/max ranges
     */
    CheckNumFocusListener(@Nonnull final Value ranges){
        _min = ranges.getMin();
        _max = ranges.getMax();
    }
    
    /** {@inheritDoc} */
    @Override
    public void focusGained(@Nullable final FocusEvent e) {/* Nothing to do*/}

    /** {@inheritDoc} */
    @Override
    public void focusLost(@Nullable final FocusEvent e) {
        if (e.widget instanceof Text) {
            final Display display = Display.getDefault();
            Text text = (Text) e.widget;
            int zahl =0;
            try{
                zahl = Integer.parseInt(text.getText());
            }catch (NumberFormatException nfe) {
                /*Ignore*/
                zahl = 0; 
            }
            if(!(_min<=zahl&&zahl<=_max)){
                text.setBackground(display.getSystemColor(SWT.COLOR_RED));
                ToolTip tt = new ToolTip(display.getActiveShell(),SWT.ICON_WARNING);
                Composite comp = text.getParent();
                Point point = new Point(text.getLocation().x+text.getBounds().width,text.getLocation().x);
                while(comp.getParent()!=null){
                    point = new Point(point.x+comp.getLocation().x,point.y+comp.getLocation().y);
                    comp=comp.getParent();
                }
                tt.setLocation(point);
                tt.setData(text);
                tt.setText("Out off range");
                tt.setMessage("Min("+_min+") oder Max("+_max+") Range Überschritten!");
                tt.setAutoHide(false);
                tt.setVisible(true);
                text.setFocus();
            }else{
                text.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
            }
        }                
    }
}
