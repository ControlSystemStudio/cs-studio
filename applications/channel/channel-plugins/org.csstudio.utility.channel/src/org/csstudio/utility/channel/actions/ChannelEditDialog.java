/**
 * 
 */
package org.csstudio.utility.channel.actions;

import static gov.bnl.channelfinder.api.Channel.Builder.channel;
import static gov.bnl.channelfinder.api.Property.Builder.property;
import static gov.bnl.channelfinder.api.Tag.Builder.tag;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.Channel.Builder;
import gov.bnl.channelfinder.api.Property;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.ResourceManager;

/**
 * A Dialog to edit a channel
 * This dialog allows you to
 * 1. add/remove tags
 * 2. add/remove/modify properties
 * 
 * You can also create new properties and tags
 * 
 * @author Kunal Shroff
 *
 */
public class ChannelEditDialog extends TitleAreaDialog {

    private ChannelModel channelModel;
    
    private Label text;
    private Table table;
    private ListViewer listViewer;
    private TableViewer tableViewer;
    
    private Button btnRemoveTag;
    private Button btnAddTag;

    private Button btnAddProperty;
    private Button btnRemoveProperty;
    
    private Collection<String> allTags;
    private Collection<String> allProperties;

    private Combo comboTags;
    private Combo comboProperties;

    
    public ChannelEditDialog(Shell parentShell, Channel channel, Collection<String> allTags, Collection<String> allProperties) {
        super(parentShell);
        setShellStyle(SWT.RESIZE);
        this.channelModel = new ChannelModel(channel);
        this.allTags = allTags;
        this.allProperties = allProperties;
    }

    public Channel getChannel() {
        return channelModel.getChannel();
    }

    /**
     * Creates the dialog's contents
     * 
     * @param parent
     *            the parent composite
     * @return Control
     */
    protected Control createContents(Composite parent) {
            Control contents = super.createContents(parent);

            // Set the title
            super.setTitle(Messages.channelEditDialogTitle);
            
            // Set the message
            super.setMessage(Messages.channelEditDialogMessage, IMessageProvider.INFORMATION);

            // Set the image
            // if (image != null) setTitleImage(image);

            return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) composite.getLayout();
        
        Composite composite_1 = new Composite(composite, SWT.NONE);
        composite_1.setLayout(new GridLayout(3, false));
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        
        Label lblNewLabel = new Label(composite_1, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNewLabel.setText("Channel Name:");
        
        text = new Label(composite_1, SWT.BORDER | SWT.SHADOW_NONE);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        text.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
        new Label(composite_1, SWT.NONE);
        
        Label lblNewLabel_1 = new Label(composite_1, SWT.NONE);
        lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNewLabel_1.setText("Tags:");
        
        comboTags = new Combo(composite_1, SWT.NONE);
        comboTags.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        btnAddTag = new Button(composite_1, SWT.NONE);
        btnAddTag.setImage(ResourceManager.getPluginImage("org.csstudio.utility.channel", "icons/add_tag.png"));
        btnAddTag.setToolTipText("Add Tag");
        btnAddTag.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                channelModel.addTag(comboTags.getItem(comboTags.getSelectionIndex()));
            }
        });
        
        new Label(composite_1, SWT.NONE);
        
        listViewer = new ListViewer(composite_1, SWT.BORDER | SWT.V_SCROLL);
        List list = listViewer.getList();
        list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        listViewer.setContentProvider(new ArrayContentProvider());
        
        btnRemoveTag = new Button(composite_1, SWT.NONE);
        btnRemoveTag.setImage(ResourceManager.getPluginImage("org.csstudio.utility.channel", "icons/remove_tag.png"));
        btnRemoveTag.setToolTipText("Remove Tag");
        btnRemoveTag.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        btnRemoveTag.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                channelModel.removeTags(Arrays.asList(AdapterUtil.convert(listViewer.getSelection(), String.class)));
            }
        });
        
        Label lblNewLabel_2 = new Label(composite_1, SWT.NONE);
        lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNewLabel_2.setText("Properties:");
        
        comboProperties = new Combo(composite_1, SWT.NONE);
        comboProperties.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        btnAddProperty = new Button(composite_1, SWT.NONE);
        btnAddProperty.setImage(ResourceManager.getPluginImage("org.csstudio.utility.channel", "icons/add_properties.png"));
        btnAddProperty.setToolTipText("Add Property");
        btnAddProperty.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                channelModel.addProperty(comboProperties.getItem(comboProperties.getSelectionIndex()),"");
            }
        });
        
        new Label(composite_1, SWT.NONE);
        
        Composite tableComposite = new Composite(composite_1, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableComposite.setLayout(tableColumnLayout);
        
        tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
        
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        
        TableViewerColumn colName = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tableColumn = colName.getColumn();
        tableColumn.setWidth(100);
        colName.getColumn().setText("Name");
        colName.setLabelProvider(new ColumnLabelProvider() {
          @Override
          public String getText(Object element) {
            Entry<String, String> p = (Entry<String, String>) element;
            return p.getKey();
          }
        });
        tableColumnLayout.setColumnData(colName.getColumn(), new ColumnWeightData(50, 100, true));
        
        TableViewerColumn colValue = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tableColumn_1 = colValue.getColumn();
        tableColumn_1.setWidth(100);
        colValue.getColumn().setText("Value");
        colValue.setLabelProvider(new ColumnLabelProvider() {
          @Override
          public String getText(Object element) {
              @SuppressWarnings("unchecked")
            Entry<String, String> p = (Entry<String, String>) element;
              return p.getValue();
          }
        });
        colValue.setEditingSupport(new EditingSupport(tableViewer) {
            
            @Override
            protected void setValue(Object element, Object value) {
                Entry<String, String> p = (Entry<String, String>) element;
                channelModel.addProperty(p.getKey(), value.toString());
            }
            
            @Override
            protected Object getValue(Object element) {
                Entry<String, String> p = (Entry<String, String>) element;
                return p.getValue().toString();
            }
            
            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(tableViewer.getTable());
            }
            
            @Override
            protected boolean canEdit(Object element) {
                return true;
            }
        });
        
        tableColumnLayout.setColumnData(colValue.getColumn(), new ColumnWeightData(50, 100, true));
        
        btnRemoveProperty = new Button(composite_1, SWT.NONE);
        btnRemoveProperty.setImage(ResourceManager.getPluginImage("org.csstudio.utility.channel", "icons/remove_properties.png"));
        btnRemoveProperty.setToolTipText("Remove Property");
        btnRemoveProperty.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        btnRemoveProperty.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected(SelectionEvent e) {
                ISelection selection = tableViewer.getSelection();
                if (selection instanceof IStructuredSelection) {
                    IStructuredSelection strucSelection = (IStructuredSelection) selection;
                    Collection<String> propertyName = (Collection<String>) strucSelection.toList().stream().map((s)->{
                        return ((Entry<String, String>)s).getKey();
                    }).collect(Collectors.toList());
                    channelModel.removeProperty(propertyName);
                }                
            }
        });
        
        // register listeners to the model
        channelModel.addPropertyChangeListener("tags", (event) -> {
            listViewer.setInput(channelModel.getTags());
        });
        channelModel.addPropertyChangeListener("properties", (event) -> {
            tableViewer.setInput(channelModel.getProperties().entrySet());
        });
        
        init();
        return parent;
    }

    private synchronized void init() {
        text.setText(channelModel.getName());
        listViewer.setInput(channelModel.getTags());
        tableViewer.setInput(channelModel.getProperties().entrySet());
        comboTags.setItems(allTags.toArray(new String[allTags.size()]));
        comboProperties.setItems(allProperties.toArray(new String[allProperties.size()]));
    }

    
    /**
     * 
     * @author Kunal Shroff
     *
     */
    private static class ChannelModel {
        private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            changeSupport.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            changeSupport.removePropertyChangeListener(listener);
        }

        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            changeSupport.addPropertyChangeListener(propertyName, listener);
        }

        public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            changeSupport.removePropertyChangeListener(propertyName, listener);
        }

        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
            changeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
            
        private final String name;
        private final String owner;
        
        private final Set<String> tags;
        private final Map<String, String> properties;
        
        public ChannelModel(Channel channel) {
            this.name = channel.getName();
            this.owner = channel.getOwner();
            this.tags = new HashSet<String>(channel.getTagNames());
            this.properties = new HashMap<String, String>();
            for (Property property : channel.getProperties()) {
                this.properties.put(property.getName(), property.getValue());
            }
        }

        public String getName() {
            return name;
        }

        public String getOwner() {
            return owner;
        }

        public Set<String> getTags() {
            return Collections.unmodifiableSet(tags) ;
        }

        public Map<String, String> getProperties() {
            return Collections.unmodifiableMap(properties);
        }
        
        public void addTag(String tag){
            Set<String> oldValue = null;
            this.tags.add(tag);
            firePropertyChange("tags", oldValue, getTags());
        }
        
        public void removeTag(String tag){
            Object oldValue = null;
            this.tags.remove(tag);
            firePropertyChange("tags", oldValue, getTags());
        }
        
        public void removeTags(Collection<String> tags){
            Object oldValue = null;
            this.tags.removeAll(tags);
            firePropertyChange("tags", oldValue, getTags());
        }
        
        public void addProperty(Property property){
            addProperty(property.getName(), property.getValue());
        }
        
        public void addProperty(String key, String value){
            Object oldValue = null;
            this.properties.put(key, value);
            firePropertyChange("properties", oldValue, getProperties());
        }
        
        public void removeProperty(Property property){
            removeProperty(property.getName());
        }
        
        public void removeProperty(String property){
            Object oldValue = null;
            this.properties.remove(property);
            firePropertyChange("properties", oldValue, getProperties());
        }
        
        public void removeProperty(Collection<String> properties){
            Object oldValue = null;
            for (String property : properties) {
                this.properties.remove(property);
            }
            firePropertyChange("properties", oldValue, getProperties());
        }
        
        public Channel getChannel(){
            Builder modifiedChannel = channel(this.name).owner(this.owner);
            java.util.List<gov.bnl.channelfinder.api.Tag.Builder> newTags = tags.stream().map(tag -> {
                return tag(tag);
            }).collect(Collectors.toList());
            java.util.List<gov.bnl.channelfinder.api.Property.Builder> newProperties = properties.entrySet().stream().map(property -> {
                return property(property.getKey()).value(property.getValue());
            }).collect(Collectors.toList());
            return modifiedChannel.withTags(newTags).withProperties(newProperties).build();
        }
    }
}
