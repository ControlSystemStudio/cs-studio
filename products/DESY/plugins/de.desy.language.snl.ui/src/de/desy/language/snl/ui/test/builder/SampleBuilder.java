package de.desy.language.snl.ui.test.builder;

import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SampleBuilder extends IncrementalProjectBuilder {

	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(final IResourceDelta delta) throws CoreException {
			final IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				SampleBuilder.this.checkXML(resource);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				SampleBuilder.this.checkXML(resource);
				break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
		public boolean visit(final IResource resource) {
			SampleBuilder.this.checkXML(resource);
			// return true to continue visiting children.
			return true;
		}
	}

	class XMLErrorHandler extends DefaultHandler {

		private final IFile file;

		public XMLErrorHandler(final IFile file) {
			this.file = file;
		}

		private void addMarker(final SAXParseException e, final int severity) {
			SampleBuilder.this.addMarker(this.file, e.getMessage(), e
					.getLineNumber(), severity);
		}

		@Override
		public void error(final SAXParseException exception)
				throws SAXException {
			this.addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		@Override
		public void fatalError(final SAXParseException exception)
				throws SAXException {
			this.addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		@Override
		public void warning(final SAXParseException exception)
				throws SAXException {
			this.addMarker(exception, IMarker.SEVERITY_WARNING);
		}
	}

	public static final String BUILDER_ID = "de.desy.language.snl.ui.sampleBuilder";

	private static final String MARKER_TYPE = "de.desy.language.snl.ui.xmlProblem";

	private SAXParserFactory parserFactory;

	private void addMarker(final IFile file, final String message,
			int lineNumber, final int severity) {
		try {
			final IMarker marker = file.createMarker(SampleBuilder.MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (final CoreException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(final int kind, final Map args,
			final IProgressMonitor monitor) throws CoreException {
		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			this.fullBuild(monitor);
		} else {
			final IResourceDelta delta = this.getDelta(this.getProject());
			if (delta == null) {
				this.fullBuild(monitor);
			} else {
				this.incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	void checkXML(final IResource resource) {
		if ((resource instanceof IFile) && resource.getName().endsWith(".xml")) {
			final IFile file = (IFile) resource;
			this.deleteMarkers(file);
			final XMLErrorHandler reporter = new XMLErrorHandler(file);
			try {
				this.getParser().parse(file.getContents(), reporter);
			} catch (final Exception e1) {
			}
		}
	}

	private void deleteMarkers(final IFile file) {
		try {
			file.deleteMarkers(SampleBuilder.MARKER_TYPE, false,
					IResource.DEPTH_ZERO);
		} catch (final CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			this.getProject().accept(new SampleResourceVisitor());
		} catch (final CoreException e) {
		}
	}

	private SAXParser getParser() throws ParserConfigurationException,
			SAXException {
		if (this.parserFactory == null) {
			this.parserFactory = SAXParserFactory.newInstance();
		}
		return this.parserFactory.newSAXParser();
	}

	protected void incrementalBuild(final IResourceDelta delta,
			final IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new SampleDeltaVisitor());
	}
}
