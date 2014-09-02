package com.englishtown.promises.internal;

import com.englishtown.promises.Promise;
import com.englishtown.promises.PromiseResolver;
import com.englishtown.promises.State;
import com.englishtown.promises.Thenable;
import com.englishtown.promises.exceptions.RejectException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by adriangonzalez on 8/26/14.
 */
public class ArrayHelper {

    private final PromiseHelper helper;

    @Inject
    public ArrayHelper(PromiseHelper helper) {
        this.helper = helper;
    }

    /**
     * One-winner competitive race.
     * Return a promise that will fulfill when one of the promises
     * in the input array fulfills, or will reject when all promises
     * have rejected.
     *
     * @param promises
     * @return {Promise} promise for the first fulfilled value
     */
    public <T> Promise<T> any(List<? extends Thenable<T>> promises) {

        PromiseResolver<T> resolver = (resolve, reject) -> {
            ValueHolder<Integer> pending = new ValueHolder<>(promises.size());
            List<Throwable> errors = new ArrayList<>();

            Function<T, Promise<T>> handleResolve = (x) -> {
                resolve.accept(x);
                return null;
            };

            Function<Throwable, Promise<T>> handleReject = (e) -> {
                errors.add(e);
                if (--pending.value == 0) {
                    reject.accept(new RejectException("All promises rejected", errors));
                }
                return null;
            };

            promises.forEach((p) -> {
                helper.toPromise(p).then(handleResolve, handleReject);
            });

            if (pending.value == 0) {
                resolve.accept(null);
            }

        };

        return new TrustedPromise<>(resolver, helper);
    }

    /**
     * N-winner competitive race
     * Return a promise that will fulfill when n input promises have
     * fulfilled, or will reject when it becomes impossible for n
     * input promises to fulfill (ie when promises.length - n + 1
     * have rejected)
     *
     * @param {array}  promises
     * @param {number} n
     * @returns {Promise} promise for the earliest n fulfillment values
     */
    public <T> Promise<List<T>> some(List<? extends Thenable<T>> promises, int n) {

        int nFinal = Math.max(n, 0);

        return new TrustedPromise<>((resolve, reject) -> {
            final ValueHolder<Integer> nFulfill = new ValueHolder<>(0);
            final ValueHolder<Integer> nReject = new ValueHolder<>(null);
            List<T> results = new ArrayList<>(nFinal);
            List<Throwable> errors = new ArrayList<>();

            Function<T, Promise<T>> handleResolve = (x) -> {
                if (nFulfill.value > 0) {
                    --nFulfill.value;
                    results.add(x);

                    if (nFulfill.value == 0) {
                        resolve.accept(results);
                    }
                }
                return null;
            };

            Function<Throwable, Promise<T>> handleReject = (e) -> {
                if (nReject.value > 0) {
                    --nReject.value; // TODO: sync?
                    errors.add(e);

                    if (nReject.value == 0) {
                        reject.accept(new RejectException("Too many rejections", errors));
                    }
                }
                return null;
            };

            nReject.value = (promises.size() - nFinal + 1);
            nFulfill.value = Math.min(nFinal, promises.size());

            if (nFulfill.value == 0) {
                resolve.accept(results);
                return;
            }

            promises.stream().forEach(p -> {
                helper.toPromise(p).then(handleResolve, handleReject);
            });

        }, helper);
    }

    /**
     * Apply f to the value of each promise in a list of promises
     * and return a new list containing the results.
     *
     * @param promises
     * @param f
     * @param fallback
     * @return {Promise}
     */
    public <T> Promise<List<T>> map(List<? extends Thenable<T>> promises, Function<T, ? extends Thenable<T>> f, Function<Throwable, ? extends Thenable<T>> fallback) {

        return helper.all(promises
                .stream()
                .map(x -> helper.toPromise(x).then(f, fallback))
                .collect(Collectors.toList()));

    }

    /**
     * Return a promise that will always fulfill with an array containing
     * the outcome states of all input promises.  The returned promise
     * will never reject.
     *
     * @param promises
     * @returns {Promise}
     */
    public <T> Promise<List<State<T>>> settle(List<? extends Thenable<T>> promises) {

        return helper.all(promises.stream().map(p -> {
            TrustedPromise<T> p1 = helper.toPromise(p);
            return p1.then(
                    x -> helper.resolve(p1.inspect()),
                    t -> helper.resolve(p1.inspect())
            );
        }).collect(Collectors.toList()));

    }

    public <T> Promise<T> reduce(List<? extends Thenable<T>> promises, BiFunction<T, T, ? extends Thenable<T>> f) {

        List<Thenable<T>> thenables = (List<Thenable<T>>) promises;

        return (Promise<T>) thenables
                .stream()
                .reduce(
                        (result, x) -> helper.toPromise(result).then(
                                r -> helper.toPromise(x).then(
                                        x1 -> f.apply(r, x1)
                                )
                        ))
                .get();

    }

    public <T, U> Promise<U> reduce(List<? extends Thenable<T>> promises, BiFunction<U, T, ? extends Thenable<U>> f, Thenable<U> initialValue) {

        //noinspection unchecked
        List<Thenable<T>> thenables = (List<Thenable<T>>) promises;

        return (Promise<U>) thenables
                .stream()
                .reduce(
                        initialValue,
                        (result, x) -> helper.toPromise(result).then(
                                r -> helper.toPromise(x).then(
                                        x1 -> f.apply(r, x1)
                                )
                        ), (c1, c2) -> c1);

    }

}
