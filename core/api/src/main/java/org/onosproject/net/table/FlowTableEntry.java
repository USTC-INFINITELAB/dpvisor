/*
 * Copyright 2014-present Open Networking Laboratory
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
package org.onosproject.net.table;


/**
 * Represents a generalized match &amp; action pair to be applied to
 * an infrastucture device.
 */
public interface FlowTableEntry extends FlowTable {


    enum FlowTableState {

        /**
         * Indicates that this table has been submitted for addition.
         * Not necessarily in the switch.
         */
        PENDING_ADD,

        /**
         * Table has been added which means it is in the switch.
         */
        ADDED,

        /**
         * Flow table has been marked for removal, might still be in switch.
         */
        PENDING_REMOVE,

        /**
         * Flow table has been removed from switch and can be purged.
         */
        REMOVED,

        /**
         * Indicates that the installation of this flow table has failed.
         */
        FAILED
    }

    /**
     * Returns the flow table state.
     *
     * @return flow table state
     */
    FlowTableState state();

    /**
     * Returns the number of milliseconds this flow rule has been applied.
     *
     * @return number of millis
     */
    long life();

    /**
     * Returns the number of packets this flow table has matched.
     *
     * @return number of packets
     */
    long packets();

    /**
     * Returns the number of bytes this flow table has matched.
     *
     * @return number of bytes
     */
    long bytes();

    // TODO: consider removing this attribute
    /**
     * When this flow table was last deemed active.
     * @return epoch time of last activity
     */
    long lastSeen();

    /**
     * Indicates the error type.
     * @return an integer value of the error
     */
    int errType();

    /**
     * Indicates the error code.
     * @return an integer value of the error
     */
    int errCode();

}
