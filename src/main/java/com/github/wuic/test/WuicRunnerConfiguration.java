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

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Annotation used to configure test runner.
 * </p>
 *
 * @author Guillaume DROUET
 * @version 1.0
 * @since 0.5.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface WuicRunnerConfiguration {

    /**
     * Default port.
     */
    int DEFAULT_PORT = 8080;

    /**
     * <p>
     * Indicates the {@link com.github.wuic.servlet.HtmlParserFilter} to be installed.
     * The filter mapping will be '/*'.
     * </p>
     *
     * <p>
     * No filter should be installed by default.
     * </p>
     *
     * @return filter to install, {@link NoFilter} if nothing should be installed
     */
    Class<? extends Filter> installFilter() default NoFilter.class;

    /**
     * <p>
     * Indicates the {@code HttpServlet} to be installed.
     * The servlet mapping will be '/wuic/*'.
     * </p>
     *
     * <p>
     * No servlet will be installed by default.
     * </p>
     *
     * @return servlet to install, {@link NoServlet} if nothing should be installed
     */
    Class<? extends HttpServlet> installServlet() default NoServlet.class;

    /**
     * <p>
     * Returns the classpath entry corresponding to the web application to be deployed.
     * </p>
     *
     * @return the path
     */
    String webApplicationPath() default "/";

    /**
     * <p>
     * Indicates any welcome page.
     * </p>
     *
     * @return the welcome page
     */
    String welcomePage() default "";

    /**
     * <p>
     * Indicates the listened port.
     * </p>
     *
     * @return the listened port (8080 by default)
     */
    int port() default DEFAULT_PORT;

    /**
     * <p>
     * Indicates the deployed domain.
     * </p>
     *
     * @return the domain (localhost by default)
     */
    String host() default "localhost";
}
