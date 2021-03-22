package net.regulus.geom;

public final class MathUtil {
    /**
     * Threshold for considering something 'zero'. E.g. a double that lies closer to zero than this value may be
     * considered as zero. This value is used to fight rounding errors that cause a double to be non-zero in a condition
     * where they theoretically should.
     */
    public static final double EPSILON = 0.0000001;

    public static final double SQRT_2 = 1.4142135623730950488;
    public static final double HALF_SQRT_2 = SQRT_2 / 2.0;

    private MathUtil() {
        throw new UnsupportedOperationException( "No MathUtil instances for you!" );
    }

    public static double lerp( double a, double b, double u ) {
        return a + ( b - a ) * u;
    }

    public static double unlerp( double a, double b, double u ) {
        return ( u - a ) / ( b - a );
    }

    public static double relerp( double a, double b, double c, double d, double u ) {
        return ( u - a ) / ( b - a ) * ( d - c ) + c;
    }

    public static double clamp( double a, double b, double u ) {
        return u < a ? a : u > b ? b : u;
    }

    public static double tryMakeZero( double d ) {
        if( d > - EPSILON && d < EPSILON )
            return 0;
        return d;
    }

    public static Vec2 tryMakeZero( Vec2 d ) {
        if( d.x > - EPSILON && d.x < EPSILON )
            d.x = 0;
        if( d.y > - EPSILON && d.y < EPSILON )
            d.y = 0;
        return d;
    }

    public static Mat2 tryMakeZero( Mat2 d ) {
        if( d.m00 > - EPSILON && d.m00 < EPSILON )
            d.m00 = 0;
        if( d.m01 > - EPSILON && d.m01 < EPSILON )
            d.m01 = 0;
        if( d.m10 > - EPSILON && d.m10 < EPSILON )
            d.m10 = 0;
        if( d.m11 > - EPSILON && d.m11 < EPSILON )
            d.m11 = 0;
        return d;
    }

    public static boolean equal( double a, double b ) {
        double d = a - b;
        return d > - EPSILON && d < EPSILON;
    }

    public static boolean inequal( double a, double b ) {
        double d = a - b;
        return d <= - EPSILON && d >= EPSILON;
    }

    public static boolean moreEqual( double a, double b ) {
        double d = a - b;
        return d > - EPSILON;
    }

    public static boolean lessEqual( double a, double b ) {
        double d = a - b;
        return d < EPSILON;
    }

    public static boolean more( double a, double b ) {
        double d = a - b;
        return d >= EPSILON;
    }

    public static boolean less( double a, double b ) {
        double d = a - b;
        return d <= - EPSILON;
    }

    public static boolean inRange( double a, double b, double u ) {
        return more( u, a ) && less( u, b );
    }

    public static boolean onRange( double a, double b, double u ) {
        return moreEqual( u, a ) && lessEqual( u, b );
    }

    public static double pythagoreanSolve( double a, double b ) {
        return Math.sqrt( a * a + b * b );
    }
}
