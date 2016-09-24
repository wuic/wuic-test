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


package com.github.wuic.test;

import org.junit.rules.ExternalResource;

import javax.xml.bind.JAXBException;
import java.io.Reader;

/**
 * <p>
 * This rule performs per-test configurations.
 * </p>
 *
 * @author Guillaume DROUET
 * @since 0.5.0
 */
public abstract class WuicConfiguration extends ExternalResource {

    /**
     * <p>
     * Adapter class.
     * </p>
     *
     * @author Guillaume DROUET
     * @since 0.5.0
     */
    public static final class Adapter extends WuicConfiguration {

        /**
         * {@inheritDoc}
         */
        @Override
        public void clearConfiguration() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setWuicXmlReader(final Reader wuicXmlFile) throws JAXBException {
        }
    }

    /*
     * {@inheritDoc}
     */
    @Override
    protected void after() {
        clearConfiguration();
    }

    /**
     * <p>
     * Clears any configuration after test execution.
     * </p>
     */
    public abstract void clearConfiguration();

    /**
     * <p>
     * Sets the current configuration accessible with the given reader.
     * </p>
     *
     * @param wuicXmlFile the XML configuration reader
     * @throws JAXBException if XML is not correct
     */
    public abstract void setWuicXmlReader(final Reader wuicXmlFile) throws JAXBException;
}
