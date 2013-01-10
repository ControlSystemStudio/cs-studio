package org.csstudio.alarm.treeview.jface;

import java.net.MalformedURLException;
import java.net.URL;

import org.csstudio.alarm.treeview.jface.UrlCellEditorValidator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for the URL cell validator
 * 
 * @author jpenning
 */
public class UrlCellEditorValidatorUnitTest {
    
    private static final String ENTERED_VALUE_IS_NOT_VALID = "Entered value is not valid.";
    private static final String MALFORMED_URL = "Malformed URL!";
    
    @Test
    public void testUrlIsOk() throws MalformedURLException {
        UrlCellEditorValidator validator = new UrlCellEditorValidator();
        Assert.assertNull(validator.isValid(new URL("file://test")));
    }
    
    @Test
    public void testValidStringsWithProtocol() {
        UrlCellEditorValidator validator = new UrlCellEditorValidator();
        Assert.assertNull(validator.isValid("file://bla"));
        Assert.assertNull(validator.isValid("file:bla"));
        Assert.assertNull(validator.isValid("http://www.desy.de"));
        Assert.assertNull(validator.isValid("http:desy"));
    }
    
    @Test
    public void testValidStringsWithoutProtocol() {
        UrlCellEditorValidator validator = new UrlCellEditorValidator();
        Assert.assertNull(validator.isValid(""));
        Assert.assertNull(validator.isValid("bla"));
    }
    
    @Test
    public void testInvalidStrings() {
        UrlCellEditorValidator validator = new UrlCellEditorValidator();
        Assert.assertTrue(validator.isValid(null).startsWith(ENTERED_VALUE_IS_NOT_VALID));
        Assert.assertTrue(validator.isValid("xttp:desy").startsWith(MALFORMED_URL));
    }
    
    @Test
    public void testOtherThanStringOrURL() {
        UrlCellEditorValidator validator = new UrlCellEditorValidator();
        Assert.assertTrue(validator.isValid(new Object()).startsWith(ENTERED_VALUE_IS_NOT_VALID));
    }
}
