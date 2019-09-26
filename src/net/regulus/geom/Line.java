package net.regulus.geom;

import static net.regulus.geom.MathUtil.*;

/**
 * Describes a line.
 */
public class Line {
    public final Vec2 pointA = new Vec2();
    public final Vec2 pointB = new Vec2();

    /**
     * Creates a line that has both points at origin.
     */
    public Line() {

    }

    /**
     * Creates a line with initial points.
     *
     * @param a The first point of the line.
     * @param b The second point of the line.
     */
    public Line( Vec2 a, Vec2 b ) {
        pointA.set( a );
        pointB.set( b );
    }

    /**
     * Creates a line by copying from andother line instance.
     *
     * @param copy The line instance to copy.
     */
    public Line( Line copy ) {
        pointA.set( copy.pointA );
        pointB.set( copy.pointB );
    }

    /**
     * Interpolates between the points of this line, putting the resulting value in the specified output vector.
     *
     * @param u   The interpolation factor.
     * @param out The output vector. When {@code null}, a new vector instance is created and returned.
     * @see MathUtil#lerp(double, double, double)
     */
    public Vec2 interpolate( double u, Vec2 out ) {
        return Vec2.put( out, lerp( pointA.x, pointB.x, u ), lerp( pointA.y, pointB.y, u ) );
    }

    /**
     * Computes the edge vector of this line, putting the resulting value in the specified output vector.
     *
     * @param out The output vector. When {@code null}, a new vector instance is created and returned.
     */
    public Vec2 edge( Vec2 out ) {
        return pointB.sub( pointA, out );
    }

    public Vec2 invEdge( Vec2 out ) {
        return pointA.sub( pointB, out );
    }

    /**
     * Sets the value of this line to the value of another line.
     * @param line The line to copy value of.
     */
    public void set( Line line ) {
        pointA.set( line.pointA );
        pointB.set( line.pointB );
    }


    public void set( Vec2 ptA, Vec2 ptB ) {
        pointA.set( ptA );
        pointB.set( ptB );
    }


    public void set( double x1, double y1, double x2, double y2 ) {
        pointA.set( x1, y1 );
        pointB.set( x2, y2 );
    }
}
