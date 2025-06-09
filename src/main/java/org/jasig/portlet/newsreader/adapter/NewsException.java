/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.newsreader.adapter;

/**
 * <p>NewsException class.</p>
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 * @since 5.1.1
 */
public class NewsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for NewsException.</p>
     */
    public NewsException() {
        super();
    }

    /**
     * <p>Constructor for NewsException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param cause a {@link java.lang.Throwable} object
     */
    public NewsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for NewsException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public NewsException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for NewsException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object
     */
    public NewsException(Throwable cause) {
        super(cause);
    }

}
