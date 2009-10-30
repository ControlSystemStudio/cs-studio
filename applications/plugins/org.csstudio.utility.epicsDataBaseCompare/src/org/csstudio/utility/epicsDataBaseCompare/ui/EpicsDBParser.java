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
package org.csstudio.utility.epicsDataBaseCompare.ui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 27.10.2009
 */
public class EpicsDBParser {

    public EpicsDBFile parseFile(String file) {
        EpicsDBFile epicsDBFile = new EpicsDBFile(file);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String zeile;
            EpicsRecord epicsRecord = null;
            while ((zeile = br.readLine()) != null) {
                zeile = zeile.trim();
                if (zeile.startsWith("record")) {
                    String[] split = zeile.split("\"");
                    if (split.length > 1) {
                        epicsRecord = new EpicsRecord(split[1]);
                    }
                } else if (zeile.startsWith("}")) {
                    if (epicsRecord != null && !epicsRecord.isEmpty()) {
                        epicsDBFile.add(epicsRecord);
                    }
                } else if (zeile.startsWith("field(INP,")) {
                    String[] split = zeile.split("\"");
                    if (split.length > 1) {
                        epicsRecord.setInp(split[1]);
                    }
                } else if (zeile.startsWith("field(OUT")) {
                    String[] split = zeile.split("\"");
                    if (split.length > 1) {
                        epicsRecord.setOut(split[1]);
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return epicsDBFile;
    }

}
