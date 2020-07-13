import java.util.concurrent.TimeUnit;

/**
 * @program: ThreadPool
 * @description:
 * @author: 闲乘月
 * @create: 2020-07-12 20:34
 **/
public class Test {
    public static void main(String[] args) {
        ThreadPool pool = new ThreadPool(2, 10000, TimeUnit.MILLISECONDS, 10);
        for (int i = 0; i < 120; i++) {
            int j=i;
            pool.execute(()->{
                System.out.println(j);
            });
        }
    }
}
