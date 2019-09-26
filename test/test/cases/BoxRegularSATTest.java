package test.cases;

import test.CanvasContext;
import net.regulus.detection.sat.PolygonPolygonSAT;
import test.TestCase;
import test.TestColors;

import javax.swing.JMenu;
import java.util.ArrayList;
import java.util.List;

import net.regulus.collider.BoxCollider;
import net.regulus.collider.RegularPolyCollider;
import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.ICollisionCollector;
import net.regulus.geom.Vec2;

public class BoxRegularSATTest implements TestCase, ICollisionCollector {

    private final RegularPolyCollider collider2 = new RegularPolyCollider();
    private final BoxCollider collider1 = new BoxCollider();
    private final List<CollisionPrimer> collisions = new ArrayList<>( 1 );

    @Override
    public String getName() {
        return "Box-Regular SAT Test";
    }

    @Override
    public void draw( CanvasContext ctx, double x, double y ) {

        ctx.fills( true );
        ctx.outlines( true );

        ctx.color( TestColors.ORANGE, 0.3 ).drawPolygon( collider1 );
        ctx.color( TestColors.BLUE, 0.3 ).drawPolygon( collider2 );



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

        PolygonPolygonSAT.collide( collider1, collider2, this );
    }



    @Override
    public void init() {
        collider2.setOffset( 0, 0 );
        collider2.setRotationOffset( 0 );
        collider2.setRadius( 2 );
        collider2.setSides( 7 );
        collider1.setSize( 2, 1 );
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
