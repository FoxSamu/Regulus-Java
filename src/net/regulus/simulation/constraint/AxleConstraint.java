package net.regulus.simulation.constraint;

import net.regulus.geom.Vec2;

public class AxleConstraint extends LinkingConstraint {

    private final Vec2 v1 = new Vec2();
    private final Vec2 v2 = new Vec2();
    private final Vec2 impulse = new Vec2();
    private final Vec2 correction = new Vec2();

    @Override
    public void correct( double dt ) {
        if( isUseless() )
            return;

        double invMassA = bodyA == null ? 0 : bodyA.mass.getInvMass();
        double invMassB = bodyB == null ? 0 : bodyB.mass.getInvMass();

        double percent = 0.4;
        normal.mul( Math.max( globalDist, 0 ) / ( invMassA + invMassB ) * percent, correction );

        if( bodyA != null ) {
            bodyA.positionalVel.add( correction.mul( invMassA, v1 ), bodyA.positionalVel );
            bodyA.position.add( correction.mul( invMassA, v1 ), bodyA.position );
        }
        if( bodyB != null ) {
            bodyB.positionalVel.sub( correction.mul( invMassB, v1 ), bodyB.positionalVel );
            bodyB.position.sub( correction.mul( invMassB, v1 ), bodyB.position );
        }
    }

    @Override
    public void resolve( double dt ) {
        if( isUseless() )
            return;

        Vec2 rv = new Vec2();
        getConstraintVelo( bodyB, ptB, v1 ).sub( getConstraintVelo( bodyA, ptA, v2 ), rv );

        double velAlongNormal = rv.dot( normal );

        if( velAlongNormal < 0 ) {
            return;
        }

        double invMassA = bodyA == null ? 0 : bodyA.mass.getInvMass();
        double invMassB = bodyB == null ? 0 : bodyB.mass.getInvMass();

        double invInertiaA = bodyA == null ? 0 : bodyA.mass.getInvInertia();
        double invInertiaB = bodyB == null ? 0 : bodyB.mass.getInvInertia();

        double racn = ptA.cross( normal );
        double rbcn = ptB.cross( normal );
        double invMassSum = invMassA + invMassB;
        double invInrtSum = racn * racn * invInertiaA + rbcn * rbcn * invInertiaB;
        double invSum = invMassSum + invInrtSum;

        double j = - velAlongNormal / invSum;

        normal.mul( j, impulse );

//        normal.mul( - Math.max( globalDist, 0 ) / ( invMassA + invMassB ), correction );
//
//        impulse.add( correction, impulse );

        if( bodyA != null )
            bodyA.addImpulse( impulse.neg( v1 ), ptA, true );
        if( bodyB != null )
            bodyB.addImpulse( impulse, ptB, true );
    }
}
