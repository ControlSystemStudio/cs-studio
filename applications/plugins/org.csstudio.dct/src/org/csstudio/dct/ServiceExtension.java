/**
 *
 */
package org.csstudio.dct;

public class ServiceExtension<E> {
    private String pluginId;
    private String id;
    private String name;
    private E service;
    private String iconPath;

    ServiceExtension(String pluginId, String id, String name, E service, String iconPath) {
        super();
        this.pluginId = pluginId;
        this.id = id;
        this.name = name;
        this.service = service;
        this.iconPath = iconPath;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public E getService() {
        return service;
    }

    public String getPluginId() {
        return pluginId;
    }

    public String getIconPath() {
        return iconPath;
    }

}