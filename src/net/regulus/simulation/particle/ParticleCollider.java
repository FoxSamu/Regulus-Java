package net.regulus.simulation.particle;

import net.regulus.collider.CircleCollider;
import net.regulus.geom.AABB;
import net.regulus.geom.Vec2;

class ParticleCollider extends CircleCollider {

    @Override
    protected void recomputeMatrix() {
    }

    @Override
    protected void recompute() {

    }

    public void set( Vec2 pos, double rad, AABB box ) {
        this.center.set( pos );
        this.radius = rad;
        this.box.set( box );
    }
}
