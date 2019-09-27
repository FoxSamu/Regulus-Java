package test.cases;

import test.CanvasContext;
import test.TestCase;
import test.TestColors;

import javax.swing.JMenu;
import java.awt.Color;

import net.regulus.collider.*;
import net.regulus.geom.Vec2;
import net.regulus.simulation.Body;
import net.regulus.simulation.World;
import net.regulus.simulation.constraint.*;

public class ConstraintPhysicsTest implements TestCase {
    private World world;

    @Override
    public String getName() {
        return "Constraint Physics Test";
    }

    @Override
    public void draw( CanvasContext ctx, double mouseX, double mouseY ) {
        ctx.fills( true ).outlines( false );

        for( Body b : world.bodies ) {
            if( b instanceof ColoredBody ) {
                ctx.color( ( (ColoredBody) b ).color, 1 );
                ctx.drawCollider( b.getCollider() );
            }
        }

        ctx.color( TestColors.WHITE );
        for( IConstraint c : world.constraints ) {
            if( ! c.enabled() )
                continue;
            if( c instanceof LinkingConstraint ) {
                LinkingConstraint lc = (LinkingConstraint) c;
                if( lc instanceof AxleConstraint ) {
                    ctx.drawCircPoint( lc.globalA( null ), 4, 2 );
                }
                if( lc instanceof DistanceConstraint || lc instanceof SpringConstraint ) {
                    Vec2 v1 = lc.globalA( null );
                    Vec2 v2 = lc.globalB( null );
                    ctx.drawLine( v1, v2 );
                    ctx.drawPoint( v1, 2 );
                    ctx.drawPoint( v2, 2 );
                }
            }
        }

    }

    @Override
    public void tick( double x, double y ) {
        double dt = 0.2;
        int itr = 20;
        for( int i = 0; i < itr; i++ ) {
            world.tick( dt / itr );
        }
    }

    @Override
    public void init() {
        world = new World();

        Body.builder()
            .collider( ICollider.plane().rotationOff( Math.PI / 2 ) )
            .addTo( world )
            .makeStatic()
            .position( 0, - 5 )
            .restitution( 0.3 )
            .build( new ColoredBody( TestColors.STATIC ) );

        Body.builder()
            .collider( ICollider.plane() )
            .addTo( world )
            .makeStatic()
            .position( - 5, 0 )
            .restitution( 0.3 )
            .build( new ColoredBody( TestColors.STATIC ) );

        Body.builder()
            .collider( ICollider.plane().rotationOff( Math.PI ) )
            .addTo( world )
            .makeStatic()
            .position( 5, 0 )
            .restitution( 0.3 )
            .build( new ColoredBody( TestColors.STATIC ) );

        Body body = Body.builder()
                        .collider( ICollider.box().size( 0.4, 4 ) )
                        .density( 1 )
                        .rotation( 1 )
                        .restitution( 0.3 )
                        .addTo( world )
                        .build( new ColoredBody( TestColors.ROPE ) );

        Body ball = Body.builder()
                        .collider( ICollider.circle().radius( 0.3 ) )
                        .density( 1 )
                        .position( ( Math.random() - Math.random() ) * 3, 0 )
                        .restitution( 0.3 )
                        .addTo( world )
                        .build( new ColoredBody( TestColors.randomColor() ) );

        IConstraint.spring()
                   .a( body, 0, -2 )
                   .b( ball, 0, 0.15 )
                   .length( 1 )
                   .constant( 0.05 )
                   .damping( 0.01 )
                   .addTo( world )
                   .build();

        IConstraint.axle()
                   .a( body, 0, 2 )
                   .b( null, 0, 2 )
                   .addTo( world )
                   .build();

        for( int i = 0; i < 5; i++ ) {
            Body.builder()
                .collider(
                    ICollider.capsule().radius( 0.2 ).length( 0.4 )
                )
                .density( 1 )
                .position( ( Math.random() - Math.random() ) * 3, 2 + i * 1.5 + Math.random() )
                .restitution( 0.3 )
                .addTo( world )
                .build( new ColoredBody( TestColors.randomColor() ) );

            Body.builder()
                .collider(
                    ICollider.box().size( 0.8, 0.2 ).build(),
                    ICollider.box().size( 0.2, 0.8 ).build()
                )
                .density( 1 )
                .position( ( Math.random() - Math.random() ) * 3, 2 + i * 1.5 + Math.random() )
                .restitution( 0.3 )
                .addTo( world )
                .build( new ColoredBody( TestColors.randomColor() ) );

            Body.builder()
                .collider( ICollider.circle().radius( 0.3 ) )
                .density( 1 )
                .position( ( Math.random() - Math.random() ) * 3, 2 + i * 1.5 + Math.random() )
                .restitution( 0.3 )
                .addTo( world )
                .build( new ColoredBody( TestColors.randomColor() ) );

            Body.builder()
                .collider( ICollider.regularPoly().radius( 0.3 ).sides( (int) ( Math.random() * 7 ) + 3 ) )
                .density( 1 )
                .position( ( Math.random() - Math.random() ) * 3, 2 + i * 1.5 + Math.random() )
                .restitution( 0.3 )
                .addTo( world )
                .build( new ColoredBody( TestColors.randomColor() ) );

            Body.builder()
                .collider( ICollider.box().size( 0.6, 0.3 ) )
                .density( 1 )
                .position( ( Math.random() - Math.random() ) * 3, 2 + i * 1.5 + Math.random() )
                .restitution( 0.3 )
                .addTo( world )
                .build( new ColoredBody( TestColors.randomColor() ) );
        }

        world.init();
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
