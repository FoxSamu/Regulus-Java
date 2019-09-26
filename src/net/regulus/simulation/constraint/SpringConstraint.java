package net.regulus.simulation.constraint;

import net.regulus.geom.Vec2;

public class SpringConstraint extends LinkingConstraint {

    private final Vec2 v1 = new Vec2();
    private final Vec2 v2 = new Vec2();
    private final Vec2 impulse = new Vec2();

    public double minDist;
    public double maxDist;
    public double springConstant = 0.05;
    public double damping = 0.01;

    private boolean solve;
    private double depth;

    @Override
    public void correct( double dt ) {
        // No correction
    }

    @Override
    public void resolve( double dt ) {
        if( isUseless() || ! solve )
            return;

        Vec2 rv = new Vec2();
        getConstraintVelo( bodyB, ptB, v1 ).sub( getConstraintVelo( bodyA, ptA, v2 ), rv );

        double velAlongNormal = rv.dot( normal );

        double j = ( springConstant * - depth - damping * velAlongNormal ) * dt;

        normal.mul( j, impulse );

        if( bodyA != null )
            bodyA.applyImpulse( impulse.neg( v1 ), ptA, true );
        if( bodyB != null )
            bodyB.applyImpulse( impulse, ptB, true );
    }

    @Override
    public void prepare() {
        if( isUseless() )
            return;
        super.prepare();
        solve = false;
        if( globalDist >= maxDist ) {
            solve = true;
            depth = globalDist - maxDist;
        } else if( globalDist <= minDist ) {
            solve = true;
            normal.neg( normal );
            depth = minDist - globalDist;
        }
    }
}
