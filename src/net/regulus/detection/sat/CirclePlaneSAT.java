package net.regulus.detection.sat;

import net.regulus.collider.CircleCollider;
import net.regulus.collider.PlaneCollider;
import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.ICollisionCollector;
import net.regulus.geom.Vec2;

public final class CirclePlaneSAT {
    private CirclePlaneSAT() {
    }

    public static void collide( CircleCollider a, PlaneCollider b, ICollisionCollector collector ) {
        Vec2 normal = b.getNormal( null );
        Vec2 centerA = a.getCenter( null );
        Vec2 centerB = b.getCenter( null );

        Vec2 rel = centerA.sub( centerB, null );
        double dot = normal.dot( rel );

        if( dot <= a.getRadius() ) {
            CollisionPrimer primer = new CollisionPrimer();
            primer.normal.set( normal );
            primer.penetrationDepth = a.getRadius() - dot;

            Vec2 tangent = normal.perp( null );
            double cross = normal.cross( rel );

            Vec2 ptA = centerA.add( normal.mul( - a.getRadius(), rel ), centerA );
            primer.collisionA.add( ptA );

            Vec2 ptB = centerB.add( tangent.mul( cross, rel ), centerB );
            primer.collisionB.add( ptB );

            collector.addCollision( primer );
        }
    }

    public static void collide( PlaneCollider a, CircleCollider b, ICollisionCollector collector ) {
        collide( b, a, c -> collector.addCollision( c.invert() ) );
    }
}
