When.java
=========

When.java is a Java implementation of the [CommonJS](http://wiki.commonjs.org/wiki/Promises) [Promises/A](http://wiki.commonjs.org/wiki/Promises/A) specification.

It is a port of the [cujojs/when](https://github.com/cujojs/when) v1.8.1 by Brian Cavalier and John Hann.


Getting started
================

Add a maven dependency to when.java
```xml
    <dependency>
      <groupId>com.englishtown</groupId>
      <artifactId>when.java</artifactId>
      <version>1.1.0-SNAPSHOT</version>
    </dependency>
```

Examples
---------

1\. The most basic scenario

The following demonstrates registering a resolution handler that is triggered in the future.

```java
        // Create the when and deferred objects
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();

        // Register on fulfilled callback
        Promise<Integer, Integer> p = d.getPromise();
        p.then(new Runnable<Promise<Integer, Integer>, Integer>() {
            @Override
            public Promise<Integer, Integer> run(Integer value) {
                // Do something
                return null;
            }
        });

        // Use the resolver to trigger the callback registered above.
        // The callback value will be 10
        d.getResolver().resolve(10);

```

2\. Chaining callbacks

The following demonstrates chaining resolution handlers and how the value can be modified.

```java
        // Create the when and deferred objects
        final When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();

        // Register chained callbacks
        Promise<Integer, Integer> p = d.getPromise();
        p.then(new Runnable<Promise<Integer, Integer>, Integer>() {
            @Override
            public Promise<Integer, Integer> run(Integer value) {
                return when.resolve(value * 2);
            }
        }).then(new Runnable<Promise<Integer, Integer>, Integer>() {
            @Override
            public Promise<Integer, Integer> run(Integer integer) {
                // Do something
                return null;
            }
        });

        // Use the resolver to trigger the callbacks registered above.
        // The first callback value will be 10
        // The second callback value will be 20
        d.getResolver().resolve(10);
```
