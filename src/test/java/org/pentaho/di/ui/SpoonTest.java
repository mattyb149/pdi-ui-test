package org.pentaho.di.ui;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;


import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotBrowser;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.gui.GUIFactory;
import org.pentaho.di.core.gui.SpoonFactory;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.pan.CommandLineOption;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.steps.fileinput.text.TextFileInputMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.widget.OsHelper;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.trans.steps.fileinput.text.TextFileInputDialog;
import org.pentaho.di.ui.util.ThreadGuiResources;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Future;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;

/**
 * Created by mburgess on 10/23/15.
 */
@RunWith( SWTBotJunit4ClassRunner.class )
public class SpoonTest extends SWTBotTestCase {

  private static SWTBot bot;

  private static Spoon staticSpoon;

  private static Matcher<Widget> allMatcher = allOf( widgetOfType( Widget.class ) );

  private final static CyclicBarrier swtBarrier = new CyclicBarrier(2);

  static {

    Future<KettleException> pluginRegistryFuture = Spoon.getKettleEnvironmentInitFuture();

    try {
      OsHelper.setAppName();
      // Bootstrap Kettle
      //
      Display display = Spoon.initDisplay( null );

      // Note: this needs to be done before the look and feel is set
      OsHelper.initOsHandlers( display );

      UIManager.setLookAndFeel( new MetalLookAndFeel() );

      List<String> args = Collections.emptyList();

      CommandLineOption[] commandLineOptions = Spoon.getCommandLineArgs( args );

      KettleException registryException = pluginRegistryFuture.get();
      if ( registryException != null ) {
        throw registryException;
      }

      PropsUI.init( display, Props.TYPE_PROPERTIES_SPOON );

      KettleLogStore
        .init( PropsUI.getInstance().getMaxNrLinesInLog(), PropsUI.getInstance().getMaxLogLineTimeoutMinutes() );

      //initLogging( commandLineOptions );
      // remember...

      staticSpoon = new Spoon();
      staticSpoon.setCommandLineOptions( commandLineOptions );
      // pull the startup perspective id from the command line options and hand it to Spoon
      /*String pId;
      StringBuffer perspectiveIdBuff = Spoon.getCommandLineOption( commandLineOptions, "perspective" ).getArgument();
      pId = perspectiveIdBuff.toString();
      if ( !Const.isEmpty( pId ) ) {
        staticSpoon.startupPerspective = pId;
      }*/
      SpoonFactory.setSpoonInstance( staticSpoon );
      staticSpoon.setDestroy( true );
      GUIFactory.setThreadDialogs( new ThreadGuiResources() );

      staticSpoon.setArguments( args.toArray( new String[args.size()] ) );

      staticSpoon.open();

      bot = new SWTBot();

      SWTBotPreferences.TIMEOUT = 10000;

    } catch ( Exception e ) {
      e.printStackTrace();
      fail( "Didn't init" );
    }


  }

  //@Test
  public void test1() {
    try {
      assertNotNull( bot );

      printWidgets(Spoon.getInstance().getShell());

    } catch ( WidgetNotFoundException wnfe ) {
      wnfe.printStackTrace();
      fail( "Something went wrong" );
    }
  }


  private void printWidgets(Shell shell) {
    List<? extends Widget> widgets = bot.widgets( allMatcher, shell );
    assertNotNull( widgets );
    for ( Widget w : widgets ) {

      System.out.println( w.getClass().getCanonicalName() );
    }
  }
}
