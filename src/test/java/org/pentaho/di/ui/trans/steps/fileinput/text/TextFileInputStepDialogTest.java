package org.pentaho.di.ui.trans.steps.fileinput.text;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.steps.fileinput.text.TextFileInputMeta;
import org.pentaho.di.ui.StepDialogTest;

import java.util.concurrent.Callable;

/**
 * Created by mburgess on 10/26/15.
 */
@RunWith( BlockJUnit4ClassRunner.class )
public class TextFileInputStepDialogTest extends StepDialogTest {
  private static Class<?> PKG = TextFileInputMeta.class;

  public static final String DIALOG_NAME = "My Dialog";

  protected TextFileInputDialog dialog;
  protected TextFileInputMeta meta;

  @Before
  public void setUp() {
    // synchronize with the thread opening the shell
    meta = new TextFileInputMeta();
    meta.setDefault();
    dialog = new TextFileInputDialog( parent, meta, transMeta, DIALOG_NAME );
  }


  @Test
  public void testEmptyInputOK() throws Exception {

    createTest(
      BaseMessages.getString( PKG, "TextFileInputDialog.DialogTitle" ),
      dialog,
      "OK",
      null
    );
    assertEquals( DIALOG_NAME, openUI() );
    verifyResults();
  }

  @Test
  public void testEmptyInputCancel() throws Exception {

    createTest(
      BaseMessages.getString( PKG, "TextFileInputDialog.DialogTitle" ),
      dialog,
      "Cancel",
      null
    );
    assertNull( openUI() );
    verifyResults();
  }

  @Test
  public void testChangeStepNameOK() throws Exception {

    createTest(
      BaseMessages.getString( PKG, "TextFileInputDialog.DialogTitle" ),
      dialog,
      "OK",
      new Callable<Void>() {
        @Override
        public Void call() throws Exception {
          assertResultGivenInput( "My Step", "My Step" );
          return null;
        }
      } );
    assertEquals( "My Step", openUI() );
    verifyResults();
  }

  @Test
  public void testChangeStepNameCancel() throws Exception {

    createTest(
      BaseMessages.getString( PKG, "TextFileInputDialog.DialogTitle" ),
      dialog,
      "Cancel",
      new Callable<Void>() {
        @Override
        public Void call() throws Exception {
          assertResultGivenInput( "My Step", "My Step" );
          return null;
        }
      } );
    openUI();
    verifyResults();
  }

  @Test
  public void testAddFilename() throws Exception {

    createTest(
      BaseMessages.getString( PKG, "TextFileInputDialog.DialogTitle" ),
      dialog,
      "OK",
      new Callable<Void>() {
        @Override
        public Void call() throws Exception {
          bot.textWithLabel( BaseMessages.getString( PKG, "TextFileInputDialog.Filename.Label" ) ).setText( "file.txt" );
          assertEquals( "file.txt", bot.textWithLabel( BaseMessages.getString( PKG, "TextFileInputDialog.Filename.Label" ) ).getText() );
          try {
            bot.button( BaseMessages.getString( PKG, "TextFileInputDialog.FilenameAdd.Button" ) ).click();
            assertEquals( "", bot.textWithLabel( BaseMessages.getString( PKG, "TextFileInputDialog.Filename.Label" ) ).getText() );
          } catch ( Exception e ) {
            e.printStackTrace();
          }
          return null;
        }
      } );
    openUI();
    verifyResults();
    String[] filenames = meta.getFileName();
    assertNotNull( filenames );
    assertEquals( 1, filenames.length );
    assertEquals( "file.txt", filenames[0] );
  }

}
