package net.regulus.detection.sat;

import net.regulus.detection.ICollisionCollector;
import net.regulus.collider.*;
import net.regulus.geom.AABB;

public final class SAT {
    private SAT() {
    }

    public static void collide( ICollider a, ICollider b, ICollisionCollector collector ) {
        AABB boxA = new AABB(), boxB = new AABB();
        a.getBox( boxA );
        b.getBox( boxB );
        if( ! AABB.overlap( boxA, boxB ) ) {
            return;
        }

        if( a instanceof CompoundCollider && b instanceof CompoundCollider ) {
            for( SimpleCollider c1 : ( (CompoundCollider) a ).colliders ) {
                for( SimpleCollider c2 : ( (CompoundCollider) b ).colliders ) {
                    collide( c1, c2, collector );
                }
            }
        } else if( a instanceof CompoundCollider && b instanceof SimpleCollider ) {
            for( SimpleCollider c1 : ( (CompoundCollider) a ).colliders ) {
                collide( c1, b, collector );
            }
        } else if( a instanceof SimpleCollider && b instanceof CompoundCollider ) {
            for( SimpleCollider c2 : ( (CompoundCollider) b ).colliders ) {
                collide( a, c2, collector );
            }
        }  else if( a instanceof PolygonalCollider ) {
            if( b instanceof PolygonalCollider ) {
                PolygonPolygonSAT.collide( (PolygonalCollider) a, (PolygonalCollider) b, collector );
            } else if( b instanceof CircleCollider ) {
                CirclePolygonSAT.collide( (PolygonalCollider) a, (CircleCollider) b, collector );
            } else if( b instanceof PlaneCollider ) {
                PolygonPlaneSAT.collide( (PolygonalCollider) a, (PlaneCollider) b, collector );
            } else if( b instanceof BarCollider ) {
                PolygonBarSAT.collide( (PolygonalCollider) a, (BarCollider) b, collector );
            } else if( b instanceof CapsuleCollider ) {
                CapsulePolygonSAT.collide( (PolygonalCollider) a, (CapsuleCollider) b, collector );
            }
        } else if( a instanceof CircleCollider ) {
            if( b instanceof PolygonalCollider ) {
                CirclePolygonSAT.collide( (CircleCollider) a, (PolygonalCollider) b, collector );
            } else if( b instanceof CircleCollider ) {
                CircleCircleSAT.collide( (CircleCollider) a, (CircleCollider) b, collector );
            } else if( b instanceof PlaneCollider ) {
                CirclePlaneSAT.collide( (CircleCollider) a, (PlaneCollider) b, collector );
            } else if( b instanceof BarCollider ) {
                CircleBarSAT.collide( (CircleCollider) a, (BarCollider) b, collector );
            } else if( b instanceof CapsuleCollider ) {
                CircleCapsuleSAT.collide( (CircleCollider) a, (CapsuleCollider) b, collector );
            }
        } else if( a instanceof CapsuleCollider ) {
            if( b instanceof PolygonalCollider ) {
                CapsulePolygonSAT.collide( (CapsuleCollider) a, (PolygonalCollider) b, collector );
            } else if( b instanceof CircleCollider ) {
                CircleCapsuleSAT.collide( (CapsuleCollider) a, (CircleCollider) b, collector );
            } else if( b instanceof PlaneCollider ) {
                CapsulePlaneSAT.collide( (CapsuleCollider) a, (PlaneCollider) b, collector );
            } else if( b instanceof BarCollider ) {
                CapsuleBarSAT.collide( (CapsuleCollider) a, (BarCollider) b, collector );
            } else if( b instanceof CapsuleCollider ) {
                CapsuleCapsuleSAT.collide( (CapsuleCollider) a, (CapsuleCollider) b, collector );
            }
        } else if( a instanceof PlaneCollider ) {
            if( b instanceof PolygonalCollider ) {
                PolygonPlaneSAT.collide( (PlaneCollider) a, (PolygonalCollider) b, collector );
            } else if( b instanceof CircleCollider ) {
                CirclePlaneSAT.collide( (PlaneCollider) a, (CircleCollider) b, collector );
            } else if( b instanceof CapsuleCollider ) {
                CapsulePlaneSAT.collide( (PlaneCollider) a, (CapsuleCollider) b, collector );
            }
        } else if( a instanceof BarCollider ) {
            if( b instanceof PolygonalCollider ) {
                PolygonBarSAT.collide( (BarCollider) a, (PolygonalCollider) b, collector );
            } else if( b instanceof CircleCollider ) {
                CircleBarSAT.collide( (BarCollider) a, (CircleCollider) b, collector );
            } else if( b instanceof CapsuleCollider ) {
                CapsuleBarSAT.collide( (BarCollider) a, (CapsuleCollider) b, collector );
            }
        }
    }
}
