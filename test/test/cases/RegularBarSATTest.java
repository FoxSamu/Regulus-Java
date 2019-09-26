package test.cases;

import test.CanvasContext;
import net.regulus.detection.sat.PolygonBarSAT;
import test.TestCase;
import test.TestColors;

import javax.swing.JMenu;
import java.util.ArrayList;
import java.util.List;

import net.regulus.collider.BarCollider;
import net.regulus.collider.RegularPolyCollider;
import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.ICollisionCollector;
import net.regulus.geom.Vec2;

public class RegularBarSATTest implements TestCase, ICollisionCollector {

    private final RegularPolyCollider collider1 = new RegularPolyCollider();
    private final BarCollider collider2 = new BarCollider();
    private final List<CollisionPrimer> collisions = new ArrayList<>( 1 );

    @Override
    public String getName() {
        return "Regular-Bar SAT Test";
    }

    @Override
    public void draw( CanvasContext ctx, double mouseX, double mouseY ) {

        ctx.fills( true );
        ctx.outlines( true );

        ctx.color( TestColors.ORANGE, 0.3 ).drawPolygon( collider1 );
        ctx.color( TestColors.BLUE, 0.3 ).drawInfBar( collider2 );

        for( CollisionPrimer primer : collisions ) {
            ctx.color( TestColors.WHITE );
            Vec2 norm = primer.normal.mul( primer.penetrationDepth, null );
            if( primer.collisionA.isEmpty() ) {
                ctx.drawVector( norm );
            } else {
                Vec2 basePt = primer.collisionA.get( 0, null );
                ctx.drawVector( norm, basePt );
            }

            ctx.color( TestColors.YELLOW );

            Vec2 d = new Vec2();
            for( Vec2.IContext c : primer.collisionA ) {
                c.get( d );
                ctx.drawPoint( d, 3 );
            }
            for( Vec2.IContext c : primer.collisionB ) {
                c.get( d );
                ctx.drawPoint( d, 3 );
            }
        }
    }

    @Override
    public void tick( double x, double y ) {
        collider1.setOffset( x, y );
        collisions.clear();
        PolygonBarSAT.collide( collider1, collider2, this );
    }



    @Override
    public void init() {
        collider2.setOffset( 0, 0 );
        collider2.setRotationOffset( Math.PI / 4 );
        collider2.setWidth( 3 );
        collider1.setRadius( 1 );
        collider1.setSides( 5 );
        collider1.setRotationOffset( 0 );
    }

    @Override
    public JMenu setupMenu() {
        return null;
    }

    @Override
    public void addCollision( CollisionPrimer primer ) {
        collisions.add( primer );
    }
}
