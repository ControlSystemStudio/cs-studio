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
package org.csstudio.dct.ui.editor.copyandpaste;

public final class InstanceTransfer extends AbstractElementTransfer {

    /**
     * Type name for this transfer type.
     */
    private static final String TYPENAME = "dct_instance_list"; //$NON-NLS-1$

    /**
     * Type ID for this transfer type.
     */
    private static final int TYPEID = registerType(TYPENAME);

    /**
     * The singleton instance.
     */
    private static InstanceTransfer _instance;

    /**
     * Private constructor (singleton pattern).
     *
     */
    private InstanceTransfer() {
        super(new InstanceCopyAndPasteStrategy());
    }

    /**
     * Returns the singleton instance.
     *
     * @return the singleton instance
     */
    public static InstanceTransfer getInstance() {
        if (_instance == null) {
            _instance = new InstanceTransfer();
        }
        return _instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String[] getTypeNames() {
        return new String[] { TYPENAME };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int[] getTypeIds() {
        return new int[] { TYPEID };
    }

}
