package net.regulus.detection.sat;

import net.regulus.collider.PlaneCollider;
import net.regulus.collider.PolygonalCollider;
import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.ICollisionCollector;
import net.regulus.geom.Line;
import net.regulus.geom.Vec2;

public final class PolygonPlaneSAT {

    private PolygonPlaneSAT() {
    }

    public static void collide( PolygonalCollider a, PlaneCollider b, ICollisionCollector collector ) {
        PolygonalCollider.Looper looper = a.looper();

        Vec2 normal = b.getNormal( null ), curr = new Vec2(), next = new Vec2(), prev = new Vec2();
        Vec2 centerB = b.getCenter( null );
        double min = Double.POSITIVE_INFINITY;

        int size = a.getGlobal().size();

        for( int i = 0; i < size; i++ ) {
            looper.goTo( i );
            looper.currGlobal( curr ).sub( centerB, curr );

            double dot = curr.dot( normal );
            if( dot < min )
                min = dot;
        }

        if( min > 0 )
            return;

        CollisionPrimer primer = new CollisionPrimer();
        primer.normal.set( normal );
        primer.penetrationDepth = - min;

        normal.neg( normal );
        double max = Double.NEGATIVE_INFINITY;
        int bestEdgeIndex = - 1;
        for( int i = 0; i < size; i++ ) {
            looper.goTo( i );
            looper.currGlobal( curr );

            double dot = curr.dot( normal );
            if( dot > max ) {
                max = dot;
                bestEdgeIndex = i;
            }
        }

        Line bestEdge;
        looper.goTo( bestEdgeIndex );
        looper.currGlobal( curr );
        looper.backwardGlobEdge( prev ).neg( prev ).norm( prev );
        looper.forwardGlobEdge( next ).neg( next ).norm( next );
        if( prev.dot( normal ) <= next.dot( normal ) ) {
            looper.prevGlobal( prev );
            bestEdge = new Line( prev, curr );
        } else {
            looper.nextGlobal( next );
            bestEdge = new Line( curr, next );
        }
        normal.neg( normal );

        Vec2 tangent = normal.perp( null );

        Line otherEdge = new Line();

        int amount = 2;
        if( ! b.containsPoint( bestEdge.pointB ) ) {
            amount = 1;
        }
        if( ! b.containsPoint( bestEdge.pointA ) ) {
            bestEdge.pointA.set( bestEdge.pointB );
            amount = 1;
        }

        double dotA = tangent.dot( bestEdge.pointA.sub( centerB, curr ) );
        double dotB = tangent.dot( bestEdge.pointB.sub( centerB, curr ) );

        centerB.add( tangent.mul( dotA, otherEdge.pointA ), otherEdge.pointA );
        primer.collisionA.add( bestEdge.pointA );
        primer.collisionB.add( otherEdge.pointA );
        if( amount == 2 ) {
            centerB.add( tangent.mul( dotB, otherEdge.pointB ), otherEdge.pointB );
            double distA = bestEdge.pointA.distSq( otherEdge.pointA );
            double distB = bestEdge.pointB.distSq( otherEdge.pointB );
            if( distB < distA ) {
                primer.collisionA.add( 1, bestEdge.pointB );
                primer.collisionB.add( 1, otherEdge.pointB );
            } else {
                primer.collisionA.add( 0, bestEdge.pointB );
                primer.collisionB.add( 0, otherEdge.pointB );
            }
        }

        collector.addCollision( primer );
    }

    public static void collide( PlaneCollider a, PolygonalCollider b, ICollisionCollector collector ) {
        collide( b, a, c -> collector.addCollision( c.invert() ) );
    }
}
