package net.regulus.geom;

/**
 * Represents the result of projecting two vectors. The following values are stored in this class about a projection:
 * <ul>
 * <li>The length of the projection, which is the scalar the tangent is multiplied with to get the projection.</li>
 * <li>The distance between projection and projected vector, which is the scalar the normal is multiplied with to get
 * the relative vector.</li>
 * <li>The tangent, which is the unit vector of both the base vector and the resulting projection.</li>
 * <li>The normal, which is a unit vector perpendicular to the tangent (counterclockwise).</li>
 * <li>The projection, which is the resulting vector of the projection operation.</li>
 * <li>The relative, the vector from the projection to the projected vector.</li>
 * <li>The projected vector, the vector that is being projected.</li>
 * <li>The base vector, the vector where the projected vector is being projected on.</li>
 * </ul>
 */
public class Projection {
    public double tangentLength;
    public double normalLength;
    public final Vec2 tangent = new Vec2();
    public final Vec2 normal = new Vec2();
    public final Vec2 projection = new Vec2();
    public final Vec2 relative = new Vec2();
    public final Vec2 projected = new Vec2();
    public final Vec2 baseVector = new Vec2();

    /**
     * Creates an empty projection.
     */
    public Projection() {

    }

    /**
     * Copies another projection into a new instance.
     * @param other The projection to copy.
     */
    public Projection( Projection other ) {
        tangentLength = other.tangentLength;
        normalLength = other.normalLength;
        tangent.set( other.tangent );
        normal.set( other.normal );
        relative.set( other.relative );
        projection.set( other.projection );
        projected.set( other.projected );
        baseVector.set( other.baseVector );
    }

    /**
     * Creates and computes a projection from specified projected and base vector.
     * @param proj The projected vector
     * @param base The base vector
     */
    public Projection( Vec2 proj, Vec2 base ) {
        baseVector.set( base );
        projected.set( proj );
        project();
    }

    /**
     * Recomputes the projection for the current base and projection vector ({@link #baseVector} and {@link
     * #projected}).
     */
    public void project() {
        baseVector.norm( tangent );
        tangent.perp( normal );
        tangentLength = projected.dot( tangent );
        tangent.mul( tangentLength, projection );
        normalLength = - projected.cross( tangent );
        normal.mul( normalLength, relative );
    }
}
