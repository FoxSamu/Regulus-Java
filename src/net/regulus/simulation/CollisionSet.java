package net.regulus.simulation;

import java.util.ArrayList;
import java.util.List;

import net.regulus.detection.CollisionPrimer;
import net.regulus.detection.ICollisionCollector;
import net.regulus.geom.Vec2;

public class CollisionSet implements ICollisionCollector {
    public final List<Collision> collisions = new ArrayList<>();
    public final Body bodyA;
    public final Body bodyB;
    private Vec2 center;

    public CollisionSet( Body bodyA, Body bodyB ) {
        this.bodyA = bodyA;
        this.bodyB = bodyB;
    }

    public void resolve(double dt) {
        for (Collision collision : collisions) {
            collision.resolve( dt, collisions.size() );
        }
    }

    public void correct() {
        for (Collision collision : collisions) {
            collision.correct( collisions.size() );
        }
    }

    @Override
    public void addCollision( CollisionPrimer primer ) {
        collisions.add( new Collision( bodyA, bodyB, primer ) );
    }

    public Vec2 getCenter() {
        if (center == null) {
            center = new Vec2();

            for (Collision c : collisions) {
                center.add( c.getCenter(), center );
            }

            center.div( collisions.size(), center );
        }
        return center;
    }
}
