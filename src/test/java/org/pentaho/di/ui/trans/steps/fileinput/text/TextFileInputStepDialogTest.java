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
package org.pentaho.di.ui.trans.steps.fileinput.text;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.steps.fileinput.text.TextFileInputMeta;
import org.pentaho.di.ui.trans.step.StepDialogTestBase;

import java.util.concurrent.Callable;

@RunWith( BlockJUnit4ClassRunner.class )
public class TextFileInputStepDialogTest extends StepDialogTestBase {
  private static Class<?> PKG = TextFileInputMeta.class;

  public static final String DIALOG_NAME = "My Dialog";

  protected TextFileInputDialog textFileInputDialog;
  protected TextFileInputMeta textFileInputMeta;

  @Before
  public void setUp() {
    // Create the objects under test (meta and dialog)
    textFileInputMeta = new TextFileInputMeta();
    textFileInputMeta.setDefault();
    textFileInputDialog = new TextFileInputDialog( parent, textFileInputMeta, transMeta, DIALOG_NAME );
  }


  @Test
  public void testEmptyInputOK() throws Exception {

    createTest(
      BaseMessages.getString( PKG, "TextFileInputDialog.DialogTitle" ),
      textFileInputDialog,
      "OK", // Button to press to dispose the dialog -- OK or Cancel
      null
    );
    assertEquals( DIALOG_NAME, openUI() );
    verifyResults();
  }

  @Test
  public void testEmptyInputCancel() throws Exception {

    createTest(
      BaseMessages.getString( PKG, "TextFileInputDialog.DialogTitle" ),
      textFileInputDialog,
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
      textFileInputDialog,
      "OK",
      new Callable<Void>() {

        public Void call() throws Exception {
          assertResultGivenInput( "Step name ", "My Step", "My Step" );
          return null;
        }
      } );
    assertEquals( "My Step", openUI() );
    verifyResults(); // Invokes the callable given above
  }

  @Test
  public void testChangeStepNameCancel() throws Exception {

    createTest(
      BaseMessages.getString( PKG, "TextFileInputDialog.DialogTitle" ),
      textFileInputDialog,
      "Cancel",
      new Callable<Void>() {

        public Void call() throws Exception {
          assertResultGivenInput( "Step name ", "My Step", "My Step" );
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
      textFileInputDialog,
      "OK",
      new Callable<Void>() {

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
    String[] filenames = textFileInputMeta.getFileName();
    assertNotNull( filenames );
    assertEquals( 1, filenames.length );
    assertEquals( "file.txt", filenames[0] );
  }

  @Test
  public void testDialogContainsPreloadedMetadata() throws Exception {
    textFileInputMeta.setFileName( new String[]{ "/path/to/files/", "/path/to/other/files/" } );
    textFileInputMeta.inputFiles.fileMask = new String[]{ ".*", ".*\\.k.." };
    textFileInputMeta.inputFiles.excludeFileMask = new String[]{ "", ".*\\.ktr" };
    textFileInputMeta.inputFiles.fileRequired = new String[]{ "Y", "N" };
    textFileInputMeta.inputFiles.includeSubFolders = new String[]{ "N", "Y" };

    textFileInputDialog = new TextFileInputDialog( parent, textFileInputMeta, transMeta, DIALOG_NAME );

    createTest(
      BaseMessages.getString( PKG, "TextFileInputDialog.DialogTitle" ),
      textFileInputDialog,
      "OK",
      new Callable<Void>() {

        public Void call() throws Exception {
          SWTBotTable table = bot.table( 0 );
          assertNotNull( table );
          assertEquals( "/path/to/files/", table.cell( 0, 1 ) );
          assertEquals( ".*", table.cell( 0, 2 ) );
          assertEquals( "", table.cell( 0, 3 ) );
          assertEquals( "Y", table.cell( 0, 4 ) );
          assertEquals( "N", table.cell( 0, 5 ) );

          assertEquals( "/path/to/other/files/", table.cell( 1, 1 ) );
          assertEquals( ".*\\.k..", table.cell( 1, 2 ) );
          assertEquals( ".*\\.ktr", table.cell( 1, 3 ) );
          assertEquals( "N", table.cell( 1, 4 ) );
          assertEquals( "Y", table.cell( 1, 5 ) );
          return null;
        }
      } );
    openUI();
    verifyResults();
  }

}
