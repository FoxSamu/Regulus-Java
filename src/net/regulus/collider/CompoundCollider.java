package net.regulus.collider;

import java.util.ArrayList;
import java.util.List;

import net.regulus.geom.AABB;
import net.regulus.geom.Mat3;

public class CompoundCollider implements ICollider {
    public final List<SimpleCollider> colliders = new ArrayList<>();

    @Override
    public boolean isCompound() {
        return true;
    }

    @Override
    public AABB getBox( AABB out ) {
        if( out == null )
            out = new AABB();
        boolean set = false;
        AABB a = new AABB();
        for( SimpleCollider collider : colliders ) {
            collider.getBox( a );
            if( ! set ) {
                out.set( a );
                set = true;
            } else {
                AABB.unite( out, a, out );
            }
        }
        return out;
    }

    @Override
    public void setBodyMatrix( Mat3 matrix ) {
        for( SimpleCollider c : colliders ) {
            c.setBodyMatrix( matrix );
        }
    }

    @Override
    public double computeInertia( double density ) {
        double ifactor = 0;
        for( SimpleCollider coll : colliders ) {
            ifactor += coll.inertiaFactor;
        }
        return ifactor * density;
    }

    @Override
    public double computeMass( double density ) {
        double area = 0;
        for( SimpleCollider coll : colliders ) {
            area += coll.area;
        }
        return area * density;
    }

    @Override
    public double computeDensityFromInertia( double density ) {
        double ifactor = 0;
        for( SimpleCollider coll : colliders ) {
            ifactor += coll.inertiaFactor;
        }
        return density / ifactor;
    }

    @Override
    public double computeDensityFromMass( double mass ) {
        double area = 0;
        for( SimpleCollider coll : colliders ) {
            area += coll.area;
        }
        return mass / area;
    }
}
