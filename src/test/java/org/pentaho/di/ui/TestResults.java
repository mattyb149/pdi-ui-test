package org.pentaho.di.ui;

/**
 * Created by mburgess on 10/26/15.
 */
public class TestResults {

  protected boolean testPassed;
  protected String testMessage;
  protected Throwable testException;

  public TestResults() {
  }

  public TestResults( boolean testPassed, String testMessage ) {
    this( testPassed, testMessage, null );
  }

  public TestResults( boolean testPassed, String testMessage, Throwable testException ) {
    this.testPassed = testPassed;
    this.testMessage = testMessage;
    this.testException = testException;
  }

  public boolean isTestPassed() {
    return testPassed;
  }

  public void setTestPassed( boolean testPassed ) {
    this.testPassed = testPassed;
  }

  public Throwable getTestException() {
    return testException;
  }

  public void setTestException( Throwable testException ) {
    this.testException = testException;
  }

  public String getTestMessage() {
    return testMessage;
  }

  public void setTestMessage( String testMessage ) {
    this.testMessage = testMessage;
  }

}
