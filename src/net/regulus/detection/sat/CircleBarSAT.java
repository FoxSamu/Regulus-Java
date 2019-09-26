package net.regulus.detection.sat;

import net.regulus.collider.BarCollider;
import net.regulus.collider.CircleCollider;
import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.ICollisionCollector;
import net.regulus.geom.Vec2;

public final class CircleBarSAT {
    private CircleBarSAT() {
    }

    public static void collide( CircleCollider a, BarCollider b, ICollisionCollector collector ) {
        Vec2 normal = b.getNormal( null );

        Vec2 centerA = a.getCenter( null );
        Vec2 centerB = b.getCenter( null );

        double radiusA = a.getRadius();
        double radiusB = b.getWidth() / 2;

        double radiusTotal = radiusA + radiusB;

        Vec2 rel = centerA.sub( centerB, null );
        double dot = normal.dot( rel );

        if( dot <= radiusTotal && dot >= - radiusTotal ) {
            double signum = dot < 0 ? - 1 : 1;

            CollisionPrimer primer = new CollisionPrimer();
            normal.mul( signum, normal );
            primer.normal.set( normal );

            primer.penetrationDepth = radiusTotal - Math.abs( dot );

            Vec2 tangent = normal.perp( null );
            double cross = normal.cross( rel );

            Vec2 ptA = centerA.add( normal.mul( - radiusA, rel ), centerA );
            primer.collisionA.add( ptA );

            Vec2 ptB = centerB.add( tangent.mul( cross, rel ), centerB )
                              .add( normal.mul( radiusB, rel ), centerB );
            primer.collisionB.add( ptB );

            collector.addCollision( primer );
        }
    }

    public static void collide( BarCollider a, CircleCollider b, ICollisionCollector collector ) {
        collide( b, a, c -> collector.addCollision( c.invert() ) );
    }
}
