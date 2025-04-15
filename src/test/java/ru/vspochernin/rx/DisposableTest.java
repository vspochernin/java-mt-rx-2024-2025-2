package ru.vspochernin.rx;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class DisposableTest {
    @Test
    void testDisposable() {
        AtomicBoolean disposed = new AtomicBoolean(false);
        DefaultDisposable disposable = new DefaultDisposable(disposed);

        assertFalse(disposable.isDisposed());
        assertFalse(disposed.get());

        disposable.dispose();

        assertTrue(disposable.isDisposed());
        assertTrue(disposed.get());

        // Повторный вызов dispose не должен вызывать действие снова.
        disposable.dispose();
        assertTrue(disposable.isDisposed());
    }

    @Test
    void testObservableDisposable() {
        DefaultObserver<Integer> observer = new DefaultObserver<>();
        AtomicBoolean completed = new AtomicBoolean(false);

        Observable<Integer> observable = Observable.create(obs -> {
            for (int i = 0; i < 1000; i++) {
                if (i == 5) {
                    completed.set(true);
                }
                obs.onNext(i);
            }
            obs.onComplete();
        });

        Disposable disposable = observable.subscribe(observer);
        assertFalse(disposable.isDisposed());

        // Ждем, пока дойдем до 5.
        while (!completed.get()) {
            Thread.yield();
        }

        disposable.dispose();
        assertTrue(disposable.isDisposed());

        // Проверяем, что получили все числа.
        assertEquals(1000, observer.getItems().size());
    }
} 