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
 * $Id$
 */
package org.csstudio.config.ioconfig.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 15.12.2009
 */
public class Diagnose {
    
    private static StringBuilder _diagString = new StringBuilder();
    
    private static int _counter = 0;
    
    private static long _oldTime;

    private static long _initTime;

    private static int _namedDBClassCounter = 0;
    
    synchronized public static void addNewLine(String line) {
//    public static void addNewLine(String line) {
        long time = new Date().getTime();
        long l = time-_oldTime;
        _oldTime = time;
        _diagString.append(_counter+": \t\t"+time+"\t"+l+"\t"+line+"\r\n");
        _counter++;
    }

    public static void clear() {
        _diagString = new StringBuilder();
        Date date = new Date();
        _diagString.append("Start um "+date+"\r\n");
        _initTime = date.getTime();
        _oldTime = date.getTime();
        _counter = 0;
        _namedDBClassCounter = 0;
    }

    public static void print() {
        Date date = new Date();
        try {
            File createTempFile = File.createTempFile("Diag", "DDB.log");
            createTempFile.createNewFile();
            FileWriter fw = new FileWriter(createTempFile);
            fw.write(_diagString.toString());
            fw.flush();
            _diagString.append(createTempFile.getAbsolutePath()+"\r\n");
        } catch (IOException e) {
            e.printStackTrace();
            _diagString.append(e.getLocalizedMessage());
        }
        System.out.print(_diagString.toString());
        System.out.print("NamedDBClass count: "+_namedDBClassCounter+"\r\n");
        
        System.out.println("Print um "+date);
        System.out.println("Zeit ab Start "+(date.getTime()-_initTime));
        
    }

    public static String getString() {
        return _diagString.toString();
    }

    public static String getCounts() {
        return ""+_counter;
    }

    public static void countNamedDBClass() {
        _namedDBClassCounter ++;
    }
    
}
