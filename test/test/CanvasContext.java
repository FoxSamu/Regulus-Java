package test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;

import net.regulus.collider.*;
import net.regulus.geom.*;
import net.regulus.simulation.particle.Particle;
import net.regulus.simulation.particle.ParticleSystem;

import static net.regulus.geom.MathUtil.*;

public class CanvasContext {
    private final Vec2 origin = new Vec2();
    private Graphics2D g;
    private int w;
    private int h;

    private final Path2D p = new Path2D.Double();
    private final Arc2D a = new Arc2D.Double();

    private final Vec2 translate = new Vec2();
    private double scale = 50;

    private Color lineColor = TestColors.STATIC;
    private Color fillColor = TestColors.STATIC;
    private Color backgroundColor = TestColors.BACKGROUND;
    private BasicStroke stroke = new BasicStroke( 2 );
    private boolean hasLine = true;
    private boolean hasFill = true;

    public CanvasContext() {
    }

    public void setup( Graphics2D g, int w, int h ) {
        this.g = g;
        this.w = w;
        this.h = h;
    }

    public CanvasContext lineColor( Color color ) {
        lineColor = color;
        return this;
    }

    public CanvasContext fillColor( Color color ) {
        fillColor = color;
        return this;
    }

    public CanvasContext color( Color color ) {
        fillColor = color;
        lineColor = color;
        return this;
    }

    public CanvasContext color( Color color, double fillAlpha ) {
        fillColor = TestColors.withAlpha( color, fillAlpha );
        lineColor = color;
        return this;
    }

    public CanvasContext backgroundColor( Color color ) {
        backgroundColor = color;
        return this;
    }

    public CanvasContext lineWidth( float width ) {
        stroke = new BasicStroke( width );
        return this;
    }

    public CanvasContext outlines( boolean l ) {
        hasLine = l;
        return this;
    }

    public CanvasContext fills( boolean l ) {
        hasFill = l;
        return this;
    }

    public void translate( double x, double y ) {
        translate.set( x, y );
    }

    public void scale( double x ) {
        scale = x;
    }


    private void fill( Shape s ) {
        g.setColor( fillColor );
        g.fill( s );
    }

    private void stroke( Shape s ) {
        g.setColor( lineColor );
        g.setStroke( stroke );
        g.draw( s );
    }

    private void draw() {
        if( hasFill )
            fill( p );
        if( hasLine )
            stroke( p );
    }

    public void clear() {
        g.setBackground( backgroundColor );
        g.clearRect( 0, 0, w, h );
    }

    public Vec2 canvasToWorld( Vec2 canvas, Vec2 out ) {
        return canvas.sub( w / 2D, h / 2D, out )
                     .sub( translate.x, translate.y, out )
                     .div( scale, out )
                     .mul( 1, - 1, out );
    }

    public Vec2 worldToCanvas( Vec2 world, Vec2 out ) {
        return world.mul( 1, - 1, out )
                    .mul( scale, out )
                    .add( translate.x, translate.y, out )
                    .add( w / 2D, h / 2D, out );
    }

    private static final int CIRCLE_SEGMENTS = 120;

    public void drawCircle( double x, double y, double r, double radius, boolean angleIndicator ) {
        p.reset();
        Vec2 tmp = new Vec2();
        boolean moved = false;
        for( int i = 0; i < CIRCLE_SEGMENTS; i++ ) {
            double a = r + (double) i / CIRCLE_SEGMENTS * 2 * Math.PI;
            tmp.setPolar( radius, a ).add( x, y, tmp );
            worldToCanvas( tmp, tmp );
            if( moved ) {
                p.lineTo( tmp.x, tmp.y );
            } else {
                p.moveTo( tmp.x, tmp.y );
                moved = true;
            }
        }
        p.closePath();
        if( angleIndicator ) {
            tmp.set( x, y );
            worldToCanvas( tmp, tmp );
            p.moveTo( tmp.x, tmp.y );

            tmp.setPolar( radius, r ).add( x, y, tmp );
            worldToCanvas( tmp, tmp );
            p.lineTo( tmp.x, tmp.y );
        }
        draw();
    }

    public void drawCapsule( double x, double y, double r, double radius, double length ) {
        p.reset();
        Vec2 tmp = new Vec2();
        Vec2 off = new Vec2( length / 2, 0 );
        off.rotate( r, off );
        boolean moved = false;
        for( int i = - CIRCLE_SEGMENTS / 4; i <= CIRCLE_SEGMENTS / 4; i++ ) {
            double a = r + (double) i / CIRCLE_SEGMENTS * 2 * Math.PI;
            tmp.setPolar( radius, a ).add( x, y, tmp ).add( off, tmp );
            worldToCanvas( tmp, tmp );
            if( moved ) {
                p.lineTo( tmp.x, tmp.y );
            } else {
                p.moveTo( tmp.x, tmp.y );
                moved = true;
            }
        }
        off.neg( off );
        for( int i = CIRCLE_SEGMENTS / 4; i <= 3 * CIRCLE_SEGMENTS / 4; i++ ) {
            double a = r + (double) i / CIRCLE_SEGMENTS * 2 * Math.PI;
            tmp.setPolar( radius, a ).add( x, y, tmp ).add( off, tmp );
            worldToCanvas( tmp, tmp );
            p.lineTo( tmp.x, tmp.y );
        }
        p.closePath();
        draw();
    }

    public void drawBox( double x, double y, double r, double w, double h ) {
        p.reset();

        Vec2 tmp = new Vec2();
        tmp.set( - w / 2, - h / 2 ).rotate( r, tmp ).add( x, y, tmp );
        worldToCanvas( tmp, tmp );
        p.moveTo( tmp.x, tmp.y );

        tmp.set( w / 2, - h / 2 ).rotate( r, tmp ).add( x, y, tmp );
        worldToCanvas( tmp, tmp );
        p.lineTo( tmp.x, tmp.y );

        tmp.set( w / 2, h / 2 ).rotate( r, tmp ).add( x, y, tmp );
        worldToCanvas( tmp, tmp );
        p.lineTo( tmp.x, tmp.y );

        tmp.set( - w / 2, h / 2 ).rotate( r, tmp ).add( x, y, tmp );
        worldToCanvas( tmp, tmp );
        p.lineTo( tmp.x, tmp.y );

        p.closePath();
        draw();
    }

    public void drawRegularPolygon( double x, double y, double r, double radius, int sides ) {
        p.reset();
        Vec2 tmp = new Vec2();
        boolean moved = false;
        for( int i = 0; i < sides; i++ ) {
            double a = r + (double) i / sides * 2 * Math.PI;
            tmp.setPolar( radius, a ).add( x, y, tmp );
            worldToCanvas( tmp, tmp );
            if( moved ) {
                p.lineTo( tmp.x, tmp.y );
            } else {
                p.moveTo( tmp.x, tmp.y );
                moved = true;
            }
        }
        p.closePath();
        draw();
    }

    public void drawPoint( double x, double y, double rad ) {
        Vec2 tmp = new Vec2();
        tmp.set( x, y );
        worldToCanvas( tmp, tmp );
        a.setArcByCenter( tmp.x, tmp.y, rad, 0, 360, Arc2D.CHORD );
        fill( a );
    }

    public void drawPoint( Vec2 pt, double rad ) {
        drawPoint( pt.x, pt.y, rad );
    }

    public void drawSquarePoint( double x, double y, double rad ) {
        Vec2 tmp = new Vec2();
        tmp.set( x, y );
        worldToCanvas( tmp, tmp );
        p.reset();
        p.moveTo( tmp.x - rad, tmp.y - rad );
        p.lineTo( tmp.x + rad, tmp.y - rad );
        p.lineTo( tmp.x + rad, tmp.y + rad );
        p.lineTo( tmp.x - rad, tmp.y + rad );
        fill( p );
    }

    public void drawPSquareoint( Vec2 pt, double rad ) {
        drawSquarePoint( pt.x, pt.y, rad );
    }

    public void drawDiamondPoint( double x, double y, double rad ) {
        Vec2 tmp = new Vec2();
        tmp.set( x, y );
        worldToCanvas( tmp, tmp );
        p.reset();
        p.moveTo( tmp.x - rad, tmp.y );
        p.lineTo( tmp.x, tmp.y - rad );
        p.lineTo( tmp.x + rad, tmp.y );
        p.lineTo( tmp.x, tmp.y + rad );
        fill( p );
    }

    public void drawDiamondPoint( Vec2 pt, double rad ) {
        drawDiamondPoint( pt.x, pt.y, rad );
    }

    public void drawXPoint( double x, double y, double rad, double lw ) {
        Vec2 tmp = new Vec2();
        rad *= HALF_SQRT_2;
        tmp.set( x, y );
        worldToCanvas( tmp, tmp );
        p.reset();
        p.moveTo( tmp.x - rad, tmp.y - rad );
        p.lineTo( tmp.x + rad, tmp.y + rad );
        p.moveTo( tmp.x - rad, tmp.y + rad );
        p.lineTo( tmp.x + rad, tmp.y - rad );
        g.setColor( fillColor );
        g.setStroke( new BasicStroke( (float) lw ) );
        g.draw( p );
    }

    public void drawXPoint( Vec2 pt, double rad, double lw ) {
        drawXPoint( pt.x, pt.y, rad, lw );
    }

    public void drawCrossPoint( double x, double y, double rad, double lw ) {
        Vec2 tmp = new Vec2();
        tmp.set( x, y );
        worldToCanvas( tmp, tmp );
        p.reset();
        p.moveTo( tmp.x - rad, tmp.y );
        p.lineTo( tmp.x + rad, tmp.y );
        p.moveTo( tmp.x, tmp.y + rad );
        p.lineTo( tmp.x, tmp.y - rad );
        g.setColor( fillColor );
        g.setStroke( new BasicStroke( (float) lw ) );
        g.draw( p );
    }

    public void drawCrossPoint( Vec2 pt, double rad, double lw ) {
        drawCrossPoint( pt.x, pt.y, rad, lw );
    }

    public void drawCircPoint( double x, double y, double rad, double lw ) {
        Vec2 tmp = new Vec2();
        rad -= lw / 2;
        tmp.set( x, y );
        worldToCanvas( tmp, tmp );
        a.setArcByCenter( tmp.x, tmp.y, rad, 0, 360, Arc2D.CHORD );
        g.setColor( fillColor );
        g.setStroke( new BasicStroke( (float) lw ) );
        g.draw( a );
    }

    public void drawCircPoint( Vec2 pt, double rad, double lw ) {
        drawCircPoint( pt.x, pt.y, rad, lw );
    }

    public void drawPointHighlight( double x, double y, double rad ) {
        Vec2 tmp = new Vec2();
        tmp.set( x, y );
        worldToCanvas( tmp, tmp );
        a.setArcByCenter( tmp.x, tmp.y, rad, 0, 360, Arc2D.CHORD );
        g.setColor( new Color( fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), (int) ( fillColor.getAlpha() * 0.3 ) ) );
        g.fill( a );
    }

    public void drawPointHighlight( Vec2 pt, double rad ) {
        drawPointHighlight( pt.x, pt.y, rad );
    }

    public void drawLine( Vec2 pt1, Vec2 pt2 ) {
        Vec2 tmp = new Vec2();
        p.reset();
        tmp.set( pt1 );
        worldToCanvas( tmp, tmp );
        p.moveTo( tmp.x, tmp.y );
        tmp.set( pt2 );
        worldToCanvas( tmp, tmp );
        p.lineTo( tmp.x, tmp.y );
        stroke( p );
    }

    public void drawArrow( Vec2 pt1, Vec2 pt2 ) {
        Vec2 p1 = new Vec2();
        Vec2 p2 = new Vec2();
        p.reset();
        p1.set( pt1 );
        worldToCanvas( p1, p1 );
        p.moveTo( p1.x, p1.y );
        p2.set( pt2 );
        worldToCanvas( p2, p2 );
        p.lineTo( p2.x, p2.y );

        Vec2 arr = new Vec2();
        // Both sine and cosine of 45 degrees are half the square root of 2, so no sine and cosine calculation here...
        p1.sub( p2, arr )
          .norm( arr )
          .mul( 2.5 * stroke.getLineWidth(), arr )
          .rotate( HALF_SQRT_2, HALF_SQRT_2, arr );
        p.moveTo( arr.x + p2.x, arr.y + p2.y );
        p.lineTo( p2.x, p2.y );
        arr.invPerp( arr );
        p.lineTo( arr.x + p2.x, arr.y + p2.y );

        stroke( p );
    }

    public void drawVector( Vec2 v ) {
        drawArrow( origin, v );
    }

    public void drawVector( Vec2 v, Vec2 origin ) {
        Vec2 tmp = new Vec2();
        drawArrow( origin, v.add( origin, tmp ) );
    }

    public void drawNormal( Vec2 v, double len ) {
        Vec2 tmp = new Vec2();
        drawLine( origin, v.norm( tmp ).mul( len / scale, tmp ) );
    }

    public void drawNormal( Vec2 v, Vec2 origin, double len ) {
        Vec2 tmp = new Vec2();
        drawLine( origin, v.norm( tmp ).mul( len / scale, tmp ).add( origin, tmp ) );
    }

    public void drawLine( Line line ) {
        drawLine( line.pointA, line.pointB );
    }

    public void drawRay( Vec2 pt1, Vec2 pt2 ) {
        double outset = stroke.getLineWidth() * 2;
        AABB screenBox = new AABB( - outset, - outset, w + outset, h + outset );

        Vec2 p1 = worldToCanvas( pt1, null );
        Vec2 p2 = worldToCanvas( pt2, null );

        Vec2[] pts = new Vec2[ 2 ];
        int found = 0;

        LineIntersection isc = new LineIntersection();
        isc.lineA.pointA.set( p1 );
        isc.lineA.pointB.set( p2 );

        isc.lineB.set( screenBox.min.x, screenBox.min.y, screenBox.max.x, screenBox.min.y );
        isc.intersect();
        if( isc.uA >= 0 && isc.inSegB() ) {
            pts[ found ] = isc.intersection.clone();
            found++;
        }

        isc.lineB.set( screenBox.min.x, screenBox.max.y, screenBox.max.x, screenBox.max.y );
        isc.intersect();
        if( isc.uA >= 0 && isc.inSegB() ) {
            pts[ found ] = isc.intersection.clone();
            found++;
        }

        isc.lineB.set( screenBox.max.x, screenBox.min.y, screenBox.max.x, screenBox.max.y );
        isc.intersect();
        if( isc.uA >= 0 && isc.segB() ) {
            pts[ found ] = isc.intersection.clone();
            found++;
        }

        isc.lineB.set( screenBox.min.x, screenBox.min.y, screenBox.min.x, screenBox.max.y );
        isc.intersect();
        if( isc.uA >= 0 && isc.segB() ) {
            pts[ found ] = isc.intersection.clone();
            found++;
        }

        if( found == 1 ) {
            p.reset();
            p.moveTo( p1.x, p1.y );
            p.lineTo( pts[ 0 ].x, pts[ 0 ].y );
            stroke( p );
        } else if( found == 2 ) {
            p.reset();
            p.moveTo( pts[ 0 ].x, pts[ 0 ].y );
            p.lineTo( pts[ 1 ].x, pts[ 1 ].y );
            stroke( p );
        }
    }

    public void drawInfLine( Vec2 pt1, Vec2 pt2 ) {
        double outset = stroke.getLineWidth() * 2;
        AABB screenBox = new AABB( - outset, - outset, w + outset, h + outset );

        Vec2 p1 = worldToCanvas( pt1, null );
        Vec2 p2 = worldToCanvas( pt2, null );

        Vec2[] pts = new Vec2[ 2 ];
        int found = 0;

        LineIntersection isc = new LineIntersection();
        isc.lineA.pointA.set( p1 );
        isc.lineA.pointB.set( p2 );

        isc.lineB.set( screenBox.min.x, screenBox.min.y, screenBox.max.x, screenBox.min.y );
        isc.intersect();
        if( isc.inSegB() ) {
            pts[ found ] = isc.intersection.clone();
            found++;
        }

        isc.lineB.set( screenBox.min.x, screenBox.max.y, screenBox.max.x, screenBox.max.y );
        isc.intersect();
        if( isc.inSegB() ) {
            pts[ found ] = isc.intersection.clone();
            found++;
        }

        isc.lineB.set( screenBox.max.x, screenBox.min.y, screenBox.max.x, screenBox.max.y );
        isc.intersect();
        if( isc.segB() ) {
            pts[ found ] = isc.intersection.clone();
            found++;
        }

        isc.lineB.set( screenBox.min.x, screenBox.min.y, screenBox.min.x, screenBox.max.y );
        isc.intersect();
        if( isc.segB() ) {
            pts[ found ] = isc.intersection.clone();
            found++;
        }

        if( found == 2 ) {
            p.reset();
            p.moveTo( pts[ 0 ].x, pts[ 0 ].y );
            p.lineTo( pts[ 1 ].x, pts[ 1 ].y );
            stroke( p );
        }
    }

    private boolean goTo( Vec2 v, boolean moved ) {
        if( moved ) {
            p.lineTo( v.x, v.y );
        } else {
            p.moveTo( v.x, v.y );
        }
        return true;
    }

    public void drawInfPlane( PlaneCollider plane ) {
        double outset = stroke.getLineWidth() * 2;
        AABB screenBox = new AABB( - outset, - outset, w + outset, h + outset );
        Vec2 topLeft = screenBox.min;
        Vec2 bottomRight = screenBox.max;
        canvasToWorld( topLeft, topLeft );
        canvasToWorld( bottomRight, bottomRight );
        Vec2 topRight = new Vec2( bottomRight.x, topLeft.y );
        Vec2 bottomLeft = new Vec2( topLeft.x, bottomRight.y );

        Vec2 normal = plane.getNormal( null );
        Vec2 tangent = normal.perp( null );

        Vec2 p1 = plane.getCenter( null );
        Vec2 p2 = p1.add( tangent, null );

        Vec2 cv = new Vec2();

        LineIntersection isc = new LineIntersection();
        isc.lineA.set( p1, p2 );

        p.reset();

        boolean moved = false;

        if( plane.containsPoint( topLeft ) ) {
            worldToCanvas( topLeft, cv );
            moved = goTo( cv, false );
        }

        isc.lineB.set( topLeft, topRight );
        isc.intersect();
        if( isc.segB() ) {
            worldToCanvas( isc.intersection, cv );
            moved = goTo( cv, moved );
        }

        if( plane.containsPoint( topRight ) ) {
            worldToCanvas( topRight, cv );
            moved = goTo( cv, moved );
        }

        isc.lineB.set( topRight, bottomRight );
        isc.intersect();
        if( isc.segB() ) {
            worldToCanvas( isc.intersection, cv );
            moved = goTo( cv, moved );
        }

        if( plane.containsPoint( bottomRight ) ) {
            worldToCanvas( bottomRight, cv );
            moved = goTo( cv, moved );
        }

        isc.lineB.set( bottomRight, bottomLeft );
        isc.intersect();
        if( isc.segB() ) {
            worldToCanvas( isc.intersection, cv );
            moved = goTo( cv, moved );
        }

        if( plane.containsPoint( bottomLeft ) ) {
            worldToCanvas( bottomLeft, cv );
            moved = goTo( cv, moved );
        }

        isc.lineB.set( bottomLeft, topLeft );
        isc.intersect();
        if( isc.segB() ) {
            worldToCanvas( isc.intersection, cv );
            moved = goTo( cv, moved );
        }

        if( moved ) {
            p.closePath();
        }

        draw();
    }

    private boolean infBarStep( BarCollider collider, Vec2 c1, Vec2 c2, LineIntersection neg, LineIntersection pos, Vec2 cv, boolean moved ) {
        neg.lineB.set( c1, c2 );
        pos.lineB.set( c1, c2 );
        neg.intersect();
        pos.intersect();

        if( collider.containsPoint( c1 ) ) {
            worldToCanvas( c1, cv );
            moved = goTo( cv, moved );
        }

        if( neg.segB() && ! pos.segB() ) {
            worldToCanvas( neg.intersection, cv );
            moved = goTo( cv, moved );
        }

        if( ! neg.segB() && pos.segB() ) {
            worldToCanvas( pos.intersection, cv );
            moved = goTo( cv, moved );
        }

        if( neg.segB() && pos.segB() ) {
            if( neg.uB < pos.uB ) {
                worldToCanvas( neg.intersection, cv );
                moved = goTo( cv, moved );
                worldToCanvas( pos.intersection, cv );
                moved = goTo( cv, moved );
            } else {
                worldToCanvas( pos.intersection, cv );
                moved = goTo( cv, moved );
                worldToCanvas( neg.intersection, cv );
                moved = goTo( cv, moved );
            }
        }

        return moved;
    }

    public void drawInfBar( BarCollider bar ) {
        double outset = stroke.getLineWidth() * 2;
        AABB screenBox = new AABB( - outset, - outset, w + outset, h + outset );
        Vec2 topLeft = screenBox.min;
        Vec2 bottomRight = screenBox.max;
        canvasToWorld( topLeft, topLeft );
        canvasToWorld( bottomRight, bottomRight );
        Vec2 topRight = new Vec2( bottomRight.x, topLeft.y );
        Vec2 bottomLeft = new Vec2( topLeft.x, bottomRight.y );

        Vec2 normal = bar.getNormal( null );
        Vec2 tangent = normal.perp( null );

        double radius = bar.getWidth() / 2;

        Vec2 n = normal.mul( radius, null );

        Vec2 pos1 = bar.getCenter( null );
        pos1.add( n, pos1 );
        Vec2 neg1 = bar.getCenter( null );
        neg1.sub( n, neg1 );
        Vec2 pos2 = pos1.add( tangent, null );
        Vec2 neg2 = neg1.add( tangent, null );

        LineIntersection posIsc = new LineIntersection();
        LineIntersection negIsc = new LineIntersection();
        posIsc.lineA.set( pos1, pos2 );
        negIsc.lineA.set( neg1, neg2 );

        Vec2 cv = new Vec2();
        boolean moved;

        p.reset();
        moved = infBarStep( bar, topLeft, topRight, negIsc, posIsc, cv, false );
        moved = infBarStep( bar, topRight, bottomRight, negIsc, posIsc, cv, moved );
        moved = infBarStep( bar, bottomRight, bottomLeft, negIsc, posIsc, cv, moved );
        moved = infBarStep( bar, bottomLeft, topLeft, negIsc, posIsc, cv, moved );

        if( moved ) {
            p.closePath();
        }

        draw();
    }

    public void drawCircle( CircleCollider circle, boolean angle ) {
        Vec2 center = circle.getCenter( null );
        drawCircle( center.x, center.y, circle.getGlobalRotation(), circle.getRadius(), angle );
    }

    public void drawCircle( CircleCollider circle ) {
        drawCircle( circle, true );
    }

    public void drawCapsule( CapsuleCollider capsule ) {
        Vec2 center = capsule.getCenter( null );
        drawCapsule( center.x, center.y, capsule.getGlobalRotation(), capsule.getRadius(), capsule.getLength() );
    }

    public void drawBox( BoxCollider box ) {
        Vec2 center = box.getCenter( null );
        drawBox( center.x, center.y, box.getGlobalRotation(), box.getWidth(), box.getHeight() );
    }

    public void drawRegularPoly( RegularPolyCollider regular ) {
        Vec2 center = regular.getCenter( null );
        drawRegularPolygon( center.x, center.y, regular.getGlobalRotation(), regular.getRadius(), regular.getSides() );
    }

    public void drawPolygon( PolygonalCollider polygon ) {
        VectorCollection global = polygon.getGlobal();
        Vec2 cv = new Vec2();

        p.reset();

        boolean moved = false;
        for( Vec2.IContext ctx : global ) {
            ctx.get( cv );
            worldToCanvas( cv, cv );
            moved = goTo( cv, moved );
        }

        if( moved ) {
            p.closePath();
        }

        draw();
    }

    public void drawCollider( ICollider collider ) {
        if( collider instanceof CompoundCollider ) {
            for( SimpleCollider c : ( (CompoundCollider) collider ).colliders ) {
                drawCollider( c );
            }
        } else if( collider instanceof CircleCollider ) {
            drawCircle( (CircleCollider) collider );
        } else if( collider instanceof CapsuleCollider ) {
            drawCapsule( (CapsuleCollider) collider );
        } else if( collider instanceof BoxCollider ) {
            drawBox( (BoxCollider) collider );
        } else if( collider instanceof RegularPolyCollider ) {
            drawRegularPoly( (RegularPolyCollider) collider );
        } else if( collider instanceof PlaneCollider ) {
            drawInfPlane( (PlaneCollider) collider );
        } else if( collider instanceof BarCollider ) {
            drawInfBar( (BarCollider) collider );
        } else if( collider instanceof PolygonalCollider ) {
            drawPolygon( (PolygonalCollider) collider );
        }
    }

    public void drawBox( AABB box ) {
        Vec2 tmp = new Vec2();
        p.reset();
        tmp.set( box.min.x, box.min.y );
        worldToCanvas( tmp, tmp );
        p.moveTo( tmp.x, tmp.y );

        tmp.set( box.max.x, box.min.y );
        worldToCanvas( tmp, tmp );
        p.lineTo( tmp.x, tmp.y );

        tmp.set( box.max.x, box.max.y );
        worldToCanvas( tmp, tmp );
        p.lineTo( tmp.x, tmp.y );

        tmp.set( box.min.x, box.max.y );
        worldToCanvas( tmp, tmp );
        p.lineTo( tmp.x, tmp.y );

        p.closePath();
        stroke( p );
    }

    public void drawParticleSystem( ParticleSystem sys ) {
        for( Particle particle : sys.particles ) {
            drawPoint( particle.position, 3 );
        }
    }
}
