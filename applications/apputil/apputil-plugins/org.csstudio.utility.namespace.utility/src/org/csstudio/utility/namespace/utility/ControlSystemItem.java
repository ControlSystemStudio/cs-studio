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
package org.csstudio.utility.namespace.utility;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.csstudio.csdata.ProcessVariable;

public class ControlSystemItem extends ProcessVariable {

    private static final long serialVersionUID = 7560030613315777768L;
    private final String TYPE_ID = "css:controlSystemItem"; //$NON-NLS-1$
    private final String _path;
    private boolean _redundant;


    public ControlSystemItem(final String name, final String path) {
        this(name, path, null);
    }

    /**
     * Constructor.
     * @param string
     * @param cleanList
     * @param attribute
     */
    public ControlSystemItem(String name, String path, Attribute attribute) {
        super(name);
        this._path = path;
        setRedundant(attribute);
    }

    /**
     * @param attribute
     */
    private void setRedundant(Attribute attribute) {
        _redundant = false;
        if(attribute!=null) {
            try {
                Object object = attribute.get();
                if(object instanceof String) {
                    String redu = (String) object;
                    _redundant = redu.compareToIgnoreCase("true") == 0;
                }
            } catch (NamingException e) {
                _redundant = false;
            }
        }
    }


    public String getPath() {
        return _path;
    }

    public boolean isRedundant() {
        return _redundant;
    }
}
