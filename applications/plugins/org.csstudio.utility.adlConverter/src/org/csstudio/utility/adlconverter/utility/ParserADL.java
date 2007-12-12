package org.csstudio.utility.adlconverter.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.csstudio.platform.logging.CentralLogger;

/**
 * 
 * Class which provides methods for parsing ADL files into a nested Map
 * with almost exactly the same structure as original ADL text file.
 * @author Tomaz Hocevar
 * 
 * @author $Author$
 * @version $Revision$
 * @since 08.10.2007
 */
public final class ParserADL {

    /**
     * Main method of class ParserADL.<br/>
     * Reads form an adl file and creates a structure of ADLWidget.
     * @return the root Object with contain the structure of the Widget.
     */
    public static ADLWidget getNextElement(File file) {
        int lineNr=0;
        ADLWidget root = new ADLWidget(file.getAbsolutePath(),null,lineNr++);
        ADLWidget children= root;
        BufferedReader buffRead = null;
        try {        

            buffRead = new BufferedReader(new FileReader(file));
            String line;

            while((line = buffRead.readLine()) != null){
                if(line.trim().length()>0){
                    if(line.contains("{")){ //$NON-NLS-1$
                        children = new ADLWidget(line,children, lineNr++);
                    }else if (line.contains("}")){ //$NON-NLS-1$
                        children.getParent().addObject(children);
                        children = children.getParent();
                    }else{
                        children.addBody(line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            CentralLogger.getInstance().error(ParserADL.class, e);
        } catch (IOException e) {
            CentralLogger.getInstance().error(ParserADL.class, e);
        } finally{
            try {
                if(buffRead!=null){
                    buffRead.close();
                }
            } catch (IOException e) {
                CentralLogger.getInstance().error(ParserADL.class,e);
            }
        }
        return root;
    }
}
