package org.csstudio.diag.pvfields;

import java.util.List;
import java.util.Map;

public interface PVModelListener
{
    public void updateProperties(Map<String, String> properties);

    public void updateFields(List<PVField> fields);
}
