/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

/**The actions to distribute widgets.
 * @author Xihui Chen
 *
 */
public class DistributeWidgetsAction extends SelectionAction {

    public enum DistributeType{
        HORIZONTAL_GAP("Distribute by Horizontal GAP", "icons/distribute_hg.png"), //$NON-NLS-2$
        HORIZONTAL_CENTERS("Distribute by Horizontal Centers", "icons/distribute_hc.png"), //$NON-NLS-2$
        HORIZONTAL_COMPRESS("Distribute by Horizontal Compress", "icons/distribute_hcompress.png"), //$NON-NLS-2$
        VERTICAL_GAP("Distribute by Vertical GAP", "icons/distribute_vg.png"), //$NON-NLS-2$
        VERTICAL_CENTERS("Distribute by Vertical Centers", "icons/distribute_vc.png"), //$NON-NLS-2$
        VERTICAL_COMPRESS("Distribute by Vertical Compress", "icons/distribute_vcompress.png"); //$NON-NLS-2$

        private String label;
        private String iconPath;
        private DistributeType(String label, String iconPath) {
            this.label = label;
            this.iconPath = iconPath;
        }

        public String getLabel(){
            return label;
        }

        public String getActionID(){
            return "org.csstudio.opibuilder.actions." + toString(); //$NON-NLS-1$
        }

        public ImageDescriptor getImageDescriptor(){
            return CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
                    OPIBuilderPlugin.PLUGIN_ID, iconPath);
        }

    }

    private DistributeType distributeType;

    /**
     * @param part the OPI Editor
     * @param pasteWidgetsAction pass the paste action will
     * help to update the enable state of the paste action
     * after copy action invoked.
     */
    public DistributeWidgetsAction(IWorkbenchPart part, DistributeType distributeType) {
        super(part);
        this.distributeType = distributeType;
        setText(distributeType.getLabel());
        setId(distributeType.getActionID()); //$NON-NLS-1$
        setImageDescriptor(distributeType.getImageDescriptor());
    }

    @Override
    protected boolean calculateEnabled() {
        if(getSelectedWidgetModels().size() < 2)
            return false;
        return true;
    }


    @Override
    public void run() {
        switch (distributeType) {
        case HORIZONTAL_GAP:
            execute(getHorizontalGapCommand());
            break;
        case HORIZONTAL_CENTERS:
            execute(getHorizontalCenterCommand());
            break;
        case HORIZONTAL_COMPRESS:
            execute(getHorizontalCompressCommand());
            break;
        case VERTICAL_GAP:
            execute(getVerticalGapCommand());
            break;
        case VERTICAL_CENTERS:
            execute(getVerticalCenterCommand());
            break;
        case VERTICAL_COMPRESS:
            execute(getVerticalCompressCommand());
            break;
        default:
            break;
        }
    }


    private Command getHorizontalGapCommand(){
        AbstractWidgetModel[] sortedModelArray = getSortedModelArray(true);
        CompoundCommand cmd = new CompoundCommand("Horizontal Gap Distribution");
        int widthSum = 0;
        for(int i=1; i<sortedModelArray.length-1; i++){
            widthSum += sortedModelArray[i].getWidth();
        }
        int averageGap = (sortedModelArray[sortedModelArray.length-1].getX() -
            (sortedModelArray[0].getX() + sortedModelArray[0].getWidth()) - widthSum)
            /(sortedModelArray.length-1);

        int startX = sortedModelArray[0].getX() + sortedModelArray[0].getWidth();
        for(int i=1; i<sortedModelArray.length-1; i++){
             cmd.add(new SetWidgetPropertyCommand(
                     sortedModelArray[i], AbstractWidgetModel.PROP_XPOS,
                     startX + averageGap));
             startX += averageGap + sortedModelArray[i].getWidth();
        }

        return cmd;

    }


    private Command getVerticalGapCommand(){
        AbstractWidgetModel[] sortedModelArray = getSortedModelArray(false);
        CompoundCommand cmd = new CompoundCommand("Vertical Gap Distribution");
        int widthSum = 0;
        for(int i=1; i<sortedModelArray.length-1; i++){
            widthSum += sortedModelArray[i].getHeight();
        }
        int averageGap = (sortedModelArray[sortedModelArray.length-1].getY() -
            (sortedModelArray[0].getY() + sortedModelArray[0].getHeight()) - widthSum)
            /(sortedModelArray.length-1);
        int startX = sortedModelArray[0].getY() + sortedModelArray[0].getHeight();
        for(int i=1; i<sortedModelArray.length; i++){
             cmd.add(new SetWidgetPropertyCommand(
                     sortedModelArray[i], AbstractWidgetModel.PROP_YPOS,
                     startX + averageGap));
             startX += averageGap + sortedModelArray[i].getHeight();
        }

        return cmd;

    }



    private Command getHorizontalCenterCommand(){
        AbstractWidgetModel[] sortedModelArray = getSortedModelArray(true);
        CompoundCommand cmd = new CompoundCommand("Horizontal Center Distribution");

        int averageGap = (getCenterLoc(sortedModelArray[sortedModelArray.length-1], true) -
            getCenterLoc(sortedModelArray[0], true))
            /(sortedModelArray.length-1);

        int startX = getCenterLoc(sortedModelArray[0], true);
        for(int i=1; i<sortedModelArray.length-1; i++){
             cmd.add(new SetWidgetPropertyCommand(
                     sortedModelArray[i], AbstractWidgetModel.PROP_XPOS,
                     startX + averageGap - sortedModelArray[i].getWidth()/2));
             startX += averageGap;
        }
        return cmd;

    }

    private Command getVerticalCenterCommand(){
        AbstractWidgetModel[] sortedModelArray = getSortedModelArray(false);
        CompoundCommand cmd = new CompoundCommand("Vertical Center Distribution");

        int averageGap = (getCenterLoc(sortedModelArray[sortedModelArray.length-1], false) -
            getCenterLoc(sortedModelArray[0], false))
            /(sortedModelArray.length-1);

        int startX = getCenterLoc(sortedModelArray[0], false);
        for(int i=1; i<sortedModelArray.length-1; i++){
             cmd.add(new SetWidgetPropertyCommand(
                     sortedModelArray[i], AbstractWidgetModel.PROP_YPOS,
                     startX + averageGap - sortedModelArray[i].getHeight()/2));
             startX += averageGap;
        }
        return cmd;

    }


    private Command getHorizontalCompressCommand(){
        AbstractWidgetModel[] sortedModelArray = getSortedModelArray(true);
        CompoundCommand cmd = new CompoundCommand("Horizontal Compress Distribution");

        int startX = sortedModelArray[0].getX() + sortedModelArray[0].getWidth();
        for(int i=1; i<sortedModelArray.length; i++){
             cmd.add(new SetWidgetPropertyCommand(
                     sortedModelArray[i], AbstractWidgetModel.PROP_XPOS,
                     startX));
             startX += sortedModelArray[i].getWidth();
        }

        return cmd;

    }

    private Command getVerticalCompressCommand(){
        AbstractWidgetModel[] sortedModelArray = getSortedModelArray(false);
        CompoundCommand cmd = new CompoundCommand("Vertical Compress Distribution");

        int startX = sortedModelArray[0].getY() + sortedModelArray[0].getHeight();
        for(int i=1; i<sortedModelArray.length; i++){
             cmd.add(new SetWidgetPropertyCommand(
                     sortedModelArray[i], AbstractWidgetModel.PROP_YPOS,
                     startX));
             startX += sortedModelArray[i].getHeight();
        }

        return cmd;

    }



    private int getCenterLoc(AbstractWidgetModel model, boolean x){
        if(x)
            return model.getX() + model.getWidth()/2;
        else
            return model.getY() + model.getHeight()/2;
    }

    private AbstractWidgetModel[] getSortedModelArray(final boolean byHorizontal){
        AbstractWidgetModel[] modelArray =
            new AbstractWidgetModel[getSelectedWidgetModels().size()];
        int i=0;
        for(AbstractWidgetModel model : getSelectedWidgetModels()){
            modelArray[i++] = model;
        }
        Arrays.sort(modelArray,new Comparator<AbstractWidgetModel>(){
            @Override
            public int compare(AbstractWidgetModel o1, AbstractWidgetModel o2) {
                int o1loc, o2loc;
                if(byHorizontal){
                    o1loc = o1.getLocation().x;
                    o2loc = o2.getLocation().x;
                }else{
                    o1loc = o1.getLocation().y;
                    o2loc = o2.getLocation().y;
                }
                if(o1loc < o2loc)
                    return -1;
                else if(o1loc> o2loc)
                    return 1;
                else
                    return 0;
            }
        });
        return modelArray;
    }


    /**
     * Gets the widget models of all currently selected EditParts.
     *
     * @return a list with all widget models that are currently selected
     */
    protected final List<AbstractWidgetModel> getSelectedWidgetModels() {
        List<?> selection = getSelectedObjects();

        List<AbstractWidgetModel> selectedWidgetModels = new ArrayList<AbstractWidgetModel>();

        for (Object o : selection) {
            if (o instanceof AbstractBaseEditPart) {
                selectedWidgetModels.add(((AbstractBaseEditPart) o)
                        .getWidgetModel());
            }
        }
        return selectedWidgetModels;
    }

}
