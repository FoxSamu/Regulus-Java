package net.regulus.simulation.constraint;

import net.regulus.geom.Vec2;
import net.regulus.simulation.Body;

public abstract class LinkingConstraint implements IConstraint {
    public Body bodyA;
    public Body bodyB;
    public final Vec2 linkA = new Vec2();
    public final Vec2 linkB = new Vec2();
    public boolean enabled = true;

    public boolean disableCollision = true;

    protected final Vec2 globalA = new Vec2();
    protected final Vec2 globalB = new Vec2();
    protected final Vec2 ptA = new Vec2();
    protected final Vec2 ptB = new Vec2();
    protected final Vec2 normal = new Vec2();
    protected double globalDist;

    private void global( Body b, Vec2 pt, Vec2 out ) {
        if( b == null )
            Vec2.put( out, pt );
        else
            b.transform( pt, out );
    }

    @Override
    public void prepare() {
        if( isUseless() )
            return;
        global( bodyA, linkA, globalA );
        global( bodyB, linkB, globalB );
        globalDist = globalA.dist( globalB );
        globalB.sub( globalA, normal ).norm( normal );
        if( bodyA != null )
            globalA.sub( bodyA.position, ptA );
        else
            ptA.set( globalA );
        if( bodyB != null )
            globalB.sub( bodyB.position, ptB );
        else
            ptB.set( globalB );
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public boolean collisionDisabled( Body bodyA, Body bodyB ) {
        return disableCollision && this.bodyA == bodyA && this.bodyB == bodyB;
    }

    public Vec2 globalA( Vec2 out ) {
        return Vec2.put( out, globalA );
    }

    public Vec2 globalB( Vec2 out ) {
        return Vec2.put( out, globalB );
    }

    public boolean isUseless() {
        return ( bodyA == null || bodyA.mass.isStatic() ) && ( bodyB == null || bodyB.mass.isStatic() );
    }

    protected Vec2 getConstraintVelo( Body body, Vec2 point, Vec2 out ) {
        if( body == null )
            return Vec2.put( out, 0, 0 );
        return body.getConstraintVelo( point, out );
    }
}
