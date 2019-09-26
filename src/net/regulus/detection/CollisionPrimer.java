package net.regulus.detection;

import net.regulus.geom.Vec2;
import net.regulus.geom.VectorList;

public class CollisionPrimer {
    public final Vec2 normal = new Vec2();
    public final VectorList collisionA = new VectorList();
    public final VectorList collisionB = new VectorList();
    public double penetrationDepth;

    public CollisionPrimer invert() {
        // Flip normal and swap detection points
        normal.neg( normal );
        Vec2 tmp1 = new Vec2();
        Vec2 tmp2 = new Vec2();
        for( int i = 0; i < collisionA.size(); i ++ ) {
            collisionA.get( i, tmp1 );
            collisionB.get( i, tmp2 );
            collisionA.set( i, tmp2 );
            collisionB.set( i, tmp1 );
        }

        return this;
    }
}
