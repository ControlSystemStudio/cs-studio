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
public class ParserADL {
    /**
     * The File to pars.
     */
	private File _file;

	/**
	 * The Root Object of ADLWidget.
	 */
    private ADLWidget _root;
    
    /**
     * The actual line number. 
     */
    private int _lineNr;

	/**
	 * Constructor, sets filename of a file to parse.
	 * @param file the file to Parse.
	 */
	public ParserADL(final File file) {
		_file = file;
	}
	
    /**
     * Main method of class ParserADL.<br/>
     * Reads form an adl file and creates a structure of ADLWidget.
     * @return the root Object with contain the structure of the Widget.
     */
    public final ADLWidget getNextElement() {
        if(_root==null){
            _root  = new ADLWidget(_file.getAbsolutePath(),null,_lineNr++);
            ADLWidget children= _root; 
            try {        

                BufferedReader buffRead = new BufferedReader(new FileReader(_file));
                String line;

                while((line = buffRead.readLine()) != null){
                    if(line.trim().length()>0){
                        if(line.contains("{")){ //$NON-NLS-1$
                            children = new ADLWidget(line,children, _lineNr++);
                        }else if (line.contains("}")){ //$NON-NLS-1$
                            children.getParent().addObject(children);
                            children = children.getParent();
                        }else{
                            children.addBody(line);
                        }
                    }
                }
                return _root;
            } catch (FileNotFoundException e) {
                CentralLogger.getInstance().error(this, e);
            } catch (IOException e) {
                CentralLogger.getInstance().error(this, e);
            }
            return null;
        }
        return _root;
    }
}
