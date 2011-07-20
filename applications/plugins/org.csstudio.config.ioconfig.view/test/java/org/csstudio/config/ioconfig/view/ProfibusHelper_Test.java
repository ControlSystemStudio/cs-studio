package org.csstudio.config.ioconfig.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.config.ioconfig.config.view.helper.ProfibusHelper;
import org.csstudio.config.ioconfig.model.pbmodel.Ranges.Value;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;

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
 * $Id: ProfibusHelper_Test.java,v 1.2 2010/07/06 12:51:22 bknerr Exp $
 */

/**
 * @author hrickens
 * @author $Author: bknerr $
 * @version $Revision: 1.2 $
 * @since 12.12.2008
 */
// CHECKSTYLE:OFF
public class ProfibusHelper_Test {


    @Test
    public void testGetTextField1(){
        final Composite composite = new Composite(new Shell(),SWT.NULL);

        Text textField = ProfibusHelper.getTextField(composite, null);

        assertNotNull(textField);
        assertFalse(textField.getEditable());
        assertNotNull(textField.getText());
        assertNull(textField.getData());

        textField = ProfibusHelper.getTextField(composite, "value");
        assertNotNull(textField);
        assertFalse(textField.getEditable());
        assertEquals(textField.getText(), "value");
        assertEquals(textField.getData(), "value");
        assertEquals(textField.getText(), textField.getData());

    }

    @Test
    public void testGetTextField2(){
        final Composite composite = new Composite(new Shell(),SWT.NULL);

        Text textField = ProfibusHelper.getTextField(composite,false, null, null, -1);

        assertNotNull(textField);
        assertFalse(textField.getEditable());
        assertNotNull(textField.getText());
        assertNull(textField.getData());
        assertTrue(textField.getListeners(SWT.Verify).length<1);

        textField = ProfibusHelper.getTextField(composite,false, "value", new Value(0,100,50), ProfibusHelper.VL_TYP_U08);
        assertNotNull(textField);
        assertFalse(textField.getEditable());
        assertEquals(textField.getText(), "value");
        assertEquals(textField.getData(), "value");
        assertEquals(textField.getText(), textField.getData());
        assertTrue(textField.getListeners(SWT.Verify).length<1);

        textField = ProfibusHelper.getTextField(composite,true, "value", new Value(0,100,50), ProfibusHelper.VL_TYP_U08);
        assertNotNull(textField);
        assertTrue(textField.getEditable());
        assertEquals(textField.getText(), "value");
        assertEquals(textField.getData(), "value");
        assertEquals(textField.getText(), textField.getData());
        assertTrue(textField.getListeners(SWT.Verify).length>0);
    }
}
//CHECKSTYLE:ON
