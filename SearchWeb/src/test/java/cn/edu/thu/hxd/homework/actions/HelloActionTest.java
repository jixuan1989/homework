package cn.edu.thu.hxd.homework.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.StrutsTestCase;

public class HelloActionTest extends StrutsTestCase {

    public void testHelloAction() throws Exception {
        ImageAction hello = new ImageAction();
        String result = hello.execute();
        assertTrue("Expected a success result!",
                ActionSupport.SUCCESS.equals(result));
        assertTrue("Expected the default message!",
                hello.getText(ImageAction.MESSAGE).equals(hello.getMessage()));
    }
}
