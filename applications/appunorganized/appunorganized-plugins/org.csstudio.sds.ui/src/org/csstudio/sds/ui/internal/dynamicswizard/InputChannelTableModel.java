/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.sds.ui.internal.dynamicswizard;

import java.util.ArrayList;
import java.util.List;

public class InputChannelTableModel {
    private List<InputChannelTableRow> _rowsForInputChannels;

    private List<InputChannelTableRow> _rowsForOutputChannels;

    public InputChannelTableModel() {
        _rowsForInputChannels = new ArrayList<InputChannelTableRow>();
        _rowsForOutputChannels = new ArrayList<InputChannelTableRow>();
    }

    public void addRowForInputChannel(InputChannelTableRow row) {
        _rowsForInputChannels.add(row);
    }

    public void addRowForOutputChannel(InputChannelTableRow row) {
        _rowsForOutputChannels.add(row);
    }

    public void removeRow(InputChannelTableRow row) {
        _rowsForInputChannels.remove(row);
        _rowsForOutputChannels.remove(row);
    }

    public void clearInputChannelDescriptions() {
        for(InputChannelTableRow row : _rowsForInputChannels) {
            row.setDescription("");
            row.setDefaultValue("");
        }
    }

    public List<InputChannelTableRow> getAllRows() {
        List<InputChannelTableRow> result = new ArrayList<InputChannelTableRow>();
        result.addAll(_rowsForInputChannels);
        result.addAll(_rowsForOutputChannels);
        return result;
    }

    public void setInputChannelDescription(final int rowIndex, final String description) {
        if(rowIndex >= _rowsForInputChannels.size()) {
            int addCount  = _rowsForInputChannels.size()-rowIndex+1;
            for(int i=0;i<addCount;i++) {
                _rowsForInputChannels.add(new InputChannelTableRow(ParameterType.IN, "", ""));
            }
        }
        InputChannelTableRow row = _rowsForInputChannels.get(rowIndex);
        row.setDescription(description);
    }

    public void setInputChannelValue(final int rowIndex, final String value) {
        assert rowIndex < _rowsForInputChannels.size();
        assert value != null : "value != null";

        InputChannelTableRow row = _rowsForInputChannels.get(rowIndex);
        row.setDefaultValue(value);
    }

    public List<InputChannelTableRow> getRowsWithContent(ParameterType type) {
        List<InputChannelTableRow> result = new ArrayList<InputChannelTableRow>();

        for(InputChannelTableRow row : getAllRows()) {
            String channel = row.getChannel();
            Object value = row.getDefaultValue();
            if (row.getParameterType() == type &&
                    ((channel!=null && channel.length()>0) || value!=null)) {
                result.add(row);
            }
        }

        return result;
    }
}
