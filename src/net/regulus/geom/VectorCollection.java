package net.regulus.geom;

import java.util.Iterator;
import java.util.function.Consumer;

public final class VectorCollection implements Iterable<Vec2.IContext> {
    private final VectorList underlying;

    VectorCollection( VectorList underlying ) {
        this.underlying = underlying;
    }

    public int size() {
        return underlying.size();
    }

    public boolean isEmpty() {
        return underlying.isEmpty();
    }

    @Override
    public Iterator<Vec2.IContext> iterator() {
        return underlying.iterator();
    }

    public void iterate( Consumer<Vec2.IContext> consumer ) {
        underlying.iterate( consumer );
    }

    public Vec2 get( int index, Vec2 out ) {
        return underlying.get( index, out );
    }
}
