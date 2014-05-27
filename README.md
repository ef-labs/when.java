[![Build Status](https://travis-ci.org/englishtown/when.java.png?branch=develop)](https://travis-ci.org/englishtown/when.java)

When.java
=========

When.java is a Java implementation of the [CommonJS](http://wiki.commonjs.org/wiki/Promises) [Promises/A](http://wiki.commonjs.org/wiki/Promises/A) specification.

It is a port of [cujojs/when](https://github.com/cujojs/when) v2.8.0 by Brian Cavalier and John Hann.


Getting started
================

Add a maven dependency to when.java
```xml
    <dependency>
      <groupId>com.englishtown</groupId>
      <artifactId>when.java</artifactId>
      <version>2.0.0</version>
    </dependency>
```

Examples
---------

1\. The most basic scenario

The following demonstrates registering a resolution handler that is triggered in the future.

```java
        // Create the when and deferred objects
        When<Integer> when = new When<>();
        Deferred<Integer> d = when.defer();

        // Register on fulfilled callback
        Promise<Integer> p = d.getPromise();
        p.then(new FulfilledRunnable<Integer>() {
            @Override
            public Promise<Integer> run(Integer value) {
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
        final When<Integer> when = new When<>();
        Deferred<Integer> d = when.defer();

        // Register chained callbacks
        Promise<Integer> p = d.getPromise();
        p.then(new FulfilledRunnable<Integer>() {
            @Override
            public Promise<Integer> run(Integer value) {
                return when.resolve(value * 2);
            }
        }).then(new RejectedRunnable<Integer>() {
            @Override
            public Promise<Integer> run(Integer integer) {
                // Do something
                return null;
            }
        });

        // Use the resolver to trigger the callbacks registered above.
        // The first callback value will be 10
        // The second callback value will be 20
        d.getResolver().resolve(10);
```
