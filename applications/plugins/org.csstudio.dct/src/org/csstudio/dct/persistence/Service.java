package org.csstudio.dct.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.csstudio.dct.model.internal.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class Service {
	public static void save(IFile file, Project project) throws Exception {
		XMLOutputter outp = new XMLOutputter();

		ProjectToXml projectToXml = new ProjectToXml(project);

		Document doc = projectToXml.createDocument();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		outp.output(doc, bos);
		file.setContents(new ByteArrayInputStream(bos.toByteArray()), true, false, new NullProgressMonitor());
	}

	public static Project load(IFile file) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(file.getContents());

		XmlToProject xmlToProject = new XmlToProject(doc);

		Project p = xmlToProject.getProject();

		return p;
	}
}
