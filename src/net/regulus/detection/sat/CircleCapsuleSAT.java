package net.regulus.detection.sat;

import net.regulus.collider.CapsuleCollider;
import net.regulus.collider.CircleCollider;
import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.ICollisionCollector;
import net.regulus.geom.Line;
import net.regulus.geom.LineDistance;
import net.regulus.geom.MathUtil;
import net.regulus.geom.Vec2;

public final class CircleCapsuleSAT {
    private CircleCapsuleSAT() {
    }

    public static void collide( CircleCollider a, CapsuleCollider b, ICollisionCollector collector ) {
        Line rig = new Line();
        b.getLeft( rig.pointA );
        b.getRight( rig.pointB );

        double radiusA = a.getRadius();
        double radiusB = b.getRadius();
        double radiusTotal = radiusA + radiusB;

        Vec2 center = a.getCenter( null );

        LineDistance ld = new LineDistance( center, rig );

        if( ld.distance <= radiusTotal ) {
            CollisionPrimer primer = new CollisionPrimer();
            primer.penetrationDepth = radiusTotal - ld.distance;
            if( MathUtil.equal( ld.distance, 0 ) ) {
                rig.edge( ld.normal )
                   .perp( ld.normal )
                   .norm( ld.normal );
            }
            primer.normal.set( ld.normal );

            Vec2 v = ld.normal.mul( radiusB, null );
            Vec2 p = v.add( ld.closest, null );
            primer.collisionB.add( p );

            ld.normal.mul( - radiusA, v );
            v.add( a.getCenter( p ), p );
            primer.collisionA.add( p );

            collector.addCollision( primer );
        }
    }

    public static void collide( CapsuleCollider a, CircleCollider b, ICollisionCollector collector ) {
        collide( b, a, c -> collector.addCollision( c.invert() ) );
    }
}
