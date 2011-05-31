/**
 * 
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;

/**
 * @author hrickens
 *
 */
public enum GSDTestFiles {
    B756_P33("B756_P33.GSD"),
    BIMF5861("BIMF5861.GSD"),
    DESY_MSyS_V10("DESY_MSyS_V10.gsd"),
    DESY_MSyS_V11("DESY_MSyS_V11.gsd"),
    PF009A8("PF009A8.gsd"),
    SOFTB203("SOFTB203.GSD"),
    YP0004C2("YP0004C2.GSD"),
    YP003051("YP003051.gsd"),
    YP0206CA("YP0206CA.gsd"),
    SiPart("SiPart.gsd");
    
    private final String _fileName;
    private GSDFileDBO _gsdFileDBO;

    /**
     * 
     */
    private GSDTestFiles(@Nonnull String fileName) {
        _fileName = fileName;
    }
    
    @Nonnull
    public String getFileAsString() throws IOException {
        FileReader fr = new FileReader("./res-test/GSDFiles/" + _fileName);
        BufferedReader bf = new BufferedReader(fr);
        try {
            String line;
            StringBuilder fileAsString = new StringBuilder();
            while (null != (line = bf.readLine())) {
                fileAsString.append(line);
                fileAsString.append(String.format("%n"));
            }
            return fileAsString.toString();
        } finally {
            bf.close();
        }
    }
    
    @Nonnull
    public GSDFileDBO getFileAsGSDFileDBO() throws IOException {
        if(_gsdFileDBO == null) {
            _gsdFileDBO = new GSDFileDBO(_fileName, getFileAsString());
        }
        return _gsdFileDBO;
    }
}
