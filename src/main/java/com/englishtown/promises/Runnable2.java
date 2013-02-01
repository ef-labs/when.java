package com.englishtown.promises;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/31/13
 * Time: 2:29 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Runnable2<TRet, TArg1, TArg2> {

    TRet run(TArg1 a1, TArg2 a2);

}