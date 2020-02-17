package cn.ecpark.tool.agent.core.jvm.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;

/**
 * @author zcn
 * @date: 2019-10-18
 **/
public class CommandExecutor {

    private final static ILogger logger = LoggerFactory.getLogger(CommandExecutor.class);

    public CommandResult execute(String[] commands){
        try {
            Process process = Runtime.getRuntime().exec(commands);
            return acquire(process);
        }catch (IOException e) {
            logger.error(e, "Fail to execute command , command: {}");
            return new CommandResult(false, e.getLocalizedMessage().getBytes());
        }
    }

    private CommandResult acquire(Process process) throws IOException{
        try {
            byte[] error = readStream(process.getErrorStream());
            if(error.length != 0){
                return new CommandResult(false, error);
            }

            byte[] content = readStream(process.getInputStream());
            return new CommandResult(true, content);
        } finally {
            process.getInputStream().close();
            process.getOutputStream().close();
            process.getErrorStream().close();
            process.destroy();
        }
    }

    private byte[] readStream(InputStream inputStream) throws IOException{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int bufSize = 1024;
        byte[] buff = new byte[bufSize];
        int len = 0;
        while( (len = inputStream.read(buff, 0 , bufSize)) != -1){
            outputStream.write(buff,0, len);
        }
        return outputStream.toByteArray();
    }

}
