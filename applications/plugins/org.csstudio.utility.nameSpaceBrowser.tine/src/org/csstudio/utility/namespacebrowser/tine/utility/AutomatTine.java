/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.namespacebrowser.tine.utility;

import org.csstudio.utility.nameSpaceBrowser.utility.Automat;
import org.csstudio.utility.nameSpaceBrowser.utility.CSSViewParameter;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 08.05.2007
 */
public class AutomatTine extends Automat {

	// State machines parameter
	private NameSpaceBrowserState _currentState = NameSpaceBrowserState.START;
	/* (non-Javadoc)
	 * @see org.csstudio.utility.nameSpaceBrowser.utility.Automat#event(org.csstudio.utility.nameSpaceBrowser.utility.Automat.Ereignis, java.lang.String)
	 */
	// @Override
	public CSSViewParameter goDown(final String select) {
        final CSSViewParameter parameter = new CSSViewParameter();
        parameter.name=select;
        parameter.fixFirst="";
        if(_currentState==NameSpaceBrowserState.START){
        	parameter.filter=select+",";
        	parameter.fixFirst="DEFAULT";
        }else{
        	parameter.filter=select;
        }

        if(select.split(",").length==2){
        	parameter.fixFirst="ALL";
        }
		if(select.split(",").length>4){
			_currentState = NameSpaceBrowserState.RECORD;
			parameter.newCSSView=false;
		}else {
			_currentState = NameSpaceBrowserState.TOP;
			parameter.newCSSView=true;
		}
        System.out.println("Zustand: "+select.split(",").length);
        return parameter;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.utility.nameSpaceBrowser.utility.Automat#getZustand()
	 */
	// @Override
	public NameSpaceBrowserState getState() {
		return _currentState;
	}

}
