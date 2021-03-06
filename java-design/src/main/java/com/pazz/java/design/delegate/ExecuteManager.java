package com.pazz.java.design.delegate;

/**
 * @author: Peng Jian
 * @create: 2018/9/29 14:20
 * @description: 委派经理
 */
public class ExecuteManager implements Delegate {

    private Delegate delegate;

    public ExecuteManager(Delegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void doWork() {
        delegate.doWork();
    }
}
