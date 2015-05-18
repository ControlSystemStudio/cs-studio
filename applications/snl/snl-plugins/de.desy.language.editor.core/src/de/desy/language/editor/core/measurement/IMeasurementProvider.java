package de.desy.language.editor.core.measurement;


public interface IMeasurementProvider {

    String getRessourceIdentifier();
    KeyValuePair[] getMeasuredData();

    void addUpdateListener(IUpdateListener listener);

}
