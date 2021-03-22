package net.regulus.simulation;

import net.regulus.detection.CollisionPrimer;
import net.regulus.geom.MathUtil;
import net.regulus.geom.Vec2;
import net.regulus.geom.VectorList;

public class Collision {
    public final Body bodyA;
    public final Body bodyB;
    public final Vec2 normal;
    public final double depth;
    public final VectorList pointsA;
    public final VectorList pointsB;

    private double accumulatedCorrectionImpulse;

    private Vec2 center;

    public Collision( Body a, Body b, CollisionPrimer primer ) {
        this.bodyA = a;
        this.bodyB = b;
        this.normal = primer.normal;
        this.depth = primer.penetrationDepth;
        this.pointsA = primer.collisionA;
        this.pointsB = primer.collisionB;
    }

    public void resolve( double dt, double scale ) {
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
            normalImpulse /= scale;

            normal.mul( normalImpulse, impulse );

            MathUtil.tryMakeZero( impulse );
            bodyA.addImpulse( impulse.neg( v1 ), contactA, false );
            bodyB.addImpulse( impulse, contactB, false );


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
            tangentImpulse /= scale;
            tangentImpulse = MathUtil.tryMakeZero( tangentImpulse );

            double mu = MathUtil.pythagoreanSolve( bodyA.staticFriction, bodyB.staticFriction );

            if( Math.abs( tangentImpulse ) < - normalImpulse * mu ) {
                tangent.mul( tangentImpulse, impulse );
            } else {
                double dynaFriction = MathUtil.pythagoreanSolve( bodyA.dynamicFriction, bodyB.dynamicFriction );
                tangent.mul( - normalImpulse * dynaFriction, impulse );
            }

            MathUtil.tryMakeZero( impulse );
            bodyA.addImpulse( impulse.neg( v1 ), contactA, true );
            bodyB.addImpulse( impulse, contactB, true );
        }
    }

    public void correct( double scale ) {
        double baumgarte = 0.6;
        double allowedPenetration = 0.001;
        double maxLinearCorrection = 0.5;
        double percent = 0.2;
        double slop = 0;

        Vec2 v1 = new Vec2();
        Vec2 v2 = new Vec2();
        Vec2 rv = new Vec2();
        Vec2 impulse = new Vec2();
        Vec2 tangent = new Vec2();
        Vec2 contactA = new Vec2();
        Vec2 contactB = new Vec2();
        Vec2 diff = new Vec2();

        int contactAmount = pointsA.size();

        for( int i = 0; i < contactAmount; i ++ ) {
            pointsA.get( i, contactA );
            pointsB.get( i, contactB );

            contactB.sub( contactA, diff );
            double penetration = -diff.dot( normal );

            contactA.sub( bodyA.position, contactA );
            contactB.sub( bodyB.position, contactB );

            double racn = contactA.cross( normal );
            double rbcn = contactB.cross( normal );
            double invMassSum = bodyA.mass.getInvMass() + bodyB.mass.getInvMass();
            double invInrtSum = racn * racn * bodyA.mass.getInvInertia() + rbcn * rbcn * bodyB.mass.getInvInertia();
            double invSum = invMassSum + invInrtSum;

            double correctionScale = baumgarte * MathUtil.clamp(-maxLinearCorrection, 0.0, penetration + allowedPenetration);

            double correctionImpulse = -correctionScale / invSum;
            //correctionImpulse /= contactAmount;
            //correctionImpulse /= scale;

            Vec2 correction = normal.mul( correctionImpulse, v2 );
            bodyA.addCorrection( correction, contactA, false );
            bodyB.addCorrection( correction.neg( v1 ), contactB, false );
        }

        bodyA.applyTotalCorrection();
        bodyB.applyTotalCorrection();

//        Vec2 correction = normal.mul( Math.max( depth - slop, 0 ) / ( bodyA.mass.getInvMass() + bodyB.mass.getInvMass() ) * percent, null );
//        Vec2 use = new Vec2();
//        bodyA.positionalVel.add( correction.mul( bodyA.mass.getInvMass(), use ), bodyA.positionalVel );
//        bodyB.positionalVel.sub( correction.mul( bodyB.mass.getInvMass(), use ), bodyB.positionalVel );
//        bodyA.position.add( correction.mul( bodyA.mass.getInvMass(), use ), bodyA.position );
//        bodyB.position.sub( correction.mul( bodyB.mass.getInvMass(), use ), bodyB.position );
    }

    public Vec2 getCenter() {
        if (center == null) {
            center = new Vec2();
            Vec2 v = new Vec2();
            for (Vec2.IContext context : pointsA) {
                context.get( v ).add( center, center );
            }
            for (Vec2.IContext context : pointsB) {
                context.get( v ).add( center, center );
            }

            center.div( pointsA.size() + pointsB.size(), center );
        }
        return center;
    }
}
