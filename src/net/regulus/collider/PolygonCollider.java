package net.regulus.collider;

import net.regulus.geom.Vec2;
import net.regulus.geom.VectorList;

public class PolygonCollider extends PolygonalCollider {
    protected final VectorList unorderedVertices = new VectorList();
    private final Vec2 tmp1 = new Vec2();
    private final Vec2 tmp2 = new Vec2();

    public PolygonCollider add( Vec2 pt ) {
        unorderedVertices.add( pt );
        recomputeVertices();
        recompute();
        return this;
    }

    public PolygonCollider add( double x, double y ) {
        unorderedVertices.add( x, y );
        recomputeVertices();
        recompute();
        return this;
    }

    public PolygonCollider add( int index, Vec2 pt ) {
        unorderedVertices.add( index, pt );
        recomputeVertices();
        recompute();
        return this;
    }

    public PolygonCollider add( int index, double x, double y ) {
        unorderedVertices.add( index, x, y );
        recomputeVertices();
        recompute();
        return this;
    }

    public PolygonCollider set( int index, Vec2 pt ) {
        unorderedVertices.set( index, pt );
        recomputeVertices();
        recompute();
        return this;
    }

    public PolygonCollider set( int index, double x, double y ) {
        unorderedVertices.set( index, x, y );
        recomputeVertices();
        recompute();
        return this;
    }

    public PolygonCollider remove( int index ) {
        unorderedVertices.remove( index );
        recomputeVertices();
        recompute();
        return this;
    }

    public PolygonCollider reset() {
        unorderedVertices.clear();
        recomputeVertices();
        recompute();
        return this;
    }

    public Vec2 get( int index, Vec2 out ) {
        return unorderedVertices.get( index, out );
    }

    public int vertexAmount() {
        return unorderedVertices.size();
    }

    protected void recomputeVertices() {
        double clockwiseness = 0;
        int l = vertexAmount();
        for( int i = 0; i < l; i++ ) {
            unorderedVertices.get( i, tmp1 );
            int j = i + 1;
            if( j == l )
                j = 0;
            unorderedVertices.get( j, tmp2 );
            clockwiseness += ( tmp2.x - tmp1.x ) * ( tmp2.y + tmp1.y );
        }
        boolean ccw = clockwiseness <= 0;
        vertices.clear();
        if( ccw ) { // We want the vertices to be always in counterclockwise order
            for( int i = 0; i < l; i++ ) {
                unorderedVertices.get( i, tmp1 );
                vertices.add( tmp1 );
            }
        } else {
            for( int i = l - 1; i >= 0; i-- ) {
                unorderedVertices.get( i, tmp1 );
                vertices.add( tmp1 );
            }
        }
    }
}
