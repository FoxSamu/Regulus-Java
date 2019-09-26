package net.regulus.geom;

/**
 * Represents the result of intersecting two lines, rays or segments.
 */
public class LineIntersection {
    public final Line lineA = new Line();
    public final Line lineB = new Line();
    public double uA;
    public double uB;
    public final Vec2 intersection = new Vec2();

    public LineIntersection() {

    }

    public LineIntersection( Vec2 l1a, Vec2 l1b, Vec2 l2a, Vec2 l2b ) {
        lineA.pointA.set( l1a );
        lineA.pointB.set( l1b );
        lineB.pointA.set( l2a );
        lineB.pointB.set( l2b );
        intersect();
    }

    public LineIntersection( Line lineA, Line lineB ) {
        this.lineA.set( lineA );
        this.lineB.set( lineB );
        intersect();
    }

    public LineIntersection( LineIntersection other ) {
        lineA.set( other.lineA );
        lineB.set( other.lineB );
        uA = other.uA;
        uB = other.uB;
        intersection.set( other.intersection );
    }


    public void intersect() {
        double x1 = lineA.pointA.x;
        double x2 = lineA.pointB.x;
        double x3 = lineB.pointA.x;
        double x4 = lineB.pointB.x;
        double y1 = lineA.pointA.y;
        double y2 = lineA.pointB.y;
        double y3 = lineB.pointA.y;
        double y4 = lineB.pointB.y;

        // The denominator, the cross product of the two line vectors
        double denom = ( y4 - y3 ) * ( x2 - x1 ) - ( x4 - x3 ) * ( y2 - y1 );

        // Line formula: p0 + u * ( p1 - p0 )
        // These values are the 'u' value for each line respectively
        uA = ( ( x4 - x3 ) * ( y1 - y3 ) - ( y4 - y3 ) * ( x1 - x3 ) ) / denom;
        uB = ( ( x2 - x1 ) * ( y1 - y3 ) - ( y2 - y1 ) * ( x1 - x3 ) ) / denom;

        // Calculate intersection point and additional values
        intersection.set( x1 + uA * ( x2 - x1 ), y1 + uA * ( y2 - y1 ) );
    }

    public boolean segA() {
        return uA >= 0 && uA <= 1;
    }

    public boolean segB() {
        return uB >= 0 && uB <= 1;
    }
    public boolean inSegA() {
        return uA > 0 && uA < 1;
    }

    public boolean inSegB() {
        return uB > 0 && uB < 1;
    }

    public LineIntersection set( Vec2 a1, Vec2 a2, Vec2 b1, Vec2 b2 ) {
        lineA.set( a1, a2 );
        lineB.set( b1, b2 );
        return this;
    }

    public LineIntersection set( Line l1, Line l2 ) {
        lineA.set( l1 );
        lineB.set( l2 );
        return this;
    }

    public LineIntersection set( LineIntersection isc ) {
        lineA.set( isc.lineA );
        lineB.set( isc.lineB );
        uA = isc.uA;
        uB = isc.uB;
        intersection.set( isc.intersection );
        return this;
    }
}
