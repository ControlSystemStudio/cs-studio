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
package org.csstudio.utility.ldap.namespacebrowser.utility;

import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ECOM_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ECON_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EREN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.FIELD_ASSIGNMENT;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.FIELD_SEPARATOR;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.FIELD_WILDCARD;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;

import org.csstudio.utility.nameSpaceBrowser.utility.Automat;
import org.csstudio.utility.nameSpaceBrowser.utility.CSSViewParameter;


/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 08.05.2007
 */
public class LDAP2Automat extends Automat {

	// State machines parameter
	private NameSpaceBrowserState _currentState = NameSpaceBrowserState.START;

	// LDAP parameter
	private String _storeName = "";
    private String _root = "";

	/* (non-Javadoc)
	 * @see org.csstudio.utility.nameSpaceBrowser.utility.Automat#event(org.csstudio.utility.nameSpaceBrowser.utility.Automat.Ereignis, java.lang.String)
	 */
	@Override
	public CSSViewParameter goDown(final String selection) {

	    final CSSViewParameter parameter = new CSSViewParameter();

	    final String[] selectionFields = selection.split(FIELD_ASSIGNMENT);
	    final String levelIdentifier = selectionFields[0];

	    if (_storeName.contains(levelIdentifier)) { // navigation to the same or a higher level
	        // Delete everything from _storeName from the beginning of the string to current level
	        final String regexp = "(.*)" + levelIdentifier + FIELD_ASSIGNMENT + "[^,]*,";
	        _storeName = _storeName.replaceFirst(regexp, "");
	    }


		if(selection.startsWith(OU_FIELD_NAME + FIELD_ASSIGNMENT)){
		    _root = selection;

		    parameter.name = _root;
			parameter.filter = EFAN_FIELD_NAME + FIELD_ASSIGNMENT + FIELD_WILDCARD;
			parameter.newCSSView = true;

			_currentState = NameSpaceBrowserState.CONTROLLER;

		} else if(selection.startsWith(EFAN_FIELD_NAME + FIELD_ASSIGNMENT)){
		    if (selection.contains(FIELD_WILDCARD)) { // [All] efans selected
		        parameter.name = _root;
		    } else {                       // <efan> selected
		        parameter.name = ECOM_FIELD_NAME + FIELD_ASSIGNMENT + "EPICS-IOC" + FIELD_SEPARATOR +
		                         selection + FIELD_SEPARATOR +
		                         _root;
		    }

			parameter.filter = ECON_FIELD_NAME + FIELD_ASSIGNMENT + FIELD_WILDCARD;
			parameter.newCSSView=true;

			_currentState=NameSpaceBrowserState.CONTROLLER;

		} else if(selection.startsWith(ECON_FIELD_NAME + FIELD_ASSIGNMENT)){
            if(selection.contains(FIELD_WILDCARD)){ // [All] econs selected
                parameter.name = _storeName;
            } else{                      // <econ> selected
                parameter.name = selection + FIELD_SEPARATOR +_storeName;
            }
			parameter.filter = EREN_FIELD_NAME + FIELD_ASSIGNMENT + FIELD_WILDCARD;
			parameter.newCSSView = false;

			_currentState = NameSpaceBrowserState.RECORD;

		}
		_storeName = parameter.name;

		return parameter;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.utility.nameSpaceBrowser.utility.Automat#getZustand()
	 */
	@Override
	public NameSpaceBrowserState getState() {
		return _currentState;
	}

}
