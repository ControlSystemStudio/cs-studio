/**
 *
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;


import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hrickens
 */
// CHECKSTYLE:OFF
@Ignore
public class AcceptGSDFileUnitTest {

    @Test
    public void GSDTestFile_3KStrND() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles._3KStrND.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void GSDTestFile_ABB_0812() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.ABB_0812.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void GSDTestFile_B756_P33() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.B756_P33.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void GSDTestFile_BIMF5861() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.BIMF5861.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void GSDTestFile_DESY_MSyS_V10() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.DESY_MSyS_V10.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void GSDTestFile_DESY_MSyS_V11() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.DESY_MSyS_V11.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void GSDTestFile_gm_04b5() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.gm_04b5.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void GSDTestFile_PF009A8() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.PF009A8.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void GSDTestFile_siem8045() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.siem8045.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void GSDTestFile_SOFTB203() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.SOFTB203.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void GSDTestFile_YP0004C2() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.YP0004C2.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void GSDTestFile_YP003051() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.YP003051.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void GSDTestFile_YP0206CA() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.YP0206CA.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }

    @Test
    public void GSDTestFile_SiPart() throws Exception {
        AbstractGsdPropertyModel model = GSDTestFiles.SiPart.getFileAsGSDFileDBO().getParsedGsdFileModel();
        Assert.assertNotNull(model);
        model = null;
    }
}
//CHECKSTYLE:ON
