package cn.ecpark.tool.util.eventbus.reactor.assignor;

public interface MultiThreadAssignor<T> {
    /**
     * 根据消费数据以及线程数返回消费线程标识（同一个标识的数据使用同一个线程消费）
     * @param msg
     * @param totalThreadNum
     * @return
     */
    int threadNum(T msg, int totalThreadNum);
}
