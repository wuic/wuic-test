Web UI Compressor - tests support
=========

This projects provides helpers based on JUnit to ease WUIC features test. Just add the following dependency:

```xml
<dependency>
    <groupId>com.github.wuic</groupId>
	<artifactId>wuic-test</artifactId>
	<version>${wuic.version}</version>
    <scope>test</scope>
</dependency>
```

### Integration test

You can write an integration test that runs a webapp deployed in the undertow servlet container.

```java
@RunWith(JUnit4.class)
@WuicRunnerConfiguration(webApplicationPath = "/myCustomPath")
public class ServletContainerTest {

    @ClassRule
    public static Server server = new Server();

    @Test
    public void basicHttpGetTest() throws IOException {
        // Assume you have an index.html page in your classpath that contains "Hello World" string
        final String content = IOUtils.readString(new InputStreamReader(server.get("/index.html").getEntity().getContent()));
        Assert.assertTrue(content, content.contains("Hello World"));
    }
}
```

See the [own unit tests of the project](https://github.com/wuic/wuic-test/tree/master/src/test/java/com/github/test/testthetest) to get full samples.