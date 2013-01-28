package com.englishtown.promises;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/29/13
 * Time: 5:59 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Deferred<TResolve, TProgress> {

    Resolver<TResolve, TProgress> getResolver();

    Promise<TResolve, TProgress> getPromise();

}
