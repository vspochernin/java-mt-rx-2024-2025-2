package ru.vspochernin.rx;

import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {
    @Test
    void testSubscribeOn() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger threadId = new AtomicInteger();
        DefaultObserver<Integer> observer = new DefaultObserver<>();

        Observable<Integer> observable = Observable.create(obs -> {
            threadId.set((int) Thread.currentThread().getId());
            obs.onNext(1);
            obs.onNext(2);
            obs.onNext(3);
            obs.onComplete();
            latch.countDown();
        });

        observable
            .subscribeOn(IOThreadScheduler.getInstance())
            .subscribe(observer);

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertNotEquals(Thread.currentThread().getId(), threadId.get());
        assertEquals(List.of(1, 2, 3), observer.getItems());
        assertTrue(observer.isCompleted());
    }

    @Test
    void testObserveOn() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger threadId = new AtomicInteger();
        DefaultObserver<Integer> observer = new DefaultObserver<>();

        Observable<Integer> observable = Observable.create(obs -> {
            obs.onNext(1);
            obs.onNext(2);
            obs.onNext(3);
            obs.onComplete();
            latch.countDown();
        });

        observable
            .observeOn(ComputationScheduler.getInstance())
            .subscribe(new Observer<Integer>() {
                @Override
                public void onNext(Integer item) {
                    threadId.set((int) Thread.currentThread().getId());
                    observer.onNext(item);
                }

                @Override
                public void onError(Throwable t) {
                    observer.onError(t);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertNotEquals(Thread.currentThread().getId(), threadId.get());
        assertEquals(List.of(1, 2, 3), observer.getItems());
        assertTrue(observer.isCompleted());
    }

    @Test
    void testSubscribeOnAndObserveOn() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger subscribeThreadId = new AtomicInteger();
        AtomicInteger observeThreadId = new AtomicInteger();
        DefaultObserver<Integer> observer = new DefaultObserver<>();

        Observable<Integer> observable = Observable.create(obs -> {
            subscribeThreadId.set((int) Thread.currentThread().getId());
            obs.onNext(1);
            obs.onNext(2);
            obs.onNext(3);
            obs.onComplete();
            latch.countDown();
        });

        observable
            .subscribeOn(IOThreadScheduler.getInstance())
            .observeOn(ComputationScheduler.getInstance())
            .subscribe(new Observer<Integer>() {
                @Override
                public void onNext(Integer item) {
                    observeThreadId.set((int) Thread.currentThread().getId());
                    observer.onNext(item);
                }

                @Override
                public void onError(Throwable t) {
                    observer.onError(t);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertNotEquals(Thread.currentThread().getId(), subscribeThreadId.get());
        assertNotEquals(Thread.currentThread().getId(), observeThreadId.get());
        assertNotEquals(subscribeThreadId.get(), observeThreadId.get());
        assertEquals(List.of(1, 2, 3), observer.getItems());
        assertTrue(observer.isCompleted());
    }
} 