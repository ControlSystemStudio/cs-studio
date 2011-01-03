package org.csstudio.alarm.treeView.jface;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the URL cell validator
 * 
 * @author jpenning
 */
public class UrlCellEditorValidatorUnitTest {
    
    private static final String ENTERED_VALUE_IS_NOT_VALID = "Entered value is not valid.";
    // private static final String MALFORMED_URL = "Malformed URL!";

    @Test
    public void testUrlIsOk() throws MalformedURLException {
        UrlCellEditorValidator validator = new UrlCellEditorValidator();
        Assert.assertEquals(null, validator.isValid(new URL("file://test")));
    }
    
    @Test
    public void testStrings() {
        UrlCellEditorValidator validator = new UrlCellEditorValidator();
        Assert.assertTrue(validator.isValid(null).startsWith(ENTERED_VALUE_IS_NOT_VALID));
        Assert.assertEquals(null, validator.isValid(""));
        Assert.assertEquals(null, validator.isValid("file://bla"));
    }

    @Test
    public void testOtherThanStringOrURL() {
        UrlCellEditorValidator validator = new UrlCellEditorValidator();
        Assert.assertTrue(validator.isValid(new Object()).startsWith(ENTERED_VALUE_IS_NOT_VALID));
    }
}
