package net.regulus.collider;

import net.regulus.geom.MathUtil;
import net.regulus.geom.Vec2;
import net.regulus.geom.VectorCollection;
import net.regulus.geom.VectorList;

public abstract class PolygonalCollider extends SimpleCollider {
    protected final VectorList vertices = new VectorList();
    protected final VectorList globalVertices = new VectorList();
    protected final VectorList satAxes = new VectorList();

    private final Vec2[] tmp = {
        new Vec2(),
        new Vec2(),
        new Vec2()
    };


    @Override
    protected void recompute() {
        recomputeGlobalVerts();
        if( vertices.size() > 1 ) {
            Looper looper = looper();
            recomputeSATAxes( looper );
            recomputeAreaCentersAndBox( looper );
            recomputeInertiaFactor( looper );
        }
    }

    protected void recomputeGlobalVerts() {
        Vec2 temp = tmp[ 0 ];

        globalVertices.clear();

        int l = vertices.size();
        for( int i = 0; i < l; i++ ) {
            vertices.get( i, temp );
            matrix.mul( temp, temp );
            globalVertices.add( temp );
        }
    }

    protected void recomputeSATAxes( Looper looper ) {
        Vec2 axis = tmp[ 0 ];
        Vec2 otherAxis = tmp[ 1 ];

        satAxes.clear();
        looper.goTo( 0 );
        int j = 0, l = globalVertices.size();
        while( j < l ) {
            looper.forwardGlobEdge( axis );
            axis.norm( axis ).perp( axis );

            boolean isNew = true;
            for( int i = 0; i < satAxes.size(); i++ ) {
                satAxes.get( i, otherAxis );
                if( MathUtil.equal( axis.cross( otherAxis ), 0 ) ) {
                    isNew = false;
                }
            }

            if( isNew ) {
                satAxes.add( axis );
            }

            looper.forward();
            j ++;
        }
    }

    protected void recomputeInertiaFactor( Looper looper ) {
        looper.goTo( 0 );
        Vec2 curr = tmp[ 0 ];
        Vec2 next = tmp[ 1 ];
        double ix = 0, iy = 0;
        int i = 0, l = vertices.size();
        while( i < l ) {
            looper.currLocal( curr );
            looper.nextLocal( next );

            double a = curr.cross( next );
            iy += (curr.x * curr.x + curr.x * next.x + next.x * next.x ) * a;
            ix += (curr.y * curr.y + curr.y * next.y + next.y * next.y ) * a;
            i ++;
        }

        ix /= 12;
        iy /= 12;

        inertiaFactor = Math.abs( ix + iy );
    }

    protected void recomputeAreaCentersAndBox( Looper looper ) {
        double area = 0;
        Vec2 curr = tmp[ 0 ];
        Vec2 next = tmp[ 1 ];

        center.set( 0, 0 );
        centerOfMass.set( 0, 0 );
        double minX = Double.POSITIVE_INFINITY,
            minY = Double.POSITIVE_INFINITY,
            maxX = Double.NEGATIVE_INFINITY,
            maxY = Double.NEGATIVE_INFINITY;

        looper.goTo( 0 );
        int i = 0, l = globalVertices.size();
        while( i < l ) {
            looper.currGlobal( curr );
            looper.nextGlobal( next );
            double cross = curr.cross( next );

            area += cross;

            center.add( curr, center );

            centerOfMass.add(
                ( curr.x + next.x ) * cross,
                ( curr.y + next.y ) * cross,
                centerOfMass
            );

            minX = Math.min( curr.x, minX );
            minY = Math.min( curr.y, minY );
            maxX = Math.max( curr.x, maxX );
            maxY = Math.max( curr.y, maxY );

            looper.forward();
            i ++;
        }

        this.area = Math.abs( area / 2 );
        center.div( vertices.size(), center );
        centerOfMass.div( 6 * this.area, centerOfMass );
        box.set( minX, minY, maxX, maxY );
    }

    protected void recomputeBox( Looper looper ) {
        Vec2 curr = tmp[ 0 ];
        double minX = Double.POSITIVE_INFINITY,
            minY = Double.POSITIVE_INFINITY,
            maxX = Double.NEGATIVE_INFINITY,
            maxY = Double.NEGATIVE_INFINITY;

        looper.goTo( 0 );
        int i = 0, l = globalVertices.size();
        while( i < l ) {
            looper.currGlobal( curr );

            minX = Math.min( curr.x, minX );
            minY = Math.min( curr.y, minY );
            maxX = Math.max( curr.x, maxX );
            maxY = Math.max( curr.y, maxY );

            looper.forward();
            i ++;
        }

        box.set( minX, minY, maxX, maxY );
    }


    public VectorCollection getVertices() {
        return vertices.collection;
    }

    public VectorCollection getGlobal() {
        return globalVertices.collection;
    }

    public VectorCollection getSATAxes() {
        return satAxes.collection;
    }

    public Looper looper() {
        return new Looper( this );
    }


    public static final class Looper {
        private int index;
        private final Vec2 tmp = new Vec2();
        private final PolygonalCollider collider;

        private Looper( PolygonalCollider collider ) {
            this.collider = collider;
        }

        public Vec2 currLocal( Vec2 out ) {
            return collider.vertices.get( currIndex(), out );
        }

        public Vec2 nextLocal( Vec2 out ) {
            return collider.vertices.get( nextIndex(), out );
        }

        public Vec2 prevLocal( Vec2 out ) {
            return collider.vertices.get( prevIndex(), out );
        }

        public Vec2 currGlobal( Vec2 out ) {
            return collider.globalVertices.get( currIndex(), out );
        }

        public Vec2 nextGlobal( Vec2 out ) {
            return collider.globalVertices.get( nextIndex(), out );
        }

        public Vec2 prevGlobal( Vec2 out ) {
            return collider.globalVertices.get( prevIndex(), out );
        }

        public Vec2 forwardLocEdge( Vec2 out ) {
            currLocal( tmp );
            out = nextLocal( out );
            return out.sub( tmp, out );
        }

        public Vec2 backwardLocEdge( Vec2 out ) {
            currLocal( tmp );
            out = prevLocal( out );
            return out.sub( tmp, out );
        }

        public Vec2 forwardGlobEdge( Vec2 out ) {
            currGlobal( tmp );
            out = nextGlobal( out );
            return out.sub( tmp, out );
        }

        public Vec2 backwardGlobEdge( Vec2 out ) {
            currGlobal( tmp );
            out = prevGlobal( out );
            return out.sub( tmp, out );
        }

        public int currIndex() {
            return index;
        }

        public int nextIndex() {
            return clampIndex( index + 1 );
        }

        public int prevIndex() {
            return clampIndex( index - 1 );
        }

        public void forward() {
            index = clampIndex( index + 1 );
        }

        public void backward() {
            index = clampIndex( index - 1 );
        }

        public void goTo( int index ) {
            this.index = clampIndex( index );
        }

        public boolean atLast() {
            return index == collider.vertices.size() - 1;
        }

        public boolean atFirst() {
            return index == 0;
        }

        private int clampIndex( int index ) {
            if( index >= 0 ) {
                return index % collider.vertices.size();
            } else {
                return index % collider.vertices.size() + collider.vertices.size();
            }
        }
    }
}
