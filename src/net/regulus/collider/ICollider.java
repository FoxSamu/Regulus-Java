package net.regulus.collider;

import java.util.ArrayList;
import java.util.List;

import net.regulus.geom.AABB;
import net.regulus.geom.Mat3;
import net.regulus.geom.Vec2;
import net.regulus.geom.VectorList;

public interface ICollider {
    boolean isCompound();
    AABB getBox( AABB out );
    void setBodyMatrix( Mat3 matrix );

    double computeMass( double density );
    double computeInertia( double density );
    double computeDensityFromInertia( double inertia );
    double computeDensityFromMass( double mass );

    static CircleBuilder circle() {
        return new CircleBuilder();
    }

    static CapsuleBuilder capsule() {
        return new CapsuleBuilder();
    }

    static BoxBuilder box() {
        return new BoxBuilder();
    }

    static RegularPolyBuilder regularPoly() {
        return new RegularPolyBuilder();
    }

    static PolygonBuilder polygon() {
        return new PolygonBuilder();
    }

    static BarBuilder bar() {
        return new BarBuilder();
    }

    static PlaneBuilder plane() {
        return new PlaneBuilder();
    }

    static CompoundBuilder compound() {
        return new CompoundBuilder();
    }

    abstract class SimpleBuilder<Coll extends SimpleCollider, This extends SimpleBuilder<Coll, This>> {
        protected final Vec2 offset = new Vec2();
        protected double rotationOffset;

        protected SimpleBuilder() {

        }

        @SuppressWarnings( "unchecked" )
        public This offset( double x, double y ) {
            offset.set( x, y );
            return (This) this;
        }

        @SuppressWarnings( "unchecked" )
        public This offset( Vec2 pos ) {
            offset.set( pos );
            return (This) this;
        }

        @SuppressWarnings( "unchecked" )
        public This rotationOff( double off ) {
            rotationOffset = off;
            return (This) this;
        }

        public abstract Coll build();
    }

    class CircleBuilder extends SimpleBuilder<CircleCollider, CircleBuilder> {
        private double radius;

        private CircleBuilder() {
        }

        public CircleBuilder radius( double rad ) {
            radius = rad;
            return this;
        }

        @Override
        public CircleCollider build() {
            CircleCollider coll = new CircleCollider();
            coll.offset.set( offset );
            coll.rotationOffset = rotationOffset;
            coll.radius = radius;
            coll.recomputeMatrix();
            coll.recompute();
            return coll;
        }
    }

    class CapsuleBuilder extends SimpleBuilder<CapsuleCollider, CapsuleBuilder> {
        private double radius;
        private double length;

        private CapsuleBuilder() {
        }

        public CapsuleBuilder radius( double rad ) {
            radius = rad;
            return this;
        }

        public CapsuleBuilder length( double rad ) {
            length = rad;
            return this;
        }

        @Override
        public CapsuleCollider build() {
            CapsuleCollider coll = new CapsuleCollider();
            coll.offset.set( offset );
            coll.rotationOffset = rotationOffset;
            coll.radius = radius;
            coll.length = length;
            coll.recomputeMatrix();
            coll.recompute();
            return coll;
        }
    }

    class BoxBuilder extends SimpleBuilder<BoxCollider, BoxBuilder> {
        private double width;
        private double height;

        private BoxBuilder() {
        }

        public BoxBuilder width( double value ) {
            width = value;
            return this;
        }

        public BoxBuilder height( double value ) {
            height = value;
            return this;
        }

        public BoxBuilder size( double w, double h ) {
            width = w;
            height = h;
            return this;
        }

        @Override
        public BoxCollider build() {
            BoxCollider coll = new BoxCollider();
            coll.offset.set( offset );
            coll.rotationOffset = rotationOffset;
            coll.width = width;
            coll.height = height;
            coll.recomputeMatrix();
            coll.recomputeVertices();
            coll.recompute();
            return coll;
        }
    }

    class RegularPolyBuilder extends SimpleBuilder<RegularPolyCollider, RegularPolyBuilder> {
        private double radius;
        private int sides;

        private RegularPolyBuilder() {
        }

        public RegularPolyBuilder radius( double rad ) {
            radius = rad;
            return this;
        }

        public RegularPolyBuilder sides( int sides ) {
            this.sides = sides;
            return this;
        }

        @Override
        public RegularPolyCollider build() {
            RegularPolyCollider coll = new RegularPolyCollider();
            coll.offset.set( offset );
            coll.rotationOffset = rotationOffset;
            coll.radius = radius;
            coll.sides = sides;
            coll.recomputeMatrix();
            coll.recomputeVertices();
            coll.recompute();
            return coll;
        }
    }

    class PolygonBuilder extends SimpleBuilder<PolygonCollider, PolygonBuilder> {
        private final VectorList vertices = new VectorList();

        private PolygonBuilder() {
        }

        public PolygonBuilder add( double x, double y ) {
            vertices.add( x, y );
            return this;
        }

        public PolygonBuilder add( Vec2 v ) {
            vertices.add( v );
            return this;
        }

        public PolygonBuilder add( double... values ) {
            int s = values.length;
            for( int i = 0; i < s; i += 2 ) {
                vertices.add( values[ i ], values[ i + 1 ] );
            }
            return this;
        }

        public PolygonBuilder add( Vec2... values ) {
            for( Vec2 value : values ) {
                vertices.add( value );
            }
            return this;
        }

        @Override
        public PolygonCollider build() {
            PolygonCollider coll = new PolygonCollider();
            coll.offset.set( offset );
            coll.rotationOffset = rotationOffset;
            Vec2 v = new Vec2();
            for( Vec2.IContext ctx : vertices ) {
                ctx.get( v );
                coll.unorderedVertices.add( v );
            }
            coll.recomputeMatrix();
            coll.recomputeVertices();
            coll.recompute();
            return coll;
        }
    }

    class BarBuilder extends SimpleBuilder<BarCollider, BarBuilder> {
        private double width;

        private BarBuilder() {
        }

        public BarBuilder width( double w ) {
            width = w;
            return this;
        }

        @Override
        public BarCollider build() {
            BarCollider coll = new BarCollider();
            coll.offset.set( offset );
            coll.rotationOffset = rotationOffset;
            coll.width = width;
            coll.recomputeMatrix();
            coll.recompute();
            return coll;
        }
    }

    class PlaneBuilder extends SimpleBuilder<PlaneCollider, PlaneBuilder> {

        private PlaneBuilder() {
        }

        @Override
        public PlaneCollider build() {
            PlaneCollider coll = new PlaneCollider();
            coll.offset.set( offset );
            coll.rotationOffset = rotationOffset;
            coll.recomputeMatrix();
            coll.recompute();
            return coll;
        }
    }

    class CompoundBuilder {

        private final List<SimpleCollider> colliders = new ArrayList<>();

        private CompoundBuilder() {
        }

        public CompoundBuilder add( SimpleBuilder<?, ?> builder ) {
            colliders.add( builder.build() );
            return this;
        }

        public CompoundBuilder add( SimpleCollider collider ) {
            colliders.add( collider );
            return this;
        }

        public CompoundCollider build() {
            CompoundCollider coll = new CompoundCollider();
            coll.colliders.addAll( colliders );
            return coll;
        }
    }
}
