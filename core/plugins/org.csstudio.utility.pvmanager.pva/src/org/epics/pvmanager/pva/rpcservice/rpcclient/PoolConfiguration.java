/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.epics.pvmanager.pva.rpcservice.rpcclient;

/**
 * A list of properties that are configurable for a rpc client pool.
 * @author fhanik (tomcat connection pool)
 * @author dkumar (modified for a rpc client pool implementation)
 */

public interface PoolConfiguration {

  public static final int DEFAULT_MAX_ACTIVE = 100;

  /**
   * rpc clients that have been abandoned (timed out) wont get closed and reported up unless the number of rpc clients in use are
   * above the percentage defined by abandonWhenPercentageFull.
   * The value should be between 0-100.
   * The default value is 0, which implies that rpc clients are eligible for
   * closure as soon as removeAbandonedTimeout has been reached.
   * @param percentage a value between 0 and 100 to indicate when rpc clients that have been abandoned/timed out are considered abandoned
   */
  public void setAbandonWhenPercentageFull(int percentage);


  /**
   * rpc clients that have been abandoned (timed out) wont get closed and reported up unless the number of rpc clients in use are
   * above the percentage defined by abandonWhenPercentageFull.
   * The value should be between 0-100.
   * The default value is 0, which implies that rpc clients are eligible for
   * closure as soon as removeAbandonedTimeout has been reached.
   * @return percentage - a value between 0 and 100 to indicate when rpc clients that have been abandoned/timed out are considered abandoned
   */
  public int getAbandonWhenPercentageFull();


  /**
   * Returns the number of rpc clients that will be established when the rpc client pool is started.
   * Default value is 10
   * @return number of rpc clients to be started when pool is started
   */
  public int getInitialSize();


  /**
   * Set the number of rpc clients that will be established when the rpc client pool is started.
   * Default value is 10.
   * If this value exceeds {@link #setMaxActive(int)} it will automatically be lowered.
   * @param initialSize the number of rpc clients to be established.
   *
   */
  public void setInitialSize(int initialSize);


  /**
   * boolean flag to set if stack traces should be logged for application code which abandoned a rpc client.
   * Logging of abandoned rpc clients adds overhead for every rpc client borrow because a stack trace has to be generated.
   * The default value is false.
   * @return true if the rpc client pool logs stack traces when rpc clients are borrowed from the pool.
   */
  public boolean isLogAbandoned();


  /**
   * boolean flag to set if stack traces should be logged for application code which abandoned a rpc client.
   * Logging of abandoned rpc clients adds overhead for every rpc client borrow because a stack trace has to be generated.
   * The default value is false.
   * @param logAbandoned set to true if stack traces should be recorded.
   */
  public void setLogAbandoned(boolean logAbandoned);


  /**
   * The maximum number of active rpc clients that can be allocated from this pool at the same time. The default value is 100
   * @return the maximum number of rpc clients used by this pool
   */
  public int getMaxActive();

  /**
   * The maximum number of active rpc clients that can be allocated from this pool at the same time. The default value is 100
   * @param maxActive hard limit for number of managed rpc clients by this pool
   */
  public void setMaxActive(int maxActive);


  /**
   * The maximum number of rpc clients that should be kept in the idle pool if {@link #isPoolSweeperEnabled()} returns true.
   * If the If {@link #isPoolSweeperEnabled()} returns true, then the idle pool can grow up to {@link #getMaxActive}
   * and will be shrunk according to {@link #getMinEvictableIdleTimeMillis()} setting.
   * Default value is maxActive:100
   * @return the maximum number of idle rpc clients.
   */
  public int getMaxIdle();

  /**
   * The maximum number of rpc clients that should be kept in the idle pool if {@link #isPoolSweeperEnabled()} returns false.
   * If the If {@link #isPoolSweeperEnabled()} returns true, then the idle pool can grow up to {@link #getMaxActive}
   * and will be shrunk according to {@link #getMinEvictableIdleTimeMillis()} setting.
   * Default value is maxActive:100
   * @param maxIdle the maximum size of the idle pool
   */
  public void setMaxIdle(int maxIdle);


  /**
   * The maximum number of milliseconds that the pool will wait (when there are no available rpc clients) and the
   * {@link #getMaxActive} has been reached) for a rpc client to be returned
   * before throwing an exception. Default value is 30000 (30 seconds)
   * @return the number of milliseconds to wait for a rpc client to become available if the pool is maxed out.
   */
  public int getMaxWait();


  /**
   * The maximum number of milliseconds that the pool will wait (when there are no available rpc clients and the
   * {@link #getMaxActive} has been reached) for a rpc client to be returned
   * before throwing an exception. Default value is 30000 (30 seconds)
   * @param maxWait the maximum number of milliseconds to wait.
   */
  public void setMaxWait(int maxWait);

  /**
   * The minimum amount of time an object must sit idle in the pool before it is eligible for eviction.
   * The default value is 60000 (60 seconds).
   * @return the minimum amount of idle time in milliseconds before a rpc client is considered idle and eligible for eviction.
   */
  public int getMinEvictableIdleTimeMillis();


  /**
   * The minimum amount of time an object must sit idle in the pool before it is eligible for eviction.
   * The default value is 60000 (60 seconds).
   * @param minEvictableIdleTimeMillis the number of milliseconds a rpc client must be idle to be eligible for eviction.
   */
  public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis);


  /**
   * The minimum number of established rpc clients that should be kept in the pool at all times.
   * The rpc client pool can shrink below this number if validation fail and rpc clients get closed.
   * Default value is derived from {@link #getInitialSize()} (also see {@link #setTestWhileIdle(boolean)}
   * The idle pool will not shrink below this value during an eviction run, hence the number of actual rpc clients
   * can be between {@link #getMinIdle()} and somewhere between {@link #getMaxIdle()} and {@link #getMaxActive()}
   * @return the minimum number of idle or established rpc clients
   */
  public int getMinIdle();


  /**
   * The minimum number of established rpc clients that should be kept in the pool at all times.
   * The rpc client pool can shrink below this number if validation fail and rpc clients get closed.
   * Default value is derived from {@link #getInitialSize()} (also see {@link #setTestWhileIdle(boolean)}
   * The idle pool will not shrink below this value during an eviction run, hence the number of actual rpc clients
   * can be between {@link #getMinIdle()} and somewhere between {@link #getMaxIdle()} and {@link #getMaxActive()}
   *
   * @param minIdle the minimum number of idle or established rpc clients
   */
  public void setMinIdle(int minIdle);

  /**
   * boolean flag to remove abandoned rpc clients if they exceed the removeAbandonedTimout.
   * If set to true a rpc client is considered abandoned and eligible for removal if it has
   * been in use longer than the {@link #getRemoveAbandonedTimeout()} and the condition for
   * {@link #getAbandonWhenPercentageFull()} is met.
   * Setting this to true can recover rpc clients from applications that fail to close a rpc client.
   * See also {@link #isLogAbandoned()} The default value is false.
   * @return true if abandoned rpc clients can be closed and expelled out of the pool
   */
  public boolean isRemoveAbandoned();


  /**
   * boolean flag to remove abandoned rpc clients if they exceed the removeAbandonedTimout.
   * If set to true a rpc client is considered abandoned and eligible for removal if it has
   * been in use longer than the {@link #getRemoveAbandonedTimeout()} and the condition for
   * {@link #getAbandonWhenPercentageFull()} is met.
   * Setting this to true can recover rpc clients from applications that fail to close a rpc client.
   * See also {@link #isLogAbandoned()} The default value is false.
   * @param removeAbandoned set to true if abandoned rpc clients can be closed and expelled out of the pool
   */
  public void setRemoveAbandoned(boolean removeAbandoned);


  /**
   * The time in seconds before a rpc client can be considered abandoned.
   * @param removeAbandonedTimeout the time in seconds before a used rpc client can be considered abandoned
   */
  public void setRemoveAbandonedTimeout(int removeAbandonedTimeout);


  /**
   * The time in seconds before a rpc client can be considered abandoned.
   * @return the time in seconds before a used rpc client can be considered abandoned
   */
  public int getRemoveAbandonedTimeout();


  /**
   * The indication of whether objects will be validated before being borrowed from the pool.
   * If the object fails to validate, it will be dropped from the pool, and we will attempt to borrow another.
   * Default value is false
   * In order to have a more efficient validation, see {@link #setValidationInterval(long)}
   * @return true if the rpc client is to be validated upon borrowing a rpc client from the pool
   * @see #getValidationInterval()
   */
  public boolean isTestOnBorrow();


  /**
   * The indication of whether objects will be validated before being borrowed from the pool.
   * If the object fails to validate, it will be dropped from the pool, and we will attempt to borrow another.
   * Default value is false
   * In order to have a more efficient validation, see {@link #setValidationInterval(long)}
   * @param testOnBorrow set to true if validation should take place before a rpc client is handed out to the application
   * @see #getValidationInterval()
   */
  public void setTestOnBorrow(boolean testOnBorrow);


  /**
   * The indication of whether objects will be validated after being returned to the pool.
   * If the object fails to validate, it will be dropped from the pool.
   * Default value is false
   * In order to have a more efficient validation, see {@link #setValidationInterval(long)}
   * @return true if validation should take place after a rpc client is returned to the pool
   * @see #getValidationInterval()
   */
  public boolean isTestOnReturn();


  /**
   * The indication of whether objects will be validated after being returned to the pool.
   * If the object fails to validate, it will be dropped from the pool.
   * Default value is false
   * In order to have a more efficient validation, see {@link #setValidationInterval(long)}
   * @param testOnReturn true if validation should take place after a rpc client is returned to the pool
   * @see #getValidationInterval()
   */
  public void setTestOnReturn(boolean testOnReturn);


  /**
   * Set to true if validation should take place while the rpc client is idle.
   * @return true if validation should take place during idle checks
   * @see #setTimeBetweenEvictionRunsMillis(int)
   */
  public boolean isTestWhileIdle();


  /**
   * Set to true if validation should take place while the rpc client is idle.
   * @param testWhileIdle true if validation should take place during idle checks
   * @see #setTimeBetweenEvictionRunsMillis(int)
   */
  public void setTestWhileIdle(boolean testWhileIdle);


  /**
   * The number of milliseconds to sleep between runs of the idle rpc client validation, abandoned cleaner
   * and idle pool resizing. This value should not be set under 1 second.
   * It dictates how often we check for idle, abandoned rpc clients, and how often we validate idle rpc client and resize the idle pool.
   * The default value is 5000 (5 seconds)
   * @return the sleep time in between validations in milliseconds
   */
  public int getTimeBetweenEvictionRunsMillis();


  /**
   * The number of milliseconds to sleep between runs of the idle rpc client validation, abandoned cleaner
   * and idle pool resizing. This value should not be set under 1 second.
   * It dictates how often we check for idle, abandoned rpc clients, and how often we validate idle rpc client and resize the idle pool.
   * The default value is 5000 (5 seconds)
   * @param timeBetweenEvictionRunsMillis the sleep time in between validations in milliseconds
   */
  public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis);


  /**
   * Avoid excess validation, only run validation at most at this frequency - time in milliseconds.
   * If a rpc client is due for validation, but has been validated previously
   * within this interval, it will not be validated again.
   * The default value is 30000 (30 seconds).
   * @return the validation interval in milliseconds
   */
  public long getValidationInterval();


  /**
   * avoid excess validation, only run validation at most at this frequency - time in milliseconds.
   * If a rpc client is due for validation, but has been validated previously
   * within this interval, it will not be validated again.
   * The default value is 30000 (30 seconds).
   * @param validationInterval the validation interval in milliseconds
   */
  public void setValidationInterval(long validationInterval);


   /**
   * Returns true if the pool sweeper is enabled for the rpc client pool.
   * The pool sweeper is enabled if any settings that require async intervention in the pool are turned on
   * <pre>
   boolean result = getTimeBetweenEvictionRunsMillis()&gt;0;
   result = result &amp;&amp; (isRemoveAbandoned() &amp;&amp; getRemoveAbandonedTimeout()&gt;0);
   result = result || (isTestWhileIdle();
   return result;
   </pre>
   *
   * @return true if a background thread is or will be enabled for this pool
   */
  public boolean isPoolSweeperEnabled();


  /**
   * Time in milliseconds to keep this rpc client  alive even when used.
   * When a rpc client is returned to the pool, the pool will check to see if the
   * ((now - time-when-connected) &gt; maxAge) has been reached, and if so,
   * it closes the rpc client rather than returning it to the pool.
   * The default value is 0, which implies that rpc clients will be left open and no
   * age check will be done upon returning the rpc client to the pool.
   * @return the time in milliseconds a rpc client will be open for when used
   */
  public long getMaxAge();


  /**
   * Time in milliseconds to keep this rpc client alive even when used.
   * When a rpc client is returned to the pool, the pool will check to see if the
   * ((now - time-when-connected) &gt; maxAge) has been reached, and if so,
   * it closes the rpc client rather than returning it to the pool.
   * The default value is 0, which implies that rpc clients will be left open and no
   * age check will be done upon returning the rpc client to the pool.
   * @param maxAge the time in milliseconds a rpc client will be open for when used
   */
  public void setMaxAge(long maxAge);


  /**
   * Return true if a lock should be used when operations are performed on the rpc client object.
   * Should be set to false unless you plan to have a background thread of your own doing idle and abandon checking
   * such as JMX clients. If the pool sweeper is enabled, then the lock will automatically be used regardless of this setting.
   * @return true if a lock is used.
   */
  public boolean getUseLock();


  /**
   * Set to true if a lock should be used when operations are performed on the rpc client object.
   * Should be set to false unless you plan to have a background thread of your own doing idle and abandon checking
   * such as JMX clients. If the pool sweeper is enabled, then the lock will automatically be used regardless of this setting.
   * @param useLock set to true if a lock should be used on rpc client operations
   */
  public void setUseLock(boolean useLock);


  /**
   * Similar to {@link #setRemoveAbandonedTimeout(int)} but instead of treating the rpc client
   * as abandoned, and potentially closing the rpc client, this simply logs the warning if
   * {@link #isLogAbandoned()} returns true. If this value is equal or less than 0, no suspect
   * checking will be performed. Suspect checking only takes place if the timeout value is larger than 0 and
   * the rpc client was not abandoned or if abandon check is disabled. If a rpc client is suspect a WARN message gets
   * logged.
   * @param seconds - the amount of time in seconds that has to pass before a rpc client is marked suspect.
   */
  public void setSuspectTimeout(int seconds);


  /**
   * Returns the time in seconds to pass before a rpc client is marked an abanoned suspect.
   * Any value lesser than or equal to 0 means the check is disabled.
   * @return Returns the time in seconds to pass before a rpc client is marked an abanoned suspect.
   */
  public int getSuspectTimeout();

}