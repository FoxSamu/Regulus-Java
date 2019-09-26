package net.regulus.simulation;

import net.regulus.detection.CollisionPrimer;
import net.regulus.geom.MathUtil;
import net.regulus.geom.Vec2;
import net.regulus.geom.VectorList;

public class Collision {
    private final Body bodyA;
    private final Body bodyB;
    private final Vec2 normal;
    private final double depth;
    private final VectorList pointsA;
    private final VectorList pointsB;

    public Collision( Body a, Body b, CollisionPrimer primer ) {
        this.bodyA = a;
        this.bodyB = b;
        this.normal = primer.normal;
        this.depth = primer.penetrationDepth;
        this.pointsA = primer.collisionA;
        this.pointsB = primer.collisionB;
    }

    public void resolve( double dt ) {
        // All vector instantiations are moved out of the loop to keep allocation amount low
        Vec2 v1 = new Vec2();
        Vec2 v2 = new Vec2();
        Vec2 rv = new Vec2();
        Vec2 impulse = new Vec2();
        Vec2 tangent = new Vec2();
        Vec2 contactA = new Vec2();
        Vec2 contactB = new Vec2();

        int contactAmount = pointsA.size();

        for( int i = 0; i < contactAmount; i ++ ) {
            pointsA.get( i, contactA ).sub( bodyA.position, contactA );
            pointsB.get( i, contactB ).sub( bodyB.position, contactB );
            double racn = contactA.cross( normal );
            double rbcn = contactB.cross( normal );
            double invMassSum = bodyA.mass.getInvMass() + bodyB.mass.getInvMass();
            double invInrtSum = racn * racn * bodyA.mass.getInvInertia() + rbcn * rbcn * bodyB.mass.getInvInertia();
            double invSum = invMassSum + invInrtSum;


            // RESTITUTION
            bodyB.getCombinedVelo( normal.perp( v1 ), contactB, rv )
                 .sub( bodyA.getCombinedVelo( normal.invPerp( v1 ), contactA, v2 ), rv );
            MathUtil.tryMakeZero( rv );

            double velAlongNormal = rv.dot( normal );
            if( velAlongNormal < 0 ) {
                return; // Velocities are separating the bodies
            }

            double restitution = Math.min( bodyA.restitution, bodyB.restitution );
            double normalImpulse = - ( 1 + restitution ) * velAlongNormal;
            normalImpulse /= invSum;
            normalImpulse /= contactAmount;

            normal.mul( normalImpulse, impulse );
            bodyA.applyImpulse( impulse.neg( v1 ), contactA, false );
            bodyB.applyImpulse( impulse, contactB, false );


            // FRICTION
            bodyB.getCombinedVelo( normal.perp( v1 ), contactB, rv )
                 .sub( bodyA.getCombinedVelo( normal.invPerp( v1 ), contactA, v2 ), rv );
            MathUtil.tryMakeZero( rv );

            normal.perp( tangent );

            double velAlongTangent = rv.dot( tangent );

            if( velAlongTangent > 0 ) {
                tangent.neg( tangent );
                velAlongTangent = - velAlongTangent;
            }
            tangent.norm( tangent );

            double tangentImpulse = - velAlongTangent;
            tangentImpulse /= invSum;
            tangentImpulse /= contactAmount;
            tangentImpulse = MathUtil.tryMakeZero( tangentImpulse );

            double mu = MathUtil.pythagoreanSolve( bodyA.staticFriction, bodyB.staticFriction );

            if( Math.abs( tangentImpulse ) < - normalImpulse * mu ) {
                tangent.mul( tangentImpulse, impulse );
            } else {
                double dynaFriction = MathUtil.pythagoreanSolve( bodyA.dynamicFriction, bodyB.dynamicFriction );
                tangent.mul( - normalImpulse * dynaFriction, impulse );
            }

            bodyA.applyImpulse( impulse.neg( v1 ), contactA, true );
            bodyB.applyImpulse( impulse, contactB, true );
        }
    }

    public void correct() {
        double percent = 0.2;
        double slop = 0.01;

        Vec2 correction = normal.mul( Math.max( depth - slop, 0 ) / ( bodyA.mass.getInvMass() + bodyB.mass.getInvMass() ) * percent, null );
        Vec2 use = new Vec2();
        bodyA.positionalVel.add( correction.mul( bodyA.mass.getInvMass(), use ), bodyA.positionalVel );
        bodyB.positionalVel.sub( correction.mul( bodyB.mass.getInvMass(), use ), bodyB.positionalVel );
        bodyA.position.add( correction.mul( bodyA.mass.getInvMass(), use ), bodyA.position );
        bodyB.position.sub( correction.mul( bodyB.mass.getInvMass(), use ), bodyB.position );
    }
}
