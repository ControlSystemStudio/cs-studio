package org.csstudio.pretune;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.csstudio.logbook.AttachmentBuilder;
import org.csstudio.logbook.LogEntryBuilder;
import org.eclipse.core.runtime.IAdapterFactory;
import org.diirt.util.array.ListNumber;
import org.diirt.vtype.VTable;
/**
 *
 * @author Kunal Shroff
 *
 */
public class AdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {

    if (adapterType != LogEntryBuilder.class)
            return null;
    if (adaptableObject instanceof VTable) {
        VTable value = (VTable) adaptableObject;
        // Write to file
        try {
        final File valueTableFile = File.createTempFile("pretune_setpoints_",".json");
        valueTableFile.deleteOnExit();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> columnNames = new ArrayList<String>(value.getColumnCount());
        List<List<Object>> channels = new ArrayList<List<Object>>(value.getRowCount());
        for (int i = 0; i < value.getRowCount(); i++) {
            channels.add(i, new ArrayList<Object>(value.getColumnCount()));
        }

        for (int columnIndex = 0; columnIndex < value.getColumnCount(); columnIndex++) {
            String columnName = value.getColumnName(columnIndex);
            switch (columnName) {
            case PreTuneEditor.SetPointPVLabel:
                columnName = PreTuneEditor.SetPointPVLabel+"_Value";
                break;
            case PreTuneEditor.ReadbackPointPVLabel:
                columnName = PreTuneEditor.ReadbackPointPVLabel+"_Value";
                break;
            case PreTuneEditor.SetPointPVLabel+"_name":
                columnName = PreTuneEditor.SetPointPVLabel;
                break;
            case PreTuneEditor.ReadbackPointPVLabel+"_name":
                columnName = PreTuneEditor.ReadbackPointPVLabel;
                break;
            default:
                break;
            }

            columnNames.add(columnName);
            Object data = value.getColumnData(columnIndex);
            if (data instanceof List){
            for (int rowIndex = 0; rowIndex < value.getRowCount(); rowIndex++) {
                channels.get(rowIndex).add(columnIndex, ((List) data).get(rowIndex));
            }
            }else if (data instanceof ListNumber){
            for (int rowIndex = 0; rowIndex < value.getRowCount(); rowIndex++) {
                channels.get(rowIndex).add(columnIndex, ((ListNumber) data).getDouble(rowIndex));
            }
            }
        }
        map.put("column_names", columnNames);
        map.put("channels", channels);
        mapper.writeValue(valueTableFile, map);
        return LogEntryBuilder.withText("logging via the pretune application.").attach(AttachmentBuilder.attachment(valueTableFile.getName())
            .inputStream(new FileInputStream(valueTableFile.getPath())));
        } catch (Exception ex) {
        ex.printStackTrace();
        }
    }
    return null;
    }

    @Override
    public Class[] getAdapterList() {
    return new Class[] { LogEntryBuilder.class };
    }

}
