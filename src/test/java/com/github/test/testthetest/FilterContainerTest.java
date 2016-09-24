/*
 * Copyright (c) 2016   The authors of WUIC
 *
 * License/Terms of Use
 * Permission is hereby granted, free of charge and for the term of intellectual
 * property rights on the Software, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to use, copy, modify and
 * propagate free of charge, anywhere in the world, all or part of the Software
 * subject to the following mandatory conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, PEACEFUL ENJOYMENT,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package com.github.test.testthetest;

import com.github.wuic.test.Server;
import com.github.wuic.test.WuicConfiguration;
import com.github.wuic.test.WuicRunnerConfiguration;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * Tests the servlet container runner.
 * </p>
 *
 * @author Guillaume DROUET
 * @since 0.5.0
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
     * Timeout.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(60);

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
