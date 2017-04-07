package com.loar.control;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;

/**
 * 单线程队列池
 *
 * @author LoaR
 */
public class QueueExecutor {

    private static QueueExecutor queueExecutor;

    private final LinkedList<Callable> tasks = new LinkedList<>();//任务队列
    private final HashMap<Integer, Future> futures = new HashMap<>();

    private final Thread thread = new MThread();

    public QueueExecutor() {
        thread.start();
    }

    /**
     * 获取默认的队列
     *
     * @return
     */
    public static synchronized QueueExecutor getInstance() {
        if (queueExecutor == null) {
            queueExecutor = new QueueExecutor();
        }
        return queueExecutor;
    }


    /**
     * 加锁是为了多线程
     * 查找第一个可执行的任务
     *
     * 这种查找不移除有个弊端，如果有个线程是无限次执行的，却一直执行失败，那么将一直占用线程
     * @return
     */
    private  Callable findFistTask() {
        synchronized (tasks) {
            for (Callable task : tasks) {
                if (task instanceof Call) {
                    Future tempF = futures.get(task.hashCode());
                    long start = ((Call) task).delayExecuteTime() + tempF.createTime;
                    if ((((Call) task).executeGap() * tempF.count + start) <= System.currentTimeMillis()
                            && start <= System.currentTimeMillis()) {
//                    tasks.remove(task);
                        return task;
                    }
                } else {
//                tasks.remove(task);
                    return task;
                }
            }
        }
        return null;
    }

    public <T> void removeTask(Callable<T> call) {
        synchronized (tasks) {
            tasks.remove(call);
        }
        futures.remove(call.hashCode());
    }

    public <T> Future<T> addTask(Callable<T> call) {
        return addTask(call, 30000L);
    }

    /**
     * @param <T>
     * @param call    相同对象不会重复添加 ，但是会立即唤醒线程
     * @param overdue 结果过期时间
     */
    public <T> Future<T> addTask(Callable<T> call, long overdue) {
        Future<T> f;
        if (tasks.contains(call)) {
            f = futures.get(call.hashCode());
        } else {
            synchronized (tasks) {
                tasks.add(call);
            }
            f = new Future<>(overdue);
            futures.put(call.hashCode(), f);
        }
        synchronized (thread){//必须加锁
            thread.notify();//添加任务后唤醒
        }
        return f;
    }

    /**
     * 加入，优先执行
     *
     * @param call
     * @param <T>
     * @return
     */
    public <T> Future<T> addUrgentTask(Callable<T> call) {
        return addAppointTask(call, 0);
    }

    /**
     * 加入指定位置
     *
     * @param call
     * @param <T>
     * @return
     */
    public <T> Future<T> addAppointTask(Callable<T> call, int index) {
        Future<T> f;
        if (tasks.contains(call)) {
            f = futures.get(call.hashCode());
        } else {
            synchronized (tasks) {
                tasks.add(index, call);
            }
            f = new Future<>();
            futures.put(call.hashCode(), f);
        }
        synchronized (thread){//必须加锁
            thread.notify();//添加任务后唤醒
        }
        return f;
    }


    private class MThread extends Thread {
        @Override
        public void run() {
            while (true) {
                Callable call = null;
                Future f = null;
                try {
                    if (tasks.isEmpty()) {
                        try {
                            //定时清理结果集合
                            synchronized (futures) {
                                for (Integer key : futures.keySet()) {
                                    Future ft = futures.get(key);
                                    if (System.currentTimeMillis() - ft.createTime > ft.overdue) {
                                        futures.remove(key);
                                    }
                                }
                            }

                            synchronized (thread) {
                                thread.wait(5000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //找到第一个可执行的任务
                        call = findFistTask();

                        //如果未找到可执行任务,重新执行
                        if (call == null) {
                            continue;
                        }

                        f = futures.get(call.hashCode());
                        f.status = Future.Status.DOING;//正在执行
                        f.result = call.call();
                        f.status = Future.Status.FINISH;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (f != null) {
                        if (call instanceof Call) {
                            if (f.count + 1 > ((Call) call).executeCount()) {//当执行次数为Integer.max时将无限次循环
                                f.status = Future.Status.FAILED;
                            } else {
                                f.status = Future.Status.UNEXECUTED;
                            }
                        } else {
                            f.status = Future.Status.FAILED;
                        }
                    }

                } finally {
                    if (f != null) {
                        f.count++;
                        if (f.status == Future.Status.FINISH || f.status == Future.Status.FAILED) {
                            //执行完后
                            if (call != null) {
                                synchronized (tasks) {
                                    tasks.remove(call);
                                }
                            }
                        } else {

                        }
                    }
                }
            }
        }
    }

}
