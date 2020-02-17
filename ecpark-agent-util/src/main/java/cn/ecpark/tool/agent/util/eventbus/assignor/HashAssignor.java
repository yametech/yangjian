package cn.ecpark.tool.agent.util.eventbus.assignor;

/**
 * 按照hashcode分配
 * @param <T>
 */
public abstract class HashAssignor<T> implements MultiThreadAssignor<T> {

    /**
     * 用于标识一批需保证顺序的数据hash值（同样hash值的数据一定在同一个线程消费），可重写该方法，按照业务ID生成hashcode
     * @return
     */
    public abstract int hashValue(T msg);

    @Override
    public final int threadNum(T msg, int totalThreadNum) {
        return Math.abs(hashValue(msg)) % totalThreadNum;
    }
}
