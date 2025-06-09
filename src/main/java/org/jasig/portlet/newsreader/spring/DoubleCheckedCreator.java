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
package org.jasig.portlet.newsreader.spring;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of double-checked locking for object creation using a {@link java.util.concurrent.locks.ReadWriteLock}
 *
 * @author Eric Dalquist
 * @version $Revision$
 * @since 5.1.1
 */
public abstract class DoubleCheckedCreator<T> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final ReadWriteLock readWriteLock;
    protected final Lock readLock;
    protected final Lock writeLock;

    /**
     * <p>Constructor for DoubleCheckedCreator.</p>
     */
    public DoubleCheckedCreator() {
        this(new ReentrantReadWriteLock());
    }

    /**
     * <p>Constructor for DoubleCheckedCreator.</p>
     *
     * @param readWriteLock a {@link java.util.concurrent.locks.ReadWriteLock} object
     */
    public DoubleCheckedCreator(ReadWriteLock readWriteLock) {
        Validate.notNull(readWriteLock, "readWriteLock can not be null");
        this.readWriteLock = readWriteLock;
        this.readLock = this.readWriteLock.readLock();
        this.writeLock = this.readWriteLock.writeLock();
    }

    /**
     * <p>create.</p>
     *
     * @param args Arguments to use when creating the object
     * @return A newly created object
     */
    protected abstract T create(Object... args);

    /**
     * <p>retrieve.</p>
     *
     * @param args Arguments to use when retrieving the object
     * @return An existing object if available
     */
    protected abstract T retrieve(Object... args);

    /**
     * The default impl returns true if value is null.
     *
     * @param value The object to validate
     * @param args Arguments to use when validating the object
     * @return true if the object is invalid and should be created, false if not.
     */
    protected boolean invalid(T value, Object... args) {
        return value == null;
    }

    /**
     * Double checking retrieval/creation of an object
     *
     * @param args Optional arguments to pass to {@link #retrieve(Object...)}, {@link #create(Object...)}, and {@link #invalid(Object, Object...)}.
     * @return A retrieved or created object.
     */
    public final T get(Object... args) {
        //Grab a read lock to try retrieving the object
        this.readLock.lock();
        try {

            //See if the object already exists
            T value = this.retrieve(args);
            if (this.invalid(value, args)) {
                //Switch to a write lock
                this.readLock.unlock();
                this.writeLock.lock();
                
                //Check if it exists now, create it if it doesn't
                try {
                    value = this.retrieve(args);
                
                    if (this.invalid(value, args)) {
                        value = this.create(args);
                        
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Created new Object='" + value + "'");
                        }
                    }
                    else if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Using retrieved Object='" + value + "'");
                    }
                }
                finally {
                    //switch back to the read lock
                    this.readLock.lock();
                    this.writeLock.unlock();
                }
            }
            else if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using retrieved Object='" + value + "'");
            }

            return value;
        }
        finally {
            this.readLock.unlock();
        }
    }
}
