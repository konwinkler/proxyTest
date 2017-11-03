import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class TestClass {

    @Test
    public void test() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.mac");

        // start the proxy
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(0);

        // get the Selenium proxy object
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        try {
            String hostIp = Inet4Address.getLocalHost().getHostAddress();
//            hostIp = "localhost";
            seleniumProxy.setHttpProxy(hostIp + ":" + proxy.getPort());
            seleniumProxy.setSslProxy(hostIp + ":" + proxy.getPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Assert.fail("invalid Host Address");
        }

        // start the browser up
        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.PROXY, seleniumProxy);

        WebDriver driver = new ChromeDriver(options);

        // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

        // create a new HAR with the label "yahoo.com"
        proxy.newHar("yahoo.com");

        // open yahoo.com
        driver.get("http://yahoo.com");

        // get the HAR data
        Har har = proxy.getHar();
    }
}
