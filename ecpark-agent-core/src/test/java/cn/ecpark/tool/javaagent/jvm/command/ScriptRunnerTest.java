package cn.ecpark.tool.javaagent.jvm.command;

import org.junit.Assert;
import org.junit.Test;

import cn.ecpark.tool.agent.core.jvm.command.CommandExecutor;
import cn.ecpark.tool.agent.core.jvm.command.CommandResult;
import cn.ecpark.tool.agent.core.util.OSUtil;

/**
 * @author zcn
 * @date: 2019-10-22
 **/
public class ScriptRunnerTest {

    @Test
    public void testRunInWindows() {
        if(OSUtil.isWindows()) {
            CommandResult result = new CommandExecutor().execute(new String[]{"ipconfig"});
            Assert.assertTrue(result.isSuccess());
            Assert.assertTrue(result.getContent().length > 0);
        }else{
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testRunInLinux(){
        if(OSUtil.isLinux()){
            CommandResult result = new CommandExecutor().execute(new String[]{"top -b -n 1"});
            Assert.assertTrue(result.isSuccess());
            Assert.assertTrue(result.getContent().length > 0);
        }else{
            Assert.assertTrue(true);
        }
    }

}
