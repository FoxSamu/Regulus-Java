package test;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.*;

import net.regulus.geom.Vec2;

public class CanvasComponent extends JComponent implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {
    private final CanvasContext ctx = new CanvasContext();

    private TestCase testCase;
    private double transX = 0;
    private double transY = 0;
    private double scale = 50;

    private double panX = 0;
    private double panY = 0;
    private double originalX = 0;
    private double originalY = 0;
    private boolean paused;

    public CanvasComponent() {
        addMouseListener( this );
        addMouseMotionListener( this );
        addMouseWheelListener( this );
        addKeyListener( this );
    }

    @Override
    public void paint( Graphics g ) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        g2d.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2d.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );

        ctx.setup( g2d, getWidth(), getHeight() );

        ctx.translate( transX, transY );
        ctx.scale( scale );

        double diff = scale - scale;
        scale += diff * 0.4;

        ctx.clear();
        if( testCase != null ) {
            Point p = MouseInfo.getPointerInfo().getLocation();
            Point s = getLocationOnScreen();
            p.translate( - s.x, - s.y );
            Vec2 pos = new Vec2( p.x, p.y );
            ctx.canvasToWorld( pos, pos );
            if( ! paused ) {
                testCase.tick( pos.x, pos.y );
            }
            testCase.draw( ctx, pos.x, pos.y );
        }
        g2d.dispose();
    }

    public void setTestCase( TestCase testCase ) {
        this.testCase = testCase;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    @Override
    public void mouseClicked( MouseEvent e ) {

    }

    @Override
    public void mousePressed( MouseEvent e ) {
        if( SwingUtilities.isLeftMouseButton( e ) ) {
            Vec2 pos = new Vec2( e.getX(), e.getY() );
            ctx.canvasToWorld( pos, pos );
            testCase.mouseDown( pos.x, pos.y );
        } else if( SwingUtilities.isRightMouseButton( e ) || SwingUtilities.isMiddleMouseButton( e ) ) {
            panX = e.getX();
            panY = e.getY();
            originalX = transX;
            originalY = transY;
        }
    }

    @Override
    public void mouseReleased( MouseEvent e ) {
        if( SwingUtilities.isLeftMouseButton( e ) ) {
            Vec2 pos = new Vec2( e.getX(), e.getY() );
            ctx.canvasToWorld( pos, pos );
            testCase.mouseUp( pos.x, pos.y );
        }
    }

    @Override
    public void mouseEntered( MouseEvent e ) {

    }

    @Override
    public void mouseExited( MouseEvent e ) {

    }

    @Override
    public void mouseDragged( MouseEvent e ) {
        if( SwingUtilities.isLeftMouseButton( e ) ) {
            Vec2 pos = new Vec2( e.getX(), e.getY() );
            ctx.canvasToWorld( pos, pos );
            testCase.mouseDrag( pos.x, pos.y );
        } else if( SwingUtilities.isRightMouseButton( e ) || SwingUtilities.isMiddleMouseButton( e ) ) {
            double offX = e.getX() - panX;
            double offY = e.getY() - panY;
            transX = originalX + offX;
            transY = originalY + offY;
        }
    }

    @Override
    public void mouseMoved( MouseEvent e ) {
        if( SwingUtilities.isLeftMouseButton( e ) ) {
            Vec2 pos = new Vec2( e.getX(), e.getY() );
            ctx.canvasToWorld( pos, pos );
            testCase.mouseMove( pos.x, pos.y );
        }
    }

    @Override
    public void keyTyped( KeyEvent e ) {

    }

    @Override
    public void keyPressed( KeyEvent e ) {
        Point p = MouseInfo.getPointerInfo().getLocation();
        Point s = getLocationOnScreen();
        p.translate( - s.x, - s.y );
        Vec2 pos = new Vec2( p.x, p.y );
        ctx.canvasToWorld( pos, pos );
        testCase.keyPress( pos.x, pos.y, e.getKeyCode(), e.getModifiers() );
    }

    @Override
    public void keyReleased( KeyEvent e ) {
        Point p = MouseInfo.getPointerInfo().getLocation();
        Point s = getLocationOnScreen();
        p.translate( - s.x, - s.y );
        Vec2 pos = new Vec2( p.x, p.y );
        ctx.canvasToWorld( pos, pos );
        testCase.keyRelease( pos.x, pos.y, e.getKeyCode(), e.getModifiers() );
    }

    @Override
    public void mouseWheelMoved( MouseWheelEvent e ) {
        double prevScale = scale;
        scale -= e.getPreciseWheelRotation() * scale / 50D;
        if( scale > 1000 )
            scale = 1000;
        if( scale < 10 )
            scale = 10;
        double diff = prevScale - scale;
        double delta = diff / prevScale;
        Vec2 c = new Vec2();
        ctx.worldToCanvas( c, c );
        Point mp = getMousePosition();
        Vec2 m = new Vec2( mp.x, mp.y );
        c.sub( m, c ).neg( c ).mul( delta, c );
        transX += c.x;
        transY += c.y;
    }

    public void setPaused( boolean paused ) {
        this.paused = paused;
    }
}
