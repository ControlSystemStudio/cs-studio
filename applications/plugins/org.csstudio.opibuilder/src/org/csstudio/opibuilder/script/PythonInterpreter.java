package org.csstudio.opibuilder.script;

import java.io.InputStream;

import org.python.antlr.base.mod;
import org.python.core.CompileMode;
import org.python.core.ParserFacade;
import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyObject;
import org.python.core.PySystemState;

/**
 * This implementation of PythonInterpreter inherits org.python.util.PythonInterpreter
 * to allow compilation from InputStream. 
 * 
 * @author Takashi Nakamoto (Cosylab)
 */
public class PythonInterpreter extends org.python.util.PythonInterpreter {

	public PythonInterpreter() {
		super();
	}

	public PythonInterpreter(PyObject dict) {
		super(dict);
	}

	public PythonInterpreter(PyObject dict, PySystemState systemState) {
		super(dict, systemState);
	}

	public PythonInterpreter(PyObject dict, PySystemState systemState, boolean useThreadLocalState) {
		super(dict, systemState, useThreadLocalState);
	}

	/**
	 * Compile Python source from InputStream.
	 * 
	 * @param s InputStream of Python source code
	 * @return PyCode
	 */
	public PyCode compile(InputStream s) {
		return compile(s, "<script>");
	}

	public PyCode compile(InputStream s, String filename) {
		mod node = ParserFacade.parse(s, CompileMode.exec, filename, cflags);
		setSystemState();
		return Py.compile_flags(node, filename, CompileMode.exec, cflags);
	}
}