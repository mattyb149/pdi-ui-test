package org.pentaho.di.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.steps.fileinput.text.TextFileInputMeta;
import org.pentaho.di.ui.trans.steps.fileinput.text.TextFileInputDialog;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by mburgess on 10/26/15.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class StepDialogTest extends SWTBotTestCase {

  protected SWTBot bot;

  protected static Thread uiThread;

  protected static Shell shell;

  private final static CyclicBarrier swtBarrier = new CyclicBarrier(2);

  private final static Display display = Display.getDefault();

  private final static Shell parent = new Shell();

  @BeforeClass
  public static void setupApp() {
    if (uiThread == null) {
      uiThread = new Thread(new Runnable() {

        @Override
        public void run() {
          try {
            while (true) {
              // open and layout the shell
              final TextFileInputMeta meta = new TextFileInputMeta();
              final TransMeta transMeta = new TransMeta();
              meta.setDefault();
              TextFileInputDialog window = new TextFileInputDialog( parent, meta, transMeta, "My Dialog" );
              window.open();
              shell = window.getShell();

              // wait for the test setup
              swtBarrier.await();

            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
      uiThread.setDaemon(true);
      uiThread.start();
    }
  }

  @Before
  public final void setupSWTBot() throws InterruptedException, BrokenBarrierException {
    // synchronize with the thread opening the shell
    swtBarrier.await();
    bot = new SWTBot(shell);
  }

  @After
  public void closeShell() throws InterruptedException {
    // close the shell
    Display.getDefault().syncExec(new Runnable() {
      public void run() {
        shell.close();
      }
    });
  }

  @Test
  public void testEmptyInput() {
    assertResultGivenInput(" ", "Empty input");
  }

  protected void assertResultGivenInput(String input, String expectedResult) {
    bot.textWithLabel("input").setText(input);
    bot.button("Compute").click();
    assertEquals(expectedResult, bot.textWithLabel("result").getText());
  }

}
