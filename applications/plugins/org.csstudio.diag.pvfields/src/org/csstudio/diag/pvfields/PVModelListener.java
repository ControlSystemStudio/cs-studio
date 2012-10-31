package org.csstudio.diag.pvfields;

import java.util.Map;

public interface PVModelListener
{
    public void updateProperties(Map<String, String> properties);

    public void updateFields(PVField[] fields);
}
