package com.englishtown.promises;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/23/13
 * Time: 12:06 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Runnable<TRet, TArg> {

    TRet run(TArg value);

}
