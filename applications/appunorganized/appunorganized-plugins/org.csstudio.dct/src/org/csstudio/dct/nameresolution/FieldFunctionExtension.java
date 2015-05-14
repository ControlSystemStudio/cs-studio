package org.csstudio.dct.nameresolution;


public class FieldFunctionExtension {
    private IFieldFunction function;
    private String name;
    private String description;
    private String signature;

    public IFieldFunction getFunction() {
        return function;
    }
    public void setFunction(IFieldFunction function) {
        this.function = function;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }



}
