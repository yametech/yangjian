package cn.ecpark.tool.agent.util.eventbus.assignor;

public interface MultiThreadAssignor<T> {
    /**
     * 根据消费数据以及线程数返回消费线程号（同一个标识的数据使用同一个线程消费）
     * @param msg	事件
     * @param totalThreadNum	总线程数
     * @return
     * 		该msg使用的线程号
     */
    int threadNum(T msg, int totalThreadNum);
}
