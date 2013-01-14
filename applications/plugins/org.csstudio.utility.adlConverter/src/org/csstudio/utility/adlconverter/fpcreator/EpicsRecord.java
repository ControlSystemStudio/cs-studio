/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.adlconverter.fpcreator;

import org.eclipse.core.runtime.Path;

/**
 * TODO (jhatje) : 
 * 
 * @author jhatje
 * @since 04.06.2012
 */
public class EpicsRecord {

    private String _recordName;
    private String _description = null;
    private final int _sequenceNo;
    private EpicsRecordTypesEnum _type;

    /**
     * Constructor.
     * @param sequenceNo 
     * @param string
     */
    public EpicsRecord(String recordName, int sequenceNo) {
        _recordName = recordName;
        _sequenceNo = sequenceNo;
        readType(recordName);
    }

    /**
     * Constructor.
     * @param string
     * @param description
     */
    public EpicsRecord(String recordName, String description, int sequenceNo) {
        _recordName = recordName;
        _description = description;
        _sequenceNo = sequenceNo;
        readType(recordName);
    }

    private void readType(String recordName) {
        String[] split = recordName.split("_");
        if (split.length > 1) {
            _type = EpicsRecordTypesEnum.getRecordType(split[split.length-1]);
        }
    }

    public EpicsRecordTypesEnum getType() {
        return _type;
    }

    public String getName() {
        return _recordName;
    }

    public Path getPath() {
        return _type.getPath();
    }

    public String getNameWithoutTypeSuffix() {
    	int lastIndexOf = _recordName.lastIndexOf("_");
    	String recordName = _recordName.substring(0, lastIndexOf);
		return recordName;
    }
}
