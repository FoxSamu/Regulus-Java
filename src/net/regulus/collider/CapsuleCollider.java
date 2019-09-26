package net.regulus.collider;

import net.regulus.geom.Vec2;

public class CapsuleCollider extends SimpleCollider {
    protected double radius;
    protected double length;

    protected final Vec2 left = new Vec2(), right = new Vec2();

    @Override
    protected void recompute() {
        area = Math.PI * radius * radius + length * radius * 2;
        inertiaFactor = capsuleI( length, radius );
        matrix.mul( 0, 0, center );
        centerOfMass.set( center );
        left.set( - length / 2, 0 ).rotate( globalRotation, left ).add( center, left );
        right.set( length / 2, 0 ).rotate( globalRotation, right ).add( center, right );
        box.set( left, right ).abs( box ).grow( radius, box );
    }

    private static double boxI( double w, double h ) {
        return w * h * ( w * w + h * h ) / 12;
    }

    private static final double D3_8 = 3.0 / 8.0;

    private static double capsuleI( double h, double r ) {
        double r2 = r * r;
        double w = r * 2;

        double boxI = boxI( w, h );
        double cmass = Math.PI * r2;
        double cinrt = cmass * 0.5 * r2;
        double mass = cmass * 0.5;
        double io = 0.5 * cinrt;
        double d1 = D3_8 * r;
        double ic = io - mass * d1 * d1;
        double d2 = d1 + 0.5 * h;
        double i = ic + mass * d2 * d2;

        return boxI + 2 * i;
    }

    public void setRadius( double radius ) {
        this.radius = radius;
        recompute();
    }

    public double getRadius() {
        return radius;
    }

    public void setLength( double length ) {
        this.length = length;
        recompute();
    }

    public double getLength() {
        return length;
    }

    public Vec2 getLeft( Vec2 out ) {
        return Vec2.put( out, left );
    }

    public Vec2 getRight( Vec2 out ) {
        return Vec2.put( out, right );
    }
}
