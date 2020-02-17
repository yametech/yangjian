package cn.ecpark.tool.agent.core.log.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import cn.ecpark.tool.agent.core.log.Converter;
import cn.ecpark.tool.agent.core.log.LogEvent;

/**
 * @author zcn
 * @date: 2019-10-14
 **/
public class ThrowableConverter implements Converter {

    private static String SEPARATOR = System.getProperty("line.separator", "\n");
    @Override
    public String convert(LogEvent event) {
        return event.getThrowable() == null ? "" : convert(event.getThrowable());
    }

    private String convert(Throwable throwable){
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String traces = "";
        try {
            throwable.printStackTrace(new PrintWriter(output, true));
            traces = output.toString();
            output.close();
        }catch (IOException e){
//            e.printStackTrace();// 此处不要打印log，导致死循环
        }
        return SEPARATOR + traces;
    }
}
