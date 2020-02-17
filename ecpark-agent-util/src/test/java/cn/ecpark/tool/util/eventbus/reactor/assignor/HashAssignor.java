package cn.ecpark.tool.util.eventbus.reactor.assignor;

/**
 * 按照hashcode分配
 * @param <T>
 */
public class HashAssignor<T> implements MultiThreadAssignor<T> {

    /**
     * 用于标识一批需保证顺序的数据hash值（同样hash值的数据一定在同一个线程消费），可重写该方法，按照业务ID生成hashcode
     * @return
     */
    public int hashValue(T msg) {
        return msg.hashCode();
    }

    @Override
    public int threadNum(T msg, int totalThreadNum) {
        return Math.abs(hashValue(msg)) % totalThreadNum;
    }
}
