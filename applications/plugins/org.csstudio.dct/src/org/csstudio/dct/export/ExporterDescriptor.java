package org.csstudio.dct.export;

public class ExporterDescriptor {
    private String id;
    private String pluginId;
    private String description;
    private String icon;
    private boolean standard;

    private IExporter exporter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public IExporter getExporter() {
        return exporter;
    }

    public void setExporter(IExporter exporter) {
        this.exporter = exporter;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    public boolean isStandard() {
        return standard;
    }

    @Override
    public String toString() {
        return description;
    }
}
