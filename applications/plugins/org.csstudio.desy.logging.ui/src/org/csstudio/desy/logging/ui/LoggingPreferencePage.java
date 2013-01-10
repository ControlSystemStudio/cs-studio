/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: AlarmServiceActivator.java,v 1.2
 * 2010/04/26 09:35:22 jpenning Exp $
 */
package org.csstudio.desy.logging.ui;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.Bundle;

/**
 * Allows editing the log4j properties file. The file is retrieved from a known location, it is found in a fragment
 * of org.apache.log4j.
 * 
 * @author jpenning
 */
public class LoggingPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {
    
    private String filePath = null;
    
    public LoggingPreferencePage() {
        super(GRID);
    }
    
    @Override
    public void init(IWorkbench workbench) {
        setDescription("Logging Preferences for Log4J");
    }

    @Override
    protected void createFieldEditors() {
        makeFileInfoArea();
        makeTextArea();
    }

    private void makeFileInfoArea() {
        StringFieldEditor fileNameEditor = new StringFieldEditor("FileName", "&File", getFieldEditorParent());
        fileNameEditor.setEnabled(false, getFieldEditorParent());
        fileNameEditor.setStringValue(getFilePath());
        addField(fileNameEditor);
    }

    private void makeTextArea() {
        StringAreaEditor stringAreaEditor =
                                            new StringAreaEditor("Log4jProperties",
                                                                 "&Edit",
                                                                 getFieldEditorParent(),
                                                                 getFilePath(),
                                                                 defaultProperties);
        addField(stringAreaEditor);
    }
    
    private String getFilePath() {
        if (filePath == null) {
            // actually the log4j properties are found inside the org.apache.log4j fragment
            Bundle bundle = Platform.getBundle("org.apache.log4j");
            Path path = new Path("log4j.properties");
            URL url = FileLocator.find(bundle, path, null);
            try {
                filePath = FileLocator.toFileURL(url).getPath();
            } catch (IOException e) {
                filePath = "File not found: " + e.getMessage();
            }
        }
        return filePath;
    }
 
    private final String defaultProperties =
            "# Configuration of the root logger. Log levels are named: trace, debug, info, warn, error, fatal.\n" + 
            "log4j.rootLogger=trace, console, file\n" + 
            "\n" + 
            "# Configuration of module-related log levels - expert stuff!\n" + 
            "# Do not remove entries, change log level properly instead. \n" + 
            "log4j.logger.org.csstudio=info\n" + 
            "log4j.logger.DAL.EPICS=warn\n" + 
            "# example for module related log level\n" + 
            "#log4j.logger.org.csstudio.alarm.service=trace\n" + 
            "\n" + 
            "# Configuration of the console appender\n" + 
            "log4j.appender.console=org.apache.log4j.ConsoleAppender\n" + 
            "log4j.appender.console.Threshold=trace\n" + 
            "log4j.appender.console.layout=org.apache.log4j.PatternLayout\n" + 
            "log4j.appender.console.layout.ConversionPattern=%d{ISO8601} %-5p [%t] %c\\: %m%n\n" + 
            "\n" + 
            "# Configuration of the file appender\n" + 
            "log4j.appender.file=org.apache.log4j.RollingFileAppender\n" + 
            "log4j.appender.file.Threshold=info\n" + 
            "log4j.appender.file.File=logs/loggingdemo.log\n" + 
            "log4j.appender.file.Append=true\n" + 
            "log4j.appender.file.MaxBackupIndex=10\n" + 
            "log4j.appender.file.MaxFileSize=500KB\n" + 
            "log4j.appender.file.layout=org.apache.log4j.PatternLayout\n" + 
            "log4j.appender.file.layout.ConversionPattern=%d{ISO8601} %-5p [%t] %c\\: %m%n\n";

    
}
