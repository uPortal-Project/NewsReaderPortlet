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
package org.jasig.portlet.newsreader.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.portlet.PortletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This Spring-managed bean is responsible for extracting the userId from the
 * PortletRequest.  The NewsReaderPortlet uses userId as a foreign key when it
 * stores data.  Normally this process is simple -- all authenticated users use
 * <code>PortletRequest.getRemoteUser</code> as a userId.  Unauthenticated
 * users, however, need a different strategy.  Users who aren't authenticated
 * will have <code>null</code> for both <code>getRemoteUser</code> and the
 * <code>USER_INFO</code> map.  We need to differentiate them (in the case of
 * multiple guest users) based on the role(s) they belong to.
 *
 * @author bgonzalez
 * @since 5.1.1
 */
@Service
public class UserIdService {

    private static final String GUEST_USER_ID_PREFIX = "g_";

    @Autowired
    private RolesService rolesService;

    private MessageDigest md5;

    /**
     * <p>init.</p>
     */
    @PostConstruct
    public void init() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            final String msg = "Failed to create the MD5 MessageDigest";
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * <p>getUserId.</p>
     *
     * @param req a {@link javax.portlet.PortletRequest} object
     * @return a {@link java.lang.String} object
     */
    public String getUserId(PortletRequest req) {
        final String rslt = req.getRemoteUser() != null
                ? req.getRemoteUser()  // Authenticated users use REMOTE_USER
                : calculateGuestUserIdHash(req);
        return rslt;
    }

    private String calculateGuestUserIdHash(PortletRequest req) {

        final StringBuilder rolesString = new StringBuilder();

        // The calculated userId will be based on roles...
        final Set<String> roles = rolesService.getUserRoles(req);
        // that are sorted into their natural order.
        final List<String> sortedRoles= new ArrayList<>(roles);
        Collections.sort(sortedRoles);

        for (String s : sortedRoles) {
            if (rolesString.length() != 0) {
                // Seperate role hashes with a token...
                rolesString.append("-");
            }
            rolesString.append(s);
        }

        // Width on the userId column is 50 characters, so we need it to be
        // short.  MD5 produces a sequence of characters that is always 32 long.
        final byte[] digest = md5.digest(rolesString.toString().getBytes());
        final String hex = DigestUtils.md2Hex(digest);

        return GUEST_USER_ID_PREFIX + hex;

    }

}
