package ru.vspochernin.rx;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Observable<T> {
    private final Consumer<Observer<T>> source;

    private Observable(Consumer<Observer<T>> source) {
        this.source = source;
    }

    public static <T> Observable<T> create(Consumer<Observer<T>> source) {
        return new Observable<>(source);
    }

    public Disposable subscribe(Observer<T> observer) {
        AtomicBoolean disposed = new AtomicBoolean(false);
        Observer<T> disposableObserver = new Observer<T>() {
            @Override
            public void onNext(T item) {
                if (!disposed.get()) {
                    observer.onNext(item);
                }
            }

            @Override
            public void onError(Throwable t) {
                if (!disposed.get()) {
                    observer.onError(t);
                }
            }

            @Override
            public void onComplete() {
                if (!disposed.get()) {
                    observer.onComplete();
                }
            }
        };

        try {
            source.accept(disposableObserver);
        } catch (Throwable t) {
            if (!disposed.get()) {
                observer.onError(t);
            }
        }

        return new DefaultDisposable(disposed);
    }

    public Observable<T> subscribeOn(Scheduler scheduler) {
        return new Observable<>(observer -> {
            AtomicBoolean disposed = new AtomicBoolean(false);
            scheduler.execute(() -> {
                if (!disposed.get()) {
                    subscribe(new Observer<T>() {
                        @Override
                        public void onNext(T item) {
                            if (!disposed.get()) {
                                observer.onNext(item);
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            if (!disposed.get()) {
                                observer.onError(t);
                            }
                        }

                        @Override
                        public void onComplete() {
                            if (!disposed.get()) {
                                observer.onComplete();
                            }
                        }
                    });
                }
            });
        });
    }

    public Observable<T> observeOn(Scheduler scheduler) {
        return new Observable<>(observer -> {
            AtomicBoolean disposed = new AtomicBoolean(false);
            subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    if (!disposed.get()) {
                        scheduler.execute(() -> {
                            if (!disposed.get()) {
                                observer.onNext(item);
                            }
                        });
                    }
                }

                @Override
                public void onError(Throwable t) {
                    if (!disposed.get()) {
                        scheduler.execute(() -> {
                            if (!disposed.get()) {
                                observer.onError(t);
                            }
                        });
                    }
                }

                @Override
                public void onComplete() {
                    if (!disposed.get()) {
                        scheduler.execute(() -> {
                            if (!disposed.get()) {
                                observer.onComplete();
                            }
                        });
                    }
                }
            });
        });
    }

    public <R> Observable<R> map(Function<T, R> mapper) {
        return new Observable<>(observer -> {
            AtomicBoolean disposed = new AtomicBoolean(false);
            subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    if (!disposed.get()) {
                        try {
                            observer.onNext(mapper.apply(item));
                        } catch (Throwable t) {
                            observer.onError(t);
                        }
                    }
                }

                @Override
                public void onError(Throwable t) {
                    if (!disposed.get()) {
                        observer.onError(t);
                    }
                }

                @Override
                public void onComplete() {
                    if (!disposed.get()) {
                        observer.onComplete();
                    }
                }
            });
        });
    }

    public Observable<T> filter(Predicate<T> predicate) {
        return new Observable<>(observer -> {
            AtomicBoolean disposed = new AtomicBoolean(false);
            subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    if (!disposed.get()) {
                        try {
                            if (predicate.test(item)) {
                                observer.onNext(item);
                            }
                        } catch (Throwable t) {
                            observer.onError(t);
                        }
                    }
                }

                @Override
                public void onError(Throwable t) {
                    if (!disposed.get()) {
                        observer.onError(t);
                    }
                }

                @Override
                public void onComplete() {
                    if (!disposed.get()) {
                        observer.onComplete();
                    }
                }
            });
        });
    }

    public <R> Observable<R> flatMap(Function<? super T, ? extends Observable<? extends R>> mapper) {
        return new Observable<>(observer -> {
            AtomicBoolean disposed = new AtomicBoolean(false);
            AtomicBoolean hasError = new AtomicBoolean(false);
            AtomicInteger activeCount = new AtomicInteger(1); // 1 for source Observable

            Observer<T> sourceObserver = new Observer<T>() {
                @Override
                public void onNext(T item) {
                    if (!disposed.get() && !hasError.get()) {
                        try {
                            Observable<? extends R> innerObservable = mapper.apply(item);
                            activeCount.incrementAndGet();

                            Observer<R> innerObserver = new Observer<R>() {
                                @Override
                                public void onNext(R innerItem) {
                                    if (!disposed.get() && !hasError.get()) {
                                        observer.onNext(innerItem);
                                    }
                                }

                                @Override
                                public void onError(Throwable t) {
                                    if (hasError.compareAndSet(false, true) && !disposed.get()) {
                                        observer.onError(t);
                                    }
                                }

                                @Override
                                public void onComplete() {
                                    if (activeCount.decrementAndGet() == 0 && !hasError.get() && !disposed.get()) {
                                        observer.onComplete();
                                    }
                                }
                            };

                            ((Observable<R>) innerObservable).subscribe(innerObserver);
                        } catch (Throwable t) {
                            if (hasError.compareAndSet(false, true) && !disposed.get()) {
                                observer.onError(t);
                            }
                        }
                    }
                }

                @Override
                public void onError(Throwable t) {
                    if (hasError.compareAndSet(false, true) && !disposed.get()) {
                        observer.onError(t);
                    }
                }

                @Override
                public void onComplete() {
                    if (activeCount.decrementAndGet() == 0 && !hasError.get() && !disposed.get()) {
                        observer.onComplete();
                    }
                }
            };

            subscribe(sourceObserver);
        });
    }
} 