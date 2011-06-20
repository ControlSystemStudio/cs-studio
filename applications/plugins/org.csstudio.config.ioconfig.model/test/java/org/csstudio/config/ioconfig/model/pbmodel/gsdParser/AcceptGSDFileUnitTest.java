/**
 * 
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @author hrickens
 */
// CHECKSTYLE:OFF
public class AcceptGSDFileUnitTest {
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
//        GSDTestFiles.B756_P33.getFileAsString();
    }
    
    @Test
    public void GSDTestFile_3KStrND() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles._3KStrND.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
    }
    
    @Test
    public void GSDTestFile_ABB_0812() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.ABB_0812.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
    }
    
    @Test
    public void GSDTestFile_B756_P33() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.B756_P33.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
    }

    @Test
    public void GSDTestFile_BIMF5861() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.BIMF5861.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
    }
    
    @Test
    public void GSDTestFile_DESY_MSyS_V10() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.DESY_MSyS_V10.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
    }
    
    @Test
    public void GSDTestFile_DESY_MSyS_V11() throws Exception {
        GsdFileParser gsdFileParser = new GsdFileParser();
        AbstractGsdPropertyModel model = gsdFileParser.parse(GSDTestFiles.DESY_MSyS_V11.getFileAsGSDFileDBO());
        Assert.assertNotNull(model);
    }
    
    @Test
    public void GSDTestFile_gm_04b5() throws Exception {
        GsdFileParser gsdFileParser = new GsdFileParser();
        AbstractGsdPropertyModel model = gsdFileParser.parse(GSDTestFiles.gm_04b5.getFileAsGSDFileDBO());
        Assert.assertNotNull(model);
    }
    
    @Test
    public void GSDTestFile_PF009A8() throws Exception {
        GsdFileParser gsdFileParser = new GsdFileParser();
        AbstractGsdPropertyModel model = gsdFileParser.parse(GSDTestFiles.PF009A8.getFileAsGSDFileDBO());
        Assert.assertNotNull(model);
    }
    
    @Test
    public void GSDTestFile_siem8045() throws Exception {
        GsdFileParser gsdFileParser = new GsdFileParser();
        AbstractGsdPropertyModel model = gsdFileParser.parse(GSDTestFiles.siem8045.getFileAsGSDFileDBO());
        Assert.assertNotNull(model);
    }
    
    @Test
    public void GSDTestFile_SOFTB203() throws Exception {
        GsdFileParser gsdFileParser = new GsdFileParser();
        AbstractGsdPropertyModel model = gsdFileParser.parse(GSDTestFiles.SOFTB203.getFileAsGSDFileDBO());
        Assert.assertNotNull(model);
    }
    
    @Test
    public void GSDTestFile_YP0004C2() throws Exception {
        GsdFileParser gsdFileParser = new GsdFileParser();
        AbstractGsdPropertyModel model = gsdFileParser.parse(GSDTestFiles.YP0004C2.getFileAsGSDFileDBO());
        Assert.assertNotNull(model);
    }
    
    @Test
    public void GSDTestFile_YP003051() throws Exception {
        GsdFileParser gsdFileParser = new GsdFileParser();
        AbstractGsdPropertyModel model = gsdFileParser.parse(GSDTestFiles.YP003051.getFileAsGSDFileDBO());
        Assert.assertNotNull(model);
    }
    
    @Test
    public void GSDTestFile_YP0206CA() throws Exception {
        GsdFileParser gsdFileParser = new GsdFileParser();
        AbstractGsdPropertyModel model = gsdFileParser.parse(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        Assert.assertNotNull(model);
    }

    @Test
    public void GSDTestFile_SiPart() throws Exception {
        GsdFileParser gsdFileParser = new GsdFileParser();
        AbstractGsdPropertyModel model = gsdFileParser.parse(GSDTestFiles.SiPart.getFileAsGSDFileDBO());
        Assert.assertNotNull(model);
    }
}
//CHECKSTYLE:ON
