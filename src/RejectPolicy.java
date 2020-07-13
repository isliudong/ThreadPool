/**
 * @program: ThreadPool
 * @description: 拒绝策略接口
 * @author: 闲乘月
 * @create: 2020-07-13 10:13
 **/
@FunctionalInterface
public interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue,T task);
}
