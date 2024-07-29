package com.video.offline.videoplayer.gui.privacy.vault.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

public class UniqueLinkedList<E> extends LinkedList<E> {
    private final HashSet<E> keys = new HashSet<>();

    @Override
    public boolean add(E e) {
        if (keys.add(e)) {
            return super.add(e);
        }
        return false;
    }

    @Override
    public void add(int index, E e) {
        if (keys.add(e)) {
            super.add(index, e);
        }
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return keys.contains(o);
    }

    @Override
    public boolean remove(@Nullable Object o) {
        if (keys.remove(o)) {
            return super.remove(o);
        }
        return false;
    }

    @Override
    public void clear() {
        keys.clear();
        super.clear();
    }

    @Override
    public E remove(int index) {
        E e = get(index);
        keys.remove(e);
        return super.remove(index);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        keys.removeAll(c);
        return super.removeAll(c);
    }

}