package net.regulus.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.regulus.detection.sat.SAT;
import net.regulus.geom.Vec2;
import net.regulus.simulation.constraint.IConstraint;
import net.regulus.simulation.particle.ParticleSystem;

public class World {
    public final List<Body> bodies = new ArrayList<>();
    public final List<CollisionSet> collisions = new ArrayList<>();
    public final List<IConstraint> constraints = new ArrayList<>();
    public final List<ParticleSystem> particleSystems = new ArrayList<>();

    public final Vec2 gravity = new Vec2( 0, - 0.1 );

    public void init() {
        for( Body body : bodies ) {
            body.prepare();
        }
        for( IConstraint constraint : constraints ) {
            if( ! constraint.enabled() )
                continue;
            constraint.prepare();
        }
    }

    public void tick( double dt ) {
        for( Body body : bodies ) {
            body.prepare();
        }
        collisions.clear();
        for( int i = 0; i < bodies.size(); i++ ) {
            Body bodyA = bodies.get( i );
            for( int j = i + 1; j < bodies.size(); j++ ) {
                Body bodyB = bodies.get( j );

                collisionDetection( bodyA, bodyB );
            }
        }
        collisions.sort( Comparator.comparing( c -> -c.getCenter().dot( gravity ) ) );
        for( CollisionSet collision : collisions ) {
            collision.resolve( dt );
        }
        for( IConstraint constraint : constraints ) {
            if( ! constraint.enabled() )
                continue;
            constraint.resolve( dt );
        }
        for( CollisionSet collision : collisions ) {
            collision.correct();
        }
        for( IConstraint constraint : constraints ) {
            if( ! constraint.enabled() )
                continue;
            constraint.prepare();
            constraint.correct( dt );
        }
        for( ParticleSystem sys : particleSystems ) {
            sys.update( dt );
        }
        Vec2 v = new Vec2();
        for( Body body : bodies ) {
            body.applyTotalImpulse();
            if( ! body.mass.isStatic() ) {
                body.velocity.add( gravity.x * dt, gravity.y * dt, body.velocity );
                double drag = 1 - body.linearDrag * dt;
                body.velocity.mul( drag, body.velocity );
                double aDrag = 1 - body.angularDrag * dt;
                body.rotationVelo *= aDrag;
            }
            body.position.add( body.velocity.mul( dt, v ), body.position );
            body.rotation += body.rotationVelo * dt;
        }
    }

    public World add( Body b ) {
        bodies.add( b );
        return this;
    }

    public World add( Body... b ) {
        bodies.addAll( Arrays.asList( b ) );
        return this;
    }

    public World add( IConstraint constr ) {
        constraints.add( constr );
        return this;
    }

    public World add( IConstraint... constrs ) {
        constraints.addAll( Arrays.asList( constrs ) );
        return this;
    }

    private void collisionDetection( Body a, Body b ) {
        if( a == b )
            return;
        if( a.mass.isStatic() && b.mass.isStatic() )
            return;
        if( a.getCollider() == null || b.getCollider() == null )
            return;

        for( IConstraint constraint : constraints ) {
            if( ! constraint.enabled() )
                continue;
            if( constraint.collisionDisabled( a, b ) || constraint.collisionDisabled( b, a ) ) {
                return;
            }
        }

        CollisionSet set = new CollisionSet( a, b );
        SAT.collide( a.getCollider(), b.getCollider(), set );

        if( set.collisions.size() > 0 )
            collisions.add( set );
    }
}
