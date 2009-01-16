package org.csstudio.display.pace.model.old;

import java.io.FileInputStream;

import org.junit.Test;

/** JUnit test of Model
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelTest
{
    @Test
    public void testModel() throws Exception
    {
        final Model model =
            new Model(new FileInputStream("configFiles/rf_admin.pace"));
    }
}
