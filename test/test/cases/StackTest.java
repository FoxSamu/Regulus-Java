package test.cases;

import test.CanvasContext;
import test.TestCase;
import test.TestColors;

import javax.swing.JMenu;
import java.awt.Color;

import net.regulus.collider.ICollider;
import net.regulus.collider.PlaneCollider;
import net.regulus.simulation.Body;
import net.regulus.simulation.World;

public class StackTest implements TestCase {
    private World world;

    @Override
    public String getName() {
        return "Stack Test";
    }

    @Override
    public void draw( CanvasContext ctx, double mouseX, double mouseY ) {
        ctx.fills( true ).outlines( false );

        for( Body b : world.bodies ) {
            if( b instanceof ColoredBody ) {
                ctx.color( ( (ColoredBody) b ).color );
                ctx.drawCollider( b.getCollider() );
            }
        }

//        Vec2 vec = new Vec2();
//        for ( CollisionSet collisionSet : world.collisions ) {
//            for ( Collision collision : collisionSet.collisions) {
//                ctx.color( Color.WHITE );
//                for( Vec2.IContext vecC : collision.pointsA ) {
//                    vecC.get( vec );
//
//                    ctx.drawPoint( vec, 3 );
//                    ctx.drawNormal( collision.normal, vec, 10 );
//                }
//
//                Vec2 antiNormal = collision.normal.neg( new Vec2() );
//                for( Vec2.IContext vecC : collision.pointsB ) {
//                    vecC.get( vec );
//
//                    ctx.drawPoint( vec, 3 );
//                    ctx.drawNormal( antiNormal, vec, 10 );
//                }
//            }
//        }
    }

    @Override
    public void tick( double x, double y ) {
        double dt = 0.2;
        int itr = 30;
        for( int i = 0; i < itr; i++ ) {
            world.tick( dt / itr );
        }
    }

    @Override
    public void init() {
        world = new World();

        ColoredBody ground = new ColoredBody();
        ground.setCollider( new PlaneCollider() );
        ( (PlaneCollider) ground.getCollider() ).setRotationOffset( Math.PI / 2 );
        ground.mass.setStatic();
        ground.position.set( 0, - 5 );
        world.add( ground );

        ground = new ColoredBody();
        ground.setCollider( new PlaneCollider() );
        ( (PlaneCollider) ground.getCollider() ).setRotationOffset( 0 );
        ground.mass.setStatic();
        ground.position.set( - 5, 0 );
        ground.restitution = 0;
        world.add( ground );

        ground = new ColoredBody();
        ground.setCollider( new PlaneCollider() );
        ( (PlaneCollider) ground.getCollider() ).setRotationOffset( Math.PI );
        ground.mass.setStatic();
        ground.position.set( 5, 0 );
        ground.restitution = 0;
        world.add( ground );

        for( int i = 0; i < 15; i++ ) {
            Body.builder()
                .collider( ICollider.box().size( 0.7, 0.7 ) )
                .density( 1 )
                .position( 0.05 * (Math.random() - Math.random()), 2 + i * 1.5 )
                .addTo( world )
                .build( new ColoredBody( TestColors.randomColor() ) );
        }

//        Body.builder()
//            .collider( ICollider.box().size( 3, 0.7 ) )
//            .density( 1 )
//            .position( 0.6, 2 + 30 * 1.5 )
//            .rotation( 0.2 )
//            .addTo( world )
//            .build( new ColoredBody( TestColors.randomColor() ) );
//
//
//        Body.builder()
//            .collider( ICollider.circle().radius( 0.8 ) )
//            .density( 1 )
//            .position( -3, 2 + 20 * 1.5 )
//            .rotation( 0.2 )
//            .addTo( world )
//            .build( new ColoredBody( TestColors.randomColor() ) );
//
//        Body.builder()
//            .collider( ICollider.regularPoly().radius( 0.8 ).sides( 6 ) )
//            .density( 1 )
//            .position( 3, 2 + 25 * 1.5 )
//            .rotation( 0.2 )
//            .addTo( world )
//            .build( new ColoredBody( TestColors.randomColor() ) );
    }

    @Override
    public JMenu setupMenu() {
        return null;
    }

    public static class ColoredBody extends Body {
        public Color color = TestColors.STATIC;

        public ColoredBody() {

        }

        public ColoredBody( Color color ) {
            this.color = color;
        }
    }
}
