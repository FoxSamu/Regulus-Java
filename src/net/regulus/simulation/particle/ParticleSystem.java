package net.regulus.simulation.particle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.sat.SAT;
import net.regulus.geom.AABB;
import net.regulus.geom.MathUtil;
import net.regulus.geom.Vec2;
import net.regulus.geom.VectorList;
import net.regulus.simulation.Body;
import net.regulus.simulation.World;

public class ParticleSystem {
    public final List<Particle> particles = new ArrayList<>();
    public final World world;

    private final List<BodyCollision> bodyCollisions = new ArrayList<>();
    private final List<ParticleCollision> particleCollisions = new ArrayList<>();

    public ParticleSystem( World world ) {
        this.world = world;
    }

    public void update( double dt ) {
        bodyCollisions.clear();
        particleCollisions.clear();
        AABB bodyBox = new AABB();
        AABB particleBox = new AABB();
        ParticleCollider particleColl = new ParticleCollider();
        for( Particle p : particles ) {
            particleBox.setCenterSize( p.position, p.radius * 2, p.radius * 2 );
            for( Body b : world.bodies ) {
                b.getCollider().getBox( bodyBox );

                if( AABB.overlap( bodyBox, particleBox ) ) {
                    particleColl.set( p.position, p.radius, particleBox );
                    SAT.collide(
                        particleColl, b.getCollider(),
                        c -> bodyCollisions.add( new BodyCollision( b, p, c ) )
                    );
                }
            }
        }
        for( BodyCollision coll : bodyCollisions ) {
            coll.correct();
            coll.solve();
        }
        for( Particle particle : particles ) {
            particle.getInteractingParticles( particleCollisions );
        }
        for( ParticleCollision coll : particleCollisions ) {
            coll.solve( dt );
        }
        Vec2 v1 = new Vec2();
        for( Particle particle : particles ) {
            particle.update( dt );
            particle.position.add( particle.velocity.mul( dt, v1 ), particle.position );
        }
    }

    public void getParticlesInArea( Vec2 center, double radius, Collection<? super Particle> out ) {
        for( Particle p : particles ) {
            if( p.position.distSq( center ) < radius * radius ) {
                out.add( p );
            }
        }
    }

    public static class BodyCollision {
        public final Body body;
        public final VectorList points;
        public final Vec2 normal;
        public final double depth;
        public final Particle particle;

        public BodyCollision( Body body, Particle particle, CollisionPrimer primer ) {
            this.body = body;
            this.points = primer.collisionB;
            this.normal = primer.normal;
            this.depth = primer.penetrationDepth;
            this.particle = particle;
        }

        public void correct() {
            double percent = 0.2;
            double slop = 0.01;

            Vec2 correction = normal.mul( Math.max( depth - slop, 0 ) / ( body.mass.getInvMass() + particle.invMass() ) * percent, null );
            Vec2 use = new Vec2();
            body.positionalVel.add( correction.mul( -body.mass.getInvMass(), use ), body.positionalVel );
            body.position.add( correction.mul( -body.mass.getInvMass(), use ), body.position );
            particle.position.sub( correction.mul( -particle.invMass(), use ), particle.position );
        }

        public void solve() {
            Vec2 v1 = new Vec2();
            Vec2 v2 = new Vec2();
            Vec2 rv = new Vec2();
            Vec2 impulse = new Vec2();
            Vec2 tangent = new Vec2();
            Vec2 contactA = new Vec2();
            Vec2 contactB = new Vec2();

            int contactAmount = points.size();

            for( int i = 0; i < contactAmount; i++ ) {
                points.get( i, contactA ).sub( body.position, contactA );
                contactB.set( particle.position );
                double racn = contactA.cross( normal );
                double invMassSum = body.mass.getInvMass() + particle.invMass();
                double invInrtSum = racn * racn * body.mass.getInvInertia();
                double invSum = invMassSum + invInrtSum;


                // RESTITUTION
                particle.velocity.sub( body.getCombinedVelo( normal.invPerp( v1 ), contactA, v2 ), rv );
                MathUtil.tryMakeZero( rv );

                double velAlongNormal = rv.dot( normal );
                if( velAlongNormal > 0 ) {
                    return; // Velocities are separating the bodies
                }

                double normalImpulse = - velAlongNormal;
                normalImpulse /= invSum;
                normalImpulse /= contactAmount;

                normal.mul( normalImpulse, impulse );
                body.addImpulse( impulse.neg( v1 ), contactA, false );
                particle.velocity.add( impulse.mul( particle.invMass(), v1 ), particle.velocity );


                // FRICTION
                particle.velocity.sub( body.getCombinedVelo( normal.invPerp( v1 ), contactA, v2 ), rv );
                MathUtil.tryMakeZero( rv );

                normal.perp( tangent );

                double velAlongTangent = rv.dot( tangent );

                if( velAlongTangent > 0 ) {
                    tangent.neg( tangent );
                    velAlongTangent = - velAlongTangent;
                }
                tangent.norm( tangent );

                double tangentImpulse = - velAlongTangent * body.particleFriction;
                tangentImpulse /= invSum;
                tangentImpulse /= contactAmount;
                tangentImpulse = MathUtil.tryMakeZero( tangentImpulse );

                tangent.mul( tangentImpulse, impulse );

                body.addImpulse( impulse.neg( v1 ), contactA, true );
                particle.velocity.add( impulse.mul( particle.invMass(), v1 ), particle.velocity );
            }
        }
    }

    public static abstract class ParticleCollision {
        public final Particle particleA;
        public final Particle particleB;
        public final Vec2 normal = new Vec2();
        public final double dist;

        public ParticleCollision( Particle particleA, Particle particleB ) {
            this.particleA = particleA;
            this.particleB = particleB;

            particleA.position.sub( particleB.position, normal ).norm( normal );
            dist = particleA.position.dist( particleB.position );
        }

        public abstract void solve( double dt );
    }
}
