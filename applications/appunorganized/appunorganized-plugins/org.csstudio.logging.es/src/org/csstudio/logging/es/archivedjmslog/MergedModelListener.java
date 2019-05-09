package org.csstudio.logging.es.archivedjmslog;

public interface MergedModelListener<T extends LogMessage>
{
    public void onChange(MergedModel<T> model);

    public void onError(MergedModel<T> model, String error);
}
