package net.regulus.simulation;

import java.util.Arrays;

import net.regulus.collider.SimpleCollider;
import net.regulus.collider.CircleCollider;
import net.regulus.collider.CompoundCollider;
import net.regulus.collider.ICollider;
import net.regulus.geom.Mat3;
import net.regulus.geom.Vec2;

public class Body {
    public final Vec2 position = new Vec2();
    public final Vec2 velocity = new Vec2();
    public final Vec2 positionalVel = new Vec2();

    protected ICollider collider;

    public double rotation;
    public double rotationVelo;

    public final Mass mass = new Mass( this );

    public double restitution;
    public double linearDrag = 0.01;
    public double angularDrag = 0.01;
    public double staticFriction = 0.08;
    public double particleFriction = 0.001;
    public double dynamicFriction = 0.08;
    public double surfaceVelo = 0;

    private final Mat3 matrix = new Mat3();
    private final Vec2 v = new Vec2();

    public ICollider getCollider() {
        return collider;
    }

    public void setCollider( ICollider collider ) {
        this.collider = collider;
        mass.recompute();
    }

    public void prepare() {
        matrix.setIdentity()
              .translate( position, matrix )
              .rotateCCW( rotation, matrix );

        if( collider != null ) {
            collider.setBodyMatrix( matrix );
        }

        positionalVel.set( 0, 0 );
    }

    public Vec2 untransform( Vec2 pt, Vec2 out ) {
        return pt.sub( position, out ).rotate( - rotation, out );
    }

    public Vec2 transform( Vec2 pt, Vec2 out ) {
        return pt.rotate( rotation, out ).add( position, out );
    }

    public void applyImpulse( Vec2 impulse, Vec2 contact, boolean rotational ) {
        velocity.add( impulse.mul( mass.getInvMass(), v ), velocity );
        // Normal-impulse should never cause circles to rotate, though it does... Special check here for circles...
        if( ! ( collider instanceof CircleCollider && ! rotational ) ) {
            rotationVelo += mass.getInvInertia() * contact.cross( impulse );
        }
    }

    public Vec2 getCombinedVelo( Vec2 tangent, Vec2 point, Vec2 out ) {
        return velocity.add( positionalVel, out )
                       .add( tangent.mul( surfaceVelo, v ), out )
                       .sub( Vec2.cross( - rotationVelo, point, v ), out );
    }

    public Vec2 getConstraintVelo( Vec2 point, Vec2 out ) {
        return velocity.sub( Vec2.cross( - rotationVelo, point, v ), out );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private static final int DENSITY = 0;
        private static final int MASS = 1;
        private static final int INV_MASS = 2;
        private static final int INERTIA = 3;
        private static final int INV_INERTIA = 4;
        private static final int STATIC = 5;

        private World world;
        private double massValue = 1;
        private int massType = DENSITY;
        private double linearDrag = 0.01;
        private double angularDrag = 0.01;
        private double staticFriction = 0.08;
        private double dynamicFriction = 0.08;
        private double surfaceVelo;
        private double restitution;
        private ICollider collider;
        private final Vec2 position = new Vec2();
        private final Vec2 velocity = new Vec2();
        private double rotation;
        private double rotationVelo;

        private Builder() {
        }

        public Builder addTo( World world ) {
            this.world = world;
            return this;
        }

        public Builder density( double value ) {
            massValue = value;
            massType = DENSITY;
            return this;
        }

        public Builder mass( double value ) {
            massValue = value;
            massType = MASS;
            return this;
        }

        public Builder invMass( double value ) {
            massValue = value;
            massType = INV_MASS;
            return this;
        }

        public Builder inertia( double value ) {
            massValue = value;
            massType = INERTIA;
            return this;
        }

        public Builder invInertia( double value ) {
            massValue = value;
            massType = INV_INERTIA;
            return this;
        }

        public Builder makeStatic() {
            massValue = 0;
            massType = STATIC;
            return this;
        }

        public Builder linearDrag( double value ) {
            linearDrag = value;
            return this;
        }

        public Builder angularDrag( double value ) {
            angularDrag = value;
            return this;
        }

        public Builder drag( double lin, double ang ) {
            angularDrag = ang;
            linearDrag = lin;
            return this;
        }

        public Builder staticFriction( double value ) {
            staticFriction = value;
            return this;
        }

        public Builder dynamicFriction( double value ) {
            dynamicFriction = value;
            return this;
        }

        public Builder friction( double stat, double dyn ) {
            staticFriction = stat;
            dynamicFriction = dyn;
            return this;
        }

        public Builder restitution( double value ) {
            restitution = value;
            return this;
        }

        public Builder bounciness( double value ) {
            restitution = value;
            return this;
        }

        public Builder position( double x, double y ) {
            position.set( x, y );
            return this;
        }

        public Builder position( Vec2 v ) {
            position.set( v );
            return this;
        }

        public Builder velocity( double x, double y ) {
            velocity.set( x, y );
            return this;
        }

        public Builder velocity( Vec2 v ) {
            velocity.set( v );
            return this;
        }

        public Builder rotation( double value ) {
            rotation = value;
            return this;
        }

        public Builder rotationVelo( double value ) {
            rotationVelo = value;
            return this;
        }

        public Builder surfaceVelo( double value ) {
            surfaceVelo = value;
            return this;
        }

        public Builder collider( ICollider collider ) {
            this.collider = collider;
            return this;
        }

        public Builder collider( ICollider.SimpleBuilder<?, ?> collider ) {
            this.collider = collider.build();
            return this;
        }

        public Builder collider( SimpleCollider... colliders ) {
            CompoundCollider cpd = new CompoundCollider();
            cpd.colliders.addAll( Arrays.asList( colliders ) );
            collider = cpd;
            return this;
        }

        public <T extends Body> T build( T instance ) {
            instance.collider = collider;
            switch( massType ) {
                default:
                case DENSITY:
                    instance.mass.setDensity( massValue );
                    break;
                case MASS:
                    instance.mass.setMass( massValue );
                    break;
                case INERTIA:
                    instance.mass.setInertia( massValue );
                    break;
                case INV_MASS:
                    instance.mass.setMass( 1 / massValue );
                    break;
                case INV_INERTIA:
                    instance.mass.setInertia( 1 / massValue );
                    break;
                case STATIC:
                    instance.mass.setStatic();
            }
            instance.linearDrag = linearDrag;
            instance.angularDrag = angularDrag;
            instance.staticFriction = staticFriction;
            instance.dynamicFriction = dynamicFriction;
            instance.surfaceVelo = surfaceVelo;
            instance.restitution = restitution;
            instance.position.set( position );
            instance.velocity.set( velocity );
            instance.rotation = rotation;
            instance.rotationVelo = rotationVelo;

            if( world != null ) {
                world.add( instance );
            }

            return instance;
        }

        public Body build() {
            return build( new Body() );
        }

        public Body buildInto( World world ) {
            return addTo( world ).build();
        }

        public <T extends Body> T buildInto( World world, T inst ) {
            return addTo( world ).build( inst );
        }
    }
}
