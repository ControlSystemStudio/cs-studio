/**
 * 
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @author hrickens
 *
 */
public class AcceptGSDFileUnitTest {
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
//        GSDTestFiles.B756_P33.getFileAsString();
    }
    
    @Test
    public void GSDTestFile_B756_P33() throws Exception {
        ParsedGsdFileModel model = GsdFileParser.parse(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        Assert.assertNotNull(model);
    }
}
