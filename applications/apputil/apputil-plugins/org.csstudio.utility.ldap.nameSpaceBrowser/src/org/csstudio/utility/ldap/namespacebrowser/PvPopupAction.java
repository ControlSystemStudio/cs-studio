///*
// * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
// * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
// *
// * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
// * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
// * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
// * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
// * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
// * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
// * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
// * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
// * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
// * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
// * OR MODIFICATIONS.
// * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
// * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
// * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
// * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
// */
//package org.csstudio.utility.ldap.namespacebrowser;
//
////
//import org.csstudio.platform.model.IProcessVariable;
//import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
//import org.csstudio.utility.ldap.namespacebrowser.ui.MainView;
//import org.eclipse.ui.IWorkbench;
//import org.eclipse.ui.IWorkbenchPage;
//import org.eclipse.ui.IWorkbenchWindow;
//import org.eclipse.ui.PartInitException;
//import org.eclipse.ui.PlatformUI;
//
///**
// * @author hrickens
// * @author $Author: hrickens $
// * @version $Revision: 1.7 $
// * @since 25.05.2011
// */
//public class PvPopupAction extends ProcessVariablePopupAction {
//
//    @Override
//    public void handlePVs(IProcessVariable[] pvNames) {
//        if(pvNames.length < 1) {
//            return;
//        }
//        IWorkbench workbench = PlatformUI.getWorkbench();
//        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
//        IWorkbenchPage page = window.getActivePage();
//            MainView view;
//            try {
//                view = (MainView) page.showView(MainView.ID);
//                view.setDefaultPVFilter(pvNames[0].getName());
//            } catch (PartInitException e) {
////            //            Plugin.logException("Cannot open PVTreeView" , e);
//            }
//    }
//}
