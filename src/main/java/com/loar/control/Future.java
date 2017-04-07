package com.loar.control;

/**
 * 执行结果
 * Created by Justsy on 2016/12/9.
 */

public class Future<T> {
    long overdue = 60 * 1000L;//过期时间
    long createTime = System.currentTimeMillis();//创建时间
    T result;
    int count = 0;//已经执行次数
    Status status = Status.UNEXECUTED;//状态

    public Future() {
    }

    public long getCreateTime() {
        return createTime;
    }

    public Future(long overdue) {
        this.overdue = overdue;
    }

    public int getCount() {
        return count;
    }

    public Status getStatus() {
        return status;
    }

    public T getResult() {
        return result;
    }

    //等待执行结果
    public T getResultWait() {
        while (true) {
            if (status == Status.FAILED || status == Status.FINISH) {
                break;
            }
        }
        return result;
    }

    enum Status {
        UNEXECUTED, FINISH, FAILED, DOING
    }
}
