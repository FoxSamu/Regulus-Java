package net.regulus.detection.sat;

import net.regulus.collider.BarCollider;
import net.regulus.collider.CapsuleCollider;
import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.ICollisionCollector;
import net.regulus.geom.Line;
import net.regulus.geom.LineIntersection;
import net.regulus.geom.MathUtil;
import net.regulus.geom.Vec2;

public final class CapsuleBarSAT {
    private CapsuleBarSAT() {
    }

    private static Vec2 computeSupport( double u, Line rig, Line offRig, Vec2 normal, double radius, int signum, Vec2 use ) {
        Vec2 out = new Vec2();
        if( u <= 0 ) {
            return rig.pointA.add( normal.mul( radius * signum, use ), out );
        } else if( u >= 1 ) {
            return rig.pointB.add( normal.mul( radius * signum, use ), out );
        }

        return offRig.interpolate( u, out );
    }

    public static void collide( CapsuleCollider a, BarCollider b, ICollisionCollector collector ) {
        Line rig = new Line();
        a.getLeft( rig.pointA );
        a.getRight( rig.pointB );

        Vec2 normal = b.getNormal( null );
        Vec2 center = b.getCenter( null );
        Vec2 v1 = new Vec2();

        double dotNA = rig.pointA.sub( center, v1 ).dot( normal );
        double dotNB = rig.pointB.sub( center, v1 ).dot( normal );

        double radiusA = a.getRadius();
        double radiusB = b.getWidth() / 2;
        double radiusTotal = radiusA + radiusB;

        if( Math.abs( dotNA ) > radiusTotal && Math.abs( dotNB ) > radiusTotal ) {
            return;
        }

        double depthA = radiusTotal - Math.min( dotNA, dotNB );
        double depthB = radiusTotal + Math.max( dotNA, dotNB );

        double depth = depthA;
        if( depthA > depthB ) {
            normal.neg( normal );
            depth = depthB;
        }

        CollisionPrimer primer = new CollisionPrimer();
        primer.normal.set( normal );
        primer.penetrationDepth = depth;

        center.add( normal.mul( radiusB, v1 ), center );

        Vec2 tangent = normal.perp( null );
        double dotTA = rig.pointA.sub( center, v1 ).dot( tangent );
        double dotTB = rig.pointB.sub( center, v1 ).dot( tangent );

        Line offRig = new Line( rig );
        rig.edge( v1 ).perp( v1 ).norm( v1 );
        if( v1.dot( normal ) > 0 ) {
            v1 = v1.neg( v1 );
        }
        v1.mul( radiusA, v1 );
        offRig.pointA.add( v1, offRig.pointA );
        offRig.pointB.add( v1, offRig.pointB );

        Vec2 ptB1 = center.add( tangent.mul( dotTA, v1 ), null );
        Vec2 ptB2 = center.add( tangent.mul( dotTB, v1 ), null );

        LineIntersection li = new LineIntersection();
        li.lineA.set( offRig );

        li.lineB.set( ptB1, ptB1.add( normal, v1 ) );
        li.intersect();
        double u1 = li.uA;
        li.lineB.set( ptB2, ptB2.add( normal, v1 ) );
        li.intersect();
        double u2 = li.uA;

        Vec2 ptA1 = computeSupport( u1, rig, offRig, normal, radiusA, - 1, v1 );
        Vec2 ptA2 = computeSupport( u2, rig, offRig, normal, radiusA, - 1, v1 );

        boolean infIsc = false;
        // Capsule rig orthogonal to bar
        if( MathUtil.equal( rig.edge( v1 ).cross( normal ), 0 ) ) {
            double pr1 = rig.pointA.dot( normal );
            double pr2 = rig.pointB.dot( normal );

            Vec2 closer;
            if( pr1 < pr2 ) closer = rig.pointA;
            else closer = rig.pointB;

            closer.sub( normal.mul( radiusA, v1 ), ptA1 );
            ptA2.set( ptA1 );
            infIsc = true;
        }

        boolean extend1 = MathUtil.moreEqual( u1, 1 ) && MathUtil.moreEqual( u2, 1 );
        boolean extend0 = MathUtil.lessEqual( u1, 0 ) && MathUtil.lessEqual( u2, 0 );
        boolean onePointOnly = extend1 || extend0 || infIsc;


        double d1 = ptB1.sub( ptA1, v1 ).dot( normal );
        double d2 = ptB2.sub( ptA2, v1 ).dot( normal );

        if( d1 < 0 && d2 < 0 ) {
            return;
        }

        double dist1 = ptA1.dist( ptB1 );
        double dist2 = ptA2.dist( ptB2 );

        if( dist2 > dist1 ) {
            if( d2 >= 0 && ! onePointOnly ) {
                primer.collisionA.add( ptA2 );
                primer.collisionB.add( ptB2 );
            }
            if( d1 >= 0 ) {
                primer.collisionA.add( ptA1 );
                primer.collisionB.add( ptB1 );
            }
        } else {
            if( d1 >= 0 && ! onePointOnly ) {
                primer.collisionA.add( ptA1 );
                primer.collisionB.add( ptB1 );
            }
            if( d2 >= 0 ) {
                primer.collisionA.add( ptA2 );
                primer.collisionB.add( ptB2 );
            }
        }

        collector.addCollision( primer );
    }

    public static void collide( BarCollider a, CapsuleCollider b, ICollisionCollector collector ) {
        collide( b, a, c -> collector.addCollision( c.invert() ) );
    }
}
