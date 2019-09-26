package net.regulus.geom;

import static java.lang.Math.*;

/**
 * Represents a 2 by 2 matrix. 2x2 matrices are used for linear transformations to shapes and vectors. The fields of
 * this matrix are meant to represent the matrix like this:
 * <pre>
 * | m00 m01 |
 * | m10 m11 |</pre>
 */
public class Mat2 {
    /**
     * The top left value of this matrix:
     * <pre>
     * | * _ |
     * | _ _ | </pre>
     */
    public double m00;

    /**
     * The top right value of this matrix:
     * <pre>
     * | _ * |
     * | _ _ | </pre>
     */
    public double m01;

    /**
     * The bottom left value of this matrix:
     * <pre>
     * | _ _ |
     * | * _ | </pre>
     */
    public double m10;

    /**
     * The bottom right value of this matrix:
     * <pre>
     * | _ _ |
     * | _ * | </pre>
     */
    public double m11;

    /**
     * Creates a matrix with each component initially set to zero.
     */
    public Mat2() {

    }

    /**
     * Creates a matrix with specified initial components. The parameter layout looks like this:
     * <pre>
     * | m00 m01 |
     * | m10 m11 |</pre>
     *
     * @param m00 The top left value
     * @param m01 The top right value
     * @param m10 The bottom left value
     * @param m11 The bottom right value
     */
    public Mat2( double m00, double m01, double m10, double m11 ) {
        this.m00 = m00;
        this.m01 = m01;
        this.m10 = m10;
        this.m11 = m11;
    }

    /**
     * Copies another matrix into a new matrix instance.
     *
     * @param other The matrix to copy.
     * @throws NullPointerException Thrown when the specified matrix is {@code null}.
     */
    public Mat2( Mat2 other ) {
        notnull( other );
        m00 = other.m00;
        m01 = other.m01;
        m10 = other.m10;
        m11 = other.m11;
    }

    /**
     * Creates an identity matrix. The identity matrix is defined as:
     * <pre>
     * | 1 0 |
     * | 0 1 |</pre>
     *
     * @return The created matrix.
     */
    public static Mat2 identity() {
        return new Mat2( 1, 0, 0, 1 );
    }

    public static Mat2 shearHorizontal( double m ) {
        return new Mat2( 1, m, 0, 1 );
    }

    public static Mat2 shearVertical( double m ) {
        return new Mat2( 1, 0, m, 1 );
    }

    public static Mat2 scaleHorizontal( double m ) {
        return new Mat2( m, 0, 0, 1 );
    }

    public static Mat2 scaleVertical( double m ) {
        return new Mat2( 1, 0, 0, m );
    }

    public static Mat2 scale( double m ) {
        return new Mat2( m, 0, 0, m );
    }

    public static Mat2 scale( double x, double y ) {
        return new Mat2( x, 0, 0, y );
    }

    public static Mat2 reflectHorizontal() {
        return new Mat2( -1, 0, 0, 1 );
    }

    public static Mat2 reflectVertical() {
        return new Mat2( 1, 0, 0, -1 );
    }

    public static Mat2 rotateCCW( double m ) {
        return new Mat2( cos( m ), -sin( m ), sin( m ), cos( m ) );
    }

    public static Mat2 rotateCW( double m ) {
        return new Mat2( cos( - m ), -sin( - m ), sin( - m ), cos( - m ) );
    }

    public static Mat2 rotate( double m ) {
        return rotateCCW( m );
    }

    /**
     * Sets the values of this matrix. The parameter layout looks like this:
     * <pre>
     * | m00 m01 |
     * | m10 m11 |</pre>
     *
     * @param m00 The top left value
     * @param m01 The top right value
     * @param m10 The bottom left value
     * @param m11 The bottom right value
     * @return This instance for convenience.
     */
    public Mat2 set( double m00, double m01, double m10, double m11 ) {
        this.m00 = m00;
        this.m01 = m01;
        this.m10 = m10;
        this.m11 = m11;
        return this;
    }

    /**
     * Copies another matrix into this matrix instance.
     *
     * @param other The matrix to copy from.
     * @return This instance for convenience.
     */
    public Mat2 set( Mat2 other ) {
        notnull( other );
        m00 = other.m00;
        m01 = other.m01;
        m10 = other.m10;
        m11 = other.m11;
        return this;
    }

    /**
     * Sets this matrix to the idenity matrix, which is defined as:
     * <pre>
     * | 1 0 |
     * | 0 1 |</pre>
     *
     * @return This instance for convenience.
     */
    public Mat2 setIdentity() {
        return set( 1, 0, 0, 1 );
    }

    /**
     * Adds two matrices, putting the result in the specified output matrix. This operation goes component-wise:
     * <pre>
     * | m00+n00 m01+n01 |
     * | m10+n10 m11+n11 |</pre>
     *
     * @param m   The matrix to add.
     * @param out The output matrix. When {@code null}, a new instance is created.
     */
    public Mat2 add( Mat2 m, Mat2 out ) {
        notnull( m );
        return put( out, m00 + m.m00, m01 + m.m01, m10 + m.m10, m11 + m.m11 );
    }

    /**
     * Transposes this matrix, putting the result in the specified output matrix. This operation switches columns and
     * rows:
     * <pre>
     * | m00 m01 |
     * | m10 m11 |</pre>
     * becomes:
     * <pre>
     * | m00 m10 |
     * | m01 m11 |<pre>
     *
     * @param out The output matrix. When {@code null}, a new instance is created.
     */
    public Mat2 transpose( Mat2 out ) {
        return put( out, m00, m10, m01, m11 );
    }

    /**
     * Multiplies this matrix with a scalar, putting the result in the specified output matrix. Unlike multiplying two
     * matrices, this operation is commutative and goes component-wise:
     * <pre>
     * | m00*s m01*s |
     * | m10*s m11*s |</pre>
     *
     * @param s   The scalar to multiply with.
     * @param out The output matrix. When {@code null}, a new instance is created.
     */
    public Mat2 mul( double s, Mat2 out ) {
        return put( out, m00 * s, m01 * s, m10 * s, m11 * s );
    }

    /**
     * Multiplies this matrix with another matrix, putting the result in the specified output matrix. Unlike multiplying
     * a matrix with a scalar, this operation is <b>not</b> commutative. The multiplication is given by the dot product
     * of the rows of this matrix and the columns of the other matrix.
     *
     * @param m   The matrix to multiply with.
     * @param out The output matrix. When {@code null}, a new instance is created.
     */
    public Mat2 mul( Mat2 m, Mat2 out ) {
        notnull( m );
        return put(
            out,
            m00 * m.m00 + m01 * m.m10, m00 * m.m01 + m01 * m.m11,
            m10 * m.m00 + m11 * m.m10, m10 * m.m01 + m11 * m.m11
        );
    }

    /**
     * Multiplies this matrix with another matrix which is given by the specified matrix values, putting the result in
     * the specified output matrix. Unlike multiplying a matrix with a scalar, this operation is <b>not</b> commutative.
     * The multiplication is given by the dot product of the rows of this matrix and the columns of the other matrix.
     *
     * @param n00 The top left value of the matrix to multiply with.
     * @param n01 The top right value of the matrix to multiply with.
     * @param n10 The bottom left value of the matrix to multiply with.
     * @param n11 The bottom right value of the matrix to multiply with.
     * @param out The output matrix. When {@code null}, a new instance is created.
     */
    public Mat2 mul( double n00, double n01, double n10, double n11, Mat2 out ) {
        return put(
            out,
            m00 * n00 + m01 * n10, m00 * n01 + m01 * n11,
            m10 * n00 + m11 * n10, m10 * n01 + m11 * n11
        );
    }

    /**
     * Multiplies another matrix, which is given by the specified matrix values, with this matrix, putting the result in
     * the specified output matrix. Unlike multiplying a matrix with a scalar, this operation is <b>not</b> commutative.
     * The multiplication is given by the dot product of the rows of this matrix and the columns of the other matrix.
     *
     * @param n00 The top left value of the matrix to multiply with.
     * @param n01 The top right value of the matrix to multiply with.
     * @param n10 The bottom left value of the matrix to multiply with.
     * @param n11 The bottom right value of the matrix to multiply with.
     * @param out The output matrix. When {@code null}, a new instance is created.
     */
    public Mat2 mulInv( double n00, double n01, double n10, double n11, Mat2 out ) {
        return put(
            out,
            n00 * m00 + n01 * m10, n00 * m01 + n01 * m11,
            n10 * m00 + n11 * m10, n10 * m01 + n11 * m11
        );
    }


    /**
     * Multiplies this matrix with a vector, putting the result in the specified output vector. This applies all the
     * transformations done in this matrix to the specified vector. Unlike multiplying a matrix with a scalar, this
     * operation is <b>not</b> commutative.
     *
     * @param v   The vector to multiply with.
     * @param out The output matrix. When {@code null}, a new instance is created.
     */
    public Vec2 mul( Vec2 v, Vec2 out ) {
        notnull( v );
        return Vec2.put( out, m00 * v.x + m01 * v.y, m10 * v.x + m11 * v.y );
    }

    /**
     * Applies a vertical shear to this matrix, putting the result in the specified output matrix. This is done by
     * multiplying the matrix with:
     * <pre>
     * | 1 0 |
     * | m 1 |</pre>
     * Here, {@code m} is the shear factor.
     *
     * @param m   The shear factor
     * @param out The output matrix. When {@code null}, a new matrix instance is created.
     */
    public Mat2 shearVertical( double m, Mat2 out ) {
        return mul( 1, 0, m, 1, out );
    }

    /**
     * Applies a horizontal shear to this matrix, putting the result in the specified output matrix. This is done by
     * multiplying the matrix with:
     * <pre>
     * | 1 m |
     * | 0 1 |</pre>
     * Here, {@code m} is the shear factor.
     *
     * @param m   The shear factor
     * @param out The output matrix. When {@code null}, a new matrix instance is created.
     */
    public Mat2 shearHorizontal( double m, Mat2 out ) {
        return mul( 1, m, 0, 1, out );
    }

    /**
     * Applies a horizontal reflection to this matrix, putting the result in the specified output matrix. This is done
     * by multiplying the matrix with:
     * <pre>
     * | -1 0 |
     * |  0 1 |</pre>
     *
     * @param out The output matrix. When {@code null}, a new matrix instance is created.
     */
    public Mat2 reflectHorizontal( Mat2 out ) {
        return mul( - 1, 0, 0, 1, out );
    }

    /**
     * Applies a vertical reflection to this matrix, putting the result in the specified output matrix. This is done by
     * multiplying the matrix with:
     * <pre>
     * | 1  0 |
     * | 0 -1 |</pre>
     *
     * @param out The output matrix. When {@code null}, a new matrix instance is created.
     */
    public Mat2 reflectVertical( Mat2 out ) {
        return mul( 1, 0, 0, - 1, out );
    }

    /**
     * Applies a vertical scale to this matrix, putting the result in the specified output matrix. This is done by
     * multiplying the matrix with:
     * <pre>
     * | 1 0 |
     * | 0 m |</pre>
     * Here, {@code m} is the scale factor.
     *
     * @param m   The scale factor
     * @param out The output matrix. When {@code null}, a new matrix instance is created.
     */
    public Mat2 scaleVertical( double m, Mat2 out ) {
        return mul( 1, 0, 0, m, out );
    }

    /**
     * Applies a horizontal scale to this matrix, putting the result in the specified output matrix. This is done by
     * multiplying the matrix with:
     * <pre>
     * | m 0 |
     * | 0 1 |</pre>
     * Here, {@code m} is the scale factor.
     *
     * @param m   The scale factor
     * @param out The output matrix. When {@code null}, a new matrix instance is created.
     */
    public Mat2 scaleHorizontal( double m, Mat2 out ) {
        return mul( m, 0, 0, 1, out );
    }

    /**
     * Applies a horizontal and vertical scale to this matrix with separate scale factors, putting the result in the
     * specified output matrix. This is done by multiplying the matrix with:
     * <pre>
     * | x 0 |
     * | 0 y |</pre>
     * Here, {@code x} is the horizontal scale factor, and {@code y} the vertical scale factor.
     *
     * @param x   The horizontal scale factor
     * @param y   The vertical scale factor
     * @param out The output matrix. When {@code null}, a new matrix instance is created.
     */
    public Mat2 scale( double x, double y, Mat2 out ) {
        return mul( x, 0, 0, y, out );
    }

    /**
     * Applies a horizontal and vertical scale to this matrix with combined scale factor, putting the result in the
     * specified output matrix. This is done by multiplying the matrix with:
     * <pre>
     * | m 0 |
     * | 0 m |</pre>
     * Here, {@code m} is the horizontal and vertical scale factor.
     *
     * @param m   The scale factor
     * @param out The output matrix. When {@code null}, a new matrix instance is created.
     */
    public Mat2 scale( double m, Mat2 out ) {
        return mul( m, 0, 0, m, out );
    }

    /**
     * Applies a counterclockwise rotation to this matrix, putting the result in the specified output matrix. This is
     * done by multiplying the matrix with:
     * <pre>
     * | cos(m) -sin(m) |
     * | sin(m)  cos(m) |</pre>
     * Here, {@code m} angle to rotate by in radians.
     *
     * This is an alias for {@link #rotate(double, Mat2)}.
     *
     * @param m   The rotation angle
     * @param out The output matrix. When {@code null}, a new matrix instance is created.
     * @see #rotate(double, Mat2)
     */
    public Mat2 rotateCCW( double m, Mat2 out ) {
        return mul( cos( m ), - sin( m ), sin( m ), cos( m ), out );
    }

    /**
     * Applies a clockwise rotation to this matrix, putting the result in the specified output matrix. This is done by
     * multiplying the matrix with:
     * <pre>
     * | cos(-m) -sin(-m) |
     * | sin(-m)  cos(-m) |</pre>
     * Here, {@code m} angle to rotate by in radians.
     *
     * @param m   The rotation angle
     * @param out The output matrix. When {@code null}, a new matrix instance is created.
     */
    public Mat2 rotateCW( double m, Mat2 out ) {
        return mul( cos( - m ), - sin( - m ), sin( - m ), cos( - m ), out );
    }

    /**
     * Applies a counterclockwise rotation to this matrix, putting the result in the specified output matrix. This is
     * done by multiplying the matrix with:
     * <pre>
     * | cos(m) -sin(m) |
     * | sin(m)  cos(m) |</pre>
     * Here, {@code m} angle to rotate by in radians.
     *
     * This is an alias for {@link #rotateCCW(double, Mat2)}.
     *
     * @param m   The rotation angle
     * @param out The output matrix. When {@code null}, a new matrix instance is created.
     * @see #rotateCCW(double, Mat2)
     */
    public Mat2 rotate( double m, Mat2 out ) {
        return rotateCCW( m, out );
    }

    /**
     * Computes the determinant of this matrix. For the matrix
     * <pre>
     * | a b |
     * | c d |</pre>
     * the determinant is defined as {@code ad - bc}.
     *
     * @return The computed determinant.
     */
    public double det() {
        return m00 * m11 - m01 * m10;
    }

    /**
     * Takes the two values of the upper row and puts them in the specified output vector, so that:
     * <pre>[x y] = [m00 m01]</pre>
     *
     * @param out The output vector. When {@code null}, a new vector instance is created.
     */
    public Vec2 row0( Vec2 out ) {
        return Vec2.put( out, m00, m01 );
    }

    /**
     * Takes the two values of the lower row and puts them in the specified output vector, so that:
     * <pre>[x y] = [m10 m11]</pre>
     *
     * @param out The output vector. When {@code null}, a new vector instance is created.
     */
    public Vec2 row1( Vec2 out ) {
        return Vec2.put( out, m10, m11 );
    }

    /**
     * Takes the two values of the left column and puts them in the specified output vector, so that:
     * <pre>[x y] = [m00 m10]</pre>
     *
     * @param out The output vector. When {@code null}, a new vector instance is created.
     */
    public Vec2 col0( Vec2 out ) {
        return Vec2.put( out, m00, m10 );
    }

    /**
     * Takes the two values of the right column and puts them in the specified output vector, so that:
     * <pre>[x y] = [m01 m11]</pre>
     *
     * @param out The output vector. When {@code null}, a new vector instance is created.
     */
    public Vec2 col1( Vec2 out ) {
        return Vec2.put( out, m01, m11 );
    }

    /**
     * Takes the top left and the bottom right value and puts them in the specified output vector, so that:
     * <pre>[x y] = [m00 m11]</pre>
     *
     * @param out The output vector. When {@code null}, a new vector instance is created.
     */
    public Vec2 diagonal0( Vec2 out ) {
        return Vec2.put( out, m00, m11 );
    }

    /**
     * Takes the top right and the bottom left value and puts them in the specified output vector, so that:
     * <pre>[x y] = [m01 m10]</pre>
     *
     * @param out The output vector. When {@code null}, a new vector instance is created.
     */
    public Vec2 diagonal1( Vec2 out ) {
        return Vec2.put( out, m01, m10 );
    }

    /**
     * Convenience method to put specific matrix values into an output matrix. A new output matrix is created when the
     * specified output matrix is {@code null}.
     *
     * @return The not-null output matrix.
     */
    public static Mat2 put( Mat2 out, double m00, double m01, double m10, double m11 ) {
        if( out == null )
            out = new Mat2();
        out.m00 = m00;
        out.m01 = m01;
        out.m10 = m10;
        out.m11 = m11;
        return out;
    }

    private static void notnull( Mat2 m ) {
        if( m == null )
            throw new NullPointerException( "Null matrix" );
    }

    private static void notnull( Vec2 m ) {
        if( m == null )
            throw new NullPointerException( "Null vector" );
    }

    public String toString() {
        return "[" + m00 + " " + m01 + " -- " + m10 + " " + m11 + "]";
    }
}
