package cn.ecpark.tool.agent.core.jvm.command;

/**
 * @author zcn
 * @date: 2019-10-21
 **/
public class CommandResult {
    private boolean isSuccess;
    private byte[] content;

    public CommandResult(boolean isSuccess, byte[] content){
        this.isSuccess = isSuccess;
        this.content = content;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public byte[] getContent() {
        return content;
    }

}
