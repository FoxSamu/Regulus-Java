package net.regulus.geom;

import java.util.Objects;

import static java.lang.Math.*;

public class Mat3 {
    public double m00;
    public double m01;
    public double m02;
    public double m10;
    public double m11;
    public double m12;
    public double m20;
    public double m21;
    public double m22;

    public Mat3() {

    }

    public Mat3( double n00, double n01, double n02, double n10, double n11, double n12, double n20, double n21, double n22 ) {
        m00 = n00;
        m01 = n01;
        m02 = n02;
        m10 = n10;
        m11 = n11;
        m12 = n12;
        m20 = n20;
        m21 = n21;
        m22 = n22;
    }

    public Mat3( Mat3 n ) {
        m00 = n.m00;
        m01 = n.m01;
        m02 = n.m02;
        m10 = n.m10;
        m11 = n.m11;
        m12 = n.m12;
        m20 = n.m20;
        m21 = n.m21;
        m22 = n.m22;
    }

    public static Mat3 identity() {
        return new Mat3(
            1, 0, 0,
            0, 1, 0,
            0, 0, 1
        );
    }

    public Mat3 set( double n00, double n01, double n02, double n10, double n11, double n12, double n20, double n21, double n22 ) {
        m00 = n00;
        m01 = n01;
        m02 = n02;
        m10 = n10;
        m11 = n11;
        m12 = n12;
        m20 = n20;
        m21 = n21;
        m22 = n22;
        return this;
    }

    public Mat3 set( Mat3 n ) {
        m00 = n.m00;
        m01 = n.m01;
        m02 = n.m02;
        m10 = n.m10;
        m11 = n.m11;
        m12 = n.m12;
        m20 = n.m20;
        m21 = n.m21;
        m22 = n.m22;
        return this;
    }

    public Mat3 setIdentity() {
        return set(
            1, 0, 0,
            0, 1, 0,
            0, 0, 1
        );
    }

    public Mat3 transpose( Mat3 out ) {
        return put(
            out,
            m00, m10, m20,
            m01, m11, m21,
            m02, m12, m22
        );
    }

    public Mat3 add( Mat3 n, Mat3 out ) {
        Objects.requireNonNull( n );
        return put(
            out,
            m00 + n.m00,
            m01 + n.m01,
            m02 + n.m02,
            m10 + n.m10,
            m11 + n.m11,
            m12 + n.m12,
            m20 + n.m20,
            m21 + n.m21,
            m22 + n.m22
        );
    }

    public Mat3 add( double n, Mat3 out ) {
        Objects.requireNonNull( n );
        return put(
            out,
            m00 + n,
            m01 + n,
            m02 + n,
            m10 + n,
            m11 + n,
            m12 + n,
            m20 + n,
            m21 + n,
            m22 + n
        );
    }

    public Mat3 add( double n00, double n01, double n02, double n10, double n11, double n12, double n20, double n21, double n22, Mat3 out ) {
        return put(
            out,
            m00 + n00,
            m01 + n01,
            m02 + n02,
            m10 + n10,
            m11 + n11,
            m12 + n12,
            m20 + n20,
            m21 + n21,
            m22 + n22
        );
    }

    public Mat3 mul( double n, Mat3 out ) {
        return put(
            out,
            m00 * n,
            m01 * n,
            m02 * n,
            m10 * n,
            m11 * n,
            m12 * n,
            m20 * n,
            m21 * n,
            m22 * n
        );
    }

    public Mat3 mul( Mat3 n, Mat3 out ) {
        Objects.requireNonNull( n );
        return put(
            out,
            m00 * n.m00 + m01 * n.m10 + m02 * n.m20,
            m00 * n.m01 + m01 * n.m11 + m02 * n.m21,
            m00 * n.m02 + m01 * n.m12 + m02 * n.m22,
            m10 * n.m00 + m11 * n.m10 + m12 * n.m20,
            m10 * n.m01 + m11 * n.m11 + m12 * n.m21,
            m10 * n.m02 + m11 * n.m12 + m12 * n.m22,
            m20 * n.m00 + m21 * n.m10 + m22 * n.m20,
            m20 * n.m01 + m21 * n.m11 + m22 * n.m21,
            m20 * n.m02 + m21 * n.m12 + m22 * n.m22
        );
    }

    public Mat3 mul( double n00, double n01, double n02, double n10, double n11, double n12, double n20, double n21, double n22, Mat3 out ) {
        return put(
            out,
            m00 * n00 + m01 * n10 + m02 * n20,
            m00 * n01 + m01 * n11 + m02 * n21,
            m00 * n02 + m01 * n12 + m02 * n22,
            m10 * n00 + m11 * n10 + m12 * n20,
            m10 * n01 + m11 * n11 + m12 * n21,
            m10 * n02 + m11 * n12 + m12 * n22,
            m20 * n00 + m21 * n10 + m22 * n20,
            m20 * n01 + m21 * n11 + m22 * n21,
            m20 * n02 + m21 * n12 + m22 * n22
        );
    }

    // @formatter:off
    public Mat3 shearVertical( double m, Mat3 out ) {
        return mul(
            1, 0, 0,
            m, 1, 0,
            0, 0, 1,
            out
        );
    }

    public Mat3 shearHorizontal( double m, Mat3 out ) {
        return mul(
            1, m, 0,
            0, 1, 0,
            0, 0, 1,
            out
        );
    }

    public Mat3 reflectVertical( Mat3 out ) {
        return mul(
            1,   0, 0,
            0, - 1, 0,
            0,   0, 1,
            out
        );
    }

    public Mat3 reflectHorizontal( Mat3 out ) {
        return mul(
            - 1, 0, 0,
              0, 1, 0,
              0, 0, 1,
            out
        );
    }

    public Mat3 scaleVertical( double m, Mat3 out ) {
        return mul(
            1, 0, 0,
            0, m, 0,
            0, 0, 1,
            out
        );
    }

    public Mat3 scaleHorizontal( double m, Mat3 out ) {
        return mul(
            m, 0, 0,
            0, 1, 0,
            0, 0, 1,
            out
        );
    }

    public Mat3 scale( double m, Mat3 out ) {
        return mul(
            m, 0, 0,
            0, m, 0,
            0, 0, 1,
            out
        );
    }

    public Mat3 scale( double x, double y, Mat3 out ) {
        return mul(
            x, 0, 0,
            0, y, 0,
            0, 0, 1,
            out
        );
    }

    public Mat3 rotateCCW( double a, Mat3 out ) {
        return mul(
            cos( a ), - sin( a ), 0,
            sin( a ),   cos( a ), 0,
                   0,          0, 1,
            out
        );
    }

    public Mat3 rotateCW( double a, Mat3 out ) {
        return mul(
            cos( - a ), - sin( - a ), 0,
            sin( - a ),   cos( - a ), 0,
                     0,            0, 1,
            out
        );
    }

    public Mat3 translate( double x, double y, Mat3 out ) {
        return mul(
            1, 0, x,
            0, 1, y,
            0, 0, 1,
            out
        );
    }

    public Mat3 translate( Vec2 v, Mat3 out ) {
        Objects.requireNonNull( v );
        return mul(
            1, 0, v.x,
            0, 1, v.y,
            0, 0,   1,
            out
        );
    }

    public Mat3 translateHorizontal( double m, Mat3 out ) {
        return mul(
            1, 0, m,
            0, 1, 0,
            0, 0, 1,
            out
        );
    }

    public Mat3 translateVertical( double m, Mat3 out ) {
        return mul(
            1, 0, 0,
            0, 1, m,
            0, 0, 1,
            out
        );
    }
    // @formatter:on

    public Mat2 getLinearTransform( Mat2 out ) {
        return Mat2.put( out, m00, m01, m10, m11 );
    }

    public Vec2 getTranslate( Vec2 out ) {
        return Vec2.put( out, m02, m12 );
    }

    public Vec2 mul( Vec2 v, Vec2 out ) {
        Objects.requireNonNull( v );
        double ox = m00 * v.x + m01 * v.y + m02;
        double oy = m10 * v.x + m11 * v.y + m12;
        return Vec2.put( out, ox, oy );
    }

    public Vec2 mul( double x, double y, Vec2 out ) {
        double ox = m00 * x + m01 * y + m02;
        double oy = m10 * x + m11 * y + m12;
        return Vec2.put( out, ox, oy );
    }

    public static Mat3 put( Mat3 out, double n00, double n01, double n02, double n10, double n11, double n12, double n20, double n21, double n22 ) {
        if( out == null ) {
            out = new Mat3();
        }
        out.m00 = n00;
        out.m01 = n01;
        out.m02 = n02;
        out.m10 = n10;
        out.m11 = n11;
        out.m12 = n12;
        out.m20 = n20;
        out.m21 = n21;
        out.m22 = n22;
        return out;
    }

    public static Mat3 put( Mat3 out, Mat3 n ) {
        if( out == null ) {
            out = new Mat3();
        }
        out.m00 = n.m00;
        out.m01 = n.m01;
        out.m02 = n.m02;
        out.m10 = n.m10;
        out.m11 = n.m11;
        out.m12 = n.m12;
        out.m20 = n.m20;
        out.m21 = n.m21;
        out.m22 = n.m22;
        return out;
    }
}
