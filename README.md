# java-mt-rx-2024-2025-2
Курсовая работа «RxJava» по дисциплине "Многопоточное и асинхронное программирование на Java". 2-й семестр 1-го курса МИФИ ИИКС РПО (2024-2025 уч. г).

## Описание проекта

Данный проект представляет собой реализацию аналогичной RxJava библиотеки. Проект разработан в рамках курсовой работы по дисциплине "Многопоточное и асинхронное программирование на Java". Подробное описание требований к проекту находится в файле [task.md](task.md).

## Архитектура реализованной системы

Реализованная система представляет собой упрощенную версию библиотеки RxJava, которая включает следующие основные компоненты:

### 1. Базовые компоненты

- **Observer** - интерфейс, определяющий методы для обработки событий потока данных:
  - `onNext(T item)` - обработка очередного элемента.
  - `onError(Throwable t)` - обработка ошибок.
  - `onComplete()` - обработка завершения потока.

- **DefaultObserver** - базовая реализация `Observer`, которая хранит полученные элементы, ошибки и состояние завершения.

- **Observable** - основной класс, представляющий источник данных:
  - Статический метод `create()` для создания Observable.
  - Метод `subscribe()` для подписки на события.
  - Поддержка отмены подписки через `Disposable`.

### 2. Операторы преобразования данных

- **map** - преобразует элементы потока с помощью заданной функции.
- **filter** - фильтрует элементы потока по заданному условию.
- **flatMap** - преобразует каждый элемент в новый Observable и объединяет результаты.

### 3. Управление потоками выполнения

- **Scheduler** - интерфейс для управления потоками выполнения.
- Реализованы три типа Scheduler:
  - **IOThreadScheduler** - использует CachedThreadPool для I/O операций.
  - **ComputationScheduler** - использует FixedThreadPool для вычислительных задач.
  - **SingleThreadScheduler** - использует один поток для последовательного выполнения.

- Методы управления потоками:
  - **subscribeOn** - определяет поток для выполнения подписки.
  - **observeOn** - определяет поток для обработки элементов.

### 4. Управление подписками и обработка ошибок

- **Disposable** - интерфейс для отмены подписки.
- Реализована обработка ошибок через метод `onError()`.
- Поддержка отмены подписки во всех операторах.

## Принципы работы Schedulers

### IOThreadScheduler

- Использует `CachedThreadPool`.
- Оптимален для I/O операций.
- Создает новые потоки по мере необходимости.
- Переиспользует неактивные потоки.

### ComputationScheduler

- Использует `FixedThreadPool`.
- Количество потоков равно количеству процессоров.
- Оптимален для вычислительных задач.
- Предотвращает создание избыточных потоков.

### SingleThreadScheduler

- Использует один поток.
- Гарантирует последовательное выполнение.
- Полезен для задач, требующих синхронизации.

## Процесс тестирования

Тестирование системы включает следующие аспекты:

1. **Базовые компоненты**
   - Тестирование создания `Observable`.
   - Тестирование подписки и отписки.
   - Тестирование обработки ошибок.

2. **Операторы**
   - Тестирование `map` и `filter`.
   - Тестирование `flatMap`.
   - Тестирование комбинаций операторов.

3. **Schedulers**
   - Тестирование работы в разных потоках.
   - Тестирование `subscribeOn` и `observeOn`.
   - Тестирование комбинаций `Schedulers`.

4. **Обработка ошибок**
   - Тестирование передачи ошибок.
   - Тестирование отмены подписки при ошибках.
   - Тестирование обработки ошибок в операторах.

## Примеры использования

### Создание и подписка

```java
Observable<Integer> observable = Observable.create(obs -> {
    obs.onNext(1);
    obs.onNext(2);
    obs.onNext(3);
    obs.onComplete();
});

DefaultObserver<Integer> observer = new DefaultObserver<>();
observable.subscribe(observer);

// Получение результатов.
List<Integer> items = observer.getItems();
Throwable error = observer.getError();
boolean completed = observer.isCompleted();
```

### Использование операторов
```java
Observable<Integer> source = Observable.create(obs -> {
    obs.onNext(1);
    obs.onNext(2);
    obs.onNext(3);
    obs.onComplete();
});

DefaultObserver<String> observer = new DefaultObserver<>();
source
    .filter(x -> x % 2 == 0)
    .map(x -> "Even: " + x)
    .subscribe(observer);

// Получение результатов.
List<String> items = observer.getItems();
```

### Использование Schedulers
```java
Observable<Integer> source = Observable.create(obs -> {
    obs.onNext(1);
    obs.onNext(2);
    obs.onNext(3);
    obs.onComplete();
});

DefaultObserver<Integer> observer = new DefaultObserver<>();
source
    .subscribeOn(IOThreadScheduler.getInstance())
    .observeOn(ComputationScheduler.getInstance())
    .subscribe(observer);

// Получение результатов.
List<Integer> items = observer.getItems();
```
