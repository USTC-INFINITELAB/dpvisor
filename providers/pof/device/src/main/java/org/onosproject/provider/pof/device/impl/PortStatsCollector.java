/*
 * Copyright 2015-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.provider.pof.device.impl;

import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.onlab.util.Timer;
import org.onosproject.floodlightpof.protocol.OFMessage;
import org.onosproject.floodlightpof.protocol.OFType;
import org.onosproject.pof.controller.PofSwitch;
import org.onosproject.pof.controller.RoleState;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Sends Port Stats Request and collect the port statistics with a time interval.
 */
public class PortStatsCollector implements TimerTask {

    // TODO: Refactoring is required using ScheduledExecutorService

    private final HashedWheelTimer timer = Timer.getTimer();
    private final PofSwitch sw;
    private final Logger log = getLogger(getClass());
    private int refreshInterval;
    private final java.util.concurrent.atomic.AtomicLong xidAtomic = new java.util.concurrent.atomic.AtomicLong(1);

    private Timeout timeout;
    private volatile boolean stopped;

    /**
     * Creates a PortStatsCollector object.
     *
     * @param sw Open Flow switch
     * @param interval time interval for collecting port statistic
     */
    public PortStatsCollector(PofSwitch sw, int interval) {
        this.sw = sw;
        this.refreshInterval = interval;
    }

    @Override
    public void run(Timeout to) throws Exception {
        if (stopped || timeout.isCancelled()) {
            return;
        }
        log.trace("Collecting stats for {}", sw.getStringId());

        //sendPortStatistic();

        if (!stopped && !timeout.isCancelled()) {
            log.trace("Scheduling stats collection in {} seconds for {}",
                    this.refreshInterval, this.sw.getStringId());
            timeout.getTimer().newTimeout(this, refreshInterval, java.util.concurrent.TimeUnit.SECONDS);
        }
    }

    synchronized void adjustPollInterval(int pollInterval) {
        this.refreshInterval = pollInterval;
        // task.cancel();
        // task = new InternalTimerTask();
        // timer.scheduleAtFixedRate(task, pollInterval * SECONDS, pollInterval * 1000);
    }

    /**
     * Sends port statistic request to switch.
     */
    private void sendPortStatistic() {
        if (sw.getRole() != RoleState.MASTER) {
            return;
        }
        Long statsXid = xidAtomic.getAndIncrement();
        OFMessage statsRequest = sw.factory().getOFMessage(OFType.PORT_STATUS);
        sw.sendMsg(statsRequest);
    }

    /**
     * Starts the collector.
     */
    public synchronized void start() {
        log.info("Starting Port Stats collection thread for {}", sw.getStringId());
        stopped = false;
        timeout = timer.newTimeout(this, 1, java.util.concurrent.TimeUnit.SECONDS);
    }

    /**
     * Stops the collector.
     */
    public synchronized void stop() {
        log.info("Stopping Port Stats collection thread for {}", sw.getStringId());
        stopped = true;
        timeout.cancel();
    }
}