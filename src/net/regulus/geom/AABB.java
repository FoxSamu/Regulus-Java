package net.regulus.geom;

import static java.lang.Math.*;

/**
 * Represents an axis-aligned bounding box, or abbreviated: an AABB. The implementation uses a lower and an upper limit,
 * which are represented by vectors, to represent the bounding box.
 */
public class AABB {
    /** The lower limit of the AABB */
    public final Vec2 min = new Vec2();
    /** The upper limit of the AABB */
    public final Vec2 max = new Vec2();

    /**
     * Constructs an AABB at origin.
     */
    public AABB() {

    }

    /**
     * Constructs an AABB with specified lower and upper limits.
     *
     * @param min The lower limit
     * @param max The upper limit
     */
    public AABB( Vec2 min, Vec2 max ) {
        this.min.set( min );
        this.max.set( max );
    }

    /**
     * Constructs an AABB with specified lower and upper limits.
     *
     * @param minx The lower limit x-coordinate
     * @param miny The lower limit y-coordinate
     * @param maxx The upper limit x-coordinate
     * @param maxy The upper limit y-coordinate
     */
    public AABB( double minx, double miny, double maxx, double maxy ) {
        min.set( minx, miny );
        max.set( maxx, maxy );
    }

    /**
     * Constructs an AABB by copying from another
     *
     * @param other The AABB to copy from
     */
    public AABB( AABB other ) {
        min.set( other.min );
        max.set( other.max );
    }

    /**
     * Sets the AABB to specified lower and upper limits.
     *
     * @param min The lower limit
     * @param max The upper limit
     */
    public AABB set( Vec2 min, Vec2 max ) {
        this.min.set( min );
        this.max.set( max );
        return this;
    }

    /**
     * Sets the AABB to specified lower and upper limits.
     *
     * @param minx The lower limit x-coordinate
     * @param miny The lower limit y-coordinate
     * @param maxx The upper limit x-coordinate
     * @param maxy The upper limit y-coordinate
     */
    public AABB set( double minx, double miny, double maxx, double maxy ) {
        min.set( minx, miny );
        max.set( maxx, maxy );
        return this;
    }

    /**
     * Copies values from another AABB into this instance.
     *
     * @param other The AABB to copy from
     */
    public AABB set( AABB other ) {
        min.set( other.min );
        max.set( other.max );
        return this;
    }

    /**
     * Sets the AABB to specified center and dimensions
     */
    public AABB setCenterSize( Vec2 center, double width, double height ) {
        center.sub( width / 2, height / 2, min );
        center.add( width / 2, height / 2, max );
        return this;
    }

    /**
     * Constructs an AABB from specified center and dimensions
     */
    public static AABB fromCenterSize( Vec2 center, double width, double height ) {
        AABB out = new AABB();
        center.sub( width / 2, height / 2, out.min );
        center.add( width / 2, height / 2, out.max );
        return out;
    }

    /**
     * Extends this AABB, putting the result in the specified output AABB.
     *
     * @param x   The amount of extension along the x-axis
     * @param y   The amount of extension along the y-axis
     * @param out The output AABB. When {@code null}, a new AABB is created.
     */
    public AABB grow( double x, double y, AABB out ) {
        return put( out, min.x - x, min.y - y, max.x + x, max.y + y );
    }

    /**
     * Extends this AABB, putting the result in the specified output AABB.
     *
     * @param m   The amount of extension along both axes
     * @param out The output AABB. When {@code null}, a new AABB is created.
     */
    public AABB grow( double m, AABB out ) {
        return grow( m, m, out );
    }

    /**
     * Compresses this AABB, putting the result in the specified output AABB.
     *
     * @param x   The amount of compression along the x-axis
     * @param y   The amount of compression along the y-axis
     * @param out The output AABB. When {@code null}, a new AABB is created.
     */
    public AABB shrink( double x, double y, AABB out ) {
        return grow( - x, - y, out );
    }

    /**
     * Compresses this AABB, putting the result in the specified output AABB.
     *
     * @param m   The amount of compression along both axes
     * @param out The output AABB. When {@code null}, a new AABB is created.
     */
    public AABB shrink( double m, AABB out ) {
        return grow( - m, out );
    }

    /**
     * Generifies this AABB, making sure the area of this box is positive, putting the result in the specified output
     * AABB.
     *
     * @param out The output AABB. When {@code null}, a new AABB is created.
     */
    public AABB abs( AABB out ) {
        return put( out, min( min.x, max.x ), min( min.y, max.y ), max( min.x, max.x ), max( min.y, max.y ) );
    }

    /**
     * Finds the AABB that wraps the specified AABBs, putting the result in the specified output AABB.
     * @param out The output AABB. When {@code null}, a new AABB is created.
     */
    public static AABB unite( AABB a, AABB b, AABB out ) {
        notnull( a );
        notnull( b );
        return put( out, min( a.min.x, b.min.x ), min( a.min.y, b.min.y ), max( a.max.x, b.max.x ), max( a.max.y, b.max.y ) );
    }

    /**
     * Checks whether the specified AABBs have overlap.
     * @return True when an overlapping exists.
     */
    public static boolean overlap( AABB a, AABB b ) {
        notnull( a );
        notnull( b );
        return a.min.x <= b.max.x && a.max.x >= b.min.x && a.min.y <= b.max.y && a.max.y >= b.min.y;
    }

    /**
     * Utility function to put a specific value into an output AABB, creating a new one when necessary.
     */
    public static AABB put( AABB out, double minx, double miny, double maxx, double maxy ) {
        if( out == null )
            out = new AABB();
        out.min.set( minx, miny );
        out.max.set( maxx, maxy );
        return out;
    }

    /**
     * Utility function to put a specific value into an output AABB, creating a new one when necessary.
     */
    public static AABB put( AABB out, AABB aabb ) {
        if( out == null )
            out = new AABB();
        out.set( aabb );
        return out;
    }

    private static void notnull( AABB b ) {
        if( b == null )
            throw new NullPointerException( "Null AABB" );
    }
}
