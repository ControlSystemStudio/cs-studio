/*
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.config.ioconfig.editorparts;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.IOConfigActivatorUI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Editor for {@link ChannelStructureDBO} node's
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 21.05.2010
 */
public class ChannelStructureEditor extends AbstractNodeEditor<ChannelStructureDBO> {

    public static final String ID = "org.csstudio.config.ioconfig.view.editor.channelstructure";
    protected static final Logger LOG = LoggerFactory.getLogger(ChannelStructureEditor.class);

    /**
     * System Line separator
     */
    private static final String LS = System.getProperty("line.separator");
    private ChannelStructureDBO _channelStructure;
    private Text _ioNameList;

    /**
     * @author hrickens
     * @author $Author: $
     * @since 30.09.2010
     */
    private final class AssembleEpicsAddSelectionListener implements
    SelectionListener {

        public AssembleEpicsAddSelectionListener() {
            // Default Constructor.
        }

        public void setChannelName(@Nonnull final ChannelDBO channel,
                                   @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
            if (moduleChannelPrototype.getType() != channel.getParent()
                    .getStructureType()) {
                channel.getParent()
                .setStructureType(moduleChannelPrototype.getType());
                if (channel.getParent().isSimple()) {
                    channel.setChannelType(moduleChannelPrototype.getType());
                }
            }
            channel.setName(moduleChannelPrototype.getName());
        }

        public void setWidgetName(@Nonnull final ChannelDBO channel,
                                  @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
            String name = moduleChannelPrototype.getName();
            name += !moduleChannelPrototype.isStructure()?"": channel.getSortIndex();
            final Text nameWidget = getNameWidget();
            if (nameWidget != null && !name.equals(channel.getName())) {
                nameWidget.setText(name);
            }
        }

        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            doAssemble();
        }

        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            doAssemble();
        }

        private void doAssemble() {
            final Set<ChannelDBO> children = getNode().getChildren();
            for (final ChannelDBO channel : children) {

                final GSDModuleDBO module = channel.getModule().getGSDModule();
                if (module != null) {
                    final TreeSet<ModuleChannelPrototypeDBO> moduleChannelPrototypes = module
                            .getModuleChannelPrototypeNH();
                    final ModuleChannelPrototypeDBO[] array = moduleChannelPrototypes
                            .toArray(new ModuleChannelPrototypeDBO[0]);
                    final ModuleChannelPrototypeDBO moduleChannelPrototype = array[channel
                            .getParent().getSortIndex()];
                    channel.setStatusAddressOffset(moduleChannelPrototype.getShift());
                    channel.setChannelNumber(moduleChannelPrototype.getOffset());
                    setWidgetName(channel, moduleChannelPrototype);
                    setChannelName(channel, moduleChannelPrototype);
                }
                try {
                    channel.assembleEpicsAddressString();
                } catch (final PersistenceException e) {
                    DeviceDatabaseErrorDialog.open(null,
                                                   "Can't calulate Epics Address. Database error!",
                                                   e);
                    LOG.error("Can't calulate Epics Address. Database error!", e);
                }
            }
            setSavebuttonEnabled("Refresh Children", true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        _channelStructure = getNode();
        super.createPartControl(parent);
        setSaveButtonSaved();
        final Composite newTabItem = getNewTabItem("Main", 3);
        Label label = new Label(newTabItem, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText("Description:");


        label = new Label(newTabItem, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText("IOName List:");

        final Button assembleButton = new Button(newTabItem, SWT.FLAT);
        final GridDataFactory gdf = GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER);
        assembleButton.setLayoutData(gdf.create());
        assembleButton.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(IOConfigActivatorUI.PLUGIN_ID,
        "icons/refresh.gif").createImage());
        assembleButton.setToolTipText("Refresh the EPICS Address String\n and save it into the DB");
        assembleButton.addSelectionListener(new AssembleEpicsAddSelectionListener());


        final StyledText text = new StyledText(newTabItem, SWT.MULTI | SWT.LEAD | SWT.BORDER
                                               | SWT.READ_ONLY);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        _ioNameList = new Text(newTabItem, SWT.MULTI | SWT.LEAD | SWT.BORDER);
        _ioNameList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,2,1));
        final ArrayList<StyleRange> styleRanges = new ArrayList<StyleRange>();
        createChildren(text, styleRanges);
        _ioNameList.addModifyListener(getMLSB());
        _ioNameList.setFocus();
        selecttTabFolder(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave(@Nullable final IProgressMonitor monitor) {
        super.doSave(monitor);
        final String text = _ioNameList.getText();
        final String[] ioNames = text.split(LS);

        ChannelDBO[] channels;
        channels = _channelStructure.getChildrenAsMap().values().toArray(new ChannelDBO[0]);
        for (int i = 0; i < channels.length && i < ioNames.length; i++) {
            channels[i].setIoName(ioNames[i]);
        }
        _ioNameList.setData(_ioNameList.getText());
        save();
    }

    /**
     * @param text
     * @param styleRanges
     */
    private void createChildren(@Nonnull final StyledText text,
                                @Nonnull final ArrayList<StyleRange> styleRanges) {
        if (_channelStructure.hasChildren()) {
            final StringBuilder sbIOName = new StringBuilder();
            final StringBuilder sbDesc = new StringBuilder();
            for (final ChannelDBO channel : _channelStructure.getChildrenAsMap().values()) {
                final int length = sbDesc.length();
                String name = channel.getName();
                name = name==null?"":name;
                sbDesc.append(name);
                final String ioName = channel.getIoName();
                if ( ioName == null || ioName.isEmpty()) {
                    sbIOName.append(LS);
                } else {
                    sbIOName.append(ioName);
                    sbDesc.append(" (" + ioName + ") ");
                    sbIOName.append(LS);
                }
                sbDesc.append(": ");
                sbDesc.append(LS);
                if (channel.getDescription() != null) {
                    sbDesc.append(channel.getDescription());
                    sbDesc.append(LS);
                }
                sbDesc.append(LS);
                final StyleRange styleRange = new StyleRange(length,
                                                             name.length() + 1,
                                                             null, null, SWT.BOLD);
                styleRanges.add(styleRange);
                final TextStyle textStyle = new TextStyle();
                textStyle.borderStyle = SWT.BORDER_DOT;
            }
            setText(_ioNameList, sbIOName.toString(), Text.LIMIT);
            text.setText(sbDesc.toString());
            final StyleRange[] array = styleRanges.toArray(new StyleRange[0]);
            text.setStyleRanges(array);
        }
    }
}
