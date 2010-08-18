package org.remotercp.common.versionservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;

public class VersionInformation {

    private String _date = "invalid";
    private String _versionId = "invalid";

    public VersionInformation() throws IOException {
        File productFile = new File(Platform.getInstallLocation().getURL()
                .getPath()
                + ".eclipseproduct");
        FileReader reader = new FileReader(productFile);
        BufferedReader bufferReader = new BufferedReader(reader);
        String line = null;
        int i = 0;
        while ((line = bufferReader.readLine()) != null) {
            if (i == 1) {
                try {
                _date = line.split("#")[1];
                } catch (Exception e) {
                    CentralLogger.getInstance().warn(
                            this,
                            "Date does not exist in file "
                            + e.getMessage());
                }
            }
            if (line.contains("version")) {
                try {
                    _versionId = line.split("=")[1];
                } catch (Exception e) {
                    CentralLogger.getInstance().warn(
                            this,
                            "Version id does not exist in file "
                                    + e.getMessage());
                }
            }
            i++;
        }
    }

    public String getDate() {
        return _date;
    }

    public String getVersionId() {
        return _versionId;
    }
}
