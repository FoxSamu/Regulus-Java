package net.regulus.collider;

public class CircleCollider extends SimpleCollider {
    protected double radius;

    @Override
    protected void recompute() {
        area = Math.PI * radius * radius;
        inertiaFactor = area * radius * radius;
        matrix.mul( 0, 0, center );
        centerOfMass.set( center );
        box.setCenterSize( center, radius * 2, radius * 2 );
    }

    public void setRadius( double radius ) {
        this.radius = radius;
        recompute();
    }

    public double getRadius() {
        return radius;
    }
}
