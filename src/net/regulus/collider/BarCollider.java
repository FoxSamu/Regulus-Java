package net.regulus.collider;

import net.regulus.geom.Projection;
import net.regulus.geom.Vec2;

public class BarCollider extends SimpleCollider {
    protected final Vec2 normal = new Vec2();
    protected double width;
    private final Projection proj = new Projection();

    @Override
    protected void recompute() {
        area = Double.POSITIVE_INFINITY;
        inertiaFactor = Double.POSITIVE_INFINITY;
        center.set( 0, 0 );
        matrix.mul( 0, 0, center );
        centerOfMass.set( center );
        box.set( Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY );
        normal.setPolar( 1, getGlobalRotation() );
    }

    public void setWidth( double width ) {
        this.width = width;
        recompute();
    }

    public double getWidth() {
        return width;
    }

    public boolean containsPoint( Vec2 point ) {
        proj.baseVector.set( normal );
        point.sub( center, proj.projected );
        proj.project();
        return proj.tangentLength >= - width / 2 && proj.tangentLength <= width / 2;
    }

    public Vec2 getNormal( Vec2 v ) {
        return Vec2.put( v, normal );
    }

}
