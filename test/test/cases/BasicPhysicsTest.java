package test.cases;

import test.CanvasContext;
import test.TestCase;
import test.TestColors;

import javax.swing.JMenu;
import java.awt.Color;

import net.regulus.collider.*;
import net.regulus.simulation.Body;
import net.regulus.simulation.World;

public class BasicPhysicsTest implements TestCase {
    private World world;

    @Override
    public String getName() {
        return "Basic Physics Test";
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

        ground = new ColoredBody();
        BoxCollider groundCollider = new BoxCollider();
        groundCollider.setSize( 10, 0.4 );
        groundCollider.setRotationOffset( - 0.3 );
        ground.setCollider( groundCollider );
        ground.mass.setStatic();
        ground.position.set( - 2.5, 0 );
        ground.restitution = 0;
        world.add( ground );

        for( int i = 0; i < 15; i++ ) {
            Body.builder()
                .collider(
                    ICollider.capsule().radius( 0.2 ).length( 0.4 )
                )
                .density( 1 )
                .position( ( Math.random() - Math.random() ) * 3, 2 + i * 1.5 + Math.random() )
                .addTo( world )
                .build( new ColoredBody( TestColors.randomColor() ) );

            Body.builder()
                .collider(
                    ICollider.box().size( 0.8, 0.2 ).build(),
                    ICollider.box().size( 0.2, 0.8 ).build()
                )
                .density( 1 )
                .position( ( Math.random() - Math.random() ) * 3, 2 + i * 1.5 + Math.random() )
                .addTo( world )
                .build( new ColoredBody( TestColors.randomColor() ) );

            Body.builder()
                .collider( ICollider.circle().radius( 0.3 ) )
                .density( 1 )
                .position( ( Math.random() - Math.random() ) * 3, 2 + i * 1.5 + Math.random() )
                .addTo( world )
                .build( new ColoredBody( TestColors.randomColor() ) );

            Body.builder()
                .collider( ICollider.regularPoly().radius( 0.3 ).sides( (int) ( Math.random() * 7 ) + 3 ) )
                .density( 1 )
                .position( ( Math.random() - Math.random() ) * 3, 2 + i * 1.5 + Math.random() )
                .addTo( world )
                .build( new ColoredBody( TestColors.randomColor() ) );

            Body.builder()
                .collider( ICollider.box().size( 0.6, 0.3 ) )
                .density( 1 )
                .position( ( Math.random() - Math.random() ) * 3, 2 + i * 1.5 + Math.random() )
                .addTo( world )
                .build( new ColoredBody( TestColors.randomColor() ) );
        }
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
