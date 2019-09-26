package test.cases;

import test.CanvasContext;
import test.TestCase;
import test.TestColors;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import net.regulus.collider.BarCollider;
import net.regulus.collider.PlaneCollider;
import net.regulus.collider.PolygonCollider;

public class ShapesTest implements TestCase {
    private double angle = 0;
    private BarCollider bar = new BarCollider();
    private PlaneCollider plane = new PlaneCollider();
    private PolygonCollider poly = new PolygonCollider();
    private boolean fills = true;

    @Override
    public String getName() {
        return "Shapes Test";
    }

    @Override
    public void draw( CanvasContext ctx, double mouseX, double mouseY ) {
        ctx.outlines( !fills );
        ctx.fills( fills );

        bar.setOffset( -5, 0 );
        bar.setRotationOffset( 0.3 );
        bar.setWidth( 2 );
        plane.setOffset( 0, -5 );
        plane.setRotationOffset( 0.3 + Math.PI / 2 );
        poly.setOffset( 2.5, 2.5 );
        poly.setRotationOffset( angle );
        ctx.color( TestColors.STATIC ).drawCollider( bar );
        ctx.color( TestColors.STATIC ).drawCollider( plane );
        ctx.color( TestColors.PURPLE ).drawCollider( poly );
        ctx.color( TestColors.RED ).drawCircle( -2.5, 0, angle, 1, true );
        ctx.color( TestColors.CYAN ).drawCapsule( -2.5, -2.5, angle, 0.6, 1.2 );
        ctx.color( TestColors.BLUE ).drawRegularPolygon( 0, 0, angle, 1, 5 );
        ctx.color( TestColors.GREEN ).drawRegularPolygon( 0, 2.5, angle, 1, 8 );
        ctx.color( TestColors.YELLOW ).drawRegularPolygon( 0, - 2.5, angle, 1, 3 );
        ctx.color( TestColors.ORANGE ).drawBox( 2.5, 0, angle, 2, 1 );

    }

    @Override
    public void tick( double x, double y ) {
        angle += Math.PI / 100D;
    }

    @Override
    public void init() {
        angle = 0;
        poly.reset();
        poly.add( -1, 0 );
        poly.add( 0, 1 );
        poly.add( 0, 0 );
        poly.add( 1, 0 );
        poly.add( 0, -1 );
    }

    @Override
    public JMenu setupMenu() {
        JMenu menu = new JMenu( "Shapes Test" );
        JCheckBoxMenuItem item = new JCheckBoxMenuItem( "Wireframes" );
        item.addChangeListener( e -> fills = ! item.getState() );
        item.setState( ! fills );
        menu.add( item );
        return menu;
    }
}
