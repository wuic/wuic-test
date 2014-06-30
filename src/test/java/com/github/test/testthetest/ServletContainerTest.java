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


package com.github.test.testthetest;

import com.github.wuic.test.Server;
import com.github.wuic.test.WuicConfiguration;
import com.github.wuic.test.WuicRunnerConfiguration;
import com.github.wuic.util.IOUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * <p>
 * Tests the servlet container runner.
 * </p>
 *
 * @author Guillaume DROUET
 * @since 0.5.0
 * @version 0.1
 */
@RunWith(JUnit4.class)
@WuicRunnerConfiguration(welcomePage = "index.html", webApplicationPath = "/testthetest")
public class ServletContainerTest {

    /**
     * The server running during tests.
     */
    @ClassRule
    public static Server server = new Server();

    /**
     * The current configuration.
     */
    @Rule
    public WuicConfiguration configuration = new WuicConfiguration();

    /**
     * <p>
     * Executes a basic HTTP request and reads the response.
     * </p>
     *
     * @throws IOException if any I/O error occurs
     */
    @Test
    public void basicHttpGetTest() throws IOException {
        final String content = IOUtils.readString(new InputStreamReader(server.get("/").getEntity().getContent()));
        Assert.assertTrue(content, content.contains("Hello World"));
    }

    /**
     * <p>
     * Executes a basic HTTP request and reads the response.
     * </p>
     *
     * @throws Exception if test fails
     */
    @Test
    public void basicWuicXmlTest() throws Exception {
        configuration.setWuicXmlReader(new FileReader(getClass().getResource("/testthetest/wuic.xml").getFile()));
        final String content = IOUtils.readString(new InputStreamReader(server.get("/wuic/heap/aggregate.css").getEntity().getContent()));
        Assert.assertTrue(content, content.contains(".cssclass {}"));
    }
}