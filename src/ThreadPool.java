import javafx.concurrent.Worker;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * @program: ThreadPool
 * @description: 自定义线程池
 * @author: 闲乘月
 * @create: 2020-07-12 16:40
 **/
public class ThreadPool {
    //排队任务
    private BlockingQueue<Runnable> taskQueue;

    //工作线程集合
    private HashSet<ThreadWorker> workers=new HashSet<>();

    //核心线程数
    private int coreSize;

    //获取任务超时时间
    private long timeout;
    //超时时间单位
    private TimeUnit unit;

    public ThreadPool(int coreSize, long timeout, TimeUnit unit, int capacity ) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.unit = unit;
        this.taskQueue=new BlockingQueue<>(capacity);
    }

    //执行任务
    public void execute(Runnable task){
        synchronized (workers){
            if(workers.size()<coreSize){
                ThreadWorker worker = new ThreadWorker(task);
                System.out.println("增加worker："+worker.getId());
                workers.add(worker);
                worker.start();

            }else {
                System.out.println("核心繁忙，当前任务:"+task.toString()+"加入任务队列");
                taskQueue.put(task);
            }
        }
    }

    class ThreadWorker extends Thread{
        private Runnable task;

        public ThreadWorker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            //不为空执行任务，为空则取队列中任务直到全部执行完成
            while (task!=null||(task=taskQueue.take(timeout,unit))!=null){
                try{
                    System.out.println("正在执行任务:"+task.toString());
                    task.run();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    task=null;
                }
            }
            synchronized (workers){
                System.out.println("移除worker:"+this.toString());
                workers.remove(this);
            }
        }
    }
}
