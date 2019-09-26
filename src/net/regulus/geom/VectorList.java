package net.regulus.geom;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public final class VectorList implements Iterable<Vec2.IContext> {
    public final VectorCollection collection = new VectorCollection( this );

    private Vec2[] vectorList;
    private int length;

    private void ensureCapacity( int size ) {
        int s = size / 16;
        if( s * 16 != size )
            size = ( s + 1 ) * 16;

        if( vectorList == null ) {
            vectorList = new Vec2[ size ];
        }
        if( size >= vectorList.length ) {
            Vec2[] old = vectorList;
            vectorList = new Vec2[ size ];
            System.arraycopy( old, 0, vectorList, 0, old.length );
        }
    }

    private void shiftRightFrom( int index ) {
        if( index >= length ) return;
        System.arraycopy( vectorList, index, vectorList, index + 1, vectorList.length - 1 - index );
        vectorList[ index ] = null;
    }

    private void shiftLeftFrom( int index ) {
        if( index >= length ) return;
        System.arraycopy( vectorList, index, vectorList, index - 1, vectorList.length - index );
        vectorList[ vectorList.length - 1 ] = null;
    }

    private void ensureNotNull( int index ) {
        if( vectorList[ index ] == null ) {
            vectorList[ index ] = new Vec2();
        }
    }

    private void addVectorAtEnd( double x, double y ) {
        ensureCapacity( length + 1 );
        int idx = length;
        length ++;
        ensureNotNull( idx );
        vectorList[ idx ].set( x, y );
    }

    private void addVectorAtIndex( int idx, double x, double y ) {
        ensureCapacity( length + 1 );
        length ++;
        shiftRightFrom( idx );
        ensureNotNull( idx );
        vectorList[ idx ].set( x, y );
    }

    private void removeVectorAtIndex( int idx ) {
        length --;
        shiftLeftFrom( idx + 1 );
    }

    private Vec2 getVectorAtIndex( int idx ) {
        ensureNotNull( idx );
        return vectorList[ idx ];
    }

    private void checkIndex0( int idx ) {
        if( idx < 0 || idx >= length ) {
            throw new IndexOutOfBoundsException( idx + "" );
        }
    }

    private void checkIndex1( int idx ) {
        if( idx < 0 || idx > length ) {
            throw new IndexOutOfBoundsException( idx + "" );
        }
    }

    public VectorList add( Vec2 vec ) {
        addVectorAtEnd( vec.x, vec.y );
        return this;
    }

    public VectorList add( double x, double y ) {
        addVectorAtEnd( x, y );
        return this;
    }

    public VectorList add( int index, Vec2 vec ) {
        checkIndex1( index );
        addVectorAtIndex( index, vec.x, vec.y );
        return this;
    }

    public VectorList add( int index, double x, double y ) {
        checkIndex1( index );
        addVectorAtIndex( index, x, y );
        return this;
    }

    public VectorList remove( int index ) {
        checkIndex0( index );
        removeVectorAtIndex( index );
        return this;
    }

    public Vec2 remove( int index, Vec2 out ) {
        checkIndex0( index );
        Vec2 vec = getVectorAtIndex( index );
        removeVectorAtIndex( index );
        return Vec2.put( out, vec );
    }

    public Vec2 get( int index, Vec2 out ) {
        checkIndex0( index );
        Vec2 vec = getVectorAtIndex( index );
        return Vec2.put( out, vec );
    }

    public VectorList set( int index, Vec2 vec ) {
        checkIndex0( index );
        ensureNotNull( index );
        vectorList[ index ].set( vec );
        return this;
    }

    public VectorList set( int index, double x, double y ) {
        checkIndex0( index );
        ensureNotNull( index );
        vectorList[ index ].set( x, y );
        return this;
    }

    public int size() {
        return length;
    }

    public void clear() {
        length = 0;
    }

    public boolean isEmpty() {
        return length == 0;
    }

    public void iterate( Consumer<Vec2.IContext> consumer ) {
        for( int i = 0; i < length; i ++ ) {
            Vec2 v = getVectorAtIndex( i );
            consumer.accept( out -> Vec2.put( out, v ) );
        }
    }

    @Override
    public Iterator<Vec2.IContext> iterator() {
        return new Itr( length );
    }

    @Override
    public Spliterator<Vec2.IContext> spliterator() {
        throw new UnsupportedOperationException( "spliterator()" );
    }

    private class Itr implements Iterator<Vec2.IContext> {
        int usualLength;
        int index = -1;
        Vec2 curr;
        final Vec2.IContext ctx = v -> Vec2.put( v, curr );

        Itr( int usualLength ) {
            this.usualLength = usualLength;
        }

        @Override
        public boolean hasNext() {
            return index < usualLength - 1;
        }

        @Override
        public Vec2.IContext next() {
            if( length != usualLength ) {
                usualLength = -1;
                throw new ConcurrentModificationException();
            }
            index ++;
            curr = getVectorAtIndex( index );
            return ctx;
        }

        @Override
        public void remove() {
            removeVectorAtIndex( index );
            usualLength --;
        }
    }
}
