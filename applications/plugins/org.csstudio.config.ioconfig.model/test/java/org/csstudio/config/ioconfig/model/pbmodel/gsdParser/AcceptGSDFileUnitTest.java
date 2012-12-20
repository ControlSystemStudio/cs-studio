/**
 *
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;


import junit.framework.Assert;

import org.junit.Test;

/**
 * @author hrickens
 */
public class AcceptGSDFileUnitTest {

    @Test
    public void gSDTestFile3KStrND() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles._3KStrND.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFileABB0812() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.ABB_0812.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFileB756P33() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.B756_P33.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFileBIMF5861() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.BIMF5861.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFileDESYMSySV10() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.DESY_MSyS_V10.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFileDesyMSySV11() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.DESY_MSyS_V11.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFilegm04b5() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.gm_04b5.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFilePF009A8() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.PF009A8.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFilesiem8045() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.siem8045.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }
    @Test
    public void gSDTestFilesiem80d1() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.siem80d1.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFileSOFTB203() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.SOFTB203.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFileYP0004C2() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.YP0004C2.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFileYP003051() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.YP003051.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFileYP0206CA() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.YP0206CA.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFileSiPart() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.SiPart.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void gSDTestFileSAMS071D() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.SAMS071D.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }
}
