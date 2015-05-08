/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.sds.ui.logic;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Document provider for SDS script rules.
 *
 * @author Alexander Will
 * @version $Revision: 1.3 $
 *
 */
public class ScriptDocumentProvider extends AbstractDocumentProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptDocumentProvider.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected IAnnotationModel createAnnotationModel(Object element)
            throws CoreException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isModifiable(Object element) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadOnly(Object element) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IDocument createDocument(Object element) throws CoreException {
        if (element instanceof IEditorInput) {
            IDocument document = new Document();
            if (setDocumentContent(document, (IEditorInput) element)) {
                return document;
            }
        }

        return null;
    }

    /**
     * Set the document content.
     *
     * @param document
     *            target document.
     * @param editorInput
     *            the editor input to fetch the content from.
     * @return true, if the content could be set.
     * @throws CoreException
     *             if an error occurred.
     */
    protected boolean setDocumentContent(IDocument document,
            IEditorInput editorInput) throws CoreException {

        boolean result = false;

        if (editorInput instanceof FileEditorInput) {
            IFile file = ((FileEditorInput) editorInput).getFile();

            InputStreamReader inReader = new InputStreamReader(file
                    .getContents());
            StringBuffer buffer = new StringBuffer();

            try {
                char[] readBuffer = new char[2048];
                int n = inReader.read(readBuffer);
                while (n > 0) {
                    buffer.append(readBuffer, 0, n);
                    n = inReader.read(readBuffer);
                }

                document.set(buffer.toString());
                result = true;
            } catch (IOException e) {
                LOG.error(e.toString());
            } finally {
                try {
                    inReader.close();
                } catch (IOException e) {
                    LOG.error(e.toString());
                }
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveDocument(IProgressMonitor monitor, Object element,
            IDocument document, boolean overwrite) throws CoreException {
        try {
            if (element instanceof FileEditorInput) {
                ByteArrayInputStream bais = new ByteArrayInputStream(document
                        .get().getBytes());
                ((FileEditorInput) element).getFile().setContents(bais, false,
                        false, null);
                monitor.done();
            } else if (element instanceof FileStoreEditorInput) {
                File file = URIUtil.toPath(
                        ((FileStoreEditorInput) element).getURI()).toFile();
                String content = document.get();
                try {
                    FileWriter fileWriter = new FileWriter(file, false);
                    BufferedWriter writer = new BufferedWriter(fileWriter);
                    writer.write(content);
                    writer.flush();
                    writer.close();
                    monitor.done();
                } catch (IOException e) {
                    LOG.error(e.toString());
                    monitor.setCanceled(true);
                }
            }
        } catch (CoreException e) {
            LOG.error(e.toString());
            monitor.setCanceled(true);
        }
    }
}
