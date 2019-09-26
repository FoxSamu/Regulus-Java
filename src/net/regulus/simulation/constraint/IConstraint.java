package net.regulus.simulation.constraint;

import net.regulus.geom.Vec2;
import net.regulus.simulation.Body;
import net.regulus.simulation.World;

public interface IConstraint {
    void prepare();
    void resolve( double dt );
    void correct( double dt );

    default boolean collisionDisabled( Body bodyA, Body bodyB ) {
        return false;
    }

    boolean enabled();

    static AxleBuilder axle() {
        return new AxleBuilder();
    }

    static DistanceBuilder distance() {
        return new DistanceBuilder();
    }

    static SpringBuilder spring() {
        return new SpringBuilder();
    }

    abstract class Builder<C extends IConstraint, This extends Builder<C, This>> {
        protected boolean enabled = true;
        protected World addTo;

        Builder() {
        }

        @SuppressWarnings( "unchecked" )
        public This addTo( World world ) {
            addTo = world;
            return (This) this;
        }

        @SuppressWarnings( "unchecked" )
        public This enabled( boolean value ) {
            enabled = value;
            return (This) this;
        }

        public abstract C build();
    }

    abstract class LinkingBuilder<C extends LinkingConstraint, This extends LinkingBuilder<C, This>> extends Builder<C, This> {
        protected final Vec2 linkA = new Vec2();
        protected final Vec2 linkB = new Vec2();
        protected Body bodyA;
        protected Body bodyB;
        protected boolean collisionDisabled = true;

        LinkingBuilder() {
        }

        @SuppressWarnings( "unchecked" )
        public This a( Body body, Vec2 link ) {
            linkA.set( link );
            bodyA = body;
            return (This) this;
        }

        @SuppressWarnings( "unchecked" )
        public This b( Body body, Vec2 link ) {
            linkB.set( link );
            bodyB = body;
            return (This) this;
        }

        @SuppressWarnings( "unchecked" )
        public This a( Body body, double x, double y ) {
            linkA.set( x, y );
            bodyA = body;
            return (This) this;
        }

        @SuppressWarnings( "unchecked" )
        public This b( Body body, double x, double y ) {
            linkB.set( x, y );
            bodyB = body;
            return (This) this;
        }

        @SuppressWarnings( "unchecked" )
        public This bodyA( Body body ) {
            bodyA = body;
            return (This) this;
        }

        @SuppressWarnings( "unchecked" )
        public This bodyB( Body body ) {
            bodyB = body;
            return (This) this;
        }

        @SuppressWarnings( "unchecked" )
        public This linkA( Vec2 link ) {
            linkA.set( link );
            return (This) this;
        }

        @SuppressWarnings( "unchecked" )
        public This linkB( Vec2 link ) {
            linkB.set( link );
            return (This) this;
        }

        @SuppressWarnings( "unchecked" )
        public This linkA( double x, double y ) {
            linkA.set( x, y );
            return (This) this;
        }

        @SuppressWarnings( "unchecked" )
        public This linkB( double x, double y ) {
            linkB.set( x, y );
            return (This) this;
        }

        @SuppressWarnings( "unchecked" )
        public This collisionDisabled( boolean value ) {
            collisionDisabled = value;
            return (This) this;
        }
    }

    class AxleBuilder extends LinkingBuilder<AxleConstraint, AxleBuilder> {

        private AxleBuilder() {
        }

        @Override
        public AxleConstraint build() {
            AxleConstraint c = new AxleConstraint();
            c.enabled = enabled;
            c.linkA.set( linkA );
            c.linkB.set( linkB );
            c.bodyA = bodyA;
            c.bodyB = bodyB;
            c.disableCollision = collisionDisabled;
            if( addTo != null ) {
                addTo.add( c );
            }
            return c;
        }
    }

    class DistanceBuilder extends LinkingBuilder<DistanceConstraint, DistanceBuilder> {
        protected double min;
        protected double max;

        private DistanceBuilder() {
        }

        public DistanceBuilder min( double min ) {
            this.min = min;
            return this;
        }

        public DistanceBuilder max( double max ) {
            this.max = max;
            return this;
        }

        public DistanceBuilder range( double min, double max ) {
            this.min = min;
            this.max = max;
            return this;
        }

        public DistanceBuilder length( double l ) {
            this.min = l;
            this.max = l;
            return this;
        }

        @Override
        public DistanceConstraint build() {
            DistanceConstraint c = new DistanceConstraint();
            c.enabled = enabled;
            c.linkA.set( linkA );
            c.linkB.set( linkB );
            c.bodyA = bodyA;
            c.bodyB = bodyB;
            c.disableCollision = collisionDisabled;
            c.minDist = min;
            c.maxDist = max;
            if( addTo != null ) {
                addTo.add( c );
            }
            return c;
        }
    }

    class SpringBuilder extends LinkingBuilder<SpringConstraint, SpringBuilder> {
        protected double min;
        protected double max;
        protected double springConstant;
        protected double damping;

        private SpringBuilder() {
        }

        public SpringBuilder min( double min ) {
            this.min = min;
            return this;
        }

        public SpringBuilder max( double max ) {
            this.max = max;
            return this;
        }

        public SpringBuilder range( double min, double max ) {
            this.min = min;
            this.max = max;
            return this;
        }

        public SpringBuilder length( double l ) {
            this.min = l;
            this.max = l;
            return this;
        }

        public SpringBuilder constant( double constant ) {
            springConstant = constant;
            return this;
        }

        public SpringBuilder damping( double damping ) {
            this.damping = damping;
            return this;
        }

        @Override
        public SpringConstraint build() {
            SpringConstraint c = new SpringConstraint();
            c.enabled = enabled;
            c.linkA.set( linkA );
            c.linkB.set( linkB );
            c.bodyA = bodyA;
            c.bodyB = bodyB;
            c.disableCollision = collisionDisabled;
            c.minDist = min;
            c.maxDist = max;
            c.springConstant = springConstant;
            c.damping = damping;
            if( addTo != null ) {
                addTo.add( c );
            }
            return c;
        }
    }
}
