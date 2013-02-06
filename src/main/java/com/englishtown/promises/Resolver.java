package com.englishtown.promises;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/29/13
 * Time: 6:01 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Resolver<TResolve, TProgress> {

    Promise<TResolve, TProgress> resolve(TResolve value);

    Promise<TResolve, TProgress> resolve(Promise<TResolve, TProgress> value);

    Promise<TResolve, TProgress> reject(TResolve reason);

    Promise<TResolve, TProgress> reject(Reason<TResolve> reason);

    TProgress progress(TProgress update);

}
