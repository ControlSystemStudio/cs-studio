/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.epicsDataBaseCompare;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.utility.epicsDataBaseCompare.ui.EpicsDBFile;
import org.csstudio.utility.epicsDataBaseCompare.ui.EpicsRecord;
import org.csstudio.utility.epicsDataBaseCompare.ui.Field;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 04.10.2011
 */
public class EpicsDBValidator {

    private final Set<EpicsRecord> _invalidRecors = new HashSet<EpicsRecord>();

    public void validate(@Nonnull final EpicsDBFile parseFile) {
        final Collection<EpicsRecord> values = parseFile.getRecords().values();
        _invalidRecors.clear();
        final StringBuilder valiText = new StringBuilder();
        valiText.append("Validate File: ").append(parseFile.getFileName()).append("\n\n");
        for (final EpicsRecord epicsRecord : values) {
            final String recordType = epicsRecord.getRecordType();
            biBoCheckZnamOnam(valiText, epicsRecord, recordType);
            inputCheck(valiText, epicsRecord, recordType);
            outputCheck(valiText, epicsRecord, recordType);
        }
        if(_invalidRecors.isEmpty()) {
            valiText.append("nothing invalid");
        }
        MessageDialog.openInformation(null, parseFile.getFileName(), valiText.toString());
    }

    /**
     * @param valiText
     * @param epicsRecord
     * @param recordType
     */
    private void inputCheck(@Nonnull final StringBuilder valiText, @Nonnull final EpicsRecord epicsRecord, @Nonnull final String recordType) {
        if(recordType.endsWith("i")) {
            final Field dtyp = epicsRecord.getField("DTYP");
            if(dtyp!=null&&!dtyp.getValue().isEmpty()) {
                final Field inp = epicsRecord.getField("INP");
                if(inp==null||inp.getValue().isEmpty()) {
                    valiText.append(epicsRecord.getRecordName()).append("\n");
                    valiText.append("\tINP = ").append("\n");
                    _invalidRecors.add(epicsRecord);
                }
            }
        }
    }

    /**
     * @param valiText
     * @param epicsRecord
     * @param recordType
     */
    private void outputCheck(@Nonnull final StringBuilder valiText, @Nonnull final EpicsRecord epicsRecord, @Nonnull final String recordType) {
        if(recordType.endsWith("o")) {
            final Field dtyp = epicsRecord.getField("DTYP");
            if(dtyp!=null&&!dtyp.getValue().isEmpty()) {
                final Field inp = epicsRecord.getField("OUT");
                if(inp==null||inp.getValue().isEmpty()) {
                    valiText.append(epicsRecord.getRecordName()).append("\n");
                    valiText.append("\tOUT = ").append("\n");
                    _invalidRecors.add(epicsRecord);
                }
            }
        }
    }

    /**
     * @param valiText
     * @param epicsRecord
     * @param recordType
     */
    private void biBoCheckZnamOnam(@Nonnull final StringBuilder valiText,
                                   @Nonnull final EpicsRecord epicsRecord,
                                   @Nonnull final String recordType) {
        if("bi".equals(recordType)||"bo".equals(recordType)) {
            final Field znam = epicsRecord.getField("ZNAM");
            final Field onam = epicsRecord.getField("ONAM");
            if(znam==null||onam==null) {
                valiText.append(epicsRecord.getRecordName()).append("\n");
                String znamValue = "";
                String onamValue = "";
                if(znam!=null) {
                    znamValue = znam.getValue();
                }
                if(onam!=null) {
                    onamValue = onam.getValue();
                }

                valiText.append("\t").append("ZNAM = ").append(znamValue).append("\n");
                valiText.append("\t").append("ONAM = ").append(onamValue).append("\n");
                _invalidRecors.add(epicsRecord);
            }
        }
    }
}
