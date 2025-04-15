package ru.vspochernin.rx;

import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultDisposable implements Disposable {

    private final AtomicBoolean disposed;

    public DefaultDisposable(AtomicBoolean disposed) {
        this.disposed = disposed;
    }

    @Override
    public void dispose() {
        disposed.set(true);
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }
} 