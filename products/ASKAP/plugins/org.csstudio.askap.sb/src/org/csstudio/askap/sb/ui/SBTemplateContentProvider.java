package org.csstudio.askap.sb.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.sb.Activator;
import org.csstudio.askap.sb.util.SBTemplate;
import org.csstudio.askap.sb.util.SBTemplateDataModel;
import org.csstudio.askap.sb.util.SchedulingBlock;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

public class SBTemplateContentProvider extends LabelProvider implements ITreeContentProvider {
	private static final Logger logger = Logger.getLogger(SBTemplateContentProvider.class.getName());

	private static Image TEMPLATE_IMAGE = null;
	private static Image SB_IMAGE = null;

	SBTemplateDataModel dataModel;
	
	public class MajorVersion {
		public long majorVersion;
		public String templateName;
		
		public MajorVersion(long majorVersion, String templateName) {
			this.majorVersion = majorVersion;
			this.templateName = templateName;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MajorVersion) {
				MajorVersion ver = (MajorVersion) obj;
				return (ver.majorVersion==this.majorVersion
						&& ver.templateName.equals(this.templateName));
			}
			
			return false;
		}
	}
	
	public SBTemplateContentProvider() {		
		
			try {
				if (TEMPLATE_IMAGE==null)
					TEMPLATE_IMAGE = Activator.getDefault().getImage("icons/template.gif");

				if (SB_IMAGE==null)
					SB_IMAGE = Activator.getDefault().getImage("icons/SB.gif");
				
			} catch (Exception e) {
				logger.log(Level.WARNING, "Could not load image", e);
			}

	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		dataModel = (SBTemplateDataModel) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return dataModel.getTemplateNames().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof String) {// if template name
			String tempalteName = parentElement.toString();
			List<Integer> versionList = dataModel.getMajorVersionsForTemplate(tempalteName);
			List<MajorVersion> majorVersions = new ArrayList<MajorVersion>();
			for (Integer i : versionList)
				majorVersions.add(new MajorVersion(i, tempalteName));
			
			return majorVersions.toArray();
		}
		
		if (parentElement instanceof MajorVersion) {
			MajorVersion major = (MajorVersion) parentElement;
			try {
				List<SchedulingBlock> sbList = dataModel.getSBForTemplate(major.templateName, major.majorVersion);
				List<SBTemplate> minorList = dataModel.getMinorVersionsForTemplate(major.templateName, major.majorVersion);
				Object children[] = new Object[sbList.size() + minorList.size() - 1];
				
				for (int i=0; i<sbList.size(); i++)
					children[i] = sbList.get(i);
				
				// skip the lastest minor version, no need to display it
				for (int i=1; i<minorList.size(); i++)
					children[sbList.size()+i-1] = minorList.get(i);
				
				return children;
			} catch (Exception e) {
				logger.log(Level.WARNING, "Could not retrieve children for " + major.templateName + "@" + major.majorVersion, e);
			}
		}
		
		return null;			
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof String)// if template name
			return null;
		
		if (element instanceof MajorVersion) {
			MajorVersion major = (MajorVersion) element;
			return major.templateName;
		}
		
		if (element instanceof SBTemplate) {
			SBTemplate template = (SBTemplate) element;
			return new MajorVersion(template.getMajorVersion(), template.getName());			
		}
		
		if (element instanceof SchedulingBlock) {
			SchedulingBlock sb = (SchedulingBlock) element;
			return new MajorVersion(sb.getMajorVersion(), sb.getTemplateName());						
		}
		return null;			
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof String) 
			return true;
		
		if (element instanceof MajorVersion)
			return true;
		
		return false;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof String) 
			return (String) element;
		
		if (element instanceof MajorVersion) {
			MajorVersion major = (MajorVersion) element;
			try {
				SBTemplate template = dataModel.getLatestVersion(major.templateName, major.majorVersion);
				return template.getVersion();
			} catch (Exception e) {
				logger.log(Level.WARNING, "Could not get latest version for " + major.templateName + "@" + major.majorVersion, e);
				return "ERROR";
			}
		}
		
		if (element instanceof SBTemplate)
			return ((SBTemplate) element).getVersion();
		
		if (element instanceof SchedulingBlock)
			return ((SchedulingBlock) element).getDisplayName();
		
		return "";
	}

	@Override	
	public Image getImage(Object element) {
		if (element instanceof SchedulingBlock)
			return SB_IMAGE;
		
		if (element instanceof SBTemplate)
			return TEMPLATE_IMAGE;
		
		return null;
	}

	public MajorVersion getLatestVersion(String templateName) {
		SBTemplate template = dataModel.getLatestVersion(templateName);
		return new MajorVersion(template.getMajorVersion(), templateName);
	}
}
