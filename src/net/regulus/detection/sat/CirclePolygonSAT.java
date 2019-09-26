package net.regulus.detection.sat;

import net.regulus.collider.CircleCollider;
import net.regulus.collider.PolygonalCollider;
import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.ICollisionCollector;
import net.regulus.geom.*;

public final class CirclePolygonSAT {
    private CirclePolygonSAT() {
    }

    public static void collide( CircleCollider a, PolygonalCollider b, ICollisionCollector collector ) {
        PolygonalCollider.Looper looper = b.looper();
        VectorCollection coll = b.getGlobal();

        LineDistance ld = new LineDistance();
        Projection pr = new Projection();
        Vec2 center = a.getCenter( ld.point );
        Vec2 curr = ld.line.pointA, next = ld.line.pointB;

        Vec2 edge = new Vec2();

        // Find the closest point to the circle's center and the normal from there to the circle
        Vec2 closestPoint = new Vec2();
        Vec2 normal = new Vec2();
        double dist = Double.POSITIVE_INFINITY;
        for( int i = 0; i < coll.size(); i++ ) {
            looper.goTo( i );
            looper.currGlobal( curr );
            looper.nextGlobal( next );
            ld.compute();

            if( ld.distance < dist ) {
                dist = ld.distance;
                normal.set( ld.normal );
                closestPoint.set( ld.closest );

                if( MathUtil.equal( dist, 0 ) ) {
                    next.sub( curr, edge ).invPerp( edge ).norm( normal );
                }
            }
        }

        // Find edge of which the normal has the highest dot product with normal found in above code
        double biggestDotProduct = Double.NEGATIVE_INFINITY;
        int index = - 1;
        for( int i = 0; i < coll.size(); i ++ ) {
            looper.goTo( i );
            looper.currGlobal( curr );
            looper.nextGlobal( next );
            next.sub( curr, edge ).invPerp( edge ).norm( edge );

            double dot = normal.dot( edge );
            if( dot > biggestDotProduct ) {
                biggestDotProduct = dot;
                index = i;
            }
        }

        if( index >= 0 ) {
            looper.goTo( index );
            looper.currGlobal( curr );
            looper.nextGlobal( next );
            next.sub( curr, edge ).invPerp( edge ).norm( edge );

            pr.baseVector.set( edge );
            center.sub( curr, pr.projected );
            pr.project();

            double signum = pr.tangentLength < 0 ? -1 : 1;
            double depth = a.getRadius() - dist * signum;
            if( depth < 0 ) return; // No overlap

            CollisionPrimer primer = new CollisionPrimer();
            normal.mul( signum, primer.normal );
            primer.penetrationDepth = depth;

            Vec2 otherPoint = primer.normal.mul( primer.penetrationDepth, null );
            otherPoint.neg( otherPoint ).add( closestPoint, otherPoint );

            primer.collisionA.add( otherPoint );
            primer.collisionB.add( closestPoint );

            collector.addCollision( primer );
        }
    }

    public static void collide( PolygonalCollider a, CircleCollider b, ICollisionCollector collector ) {
        collide( b, a, c -> collector.addCollision( c.invert() ) );
    }
}
