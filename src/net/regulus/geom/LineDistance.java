package net.regulus.geom;

/**
 * Represents the result of computing the shortest distance between a point and a line segment.
 */
public class LineDistance {
    public static final int LINE = 0;
    public static final int POINT_A = 1;
    public static final int POINT_B = 2;

    public final Line line = new Line();
    public final Vec2 point = new Vec2();
    public double distance;
    public double u;
    private final Projection proj = new Projection();
    public final Vec2 normal = new Vec2();
    public final Vec2 relative = new Vec2();
    public final Vec2 edge = proj.baseVector; // These are the same
    public final Vec2 closest = new Vec2();
    public int type;

    public LineDistance() {
    }

    public LineDistance( Vec2 point, Line line ) {
        this.line.set( line );
        this.point.set( point );
        compute();
    }

    public LineDistance( LineDistance other ) {
        line.set( other.line );
        point.set( other.point );
        distance = other.distance;
        u = other.u;
        normal.set( other.normal );
        relative.set( other.relative );
        edge.set( other.edge );
        closest.set( other.closest );
        type = other.type;
    }

    public void copy( LineDistance other ) {
        line.set( other.line );
        point.set( other.point );
        distance = other.distance;
        u = other.u;
        normal.set( other.normal );
        relative.set( other.relative );
        edge.set( other.edge );
        closest.set( other.closest );
        type = other.type;
    }

    public void compute() {
        line.edge( edge );
        proj.baseVector.set( edge );
        point.sub( line.pointA, proj.projected );
        proj.project();

        u = proj.tangentLength / proj.baseVector.mag();
        if( u < 0 ) {
            u = 0;
            closest.set( line.pointA );
            type = POINT_A;
        } else if( u > 1 ) {
            u = 1;
            closest.set( line.pointB );
            type = POINT_B;
        } else {
            // closest = edge * u + line.pointA
            edge.mul( u, closest ).add( line.pointA, closest );
            type = LINE;
        }

        point.sub( closest, relative );
        distance = relative.mag();
        if( MathUtil.equal( distance, 0 ) ) {
            edge.perp( normal ).norm( normal );
        } else {
            relative.norm( normal );
        }
    }
}
