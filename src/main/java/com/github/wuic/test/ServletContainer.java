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

import com.github.wuic.util.IOUtils;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.io.IOException;

/**
 * This class runner could be used with @RunWith annotation to execute unit test inside a servlet container.
 * The class wraps undertow to deploy web application.
 *
 * @author Guillaume DROUET
 * @since 0.5.0
 * @version 0.1
 */
public class ServletContainer extends BlockJUnit4ClassRunner {

    /**
     * The default host name.
     */
    public static final String DEFAULT_HOST = "localhost";

    /**
     * The default port.
     */
    public static final int DEFAULT_PORT = 9876;

    /**
     * The host name.
     */
    public static String host = DEFAULT_HOST;

    /**
     * The listened port.
     */
    public static int port = DEFAULT_PORT;

    /**
     * The handler that must be set by unit tests.
     */
    public static HttpHandler handler;

    /**
     * The root handler.
     */
    public static HttpHandler rootHandler = new HttpHandler() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            handler.handleRequest(exchange);
        }
    };

    /**
     * <p>
     * Builds a new instance.
     * </p>
     *
     * @param klass the run class
     * @throws InitializationError if instantiation fails
     */
    public ServletContainer(final Class<?> klass) throws InitializationError {
        super(klass);
    }

    /**
     * <p>
     * Helps to execute an HTTP request with GET method.
     * </p>
     *
     * @param path the path to execute.
     * @return the http response
     * @throws IOException if I/O error occurs
     */
    public static HttpResponse get(final String path) throws IOException {
        final HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(IOUtils.mergePath("http://" + host + ":" + port, path));
        return client.execute(get);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final RunNotifier notifier) {
        final Undertow server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(rootHandler)
                .build();

        notifier.addListener(new RunListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void testRunStarted(final Description description) throws Exception {
                // We should deploy custom configuration detected from annotations here.
                super.testRunStarted(description);
            }
        });

        server.start();
        super.run(notifier);
    }
}
