import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: ThreadPool
 * @description: 自定义阻塞队列
 * @author: 闲乘月
 * @create: 2020-07-12 16:41
 **/
public class BlockingQueue<T> {
    //任务队列
    private Deque<T> queue = new ArrayDeque<>();
    //锁
    private ReentrantLock lock = new ReentrantLock();

    //条件变量
    private Condition fullWait = lock.newCondition();
    private Condition emptyWait = lock.newCondition();

    //容量
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    //阻塞获取
    public T take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    emptyWait.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWait.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    //超时阻塞获取
    public T take(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);

            while (queue.isEmpty()) {
                try {
                    if (nanos <= 0) {
                        return null;
                    }
                    //emptyWait()返回剩余时间
                    nanos = emptyWait.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            T t = queue.removeFirst();
            fullWait.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    //阻塞添加
    public void put(T t) {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                try {
                    System.out.println("线程池任务队列繁忙" + "任务:" + t.toString() + "等待加入");
                    fullWait.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(t.toString() + "加入任务队列");
            queue.addLast(t);
            emptyWait.signal();
        } finally {
            lock.unlock();
        }
    }

    //超时阻塞添加
    public boolean put(T t, long timeout, TimeUnit unit) {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            while (queue.size() == capacity) {
                try {
                    if (nanos <= 0){
                        System.out.println(t.toString()+"超时放弃任务false...");
                        return false;
                    }

                    System.out.println("线程池任务队列繁忙" + "任务:" + t.toString() + "等待加入");
                    nanos = fullWait.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(t.toString() + "加入任务队列");
            queue.addLast(t);
            emptyWait.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    //获取当前队列大小
    public int size() {
        lock.lock();

        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

   //自定义添加策略
    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
            lock.lock();
            try{
                //判断队列是否满
                if(queue.size()==capacity){
                    //交由调用者自己实现队列满时具体策略
                    rejectPolicy.reject(this,task);
                }else {
                    //空闲
                    System.out.println(task.toString()+"加入任务队列");
                    queue.addLast(task);
                    emptyWait.signal();

                }
            }finally {
                lock.unlock();
            }

    }
}
