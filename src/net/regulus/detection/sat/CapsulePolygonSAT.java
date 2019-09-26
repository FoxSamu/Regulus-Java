package net.regulus.detection.sat;

import net.regulus.collider.CapsuleCollider;
import net.regulus.collider.PolygonalCollider;
import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.ICollisionCollector;
import net.regulus.geom.*;

public final class CapsulePolygonSAT {
    private CapsulePolygonSAT() {
    }

    private static boolean contains( VectorList satAxes, Vec2 axis, Vec2 use ) {
        for( int i = 0; i < satAxes.size(); i++ ) {
            satAxes.get( i, use );
            if( MathUtil.equal( axis.cross( use ), 0 ) ) {
                return true;
            }
        }
        return false;
    }

    private static int overlapDirection( double minA, double maxA, double minB, double maxB ) {
        if( minA <= maxB && minB <= maxA ) {
            if( maxA - minB <= maxB - minA ) {
                return - 1;
            } else {
                return 1;
            }
        }
        return 0;
    }

    private static double overlap( double minA, double maxA, double minB, double maxB ) {
        if( maxA - minB <= maxB - minA ) {
            return maxA - minB;
        } else {
            return maxB - minA;
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

    public static void collide( CapsuleCollider a, PolygonalCollider b, ICollisionCollector collector ) {

        // Some initial definitions
        Vec2 v1 = new Vec2(), v2 = new Vec2();
        Line rig = new Line( a.getLeft( v1 ), a.getRight( v2 ) );

        Vec2 curr = new Vec2(), next = new Vec2(), edgeV = new Vec2(), axis = new Vec2();
        Line edge = new Line();
        int size = b.getGlobal().size();
        PolygonalCollider.Looper looper = b.looper();

        LineDistance dist = new LineDistance();
        dist.distance = Double.POSITIVE_INFINITY;
        LineDistance ld = new LineDistance();

        // Collect projection axes, which are:
        Vec2 closerPoint = new Vec2();
        VectorList satAxes = new VectorList();

        // 1: All the different polygon axes
        for( int i = 0; i < size; i++ ) {
            looper.goTo( i );
            looper.currGlobal( curr );
            looper.nextGlobal( next );
            edge.set( curr, next );

            next.sub( curr, edgeV ).perp( edgeV ).norm( edgeV );

            if( ! contains( satAxes, edgeV, v1 ) ) {
                satAxes.add( edgeV );
            }

            ld.line.set( edge );
            ld.point.set( rig.pointA );
            ld.compute();
            if( ld.distance < dist.distance ) {
                dist.copy( ld );
                closerPoint.set( rig.pointA );
            }

            ld.line.set( edge );
            ld.point.set( rig.pointB );
            ld.compute();
            if( ld.distance < dist.distance ) {
                dist.copy( ld );
                closerPoint.set( rig.pointB );
            }
        }

        // 2: The orthogonal axis of the capsule's rig, if not parallel to one of the polygon
        int capsuleAxisI = - 1;
        rig.edge( edgeV ).perp( edgeV ).norm( edgeV );
        if( ! contains( satAxes, edgeV, v1 ) ) {
            capsuleAxisI = satAxes.size();
            satAxes.add( edgeV );
        }

        // 3: The axis parallel to the shortest line between the capsule's rig and the polygon boundary, if not parallel
        //    to one of the polygon
        int pointAxisI = - 1;
        if( ! contains( satAxes, dist.normal, v1 ) ) {
            pointAxisI = satAxes.size();
            satAxes.add( dist.normal );
        }

        Vec2 normal = new Vec2();
        double depth = Double.POSITIVE_INFINITY;
        int index = - 1;

        double radius = a.getRadius();

        int axiscount = satAxes.size();
        for( int i = 0; i < axiscount; i++ ) {
            satAxes.get( i, axis );

            double d1 = rig.pointA.dot( axis );
            double d2 = rig.pointB.dot( axis );

            double mina = Math.min( d1, d2 ) - radius;
            double maxa = Math.max( d1, d2 ) + radius;

            double minb = Double.POSITIVE_INFINITY;
            double maxb = Double.NEGATIVE_INFINITY;

            for( int j = 0; j < size; j++ ) {
                looper.goTo( j );
                looper.currGlobal( curr );
                double dot = curr.dot( axis );

                if( dot < minb )
                    minb = dot;
                if( dot > maxb )
                    maxb = dot;
            }

            int od = overlapDirection( mina, maxa, minb, maxb );
            if( od == 0 ) {
                return;
            }

            double ov = overlap( mina, maxa, minb, maxb );
            if( ov < depth ) {
                depth = ov;
                axis.mul( od, normal );
                index = i;
            }
        }

        if( index < 0 )
            return;

        // Now that we know the capsule collides with the polygon, we can distinguish three different cases
        // Every case has it's own way of finding the support points...

        boolean pointAxis = index == pointAxisI;

        CollisionPrimer primer = new CollisionPrimer();
        primer.normal.set( normal );
        primer.penetrationDepth = depth;

        if( pointAxis ) {
            // First (and least complicated) case: least-depth axis isn't orthogonal to anything
            closerPoint.add( normal.mul( - radius, v1 ), v2 );
            primer.collisionA.add( v2 );
            primer.collisionB.add( dist.line.interpolate( dist.u, v1 ) );
        } else {
            // Second or third case... We need the best edge for both cases so before jumping into one of them:
            int bestEdgeIndex = - 1;
            double highestDot = Double.NEGATIVE_INFINITY;
            Vec2 prev = new Vec2();

            for( int i = 0; i < size; i++ ) {
                looper.goTo( i );
                looper.currGlobal( curr );
                double dot = curr.dot( normal );
                if( dot > highestDot ) {
                    highestDot = dot;
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

            boolean capsuleAxis = index == capsuleAxisI;
            if( capsuleAxis ) {
                // Second case: least-depth axis is orthogonal to the capsule's rig

                // Find the point of the best edge that is closer to the capsule's rig
                double leastDist = Double.POSITIVE_INFINITY;
                ld.line.set( rig );
                ld.point.set( bestEdge.pointA );
                ld.compute();
                if( ld.distance < leastDist && ld.type == LineDistance.LINE ) {
                    leastDist = ld.distance;
                    closerPoint.set( bestEdge.pointA );
                    curr.set( ld.closest );
                }
                ld.point.set( bestEdge.pointB );
                ld.compute();
                if( ld.distance < leastDist && ld.type == LineDistance.LINE ) {
                    closerPoint.set( bestEdge.pointB );
                    curr.set( ld.closest );
                }

                primer.collisionB.add( closerPoint );
                primer.collisionA.add( curr.add( normal.mul( - radius, v1 ), v2 ) );
            } else {
                // Third (and most complicated) case: least-depth axis is orthogonal to a polygon edge

                Line offRig = new Line( rig );
                Vec2 rigNormal = rig.edge( edgeV ).norm( edgeV ).perp( null );
                double ndot = rigNormal.dot( normal.neg( v2 ) );
                if( ndot < 0 ) {
                    rigNormal.neg( rigNormal );
                }
                rigNormal.mul( radius, rigNormal );
                offRig.pointA.add( rigNormal, offRig.pointA );
                offRig.pointB.add( rigNormal, offRig.pointB );


                LineIntersection li = new LineIntersection();
                li.lineA.set( bestEdge );
                li.lineB.set( rig.pointA, rig.pointA.add( normal, v1 ) );
                li.intersect();
                double uPoly1 = MathUtil.clamp( 0, 1, li.uA );
                li.lineA.set( bestEdge );
                li.lineB.set( rig.pointB, rig.pointB.add( normal, v1 ) );
                li.intersect();
                double uPoly2 = MathUtil.clamp( 0, 1, li.uA );

                bestEdge.interpolate( uPoly1, v1 );
                bestEdge.interpolate( uPoly2, v2 );
                bestEdge.set( v1, v2 );
                Vec2 ptB1 = bestEdge.pointA;
                Vec2 ptB2 = bestEdge.pointB;

                li.lineA.set( offRig );
                li.lineB.set( bestEdge.pointA, bestEdge.pointA.add( normal, v1 ) );
                li.intersect();
                double uCap1 = li.uA;
                li.lineA.set( offRig );
                li.lineB.set( bestEdge.pointB, bestEdge.pointB.add( normal, v1 ) );
                li.intersect();
                double uCap2 = li.uA;

                Vec2 ptA1 = computeSupport( uCap1, rig, offRig, normal, radius, -1, v1 );
                Vec2 ptA2 = computeSupport( uCap2, rig, offRig, normal, radius, -1, v1 );

                boolean infIsc = false;
                // Capsule rig orthogonal to edge
                if( MathUtil.equal( rig.edge( v1 ).cross( normal ), 0 ) ) {
                    double pr1 = rig.pointA.dot( normal );
                    double pr2 = rig.pointB.dot( normal );

                    Vec2 closer;
                    if( pr1 < pr2 ) closer = rig.pointA;
                    else closer = rig.pointB;

                    closer.sub( normal.mul( radius, v1 ), ptA1 );
                    ptA2.set( ptA1 );
                    infIsc = true;
                }

                boolean extend1 = MathUtil.moreEqual( uCap1, 1 ) && MathUtil.moreEqual( uCap2, 1 );
                boolean extend0 = MathUtil.lessEqual( uCap1, 0 ) && MathUtil.lessEqual( uCap2, 0 );
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
                    if( d1 >= 0 && ! onePointOnly  ) {
                        primer.collisionA.add( ptA1 );
                        primer.collisionB.add( ptB1 );
                    }
                    if( d2 >= 0 ) {
                        primer.collisionA.add( ptA2 );
                        primer.collisionB.add( ptB2 );
                    }
                }
            }
        }

        collector.addCollision( primer );
    }

    public static void collide( PolygonalCollider a, CapsuleCollider b, ICollisionCollector collector ) {
        collide( b, a, c -> collector.addCollision( c.invert() ) );
    }
}
