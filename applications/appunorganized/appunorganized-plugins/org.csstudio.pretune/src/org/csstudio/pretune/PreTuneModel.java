package org.csstudio.pretune;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Kunal Shroff
 *
 */
public class PreTuneModel {
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    private final List<String> columnHeaders;
    private final List<List<Object>> channels;
    private double scalingFactor;
    private double refStepSize;
    private String formula;

    @SuppressWarnings("unchecked")
    public PreTuneModel(String filePath) throws JsonParseException,
            JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(new File(filePath),
                Map.class);
        columnHeaders = (List<String>) map.get("column_names");
        channels = (List<List<Object>>) map.get("channels");
        if (map.containsKey("config_scaling_factor")) {
            scalingFactor = (Double) map.get("config_scaling_factor");
        } else {
            scalingFactor = 1.0;
        }
        if (map.containsKey("config_ref_step_size")) {
            refStepSize = (Double) map.get("config_ref_step_size");
        } else {
            refStepSize = 0.0;
        }
        formula = "";
    }

    public void setScalingFactor(double scalingFactor) {
        Object oldScalingFactor = this.scalingFactor;
        this.scalingFactor = scalingFactor;
        pcs.firePropertyChange("scalingFactor", oldScalingFactor, scalingFactor);
    }

    public double getScalingFactor() {
        return scalingFactor;
    }

    public void setRefStepSize(double refStepSize) {
        Object oldRefStepSize = this.refStepSize;
        this.refStepSize = refStepSize;
        pcs.firePropertyChange("refStepSize", oldRefStepSize, refStepSize);
    }

    public double getRefStepSize() {
        return refStepSize;
    }

    public List<String> getColumnHeaders() {
        return columnHeaders;
    }

    public List<List<Object>> getChannels() {
        return channels;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        Object oldFormula = this.formula;
        this.formula = formula;
        pcs.firePropertyChange("formula", oldFormula, formula);
    }

}