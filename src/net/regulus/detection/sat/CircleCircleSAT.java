package net.regulus.detection.sat;

import net.regulus.collider.CircleCollider;
import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.ICollisionCollector;
import net.regulus.geom.Vec2;

public final class CircleCircleSAT {
    private CircleCircleSAT() {
    }

    public static void collide( CircleCollider a, CircleCollider b, ICollisionCollector collector ) {
        Vec2 centerA = a.getCenter( null );
        Vec2 centerB = b.getCenter( null );

        double radiusA = a.getRadius();
        double radiusB = b.getRadius();
        double radiusTotal = radiusA + radiusB;
        double dist = centerA.distSq( centerB );

        if( dist <= radiusTotal * radiusTotal ) {
            CollisionPrimer primer = new CollisionPrimer();
            dist = Math.sqrt( dist );
            primer.penetrationDepth = radiusTotal - dist;
            centerA.sub( centerB, primer.normal ).norm( primer.normal );

            Vec2 coll = new Vec2();
            primer.normal.mul( radiusA, coll ).neg( coll ).add( centerA, coll );
            primer.collisionA.add( coll );
            primer.normal.mul( radiusB, coll ).add( centerB, coll );
            primer.collisionB.add( coll );

            collector.addCollision( primer );
        }
    }
}
