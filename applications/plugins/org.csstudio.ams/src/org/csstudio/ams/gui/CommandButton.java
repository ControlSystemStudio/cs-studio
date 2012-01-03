
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.ams.gui;

import java.io.InputStream;


import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.Log;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class CommandButton extends Button {
    
    public static final int NEW = 0;
    public static final int SAVE = 1;
    public static final int DELETE = 2;
    public static final int BACK = 3;
    public static final int RIGHT = 4;
    public static final int LEFT = 5;
    public static final int UP = 6;
    public static final int DOWN = 7;
    public static final int TOP = 8;
    public static final int BOTTOM = 9;
    public static final int CHECKED = 10;
    public static final int UNCHECKED = 11;
    public static final int CLEAR = 12;
    public static final int REFRESH = 13;
    public static final int IDENTIFY = 14;
    
    

    private static String[] paths = new String[] {
    	"document_new.png", "disk_blue.png", "delete2.png",
        "element_previous.png", "navigate_right.png", "navigate_left.png",
        "navigate_up.png", "navigate_down.png", "navigate_up2.png", "navigate_down2.png",
        "checked.gif","unchecked.gif","document_plain.png","refresh.png","identify.png"
    };
    
    private static Image[] icons = new Image[paths.length];

    private int type = 0;

    /**
     * Creates a new CommandButton object.
     *
     * @param parent DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public CommandButton(Composite parent, int type) {
    	super(parent, SWT.PUSH | SWT.BORDER);

    	this.type = type;
    	setImage(getImage(type));
    }
    
    public int getType() {
    	return type;
    }
    
    @Override
    protected void checkSubclass() {
        // Nothing to do here?
    }    


    public synchronized static Image getImage(int id)
    {
      if(icons.length <= id || id < 0)
        return null;

      if(icons[id] != null)
        return icons[id];

      String res = "/icons/" + paths[id];

      try
      {
        InputStream sourceStream = AmsActivator.class.getResourceAsStream(res);
        ImageData source = new ImageData(sourceStream);
        source.transparentPixel = 0;
        ImageData mask = source.getTransparencyMask();
        icons[id] = new Image(null, source, mask);

        return icons[id];
      }
      catch(Exception ex)
      {
    	  Log.log(Log.FATAL, ex);
      }
      return null;
    } 
}
