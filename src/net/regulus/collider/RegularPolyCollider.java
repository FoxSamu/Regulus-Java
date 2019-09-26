package net.regulus.collider;

public class RegularPolyCollider extends PolygonalCollider {
    protected int sides = 3;
    protected double radius;

    public RegularPolyCollider() {
        recomputeVertices();
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius( double radius ) {
        this.radius = radius;
        recomputeVertices();
        recompute();
    }

    public int getSides() {
        return sides;
    }

    public void setSides( int sides ) {
        this.sides = Math.max( sides, 3 );
        recomputeVertices();
        recompute();
    }

    protected void recomputeVertices() {
        vertices.clear();
        for( int i = 0; i < sides; i++ ) {
            double prog = ( i / (float) sides ) * 2 * Math.PI;
            double x = Math.cos( prog ) * radius;
            double y = Math.sin( prog ) * radius;
            vertices.add( x, y );
        }
        recomputeGlobalVerts();
    }

    @Override
    protected void recompute() {
        recomputeGlobalVerts();
        matrix.mul( 0, 0, center );
        centerOfMass.set( center );
        area = radius * radius * sides * Math.sin( 2 * Math.PI / sides ) * 0.5;

        Looper looper = looper();
        recomputeSATAxes( looper );
        recomputeBox( looper );
        recomputeInertiaFactor( looper );
    }
}
