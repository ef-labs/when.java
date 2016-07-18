When.java
=========

When.java is a Java implementation of the [CommonJS](http://wiki.commonjs.org/wiki/Promises) [Promises/A](http://wiki.commonjs.org/wiki/Promises/A) specification.

It is a port of [cujojs/when](https://github.com/cujojs/when) v3.2.3 by Brian Cavalier and John Hann.

[![Build Status](http://img.shields.io/travis/ef-labs/when.java.svg?maxAge=2592000&style=flat-square)](https://travis-ci.org/ef-labs/when.java)
[![Maven Central](https://img.shields.io/maven-central/v/com.englishtown/when.java.svg?maxAge=2592000&style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.englishtown/when.java/)

Getting started
================

Add a maven dependency to when.java
```xml
    <dependency>
      <groupId>com.englishtown</groupId>
      <artifactId>when.java</artifactId>
      <version>3.0.0-SNAPSHOT</version>
    </dependency>
```

Examples
---------

1\. The most basic scenario

The following demonstrates registering a resolution handler that is triggered in the future.

```java
        // Create the when and deferred objects
        When when = WhenFactory.createAsync();
        Deferred<Integer> d = when.defer();

        // Register on fulfilled callback
        Promise<Integer> p = d.getPromise();
        p.then(value -> {
                // Do something
                return null;
        });

        // Use the resolver to trigger the callback registered above.
        // The callback value will be 10
        d.resolve(10);

```

2\. Chaining callbacks

The following demonstrates chaining resolution handlers and how the value can be modified.

```java
        // Create the when and deferred objects
        When when = WhenFactory.createAsync();
        CountDownLatch latch = new CountDownLatch(1);

        Deferred<Integer> d = when.defer();
        Promise<Integer> p = d.getPromise();

        p.then(value -> {
            return when.resolve(2 * value);
        }).then(value2 -> {
            // Do something
            assertEquals(20, value2.intValue());
            return when.resolve(String.valueOf(value2));
        }).then(value3 -> {
            assertEquals("20", value3);
            latch.countDown();
            return null;
        });

        // Use the resolver to trigger the callbacks registered above.
        // The first callback value will be Integer 10
        // The second callback value will be Integer 20
        // The third callback value will be String "20"
        d.resolve(10);
        
        
```
