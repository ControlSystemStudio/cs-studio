/*
 * Copyright © 2008, Brian Joyce
 * By Brian Joyce, Duolog Technologies Ltd., Galway, Ireland
 * June 13, 2008
 *
 * http://www.eclipse.org/articles/Article-PDEJUnitAntAutomation/index.html#PDETestListener
 */
package org.csstudio.testsuite.pde;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 16.06.2011
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter;
import org.eclipse.jdt.internal.junit.model.ITestRunListener2;
//CHECKSTYLE OFF: |
@SuppressWarnings("all")
public class PDETestListener implements ITestRunListener2 {
    private final Object resultsCollector;
    private int totalNumberOfTests;
    private int testsRunCount;
    private int numberOfTestsPassed;
    private int numberOfTestsFailed;
    private int numberOfTestsWithError;
    private boolean testRunEnded = false;
    private XMLJUnitResultFormatter xmlResultsFormatter;
    private File outputFile;
    private final String suiteName;
    private final JUnitTest junitTestSuite;
    private TestCase currentTest;

    public PDETestListener(final Object collector, final String suite) {
        resultsCollector = collector;
        suiteName = suite;
        junitTestSuite = new JUnitTest(suiteName);
        junitTestSuite.setProperties(System.getProperties());
    }

    public void setOutputFile(final String filename) {
        outputFile = new File(filename);
    }

    public File getOutputFile() {
        if (outputFile == null) {
            setOutputFile("TEST-" + suiteName + ".xml");
        }
        return outputFile;
    }

    public synchronized boolean failed() {
        return numberOfTestsFailed + numberOfTestsWithError > 0 || testRunEnded && testsRunCount == 0;
    }

    public synchronized int count() {
        return testsRunCount;
    }

    private XMLJUnitResultFormatter getXMLJUnitResultFormatter() {
        if (xmlResultsFormatter == null) {
            xmlResultsFormatter = new XMLJUnitResultFormatter();
            try {
                xmlResultsFormatter.setOutput(new FileOutputStream(getOutputFile()));
            } catch (final FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return xmlResultsFormatter;
    }

    @Override
    public synchronized void testRunStarted(final int testCount) {
        totalNumberOfTests = testCount;
        testsRunCount = 0;
        numberOfTestsPassed = 0;
        numberOfTestsFailed = 0;
        numberOfTestsWithError = 0;
        testRunEnded = false;
        getXMLJUnitResultFormatter().startTestSuite(junitTestSuite);
        System.out.println("PDE Test Run Started - running " + totalNumberOfTests + " tests ...");
    }

    @Override
    public synchronized void testRunEnded(final long elapsedTime) {
        testRunEnded = true;
        junitTestSuite.setCounts(testsRunCount, numberOfTestsFailed, numberOfTestsWithError);
        junitTestSuite.setRunTime(elapsedTime);
        getXMLJUnitResultFormatter().endTestSuite(junitTestSuite);
        System.out.println("Test Run Ended   - " + (failed() ? "FAILED" : "PASSED") + " - Total: " + totalNumberOfTests
                + " (Errors: " + numberOfTestsWithError
                + ", Failed: " + numberOfTestsFailed
                + ", Passed: " + numberOfTestsPassed + "), duration " + elapsedTime + "ms.");

        synchronized (resultsCollector) {
            resultsCollector.notifyAll();
        }
    }

    @Override
    public synchronized void testRunStopped(final long elapsedTime) {
        System.out.println("Test Run Stopped");
        testRunEnded(elapsedTime);
    }

    @Override
    public synchronized void testRunTerminated() {
        System.out.println("Test Run Terminated");
        testRunEnded(0);
    }

    @Override
    public synchronized void testStarted(final String testId, final String testName) {
        testsRunCount++;
        currentTest = new WrapperTestCase(testName);
        getXMLJUnitResultFormatter().startTest(currentTest);
        System.out.println("  Test Started - " + count() + " - " + testName);
    }

    @Override
    public synchronized void testEnded(final String testId, final String testName) {
        numberOfTestsPassed = count() - (numberOfTestsFailed + numberOfTestsWithError);
        getXMLJUnitResultFormatter().endTest(currentTest);
        System.out.println("  Test Ended   - " + count() + " - " + testName);
    }

    @Override
    public synchronized void testFailed(final int status, final String testId, final String testName, final String trace, final String expected, final String actual) {
        String statusMessage = String.valueOf(status);
        if (status == ITestRunListener2.STATUS_OK) {
            numberOfTestsPassed++;
            statusMessage = "OK";
        } else if (status == ITestRunListener2.STATUS_FAILURE) {
            numberOfTestsFailed++;
            statusMessage = "FAILED";
            getXMLJUnitResultFormatter().addFailure(currentTest, new AssertionFailedError(trace));
        } else if (status == ITestRunListener2.STATUS_ERROR) {
            numberOfTestsWithError++;
            statusMessage = "ERROR";
            getXMLJUnitResultFormatter().addError(currentTest, new Exception(trace));
        }
        System.out.println("  Test Failed  - " + count() + " - " + testName + " - status: " + statusMessage
                + ", trace: " + trace + ", expected: " + expected + ", actual: " + actual);
    }

    @Override
    public synchronized void testReran(final String testId, final String testClass, final String testName, final int status, final String trace, final String expected, final String actual) {
        String statusMessage = String.valueOf(status);
        if (status == ITestRunListener2.STATUS_OK) {
            statusMessage = "OK";
        } else if (status == ITestRunListener2.STATUS_FAILURE) {
            statusMessage = "FAILED";
        } else if (status == ITestRunListener2.STATUS_ERROR) {
            statusMessage = "ERROR";
        }

        System.out.println("  Test ReRan   - " + testName + " - test class: " + testClass + ", status: " + statusMessage
                + ", trace: " + trace + ", expected: " + expected + ", actual: " + actual);
    }

    @Override
    public synchronized void testTreeEntry(final String description) {
        System.out.println("Test Tree Entry - Description: " + description);
    }

    private static final class WrapperTestCase extends TestCase {

        public WrapperTestCase(final String name) {
            super(name);
        }

        @Override
        public int countTestCases() {
            return 1;
        }

        @Override
        public void run(final TestResult result) {
            // EMPTY
        }
    }
}
//CHECKSTYLE ON: |
