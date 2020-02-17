package cn.ecpark.tool.agent.core.log.converter;

import cn.ecpark.tool.agent.core.log.Converter;
import cn.ecpark.tool.agent.core.log.LogEvent;

/**
 * @author zcn
 * @date: 2019-10-14
 **/
public class ClassMethodConverter implements Converter {
    @Override
    public String convert(LogEvent event) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        StackTraceElement found = null;
        for(int i = 0, len = stackTraceElements.length; i < len - 1; i++){
            StackTraceElement ele = stackTraceElements[i];
            if(ele.getClassName().endsWith(event.getTargetClass())){
                found = ele;
                break;
            }
        }

        if(found == null){
            found = stackTraceElements[5];
        }

        return new StringBuilder()
                .append(cutClassName(found.getClassName()))
                .append(found.getMethodName())
                .append("(").append(found.getLineNumber()).append(")")
                .toString();
    }

    private static String cutClassName(String className){
        String[] classes = className.split("\\.");

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < classes.length; i++){
            String c = classes[i];
            sb.append(i < classes.length - 1 ? c.charAt(0) : c).append(".");
        }
        return sb.toString();
    }

}
