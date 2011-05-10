/*
		* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
		* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
		*
		* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
		* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
		* TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
		* NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
		* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
		* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
		* THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
		* IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
		* CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
		* NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
		* DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
		* OR MODIFICATIONS.
		* THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
		* USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
		* PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
		* AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
		*/
package org.csstudio.config.ioconfig.editorparts;

import java.util.ArrayList;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Editor for {@link ChannelStructureDBO} node's
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 21.05.2010
 */
public class ChannelStructureEditor extends AbstractNodeEditor {

    public static final String ID = "org.csstudio.config.ioconfig.view.editor.channelstructure";

    /**
     * System Line separator
     */
    private static final String LS = System.getProperty("line.separator");
    private ChannelStructureDBO _channelStructure;
    private Text _ioNameList;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        _channelStructure = (ChannelStructureDBO) getNode();
        super.createPartControl(parent);
        setSaveButtonSaved();
        Composite newTabItem = getNewTabItem("Main", 2);
        Label label = new Label(newTabItem, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText("Description:");

        label = new Label(newTabItem, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText("IOName List:");

        StyledText text = new StyledText(newTabItem, SWT.MULTI | SWT.LEAD | SWT.BORDER
                | SWT.READ_ONLY);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        _ioNameList = new Text(newTabItem, SWT.MULTI | SWT.LEAD | SWT.BORDER);
        _ioNameList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ArrayList<StyleRange> styleRanges = new ArrayList<StyleRange>();
        
        try {
            createChildren(text, styleRanges);
        } catch (PersistenceException e) {
            DeviceDatabaseErrorDialog.open(null, "Can't create node! Database error.", e);
            CentralLogger.getInstance().error(this, e.getLocalizedMessage());
        }
        
        _ioNameList.addModifyListener(getMLSB());
        _ioNameList.setFocus();
        selecttTabFolder(0);
    }

	/**
	 * @param text
	 * @param styleRanges
	 * @throws PersistenceException 
	 */
	private void createChildren(@Nonnull StyledText text,
			@Nonnull ArrayList<StyleRange> styleRanges) throws PersistenceException {
		if (_channelStructure.hasChildren()) {
            StringBuilder sbIOName = new StringBuilder();
            StringBuilder sbDesc = new StringBuilder();
            for (AbstractNodeDBO node : _channelStructure.getChildrenAsMap().values()) {
                ChannelDBO channel = (ChannelDBO) node;
                int length = sbDesc.length();
                sbDesc.append(channel.getName());
                if ( (channel.getIoName() == null) || channel.getIoName().isEmpty()) {
                    sbIOName.append(LS);
                } else {
                    sbIOName.append(channel.getIoName());
                    sbDesc.append(" (" + channel.getIoName() + ") ");
                    sbIOName.append(LS);
                }
                sbDesc.append(": ");
                sbDesc.append(LS);
                if (channel.getDescription() != null) {
                    sbDesc.append(channel.getDescription());
                    sbDesc.append(LS);
                }
                sbDesc.append(LS);
                StyleRange styleRange = new StyleRange(length,
                                                       channel.getName().length() + 1,
                                                       null, null, SWT.BOLD);
                styleRanges.add(styleRange);
                TextStyle textStyle = new TextStyle();
                textStyle.borderStyle = SWT.BORDER_DOT;
            }
            setText(_ioNameList, sbIOName.toString(), Text.LIMIT);
            text.setText(sbDesc.toString());
            StyleRange[] array = styleRanges.toArray(new StyleRange[0]);
            text.setStyleRanges(array);
        }
	}

    @Override
    public boolean fill(@Nullable final GSDFileDBO gsdFile) {
        return false;
    }

    @Override
    @CheckForNull
    public GSDFileDBO getGsdFile() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave(@Nullable final IProgressMonitor monitor) {
        super.doSave(monitor);
        String text = _ioNameList.getText();
        String[] ioNames = text.split(LS);
        
        ChannelDBO[] channels;
        try {
            channels = _channelStructure.getChildrenAsMap().values().toArray(new ChannelDBO[0]);
            for (int i = 0; (i < channels.length) && (i < ioNames.length); i++) {
                channels[i].setIoName(ioNames[i]);
            }
            _ioNameList.setData(_ioNameList.getText());
            save();
        } catch (PersistenceException e) {
            DeviceDatabaseErrorDialog.open(null, "Can't node save. Database error.", e);
            CentralLogger.getInstance().error(this, e.getLocalizedMessage());
        }
    }
}
