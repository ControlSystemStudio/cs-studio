package de.desy.language.editor.core.measurement;

public class KeyValuePair {

    private String _key;
    private int _value;

    public KeyValuePair(String key, int value) {
        _key = key;
        _value = value;
    }

    public int getValue() {
        return _value;
    }

    public String getKey() {
        return _key;
    }

    public void inkrementValue() {
        _value++;
    }

}
