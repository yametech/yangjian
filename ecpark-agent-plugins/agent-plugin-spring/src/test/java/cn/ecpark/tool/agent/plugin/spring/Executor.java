package cn.ecpark.tool.agent.plugin.spring;

public class Executor {
    private static Executor INSTANCE = new Executor();

    public void doExecute() {
        for (int i = 0; i < 700; i++) {
            double sum = 0.0D;
            sum += i;
            double tmp = sum / (i + 1);
            tmp += sum;
            sum += tmp;
            sum = Math.log10(sum) * Math.log1p(sum) / Math.asin(sum) + Math.cos(sum);
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Executor Instance() {
        return INSTANCE;
    }
}
