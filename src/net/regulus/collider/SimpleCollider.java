package net.regulus.collider;

import net.regulus.geom.AABB;
import net.regulus.geom.Mat2;
import net.regulus.geom.Mat3;
import net.regulus.geom.Vec2;

public abstract class SimpleCollider implements ICollider {
    protected final Vec2 offset = new Vec2();
    protected double rotationOffset;
    protected double inertiaFactor;

    protected final Mat3 matrix = Mat3.identity();
    protected final Mat3 bodyMatrix = Mat3.identity();
    protected final Mat2 linearTransforms = Mat2.identity();
    protected final Vec2 direction = new Vec2();
    protected double globalRotation;

    protected final Vec2 center = new Vec2();
    protected final Vec2 centerOfMass = new Vec2();
    protected double area;
    protected final AABB box = new AABB();

    public Vec2 getOffset( Vec2 out ) {
        return Vec2.put( out, offset );
    }

    public void setOffset( Vec2 off ) {
        offset.set( off );
        recomputeMatrix();
        recompute();
    }

    public void setOffset( double x, double y ) {
        offset.set( x, y );
        recomputeMatrix();
        recompute();
    }

    public void setRotationOffset( double rotationOffset ) {
        this.rotationOffset = rotationOffset;
        recomputeMatrix();
        recompute();
    }

    public double getRotationOffset() {
        return rotationOffset;
    }

    @Override
    public void setBodyMatrix( Mat3 bodyMatrix ) {
        this.bodyMatrix.set( bodyMatrix );
        recomputeMatrix();
        recompute();
    }


    public Mat3 getGlobalMatrix( Mat3 out ) {
        return Mat3.put( out, matrix );
    }

    public Vec2 getCenter( Vec2 out ) {
        return Vec2.put( out, center );
    }

    public Vec2 getCenterOfMass( Vec2 out ) {
        return Vec2.put( out, centerOfMass );
    }

    @Override
    public AABB getBox( AABB out ) {
        return AABB.put( out, box );
    }

    public double getArea() {
        return area;
    }


    protected void recomputeMatrix() {
        matrix.setIdentity().mul( bodyMatrix, matrix )
              .translate( offset, matrix )
              .rotateCCW( rotationOffset, matrix );
        matrix.getLinearTransform( linearTransforms );

        direction.set( 1, 0 );
        linearTransforms.mul( direction, direction );
        globalRotation = direction.dir();
    }

    public double getGlobalRotation() {
        return globalRotation;
    }

    public Vec2 getDirection( Vec2 out ) {
        return Vec2.put( out, direction );
    }

    protected abstract void recompute();

    @Override
    public double computeInertia( double density ) {
        return inertiaFactor * density;
    }

    @Override
    public double computeMass( double density ) {
        return area * density;
    }

    @Override
    public double computeDensityFromInertia( double inertia ) {
        return inertia / inertiaFactor;
    }

    @Override
    public double computeDensityFromMass( double mass ) {
        return mass / area;
    }

    public double getInertiaFactor() {
        return inertiaFactor;
    }

    @Override
    public boolean isCompound() {
        return false;
    }
}
