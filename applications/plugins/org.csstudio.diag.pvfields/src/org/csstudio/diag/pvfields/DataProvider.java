package org.csstudio.diag.pvfields;

public interface DataProvider
{
    public void run(String name, PVModelListener pvModelListener) throws Exception;
}
