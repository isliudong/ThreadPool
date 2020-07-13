import java.util.concurrent.TimeUnit;

/**
 * @program: ThreadPool
 * @description:
 * @author: 闲乘月
 * @create: 2020-07-12 20:34
 **/
public class Test {
    public static void main(String[] args) {
        ThreadPool pool = new ThreadPool(2, 10000, TimeUnit.MILLISECONDS, 2,((queue, task) -> {
            //死等策略
            //queue.put(task);
            //超时等策略
            queue.put(task,1,TimeUnit.MILLISECONDS);
        }));
        for (int i = 0; i < 500; i++) {
            int j=i;
            pool.execute(()->{
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(j);
            });
        }
    }
}
