package net.regulus.collider;

public class BoxCollider extends PolygonalCollider {
    protected double width;
    protected double height;

    public BoxCollider() {
        recomputeVertices();
    }

    public void setWidth( double width ) {
        this.width = width;
        recomputeVertices();
        recompute();
    }

    public void setHeight( double height ) {
        this.height = height;
        recomputeVertices();
        recompute();
    }

    public void setSize( double width, double height ) {
        this.width = width;
        this.height = height;
        recomputeVertices();
        recompute();
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    protected void recomputeVertices() {
        vertices.clear();
        vertices.add( - width / 2, - height / 2 );
        vertices.add( width / 2, - height / 2 );
        vertices.add( width / 2, height / 2 );
        vertices.add( - width / 2, height / 2 );
        recomputeGlobalVerts();
    }

    @Override
    protected void recompute() {
        recomputeGlobalVerts();
        matrix.mul( 0, 0, center );
        centerOfMass.set( center );
        area = width * height;

        Looper looper = looper();
        recomputeSATAxes( looper );
        recomputeBox( looper );
        recomputeInertiaFactor( looper );
    }
}
