/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.newsreader.mvc.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.ResourceFilter;
import javax.portlet.filter.ResourceResponseWrapper;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.WebUtils;

/**
 * ETag filter code copied from org.springframework.web.filter.ShallowEtagHeaderFilter.
 * 
 * @version $Revision$
 */
public class ShallowEtagHeaderPortletFilter implements ResourceFilter {

    private static String HEADER_IF_NONE_MATCH = "If-None-Match";

    protected final Log logger = LogFactory.getLog(getClass());

    public void destroy() {
        // nothing to see here
    }

    public void init(FilterConfig filterConfig) throws PortletException {
        // nothing to see here
    }

    public void doFilter(ResourceRequest request, ResourceResponse response,
            FilterChain filterChain) throws IOException, PortletException {

        ShallowEtagResponseWrapper responseWrapper = new ShallowEtagResponseWrapper(response);
        filterChain.doFilter(request, responseWrapper);

        byte[] body = responseWrapper.toByteArray();
        int statusCode = responseWrapper.getStatusCode();

        if (isEligibleForEtag(request, responseWrapper, statusCode, body)) {
            
            // generate a response ETag
            String responseETag = generateETagHeaderValue(body);
            
            // set the response both directly on the header and in the cache control
            response.setProperty(ResourceResponse.ETAG, responseETag);
            response.getCacheControl().setETag(responseETag);

            String requestETag = request.getProperty(HEADER_IF_NONE_MATCH);
            
            // TODO: get the ETag directly from the ResourceRequest API
            //  String requestETag = request.getETag();
            
            if (responseETag.equals(requestETag)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("ETag [" + responseETag + "] equal to If-None-Match, sending 304");
                }
                response.setContentLength(0);
                response.setProperty(ResourceResponse.HTTP_STATUS_CODE, String.valueOf(HttpServletResponse.SC_NOT_MODIFIED));
            }
            else {
                if (logger.isTraceEnabled()) {
                    logger.trace("ETag [" + responseETag + "] not equal to If-None-Match [" + requestETag +
                            "], sending normal response");
                }
                copyBodyToResponse(body, response);
            }
        }
        else {
            if (logger.isTraceEnabled()) {
                logger.trace("Response with status code [" + statusCode + "] not eligible for ETag");
            }
            copyBodyToResponse(body, response);
        }

    }

    private void copyBodyToResponse(byte[] body, ResourceResponse response) throws IOException {
        response.setContentLength(body.length);
        if (body.length > 0) {
            FileCopyUtils.copy(body, response.getPortletOutputStream());
        }
    }

    /**
     * Indicates whether the given request and response are eligible for ETag generation.
     *
     * <p>Default implementation returns {@code true} for response status codes in the {@code 2xx} series.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param responseStatusCode the HTTP response status code
     * @param responseBody the response body
     * @return {@code true} if eligible for ETag generation; {@code false} otherwise
     */
    protected boolean isEligibleForEtag(ResourceRequest request,
            ResourceResponse response,
            int responseStatusCode,
            byte[] responseBody) {
        return (responseStatusCode >= 200 && responseStatusCode < 300);
    }

    /**
     * Generate the ETag header value from the given response body byte array.
     *
     * <p>The default implementation generates an MD5 hash.
     *
     * @param bytes the response bdoy as byte array
     * @return the ETag header value
     * @see org.springframework.util.DigestUtils
     */
    protected String generateETagHeaderValue(byte[] bytes) {
        StringBuilder builder = new StringBuilder("\"0");
        DigestUtils.appendMd5DigestAsHex(bytes, builder);
        builder.append('"');
        return builder.toString();
    }

    /**
     * {@link HttpServletRequest} wrapper that buffers all content written to the {@linkplain #getOutputStream() output
     * stream} and {@linkplain #getWriter() writer}, and allows this content to be retrieved via a {@link #toByteArray()
     * byte array}.
     */
    private static class ShallowEtagResponseWrapper extends ResourceResponseWrapper {

        private final ByteArrayOutputStream content = new ByteArrayOutputStream();

        private final ServletOutputStream outputStream = new ResponseServletOutputStream();

        private PrintWriter writer;

        private int statusCode = HttpServletResponse.SC_OK;

        private ShallowEtagResponseWrapper(ResourceResponse response) {
            super(response);
        }
        
        @Override
        public void setProperty(String name, String value) {
            super.setProperty(name, value);
            if (ResourceResponse.HTTP_STATUS_CODE.equals(name)) {
                this.statusCode = Integer.parseInt(value);
            }
        }

        @Override
        public ServletOutputStream getPortletOutputStream() {
            return this.outputStream;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (this.writer == null) {
                String characterEncoding = getCharacterEncoding();
                this.writer = (characterEncoding != null ? new ResponsePrintWriter(characterEncoding) :
                        new ResponsePrintWriter(WebUtils.DEFAULT_CHARACTER_ENCODING));
            }
            return this.writer;
        }

        @Override
        public void resetBuffer() {
            this.content.reset();
        }

        @Override
        public void reset() {
            super.reset();
            resetBuffer();
        }

        private int getStatusCode() {
            return statusCode;
        }

        private byte[] toByteArray() {
            return this.content.toByteArray();
        }

        private class ResponseServletOutputStream extends ServletOutputStream {

            @Override
            public void write(int b) throws IOException {
                content.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                content.write(b, off, len);
            }

        }

        private class ResponsePrintWriter extends PrintWriter {

            private ResponsePrintWriter(String characterEncoding) throws UnsupportedEncodingException {
                super(new OutputStreamWriter(content, characterEncoding));
            }

            @Override
            public void write(char buf[], int off, int len) {
                super.write(buf, off, len);
                super.flush();
            }

            @Override
            public void write(String s, int off, int len) {
                super.write(s, off, len);
                super.flush();
            }

            @Override
            public void write(int c) {
                super.write(c);
                super.flush();
            }

        }

    }

}
