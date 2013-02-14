When.java
=========

When.java is a Java implementation of the [CommonJS](http://wiki.commonjs.org/wiki/Promises) [Promises/A](http://wiki.commonjs.org/wiki/Promises/A) specification.

It is a port of the [cujojs/when](https://github.com/cujojs/when) v1.7.1 by Brian Cavalier and John Hann.


Getting started
================

Add a maven dependency to when.java
```xml
    <dependency>
      <groupId>com.englishtown</groupId>
      <artifactId>when.java</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </dependency>
```

Examples
---------

The following demonstrates the most basic scenario where a resolution handler is registered and then triggered in the future.

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
