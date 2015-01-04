/*
 * "Copyright (c) 2015   Capgemini Technology Services (hereinafter "Capgemini")
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
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * Tests the servlet container runner.
 * </p>
 *
 * @author Guillaume DROUET
 * @since 0.5.0
 * @version 0.1
 */
@WuicRunnerConfiguration(welcomePage = "index.html",
        webApplicationPath = "/testthetest",
        installFilter = FilterTest.class)
public class FilterContainerTest {

    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 4000;

    /**
     * Needs to be incremented by filter.
     */
    public static final AtomicInteger FILTER_COUNT = new AtomicInteger(0);

    /**
     * The server running during tests.
     */
    @ClassRule
    public static Server server = new Server();

    /**
     * The current configuration.
     */
    @Rule
    public WuicConfiguration configuration = new WuicConfiguration.Adapter();

    /**
     * <p>
     * Executes a basic HTTP request and reads the response.
     * </p>
     *
     * @throws java.io.IOException if any I/O error occurs
     */
    @Test
    public void basicHttpGetTest() throws IOException {
        final String expect = "Hello World";
        final byte[] buff = new byte[BUFFER_LEN];
        server.get("/wuic/test").getEntity().getContent().read(buff, 0, buff.length);
        final String res = new String(buff);
        Assert.assertTrue(res, res.contains(expect));
        Assert.assertNotEquals(0, FILTER_COUNT.get());
    }
}
