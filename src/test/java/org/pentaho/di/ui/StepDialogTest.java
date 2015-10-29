/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.pentaho.di.ui;

import com.google.common.util.concurrent.SettableFuture;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.BeforeClass;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;

public abstract class StepDialogTest extends SWTBotTestCase {

  protected SWTBot bot;

  protected static Thread uiThread;

  protected static Shell shell;

  protected CyclicBarrier swtBarrier = new CyclicBarrier( 2 );

  protected final static Display display = Display.getDefault();

  protected final static Shell parent = new Shell();

  private BaseStepDialog dialog;

  protected static final TransMeta transMeta = new TransMeta();

  private SettableFuture<TestResults> testResultFuture;

  protected Map<String, String> mappedButtonNames = new HashMap<String, String>() {{
    put( "OK", "  &OK  " );
    put( "Cancel", "  &Cancel" );
    // TODO More
  }};

  @BeforeClass
  public static void setUpBeforeClass() {
    PropsUI.init( display, Props.TYPE_PROPERTIES_SPOON );

    KettleLogStore
      .init( PropsUI.getInstance().getMaxNrLinesInLog(), PropsUI.getInstance().getMaxLogLineTimeoutMinutes() );
    try {
      KettleEnvironment.init();
    } catch ( KettleException e ) {
      e.printStackTrace();
    }
  }


  public void createTest( final String shellTitle, BaseStepDialog dialog, final String endButton, final Callable<Void> testMethod ) {
    swtBarrier.reset();
    testResultFuture = SettableFuture.create();
    // We assume the textFileInputDialog parameter implements StepDialogInterface, that is the only pattern that works in PDI.
    this.dialog = dialog;
    if ( uiThread == null ) {
      uiThread = new Thread( new Runnable() {

        @Override
        public void run() {
          TestResults testResults = new TestResults( true, "" );
          try {

            // wait for the test setup
            swtBarrier.await();
            while ( ( shell = StepDialogTest.this.dialog.getShell() ) == null ) {
              Thread.sleep( 100 );
            }
            bot = new SWTBot( shell );
            bot.shell( shellTitle ).activate();

            if ( testMethod != null ) {
              testMethod.call();
            }

          } catch ( InterruptedException e ) {
            testResultFuture.setException( e );
            fail( "Interrupted during testing" );
            e.printStackTrace();
          } catch ( BrokenBarrierException e ) {
            testResultFuture.setException( e );
            fail( "Broken barrier during testing" );
            e.printStackTrace();
          } catch ( Exception e ) {
            testResultFuture.setException( e );
          } catch ( AssertionError assertionError ) {
            testResults.setTestPassed( false );
            testResults.setTestMessage( assertionError.getMessage() );
          } finally {
            testResultFuture.set( testResults );
            String buttonText = mappedButtonNames.get( endButton );
            if ( buttonText == null ) {
              buttonText = endButton;
            }
            SWTBotButton button = bot.button( buttonText );
            button.click();

          }
        }
      } );
      uiThread.setDaemon( true );
      uiThread.start();
    }

  }

  protected String openUI() throws Exception {
    swtBarrier.await();
    return ( (StepDialogInterface) dialog ).open();
  }

  @After
  public void tearDown() throws InterruptedException {
    uiThread = null;
  }


  protected void verifyResults() throws Exception {
    TestResults results = testResultFuture.get();
    assertTrue( results.getTestMessage(), results.isTestPassed() );
  }

  protected void assertResultGivenInput( String input, String expectedResult ) {
    bot.textWithLabel( "Step name " ).setText( input );
    assertEquals( expectedResult, bot.textWithLabel( "Step name " ).getText() );
  }

  private void printWidgets( Shell shell, Class<? extends Widget> clazz ) {
    Matcher<Widget> allMatcher = allOf( widgetOfType( clazz ) );

    List<? extends Widget> widgets = bot.widgets( allMatcher, shell );
    assertNotNull( widgets );
    for ( Widget w : widgets ) {

      System.out.println( w.getClass().getCanonicalName() );
    }
  }

}
