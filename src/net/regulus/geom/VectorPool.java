package net.regulus.geom;

import java.util.Stack;

public class VectorPool {
    private final Stack<Vec2> vecs = new Stack<>();

    public Vec2 retain( double x, double y) {
        if( vecs.isEmpty() ) {
            return new Vec2( x, y );
        } else {
            return vecs.pop().set( x, y );
        }
    }

    public Vec2 retain( double v ) {
        return retain( v, v );
    }

    public Vec2 retain( Vec2 v ) {
        return retain( v.x, v.y );
    }

    public Vec2 retain() {
        return retain( 0, 0 );
    }

    public void release( Vec2 v ) {
        vecs.push( v );
    }

    public void release( Vec2... v ) {
        for( Vec2 v1 : v ) {
            vecs.push( v1 );
        }
    }
}
