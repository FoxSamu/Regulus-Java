import test.CanvasComponent;
import test.Fullscreen;
import test.TestCase;
import test.TestCaseMenuItem;
import test.cases.*;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

public final class Main implements MenuListener {
    private final JFrame frame = new JFrame( "Regulus Test Environment" );

    private final JMenuBar menuBar = new JMenuBar();
    private final ButtonGroup testCaseGroup = new ButtonGroup();
    private JMenu testMenu;

    private TestCase current;
    private final CanvasComponent canvas = new CanvasComponent();
    private final Timer timer = new Timer( 1000 / 60, this::redraw );

    private boolean paused = false;

    private final List<TestCase> testCases = Arrays.asList(
        new ConstraintPhysicsTest(),
        new CapsuleBoxSATTest(),
        new CapsuleBarSATTest(),
        new CapsulePlaneSATTest(),
        new CapsulePolygonSATTest(),
        new CapsuleCapsuleSATTest(),
        new CircleCapsuleSATTest(),
        new BasicPhysicsTest(),
        new RegularBarSATTest(),
        new RegularPlaneSATTest(),
        new CircleBarSATTest(),
        new CirclePlaneSATTest(),
        new BoxBoxSATTest(),
        new RegularRegularSATTest(),
        new BoxRegularSATTest(),
        new BoxCircleSATTest(),
        new RegularCircleSATTest(),
        new CircleRegularSATTest(),
        new CircleBoxSATTest(),
        new CircleCircleSATTest(),
        new ShapesTest()
    );

    private Main() {
        frame.setContentPane( canvas );
        setupMenu();

        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        Fullscreen.makeFullscreenable( frame );
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation( screenSize.width / 2 - 400, screenSize.height / 2 - 300 );
        frame.setVisible( true );

        setup( testCases.get( 0 ) );

        canvas.setBackground( Color.BLACK );

        timer.setRepeats( true );
        timer.start();
    }

    private void redraw( ActionEvent e ) {
        canvas.setPaused( paused );
        canvas.repaint();
    }

    private void setupMenu() {
        frame.setJMenuBar( menuBar );

        JMenu menu = new JMenu( "Test Case" );
        menuBar.add( menu );

        JMenuItem restartItem = new JMenuItem( "Restart Current" );
        restartItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.META_DOWN_MASK ) );
        restartItem.addActionListener( e -> setup( current ) );
        menu.add( restartItem );

        JMenuItem pauseItem = new JMenuItem( paused ? "Play" : "Pause" );
        pauseItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, 0 ) );
        pauseItem.addActionListener( e -> {
            paused = ! paused;
            pauseItem.setText( paused ? "Play" : "Pause" );
        } );
        menu.add( pauseItem );

        JSeparator separator = new JSeparator();
        menu.add( separator );

        int i = 0;
        for( TestCase c : testCases ) {
            TestCaseMenuItem item = new TestCaseMenuItem( c );
            testCaseGroup.add( item );
            menu.add( item );

            item.addActionListener( e -> {
                TestCaseMenuItem src = (TestCaseMenuItem) e.getSource();
                if( src.getTestCase() == current )
                    return;
                setup( src.getTestCase() );
            } );

            if( i == 0 ) {
                item.setSelected( true );
            }


            i++;
        }

        menu.addMenuListener( this );
    }

    private void setup( TestCase tc ) {
        if( testMenu != null )
            menuBar.remove( testMenu );
        testMenu = tc.setupMenu();
        if( testMenu != null )
            menuBar.add( testMenu );

        canvas.setTestCase( tc );
        current = tc;
        tc.init();
    }

    public static void main( String[] args ) {
        System.setProperty( "apple.laf.useScreenMenuBar", "true" );
        System.setProperty( "com.apple.mrj.application.apple.menu.about.name", "Regulus Test Environment" );
        SwingUtilities.invokeLater( Main::new );
    }

    @Override
    public void menuSelected( MenuEvent e ) {

    }

    @Override
    public void menuDeselected( MenuEvent e ) {

    }

    @Override
    public void menuCanceled( MenuEvent e ) {

    }
}
