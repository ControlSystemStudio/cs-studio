/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id: Diagnose.java,v 1.2 2010/08/20 13:33:06 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.annotation.Nonnull;


/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 15.12.2009
 */
public final class Diagnose {
    
    private static StringBuilder _DIAG_STRING = new StringBuilder();
    
    private static int _COUNTER = 0;
    
    private static long _OLD_TIME;

    private static long _INIT_TIME;

    private static int _NAMED_DB_CLASS_COUNTER = 0;
    
    /**
     * Constructor.
     */
    private Diagnose() {
        // Defualt Constructor
    }
    
    public static synchronized void addNewLine(@Nonnull String line) {
        long time = new Date().getTime();
        long l = time-_OLD_TIME;
        _OLD_TIME = time;
        _DIAG_STRING.append(_COUNTER+": \t\t"+time+"\t"+l+"\t"+line+"\r\n");
        _COUNTER++;
    }

    public static void clear() {
        _DIAG_STRING = new StringBuilder();
        Date date = new Date();
        _DIAG_STRING.append("Start um "+date+"\r\n");
        _INIT_TIME = date.getTime();
        _OLD_TIME = date.getTime();
        _COUNTER = 0;
        _NAMED_DB_CLASS_COUNTER = 0;
    }

    public static void print() {
        Date date = new Date();
        try {
            File createTempFile = File.createTempFile("Diag", "DDB.log");
            createTempFile.createNewFile();
            FileWriter fw = new FileWriter(createTempFile);
            fw.write(_DIAG_STRING.toString());
            fw.flush();
            _DIAG_STRING.append(createTempFile.getAbsolutePath()+"\r\n");
        } catch (IOException e) {
            e.printStackTrace();
            _DIAG_STRING.append(e.getLocalizedMessage());
        }
        System.out.print(_DIAG_STRING.toString());
        System.out.print("NamedDBClass count: "+_NAMED_DB_CLASS_COUNTER+"\r\n");
        
        System.out.println("Print um "+date);
        System.out.println("Zeit ab Start "+(date.getTime()-_INIT_TIME));
        
    }

    @Nonnull
    public static String getString() {
        return _DIAG_STRING.toString();
    }

    @Nonnull
    public static String getCounts() {
        return ""+_COUNTER;
    }

    public static void countNamedDBClass() {
        _NAMED_DB_CLASS_COUNTER++;
    }
    
}
