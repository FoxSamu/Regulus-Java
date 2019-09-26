package net.regulus.detection.sat;

import net.regulus.collider.PolygonalCollider;
import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.ICollisionCollector;
import net.regulus.geom.*;

public final class PolygonPolygonSAT {
    private PolygonPolygonSAT() {
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

    public static void collide( PolygonalCollider a, PolygonalCollider b, ICollisionCollector collector ) {
        PolygonalCollider.Looper looperA = a.looper();
        PolygonalCollider.Looper looperB = b.looper();
        int sizeA = a.getGlobal().size();
        int sizeB = b.getGlobal().size();

        int maxSize = Math.max( sizeA, sizeB );

        VectorList satAxes = new VectorList();

        Vec2 curr = new Vec2(), next = new Vec2(), axis = new Vec2(), use = new Vec2();

        // Collect SAT-axes
        for( int i = 0; i < sizeA; i++ ) {
            looperA.goTo( i );
            looperA.currGlobal( curr );
            looperA.nextGlobal( next );
            next.sub( curr, axis ).perp( axis ).norm( axis );
            if( ! contains( satAxes, axis, use ) ) {
                satAxes.add( axis );
            }
        }

        for( int i = 0; i < sizeB; i++ ) {
            looperB.goTo( i );
            looperB.currGlobal( curr );
            looperB.nextGlobal( next );

            next.sub( curr, axis ).perp( axis ).norm( axis );
            if( ! contains( satAxes, axis, use ) ) {
                satAxes.add( axis );
            }
        }


        // Project polygons on SAT axes
        Projection proj = new Projection();

        int axesAmount = satAxes.size();
        double smallestOverlap = Double.POSITIVE_INFINITY;
        Vec2 normal = new Vec2();
        for( int i = 0; i < axesAmount; i++ ) {
            satAxes.get( i, axis );

            double minA = Double.POSITIVE_INFINITY;
            double maxA = Double.NEGATIVE_INFINITY;
            double minB = Double.POSITIVE_INFINITY;
            double maxB = Double.NEGATIVE_INFINITY;

            proj.baseVector.set( axis );
            for( int j = 0; j < maxSize; j++ ) {
                if( j < sizeA ) {
                    looperA.goTo( j );
                    looperA.currGlobal( curr );
                    proj.projected.set( curr );
                    proj.project();

                    double tl = proj.tangentLength;
                    if( tl < minA )
                        minA = tl;
                    if( tl > maxA )
                        maxA = tl;
                }

                if( j < sizeB ) {
                    looperB.goTo( j );
                    looperB.currGlobal( curr );
                    proj.projected.set( curr );
                    proj.project();

                    double tl = proj.tangentLength;
                    if( tl < minB )
                        minB = tl;
                    if( tl > maxB )
                        maxB = tl;
                }
            }

            int overlapDir = overlapDirection( minA, maxA, minB, maxB );
            if( overlapDir == 0 ) {
                // Separating axis found: no overlap
                return;
            }

            double overlap = overlap( minA, maxA, minB, maxB );
            if( overlap < smallestOverlap ) {
                smallestOverlap = overlap;
                axis.mul( overlapDir, normal );
            }
        }

        CollisionPrimer primer = new CollisionPrimer();
        primer.normal.set( normal );
        primer.penetrationDepth = smallestOverlap;

        collector.addCollision( primer );


        // Find best edge for A
        int bestEdgeIndexA = - 1, bestEdgeIndexB = - 1;
        double dotA = Double.NEGATIVE_INFINITY, dotB = Double.NEGATIVE_INFINITY;
        Vec2 prev = new Vec2();

        normal.neg( normal );

        for( int i = 0; i < sizeA; i++ ) {
            looperA.goTo( i );
            looperA.currGlobal( curr );
            double dot = curr.dot( normal );
            if( dot > dotA ) {
                dotA = dot;
                bestEdgeIndexA = i;
            }
        }

        Line bestEdgeA;
        looperA.goTo( bestEdgeIndexA );
        looperA.currGlobal( curr );
        looperA.backwardGlobEdge( prev ).neg( prev ).norm( prev );
        looperA.forwardGlobEdge( next ).neg( next ).norm( next );
        if( prev.dot( normal ) <= next.dot( normal ) ) {
            looperA.prevGlobal( prev );
            bestEdgeA = new Line( prev, curr );
        } else {
            looperA.nextGlobal( next );
            bestEdgeA = new Line( curr, next );
        }


        // FInd best edge for B
        normal.neg( normal );

        for( int i = 0; i < sizeB; i++ ) {
            looperB.goTo( i );
            looperB.currGlobal( curr );
            double dot = curr.dot( normal );
            if( dot > dotB ) {
                dotB = dot;
                bestEdgeIndexB = i;
            }
        }

        Line bestEdgeB;
        looperB.goTo( bestEdgeIndexB );
        looperB.currGlobal( curr );
        looperB.backwardGlobEdge( prev ).neg( prev ).norm( prev );
        looperB.forwardGlobEdge( next ).neg( next ).norm( next );
        if( prev.dot( normal ) <= next.dot( normal ) ) {
            looperB.prevGlobal( prev );
            bestEdgeB = new Line( prev, curr );
        } else {
            looperB.nextGlobal( next );
            bestEdgeB = new Line( curr, next );
        }


        // Compute reference and incidence edge
        boolean flip = false;
        Line ref, inc;


        int amount = 2;
        LineIntersection isc = new LineIntersection();

        dotA = Math.abs( bestEdgeA.edge( axis ).dot( normal ) );
        dotB = Math.abs( bestEdgeB.edge( axis ).dot( normal ) );

        if( dotA <= dotB ) {
            ref = bestEdgeA;
            inc = bestEdgeB;
        } else {
            ref = bestEdgeB;
            inc = bestEdgeA;
            flip = true;
        }

        // Clipping plane 1
        isc.set( ref.pointA, ref.pointA.add( normal, use ), inc.pointA, inc.pointB ).intersect();
        if( isc.segB() ) {
            ref.edge( axis ).norm( axis );

            double proj1 = inc.pointA.sub( ref.pointA, use ).dot( axis );
            double proj2 = inc.pointB.sub( ref.pointA, use ).dot( axis );

            if( proj1 <= 0 ) {
                inc.pointA.set( isc.intersection );
            } else if( proj2 <= 0 ) {
                inc.pointB.set( isc.intersection );
            }
        }

        // Clipping plane 2
        isc.set( ref.pointB, ref.pointB.add( normal, use ), inc.pointA, inc.pointB ).intersect();
        if( isc.segB() ) {
            ref.invEdge( axis ).norm( axis );

            double proj1 = inc.pointA.sub( ref.pointB, use ).dot( axis );
            double proj2 = inc.pointB.sub( ref.pointB, use ).dot( axis );

            if( proj1 <= 0 ) {
                inc.pointA.set( isc.intersection );
            } else if( proj2 <= 0 ) {
                inc.pointB.set( isc.intersection );
            }
        }

        // Clipping plane 3
        isc.set( ref, inc ).intersect();
        if( isc.segB() ) {
            if( flip )
                normal.neg( axis );
            else
                axis.set( normal );

            double proj1 = inc.pointA.sub( ref.pointA, use ).dot( axis );
            double proj2 = inc.pointB.sub( ref.pointA, use ).dot( axis );

            if( proj1 <= 0 ) {
                inc.pointA.set( inc.pointB );
                amount --;
            }
            if( proj2 <= 0 ) {
                amount --;
            }
        }

        // In some cases, when two polygons are very close to each other without overlapping, SAT may conclude there is
        // overlap. When computing collision points, it will turn out that all collision points lay inside the third
        // clipping pane, meaning that it separates the polygons. Special cases are filtered out here...
        if( amount <= 0 ) {
            return;
        }

        // Compute points on other polygon
        if( amount == 1 ) {
            normal.mul( ( flip ? 1 : - 1 ) * smallestOverlap, axis );
            inc.pointA.add( axis, ref.pointA );
        } else {
            isc.set( inc.pointA, inc.pointA.add( normal, use ), ref.pointA, ref.pointB ).intersect();
            curr.set( isc.intersection );

            isc.set( inc.pointB, inc.pointB.add( normal, use ), ref.pointA, ref.pointB ).intersect();
            next.set( isc.intersection );

            ref.pointA.set( curr );
            ref.pointB.set( next );
        }

        // Complete CollisionPrimer
        if( amount == 1 ) {
            if( flip ) {
                primer.collisionA.add( inc.pointA );
                primer.collisionB.add( ref.pointA );
            } else {
                primer.collisionA.add( ref.pointA );
                primer.collisionB.add( inc.pointA );
            }
        } else {
            boolean bFirst = inc.pointA.distSq( ref.pointA ) < inc.pointB.distSq( ref.pointB );
            if( flip ) {
                if( bFirst ) {
                    primer.collisionA.add( inc.pointB );
                    primer.collisionA.add( inc.pointA );
                    primer.collisionB.add( ref.pointB );
                    primer.collisionB.add( ref.pointA );
                } else {
                    primer.collisionA.add( inc.pointA );
                    primer.collisionA.add( inc.pointB );
                    primer.collisionB.add( ref.pointA );
                    primer.collisionB.add( ref.pointB );
                }
            } else {
                if( bFirst ) {
                    primer.collisionB.add( inc.pointB );
                    primer.collisionB.add( inc.pointA );
                    primer.collisionA.add( ref.pointB );
                    primer.collisionA.add( ref.pointA );
                } else {
                    primer.collisionB.add( inc.pointA );
                    primer.collisionB.add( inc.pointB );
                    primer.collisionA.add( ref.pointA );
                    primer.collisionA.add( ref.pointB );
                }
            }
        }

        collector.addCollision( primer );
    }
}
