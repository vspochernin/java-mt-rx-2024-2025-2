package ru.vspochernin.rx;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ObservableTest {
    @Test
    void testBasicObservable() {
        DefaultObserver<Integer> observer = new DefaultObserver<>();

        Observable<Integer> observable = Observable.create(obs -> {
            obs.onNext(1);
            obs.onNext(2);
            obs.onNext(3);
            obs.onComplete();
        });

        observable.subscribe(observer);

        assertEquals(List.of(1, 2, 3), observer.getItems());
        assertNull(observer.getError());
        assertTrue(observer.isCompleted());
    }

    @Test
    void testErrorHandling() {
        DefaultObserver<Integer> observer = new DefaultObserver<>();
        RuntimeException error = new RuntimeException("Test error");

        Observable<Integer> observable = Observable.create(obs -> {
            obs.onNext(1);
            throw error;
        });

        observable.subscribe(observer);

        assertEquals(List.of(1), observer.getItems());
        assertEquals(error, observer.getError());
        assertFalse(observer.isCompleted());
    }
} 