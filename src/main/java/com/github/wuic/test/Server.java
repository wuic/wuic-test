/*
 * "Copyright (c) 2014   Capgemini Technology Services (hereinafter "Capgemini")
 *
 * License/Terms of Use
 * Permission is hereby granted, free of charge and for the term of intellectual
 * property rights on the Software, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to use, copy, modify and
 * propagate free of charge, anywhere in the world, all or part of the Software
 * subject to the following mandatory conditions:
 *
 * -   The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Any failure to comply with the above shall automatically terminate the license
 * and be construed as a breach of these Terms of Use causing significant harm to
 * Capgemini.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, PEACEFUL ENJOYMENT,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of Capgemini shall not be used in
 * advertising or otherwise to promote the use or other dealings in this Software
 * without prior written authorization from Capgemini.
 *
 * These Terms of Use are subject to French law.
 *
 * IMPORTANT NOTICE: The WUIC software implements software components governed by
 * open source software licenses (BSD and Apache) of which CAPGEMINI is not the
 * author or the editor. The rights granted on the said software components are
 * governed by the specific terms and conditions specified by Apache 2.0 and BSD
 * licenses."
 */


package com.github.wuic.test;

import com.github.wuic.jee.WuicServletContextListener;
import com.github.wuic.util.IOUtils;
import io.undertow.Undertow;
import io.undertow.jsp.HackInstanceManager;
import io.undertow.jsp.JspServletBuilder;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.ListenerInfo;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jasper.deploy.JspPropertyGroup;
import org.apache.jasper.deploy.TagAttributeInfo;
import org.apache.jasper.deploy.TagInfo;
import org.apache.jasper.deploy.TagLibraryInfo;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * <p>
 * Runs the server when before the first test is loaded and stops it after the last one. Class must be annotated
 * with {@link WuicRunnerConfiguration}.
 * </p>
 *
 * @author Guillaume DROUET
 * @since 0.5.0
 * @version 0.1
 */
public class Server implements TestRule {

    /**
     * The servlet path for {@link com.github.wuic.servlet.WuicServlet}.
     */
    private static final String WUIC_SERVLET_PATH = "/wuic";

    /**
     * The mapping for {@link com.github.wuic.servlet.WuicServlet}.
     */
    private static final String WUIC_SERVLET_MAPPING = WUIC_SERVLET_PATH + "/*";

    /**
     * The logger.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * The running server.
     */
    private Undertow server;

    /**
     * The host.
     */
    private String host;

    /**
     * The port.
     */
    private int port;

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() {
                try {
                    before(description);
                    base.evaluate();
                } catch (Throwable t) {
                    log.error("Unable to apply statement", t);
                } finally {
                    after();
                }
            }
        };
    }

    /**
     * <p>
     * Runs the server.
     * </p>
     *
     * @param description the test description
     * @throws ClassNotFoundException if tested class is not in the classpath
     * @throws ServletException if the server fails to start
     */
    protected void before(final Description description) throws ClassNotFoundException, ServletException {

        // Look for configuration
        final Class<?> clazz = Class.forName(description.getClassName());
        final WuicRunnerConfiguration runnerConfiguration = clazz.getAnnotation(WuicRunnerConfiguration.class);

        if (runnerConfiguration == null) {
            throw new IllegalArgumentException();
        }

        final String webApplicationPath = runnerConfiguration.webApplicationPath();
        final Class<? extends Filter> installFilter = runnerConfiguration.installFilter();
        final Class<? extends HttpServlet> installServlet = runnerConfiguration.installServlet();
        final String welcomePage = runnerConfiguration.welcomePage();
        port = runnerConfiguration.port();
        host = runnerConfiguration.host();

        final URL resource = clazz.getResource(webApplicationPath);

        if (resource == null) {
            log.error(String.format("Web application path '%s' not found in classpath", webApplicationPath), new IllegalStateException());
        } else {
            // Creates the webapp handler
            final PathHandler root = new PathHandler();
            final io.undertow.servlet.api.ServletContainer container = io.undertow.servlet.api.ServletContainer.Factory.newInstance();

            final DeploymentInfo builder = new DeploymentInfo()
                    .setClassLoader(clazz.getClassLoader())
                    .setContextPath("/")
                    .setDeploymentName("ServletContainer.war")
                    .addListener(new ListenerInfo(WuicServletContextListener.class))
                    .setResourceManager(new FileResourceManager(new File(resource.getFile()), 100));

            if (!welcomePage.isEmpty()) {
                builder.addWelcomePage(welcomePage);
            }

            if (!NoFilter.class.equals(installFilter)) {
                builder.addFilter(new FilterInfo("Filter", installFilter));
                builder.addFilterUrlMapping("Filter", "/*", DispatcherType.REQUEST);
            }

            if (!NoServlet.class.equals(installServlet)) {
                builder.addServlet(Servlets.servlet("WuicServlet", installServlet).addMapping(WUIC_SERVLET_MAPPING));
            }

            final HashMap<String, JspPropertyGroup> jspPropertyGroups = new HashMap<String, JspPropertyGroup>();
            final JspPropertyGroup jspPropertyGroup = new JspPropertyGroup();
            jspPropertyGroups.put("", jspPropertyGroup);
            builder.addServlet(JspServletBuilder.createServlet("Default Jsp Servlet", "*.jsp"));
            final HashMap<String, TagLibraryInfo> tagLibs = new HashMap<String, TagLibraryInfo>();
            final TagInfo wuic = new TagInfo();
            wuic.setDisplayName("Web UI Compressor");
            wuic.setTagClassName("com.github.wuic.tag.WuicTag");
            wuic.setTagName("html-import");
            wuic.setBodyContent("empty");
            final TagAttributeInfo workflowId = new TagAttributeInfo();
            workflowId.setName("workflowId");
            wuic.addTagAttributeInfo(workflowId);

            final TagLibraryInfo wuicTagLibInfo = new TagLibraryInfo();
            wuicTagLibInfo.addTagInfo(wuic);
            wuicTagLibInfo.setPrefix("wuic");
            wuicTagLibInfo.setTlibversion("1.0.0");
            wuicTagLibInfo.setJspversion("2.1");
            wuicTagLibInfo.setUri("http://www.github.com/wuic");
            tagLibs.put("http://www.github.com/wuic", wuicTagLibInfo);

            final TagInfo wuicConfig = new TagInfo();
            wuicConfig.setDisplayName("Web UI Compressor Conf");
            wuicConfig.setTagClassName("com.github.wuic.tag.WuicXmlConfigurationTag");
            wuicConfig.setTagName("xml-configuration");
            wuicConfig.setBodyContent("tagdependent");

            final TagLibraryInfo wuicConfTagLibInfo = new TagLibraryInfo();
            wuicConfTagLibInfo.addTagInfo(wuicConfig);
            wuicConfTagLibInfo.setPrefix("wuic-conf");
            wuicConfTagLibInfo.setTlibversion("1.0.0");
            wuicConfTagLibInfo.setJspversion("2.1");
            wuicConfTagLibInfo.setUri("http://www.github.com/wuic/xml-conf");
            tagLibs.put("http://www.github.com/wuic/xml-conf", wuicConfTagLibInfo);

            JspServletBuilder.setupDeployment(builder, jspPropertyGroups, tagLibs, new HackInstanceManager());
            builder.addInitParameter(WuicServletContextListener.WUIC_SERVLET_CONTEXT_PARAM, WUIC_SERVLET_PATH);

            // Deploy then start
            final DeploymentManager manager = container.addDeployment(builder);
            manager.deploy();
            root.addPrefixPath(builder.getContextPath(), manager.start());
            server = Undertow.builder()
                    .addHttpListener(port, host)
                    .setHandler(root)
                    .build();
            server.start();
        }
    }

    /**
     * <p>
     * Stops the server.
     * </p>
     */
    protected void after() {
        server.stop();
    }

    /**
     * <p>
     * Helps to execute an HTTP request with GET method.
     * </p>
     *
     * @param path the path to execute.
     * @return the http response
     * @throws java.io.IOException if I/O error occurs
     */
    public HttpResponse get(final String path) throws IOException {
        final HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(IOUtils.mergePath("http://" + host + ":" + port, path));
        return client.execute(get);
    }
}
