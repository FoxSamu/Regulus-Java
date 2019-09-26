package net.regulus.collider;

import net.regulus.geom.Projection;
import net.regulus.geom.Vec2;

public class PlaneCollider extends SimpleCollider {
    protected final Vec2 normal = new Vec2();
    private final Projection proj = new Projection();

    @Override
    protected void recompute() {
        area = Double.POSITIVE_INFINITY;
        inertiaFactor = Double.POSITIVE_INFINITY;
        center.set( 0, 0 );
        matrix.mul( 0, 0, center );
        centerOfMass.set( center );
        box.set( Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY );
        normal.set( direction );
    }

    public boolean containsPoint( Vec2 point ) {
        proj.baseVector.set( normal );
        point.sub( center, proj.projected );
        proj.project();
        return proj.tangentLength <= 0;
    }

    public Vec2 getNormal( Vec2 v ) {
        return Vec2.put( v, normal );
    }
}
