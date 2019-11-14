package net.regulus.geom;

/**
 * Represents a vector or point in 2D space, using cartesian coordinates for this representation. While instances are
 * mainly used to represent vectors (relative coordinates), they are also used to represent points (absolute
 * coordinates).
 */
public class Vec2 implements Cloneable {
    public double x;
    public double y;

    /**
     * Constructs a new vector with it's initial values specified.
     *
     * @param x The initial x-value
     * @param y The initial y-value
     */
    public Vec2( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a new vector with it's initial values specified by a scalar, applying this value to both x and y
     * coordinates.
     *
     * @param a The initial value for x and y coordinates
     */
    public Vec2( double a ) {
        x = a;
        y = a;
    }

    /**
     * Constructs a new vector based on an array. This method handles four cases:
     * <ul>
     * <li>The specified array is {@code null}. In this case, an {@link IllegalArgumentException} is
     * thrown.</li>
     * <li>The specified array contains no elements ({@code arr.length == 0}). In this case, the initial values of this
     * vector are zero.</li>
     * <li>The specified array contains one element ({@code arr.length == 1}). In this case, the initial values of this
     * vector are the value of the first and only element in the array.</li>
     * <li>The specified array contains multiple elements ({@code arr.length > 1}). In this case, the initial values of
     * this vector, for x and y, are the first and second element respectively.</li>
     * </ul>
     *
     * @param arr The array where initial values will be based on
     * @throws IllegalArgumentException Thrown when the specified array is {@code null}.
     */
    public Vec2( double[] arr ) {
        if( arr == null )
            throw new IllegalArgumentException( "Null array" );
        if( arr.length == 0 )
            return; // No init necessary, values are zero by default
        x = arr[ 0 ];
        y = arr[ arr.length == 1 ? 0 : 1 ];
    }

    /**
     * Constructs a new vector, copying it's initial values from another vector. This is basically cloning the specified
     * vector. When the specified vector is {@code null}, an {@link IllegalArgumentException} is thrown.
     *
     * @param vec The vector to copy initial values from
     * @throws IllegalArgumentException Thrown when the specified vector is {@code null}.
     */
    public Vec2( Vec2 vec ) {
        notnull( vec );
        x = vec.x;
        y = vec.y;
    }

    /**
     * Constructs a new vector, using zero as initial values for both coordinates.
     */
    public Vec2() {
    }

    /**
     * Constructs a new vector from polar coordinates. This converts the specified polar coordinates to cartesian
     * coordinates, assuming that when the direction angle is zero, the vector points to the right, and then goes
     * counterclockwise as the direction angle increases (for this, we assume graph coordinates (y = up), not screen
     * coordinates (y = down)).
     *
     * @param mag The magnitude of the resulting vector
     * @param dir The direction of the resulting vector
     * @return A new vector with the specified polar coordinates as initial value, converted to cartesian coordinates.
     */
    public static Vec2 fromPolar( double mag, double dir ) {
        double x = Math.cos( dir ) * mag;
        double y = Math.sin( dir ) * mag;
        return new Vec2( x, y );
    }

    /**
     * Constructs a new vector from polar coordinates, with magnitude 1. This method directly references {@link
     * #fromPolar}.
     *
     * @param dir The direction of the resulting vector
     * @return A new vector with the specified direction, and magnitude 1, converted to cartesian coordinates.
     *
     * @see #fromPolar(double, double)
     */
    public static Vec2 fromNormal( double dir ) {
        return fromPolar( 1, dir );
    }

    /**
     * Sets the value of this vector to the specified cartesian coordinates.
     *
     * @param x The x coordinate of the new value
     * @param y The y coordinate of the new value
     * @return This instance, for convenience...
     */
    public Vec2 set( double x, double y ) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Sets both x and y values of this vector to the same scalar value.
     *
     * @param s The new value for x and y coordinates
     * @return This instance, for convenience...
     */
    public Vec2 set( double s ) {
        x = s;
        y = s;
        return this;
    }

    /**
     * Sets the value of this vector to the value of another vector.
     *
     * @param v The vector to copy values from
     * @return This instance, for convenience...
     */
    public Vec2 set( Vec2 v ) {
        notnull( v );
        x = v.x;
        y = v.y;
        return this;
    }

    public Vec2 setPolar( double mag, double dir ) {
        x = Math.cos( dir ) * mag;
        y = Math.sin( dir ) * mag;
        return this;
    }


    /**
     * Adds x and y values to the x and y value of this vector respectively, putting the resulting value in the
     * specified output vector.
     *
     * @param x   The value to add to the x coordinate
     * @param y   The value to add to the y coordinate
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 add( double x, double y, Vec2 out ) {
        return put( out, this.x + x, this.y + y );
    }

    /**
     * Adds a scalar to the x and y value of this vector, putting the resulting value in the specified output vector.
     *
     * @param s   The value to add to the x and y coordinate
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 add( double s, Vec2 out ) {
        return put( out, this.x + s, this.y + s );
    }

    /**
     * Adds another vector to this vector, putting the resulting value in the specified output vector.
     *
     * @param other The vector to add to this vector.
     * @param out   The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 add( Vec2 other, Vec2 out ) {
        notnull( other );
        return put( out, this.x + other.x, this.y + other.y );
    }


    /**
     * Subtracts x and y values from the x and y value of this vector respectively, putting the resulting value in the
     * specified output vector.
     *
     * @param x   The value to subtract from the x coordinate
     * @param y   The value to subtract from the y coordinate
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 sub( double x, double y, Vec2 out ) {
        return put( out, this.x - x, this.y - y );
    }

    /**
     * Subtracts a scalar from the x and y value of this vector, putting the resulting value in the specified output
     * vector.
     *
     * @param s   The value to subtract from the x and y coordinate
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 sub( double s, Vec2 out ) {
        return put( out, this.x - s, this.y - s );
    }

    /**
     * Subtracts another vector from this vector, putting the resulting value in the specified output vector.
     *
     * @param other The vector to subtract from this vector.
     * @param out   The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 sub( Vec2 other, Vec2 out ) {
        notnull( other );
        return put( out, this.x - other.x, this.y - other.y );
    }


    /**
     * Multiplies the x and y value of this vector with specified x and y value respectively, putting the resulting
     * value in the specified output vector.
     *
     * @param x   The value to multiply the x coordinate with
     * @param y   The value to multiply the y coordinate with
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 mul( double x, double y, Vec2 out ) {
        return put( out, this.x * x, this.y * y );
    }

    /**
     * Multiplies this vector by a scalar, putting the resulting value in the specified output vector.
     *
     * @param s   The value to multiply the x and y coordinate with
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 mul( double s, Vec2 out ) {
        return put( out, this.x * s, this.y * s );
    }

    /**
     * Multiplies this vector by another vector, putting the resulting value in the specified output vector.
     *
     * @param other The vector to multiply this vector by
     * @param out   The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 mul( Vec2 other, Vec2 out ) {
        notnull( other );
        return put( out, this.x * other.x, this.y * other.y );
    }


    /**
     * Divides the x and y value of this vector by specified x and y value respectively, putting the resulting value in
     * the specified output vector.
     *
     * @param x   The value to divide the x coordinate by
     * @param y   The value to divide the y coordinate by
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 div( double x, double y, Vec2 out ) {
        return put( out, this.x / x, this.y / y );
    }

    /**
     * Divides this vector by a scalar, putting the resulting value in the specified output vector.
     *
     * @param s   The value to divide the x and y coordinate by
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 div( double s, Vec2 out ) {
        return put( out, this.x / s, this.y / s );
    }

    /**
     * Divides this vector by another vector, putting the resulting value in the specified output vector.
     *
     * @param other The vector to divide this vector by
     * @param out   The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 div( Vec2 other, Vec2 out ) {
        notnull( other );
        return put( out, this.x / other.x, this.y / other.y );
    }


    /**
     * Returns the dot product of this vector and the vector represented by the specified x and y coordinates.
     *
     * @param x The x coordinate of the other vector
     * @param y The y coordinate of the other vector
     * @return The dot product, which is a scalar.
     */
    public double dot( double x, double y ) {
        return this.x * x + this.y * y;
    }

    /**
     * Returns the dot product of this vector and another vector.
     *
     * @param other The other vector to do dot product with
     * @return The dot product, which is a scalar.
     */
    public double dot( Vec2 other ) {
        notnull( other );
        return this.x * other.x + this.y * other.y;
    }


    /**
     * Returns the cross product of this vector and the vector represented by the specified x and y coordinates.
     *
     * @param x The x coordinate of the other vector
     * @param y The y coordinate of the other vector
     * @return The cross product, which is a scalar.
     */
    public double cross( double x, double y ) {
        return this.x * y - this.y * x;
    }

    /**
     * Returns the cross product of this vector and another vector.
     *
     * @param other The other vector to do cross product with
     * @return The cross product, which is a scalar.
     */
    public double cross( Vec2 other ) {
        notnull( other );
        return this.x * other.y - this.y * other.x;
    }

    /**
     * Negates this vector, putting the resulting value in the specified output vector.
     *
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 neg( Vec2 out ) {
        return put( out, - x, - y );
    }

    /**
     * Computes the counterclockwise perpendicular/orthogonal vector to this vector, putting the resulting value in the
     * specified output vector.
     *
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 perp( Vec2 out ) {
        return put( out, - y, x );
    }

    /**
     * Computes the clockwise perpendicular/orthogonal vector to this vector, putting the resulting value in the
     * specified output vector.
     *
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 invPerp( Vec2 out ) {
        return put( out, y, - x );
    }

    /**
     * Computes the squared magnitude (length) of this vector.
     *
     * @return The computed squared magnitude of this vector.
     */
    public double magSq() {
        return x * x + y * y;
    }

    /**
     * Computes the magnitude (length) of this vector.
     *
     * @return The computed magnitude of this vector.
     */
    public double mag() {
        return Math.sqrt( magSq() );
    }

    /**
     * Computes the direction of this vector. This is the counterclockwise angle offset from a vector pointing towards
     * positive x, defined by {@link Math#atan2}{@code (y, x)}
     *
     * @return The direction of this vector.
     */
    public double dir() {
        return Math.atan2( y, x );
    }

    /**
     * Assuming this vector is a point, computes the squared distance between this point and the point specified by the
     * given coordinates.
     *
     * @param x The x coordinate to compute distance to
     * @param y The y coordinate to compute distance to
     * @return The squared distance from this vector to the specified coordinates
     */
    public double distSq( double x, double y ) {
        double dx = this.x - x;
        double dy = this.y - y;
        return dx * dx + dy * dy;
    }

    /**
     * Assuming all involved vectors are a point, computes the squared distance between this point and the point
     * represented by the specified vector.
     *
     * @param v The vector to compute distance to
     * @return The squared distance from this vector to the other vector
     */
    public double distSq( Vec2 v ) {
        double dx = x - v.x;
        double dy = y - v.y;
        return dx * dx + dy * dy;
    }

    /**
     * Assuming this vector is a point, computes the distance between this point and the point specified by the given
     * coordinates.
     *
     * @param x The x coordinate to compute distance to
     * @param y The y coordinate to compute distance to
     * @return The distance from this vector to the specified coordinates
     */
    public double dist( double x, double y ) {
        return Math.sqrt( distSq( x, y ) );
    }

    /**
     * Assuming all involved vectors are a point, computes the distance between this point and the point represented by
     * the specified vector.
     *
     * @param v The vector to compute distance to
     * @return The distance from this vector to the other vector
     */
    public double dist( Vec2 v ) {
        return Math.sqrt( distSq( v ) );
    }

    /**
     * Returns the angle between this vector and the vector represented by the specified coordinates.
     *
     * @param x The x coordinate of the other vector
     * @param y The y coordinate of the other vector
     * @return The angle, in radians, between this vector and the specified vector
     */
    public double angle( double x, double y ) {
        return Math.acos( dot( x, y ) );
    }

    /**
     * Returns the angle between this vector and the specified vector.
     *
     * @param v The other vector
     * @return The angle, in radians, between this vector and the specified vector
     */
    public double angle( Vec2 v ) {
        notnull( v );
        return Math.acos( dot( v ) / ( mag() * v.mag() ) );
    }

    /**
     * Normalizes this vector, meaning that the resulting vector will be an unit vector with the same direction as this
     * vector, putting the resulting value in the specified output vector.
     *
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public Vec2 norm( Vec2 out ) {
        double mag = mag();
        if( mag == 0 ) {
            return put( out, 1, 0 );
        } else {
            return put( out, x / mag, y / mag );
        }
    }

    /**
     * Returns the cross product of a scalar and a vector, putting the resulting value in the specified output vector.
     * Note that this operation is not commutative, see {@link #cross(Vec2, double, Vec2)} for flipped operation.
     *
     * @param s   The left-hand side of this cross product
     * @param v   The right-hand side of this cross product
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public static Vec2 cross( double s, Vec2 v, Vec2 out ) {
        notnull( v );
        return put( out, - s * v.y, s * v.x );
    }

    /**
     * Returns the cross product of a vector and a scalar, putting the resulting value in the specified output vector.
     * Note that this operation is not commutative, see {@link #cross(double, Vec2, Vec2)} for flipped operation.
     *
     * @param s   The left-hand side of this cross product
     * @param v   The right-hand side of this cross product
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     */
    public static Vec2 cross( Vec2 v, double s, Vec2 out ) {
        notnull( v );
        return put( out, - s * v.y, s * v.x );
    }

    /**
     * Rotates this vector by a specific angle around it's origin. Rotation is counterclockwise when positive y
     * direction is assumed as 'up'. This multiplies the following matrix with this vector:
     * <pre>
     * | sin(a) -cos(a) |
     * | cos(a) sin(a)  |</pre>
     * Herein is {@code a} the specified angle.
     * <p>
     * The lenght of the vector is preserved, meaning that the resulting vector has the same length as this vector. The
     * resulting value is put into the specified output vector.
     *
     * @param angle The angle to rotate by, in radians.
     * @param out   The resulting vector instance. When {@code null}, a new instance is created.
     * @see #rotate(double, double, Vec2)
     */
    public Vec2 rotate( double angle, Vec2 out ) {
        double sin = Math.sin( angle );
        double cos = Math.cos( angle );
        return put( out, cos * x - sin * y, sin * x + cos * y );
    }

    /**
     * Rotates this vector by a specific angle around it's origin. The angle is, for this method, already precomputed
     * into it's sine and cosine. Rotation is counterclockwise when positive y direction is assumed as 'up'. This
     * multiplies the following matrix with this vector:
     * <pre>
     * | sin -cos |
     * | cos sin  |</pre>
     * Herein is {@code sin} the specified sine of the angle, and {@code cos} the specified cosine of the angle.
     * <p>
     * The lenght of the vector is preserved, meaning that the resulting vector has the same length as this vector. The
     * resulting value is put into the specified output vector.
     *
     * @param sin The sine of the angle to rotate by, in radians.
     * @param cos The cosine of the angle to rotate by, in radians.
     * @param out The resulting vector instance. When {@code null}, a new instance is created.
     * @see #rotate(double, Vec2)
     */
    public Vec2 rotate( double sin, double cos, Vec2 out ) {
        return put( out, cos * x - sin * y, sin * x + cos * y );
    }

    @Override
    public Vec2 clone() {
        return new Vec2( this );
    }

    /**
     * Convenience method to put values into a specific vector instance, or a new one if null. This also solves the need
     * of using extra locals when swapping coordinates (e.g. for calculating perpendicular vector).
     */
    public static Vec2 put( Vec2 out, double x, double y ) {
        if( out == null )
            return new Vec2( x, y );
        return out.set( x, y );
    }

    /**
     * Convenience method to put values into a specific vector instance, or a new one if null. This also solves the need
     * of using extra locals when swapping coordinates (e.g. for calculating perpendicular vector).
     */
    public static Vec2 put( Vec2 out, Vec2 other ) {
        if( out == null )
            return new Vec2( other );
        return out.set( other );
    }

    /**
     * Convenience method that throws an {@link IllegalArgumentException} when the specified vector is {@code null}.
     */
    private static void notnull( Vec2 vec ) {
        if( vec == null )
            throw new IllegalArgumentException( "Null vector" );
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    public interface IContext {
        Vec2 get( Vec2 out );
    }

    public static class Context implements IContext {
        private final Vec2 vec;

        public Context( Vec2 vec ) {
            this.vec = vec;
        }

        @Override
        public Vec2 get( Vec2 out ) {
            return Vec2.put( out, vec );
        }
    }
}
