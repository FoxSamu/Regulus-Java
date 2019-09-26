package net.regulus.detection.sat;

import net.regulus.collider.CapsuleCollider;
import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.ICollisionCollector;
import net.regulus.geom.*;

public final class CapsuleCapsuleSAT {
    private CapsuleCapsuleSAT() {
    }

    public static void collide( CapsuleCollider a, CapsuleCollider b, ICollisionCollector collector ) {
        Line rigA = new Line();
        Line rigB = new Line();

        double radiusA = a.getRadius();
        double radiusB = b.getRadius();
        double radiusTotal = radiusA + radiusB;

        a.getLeft( rigA.pointA );
        a.getRight( rigA.pointB );
        b.getLeft( rigB.pointA );
        b.getRight( rigB.pointB );

        int refRig = - 1;
        LineDistance dist = new LineDistance();
        LineDistance secondDist = new LineDistance();
        dist.distance = Double.POSITIVE_INFINITY;
        secondDist.type = - 1;

        LineDistance ld = new LineDistance();

        for( int i = 0; i < 4; i++ ) {
            int rigI = i >>> 1 & 1;
            int pntI = i & 1;

            Line rig = rigI == 0 ? rigA : rigB;
            Line otherRig = rigI == 0 ? rigB : rigA;
            Vec2 pnt = pntI == 0 ? otherRig.pointA : otherRig.pointB;

            ld.line.set( rig );
            ld.point.set( pnt );
            ld.compute();
            if( ld.distance < dist.distance ) {
                secondDist.copy( dist );
                dist.copy( ld );
                refRig = rigI;
            }
        }

        if( dist.distance <= radiusTotal ) {

            CollisionPrimer primer = new CollisionPrimer();

            // We've found a collision, but the determined normal is still just the axis of least penetration: we still
            // have to find the actual normal by projecting against the actual normal and least penetration.

            Vec2 normal = dist.normal;

            double dot1 = rigA.pointA.dot( normal );
            double dot2 = rigA.pointB.dot( normal );
            double minA = Math.min( dot1, dot2 ) - radiusA;
            double maxA = Math.max( dot1, dot2 ) + radiusA;
            dot1 = rigB.pointA.dot( normal );
            dot2 = rigB.pointB.dot( normal );
            double minB = Math.min( dot1, dot2 ) - radiusB;
            double maxB = Math.max( dot1, dot2 ) + radiusB;

            double d = maxA - minB;
            double depth = maxB - minA;
            if( d < depth ) {
                normal.neg( normal );
                depth = d;
            }

            primer.penetrationDepth = depth;
            primer.normal.set( normal );


            Line ref = refRig == 0 ? rigA : rigB;
            Line inc = refRig == 0 ? rigB : rigA;
            Vec2 incPoint = dist.point;
            Vec2 refPoint = dist.closest;
            Vec2 v = new Vec2();

            VectorList refList = refRig == 0 ? primer.collisionA : primer.collisionB;
            VectorList incList = refRig == 0 ? primer.collisionB : primer.collisionA;
            int signum = refRig == 0 ? - 1 : 1;
            double refRadius = refRig == 0 ? radiusA : radiusB;
            double incRadius = refRig == 0 ? radiusB : radiusA;

            if( dist.type != LineDistance.LINE ) {
                refList.add( refPoint.add( normal.mul( refRadius * signum, v ), v ) );
                incList.add( incPoint.add( normal.mul( - incRadius * signum, v ), v ) );
            } else {
                Vec2 v2 = new Vec2();
                Vec2 n = normal.mul( signum, null );
                Line incOff = new Line( inc );
                Line refOff = new Line( ref );

                Vec2 incNormal = incOff.edge( v ).perp( v ).norm( null );
                Vec2 refNormal = refOff.edge( v ).perp( v ).norm( null );
                double incNDot = incNormal.dot( n.neg( v ) );
                double refNDot = refNormal.dot( n );
                if( incNDot < 0 ) incNormal.neg( incNormal );
                if( refNDot < 0 ) refNormal.neg( refNormal );
                incNormal.mul( incRadius, incNormal );
                refNormal.mul( refRadius, refNormal );

                incOff.pointA.add( incNormal, incOff.pointA );
                incOff.pointB.add( incNormal, incOff.pointB );
                refOff.pointA.add( refNormal, refOff.pointA );
                refOff.pointB.add( refNormal, refOff.pointB );

                LineIntersection li = new LineIntersection();
                li.lineA.set( refOff );
                li.lineB.set( inc.pointA, inc.pointA.add( n, v ) );
                li.intersect();
                double u1ref = MathUtil.clamp( 0, 1, li.uA );
                li.lineA.set( refOff );
                li.lineB.set( inc.pointB, inc.pointB.add( n, v ) );
                li.intersect();
                double u2ref = MathUtil.clamp( 0, 1, li.uA );

                Vec2 pt1ref = refOff.interpolate( u1ref, null );
                Vec2 pt2ref = refOff.interpolate( u2ref, null );

                li.lineA.set( incOff );
                li.lineB.set( pt1ref, pt1ref.add( n, v2 ) );
                li.intersect();
                double u1inc = li.uA;
                li.lineA.set( incOff );
                li.lineB.set( pt2ref, pt2ref.add( n, v2 ) );
                li.intersect();
                double u2inc = li.uA;

                Vec2 pt1inc = computeSupport( u1inc, inc, incOff, normal, incRadius, - signum, v );
                Vec2 pt2inc = computeSupport( u2inc, inc, incOff, normal, incRadius, - signum, v );

                boolean infIsc = false;
                // Capsule rigs orthogonal
                if( MathUtil.equal( inc.edge( v ).cross( normal ), 0 ) ) {
                    double pr1 = inc.pointA.dot( normal );
                    double pr2 = inc.pointB.dot( normal );

                    Vec2 closer;
                    if( pr1 < pr2 ) closer = inc.pointA;
                    else closer = inc.pointB;

                    closer.sub( normal.mul( incRadius, v ), pt1inc );
                    pt2inc.set( pt1inc );
                    infIsc = true;
                }

                boolean extend1 = MathUtil.moreEqual( u1inc, 1 ) && MathUtil.moreEqual( u2inc, 1 );
                boolean extend0 = MathUtil.lessEqual( u1inc, 0 ) && MathUtil.lessEqual( u2inc, 0 );
                boolean deepestOnly = extend1 || extend0 || infIsc;

                double d1 = pt1ref.sub( pt1inc, v ).dot( n );
                double d2 = pt2ref.sub( pt2inc, v ).dot( n );

                if( d1 < 0 && d2 < 0 ) {
                    return;
                }

                double dist1 = pt1ref.dist( pt1inc );
                double dist2 = pt2ref.dist( pt2inc );

                if( dist2 > dist1 ) {
                    if( d2 >= 0 && ! deepestOnly ) {
                        refList.add( pt2ref );
                        incList.add( pt2inc );
                    }
                    if( d1 >= 0 ) {
                        refList.add( pt1ref );
                        incList.add( pt1inc );
                    }
                } else {
                    if( d1 >= 0 && ! deepestOnly  ) {
                        refList.add( pt1ref );
                        incList.add( pt1inc );
                    }
                    if( d2 >= 0 ) {
                        refList.add( pt2ref );
                        incList.add( pt2inc );
                    }
                }
            }

            collector.addCollision( primer );
        }
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
}
