/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.client.latency;

/**
 * 延迟机制接口规范
 * 为了解决broker宕机的情况：
 * 首先，NameServer检测Broker是否可用是有延迟的，最短一次心跳检测间隔10s；
 * 其次，NameServer不会检测到Broker宕机后马上推送消息给消息生产者，而是消息生产者“每隔30秒更新一次路由信息”，
 * 所以生产者最快感知Broker最新的路由信息也需要30s。
 * 如果能引入一种机制，在Broker宕机期间，如果一次消息发送失败后，可以将该Broker暂时排除在消息队列的选择范围中。
 * @param <T>
 */
public interface LatencyFaultTolerance<T> {
    /**
     * 更新失败条目
     * @param name brokerName
     * @param currentLatency 当前延迟时间
     * @param notAvailableDuration 不可用的持续时间（就是broker要被规避的时间）
     */
    void updateFaultItem(final T name, final long currentLatency, final long notAvailableDuration);

    /**
     * 判断是否可用,一般都是broker
     * @param name
     * @return
     */
    boolean isAvailable(final T name);

    /**
     * 移除broker
     * @param name
     */
    void remove(final T name);

    /**
     * 至少选出一个可用的broker
     * @return
     */
    T pickOneAtLeast();
}
