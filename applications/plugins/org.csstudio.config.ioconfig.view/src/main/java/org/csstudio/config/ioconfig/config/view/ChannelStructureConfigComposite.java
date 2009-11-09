/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
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
/*
 * $Id$
 */
package org.csstudio.config.ioconfig.config.view;

import java.util.ArrayList;

import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.pbmodel.Channel;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructure;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 28.10.2009
 */
public class ChannelStructureConfigComposite extends NodeConfig {

    /**
     * System Line separator
     */
    private static final String LS = System.getProperty( "line.separator" );
    private Node _channelStructure;
    private Text _ioNameList;

    /**
     * That is a Config View that only show a Description.
     * Is is useful to show nodes without properties to configure. 
     * 
     * @param parent The Parent composite.
     * @param profiBusTreeView the Navigate Profibus Tree-view
     * @param style the Composite Style.
     * @param node the node to "Configure"
     * @param string The Description text. 
     */
    public ChannelStructureConfigComposite(Composite parent, ProfiBusTreeView profiBusTreeView, ChannelStructure channelStructure) {
        super(parent,profiBusTreeView, channelStructure!=null?channelStructure.getClass().getSimpleName():"", channelStructure, false);
        profiBusTreeView.setConfiguratorName("Channel Structure Configuration");
        _channelStructure = channelStructure;
        setSaveButtonSaved();
        Composite newTabItem = getNewTabItem("Main", 2);
        Label label = new Label(newTabItem, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText("Description:");
        
        label = new Label(newTabItem, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        label.setText("IOName List:");
        
        StyledText text = new StyledText(newTabItem, SWT.MULTI | SWT.LEAD | SWT.BORDER|SWT.READ_ONLY);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        _ioNameList = new Text(newTabItem, SWT.MULTI | SWT.LEAD | SWT.BORDER);
        _ioNameList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ArrayList<StyleRange> styleRanges = new ArrayList<StyleRange>();
        if(_channelStructure.hasChildren()) {
           StringBuilder sbIOName = new StringBuilder();
           StringBuilder sbDesc = new StringBuilder();
           for (Node node : _channelStructure.getChildrenAsMap().values()) {
               Channel channel = (Channel) node;
               sbDesc.append(LS);
               int length = sbDesc.length();
               sbDesc.append(channel.getName());
               sbDesc.append(": ");
               if(channel.getIoName()==null) {
                   sbIOName.append(LS);
               }else {
                   sbIOName.append(channel.getIoName());
                   sbDesc.append("("+channel.getIoName()+") ");
                   sbIOName.append(LS);
               }
               sbDesc.append(LS);
               if(channel.getDescription()!=null) {
                   sbDesc.append(channel.getDescription());
                   sbDesc.append(LS);
               }
               styleRanges.add(new StyleRange(length, channel.getName().length()+1,null,null,SWT.BOLD));
           }
           setText(_ioNameList, sbIOName.toString(), Text.LIMIT);
           text.setText(sbDesc.toString());
           text.setStyleRanges(styleRanges.toArray(new StyleRange[0]));
        }
        _ioNameList.addModifyListener(getMLSB());
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.config.view.NodeConfig#fill(org.csstudio.config.ioconfig.model.pbmodel.GSDFile)
     */
    @Override
    public boolean fill(GSDFile gsdFile) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.config.view.NodeConfig#getGSDFile()
     */
    @Override
    public GSDFile getGSDFile() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.config.view.NodeConfig#getNode()
     */
    @Override
    public Node getNode() {
        return _channelStructure;
    }
    
    @Override
    public void store() {
        super.store();
        String text = _ioNameList.getText();
        String[] ioNames = text.split(LS);
        Channel[] channels = _channelStructure.getChildrenAsMap().values().toArray(new Channel[0]);
        for (int i = 0; i < channels.length && i < ioNames.length; i++) {
            channels[i].setIoName(ioNames[i]);
        }
        _ioNameList.setData(_ioNameList.getText());
        save();
    }

}
