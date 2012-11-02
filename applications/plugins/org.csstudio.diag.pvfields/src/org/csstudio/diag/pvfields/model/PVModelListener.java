package org.csstudio.diag.pvfields.model;

import java.util.List;
import java.util.Map;

import org.csstudio.diag.pvfields.PVField;

public interface PVModelListener
{
    public void updateProperties(Map<String, String> properties);

    public void updateFields(List<PVField> fields);
}
