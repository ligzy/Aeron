/*
 * Copyright 2014 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.aeron.driver;

import uk.co.real_logic.aeron.common.BackoffIdleStrategy;
import uk.co.real_logic.aeron.common.BitUtil;
import uk.co.real_logic.aeron.common.IdleStrategy;
import uk.co.real_logic.aeron.common.TimerWheel;
import uk.co.real_logic.aeron.common.concurrent.broadcast.BroadcastBufferDescriptor;
import uk.co.real_logic.aeron.common.concurrent.ringbuffer.RingBufferDescriptor;

import java.util.concurrent.TimeUnit;

import static java.lang.Integer.getInteger;
import static java.lang.Long.getLong;
import static java.lang.System.getProperty;

/**
 * Configuration options for the media driver.
 */
public class Configuration
{
    /**
     * Byte buffer size (in bytes) for reads
     */
    public static final String READ_BUFFER_SZ_PROP_NAME = "aeron.rcv.buffer.size";

    /**
     * Size (in bytes) of the log buffers for publication terms
     */
    public static final String TERM_BUFFER_SZ_PROP_NAME = "aeron.term.buffer.size";

    /**
     * Size (in bytes) of the log buffers for terms for incoming connections
     */
    public static final String TERM_BUFFER_SZ_MAX_PROP_NAME = "aeron.term.buffer.size.max";

    /**
     * Size (in bytes) of the command buffers between threads
     */
    public static final String COMMAND_BUFFER_SZ_PROP_NAME = "aeron.command.buffer.size";

    /**
     * Size (in bytes) of the conductor buffers between the media driver and the client
     */
    public static final String CONDUCTOR_BUFFER_SZ_PROP_NAME = "aeron.conductor.buffer.size";

    /**
     * Size (in bytes) of the broadcast buffers from the media driver to the clients
     */
    public static final String TO_CLIENTS_BUFFER_SZ_PROP_NAME = "aeron.clients.buffer.size";

    /**
     * Property name for size of the memory mapped buffers for the counters file
     */
    public static final String COUNTER_BUFFERS_SZ_PROP_NAME = "aeron.dir.counters.size";

    /**
     * Property name for size of the initial window
     */
    public static final String INITIAL_WINDOW_SIZE_PROP_NAME = "aeron.rcv.initial.window.size";

    /**
     * Property name for status message timeout in nanoseconds
     */
    public static final String STATUS_MESSAGE_TIMEOUT_PROP_NAME = "aeron.rcv.status.message.timeout";

    /**
     * Property name for SO_RCVBUF setting on UDP sockets
     */
    public static final String SOCKET_RCVBUF_SZ_PROP_NAME = "aeron.socket.so_rcvbuf";

    /**
     * Property name for SO_SNDBUF setting on UDP sockets
     */
    public static final String SOCKET_SNDBUF_SZ_PROP_NAME = "aeron.socket.so_sndbuf";

    /**
     * Property name for linger timeout for publications
     */
    public static final String PUBLICATION_LINGER_PROP_NAME = "aeron.publication.linger.timeout";

    /**
     * Property name for window limit on publication side
     */
    public static final String PUBLICATION_TERM_WINDOW_SIZE_PROP_NAME = "aeron.publication.term.window.size";
    public static final int PUBLICATION_TERM_WINDOW_SZ = getInteger(PUBLICATION_TERM_WINDOW_SIZE_PROP_NAME, 0);

    /**
     * Property name for window limit on subscription side
     */
    public static final String SUBSCRIPTION_TERM_WINDOW_SIZE_PROP_NAME = "aeron.subscription.term.window.size";
    public static final int SUBSCRIPTION_TERM_WINDOW_SZ = getInteger(SUBSCRIPTION_TERM_WINDOW_SIZE_PROP_NAME, 0);

    /**
     * Property name for client liveness timeout
     */
    public static final String CLIENT_LIVENESS_TIMEOUT_PROP_NAME = "aeron.client.liveness.timeout";

    /**
     * Property name for connection liveness timeout
     */
    public static final String CONNECTION_LIVENESS_TIMEOUT_PROP_NAME = "aeron.connection.liveness.timeout";

    /**
     * Property name for data loss rate
     */
    public static final String DATA_LOSS_RATE_PROP_NAME = "aeron.debug.data.loss.rate";

    /**
     * Property name for data loss seed
     */
    public static final String DATA_LOSS_SEED_PROP_NAME = "aeron.debug.data.loss.seed";

    /**
     * Property name for control loss rate
     */
    public static final String CONTROL_LOSS_RATE_PROP_NAME = "aeron.debug.control.loss.rate";

    /**
     * Property name for control loss seed
     */
    public static final String CONTROL_LOSS_SEED_PROP_NAME = "aeron.debug.control.loss.seed";

    /**
     * Default byte buffer size for reads
     */
    public static final int READ_BYTE_BUFFER_SZ_DEFAULT = 4096;
    public static final int READ_BYTE_BUFFER_SZ = getInteger(READ_BUFFER_SZ_PROP_NAME, READ_BYTE_BUFFER_SZ_DEFAULT);

    /**
     * Default term buffer size.
     */
    public static final int TERM_BUFFER_SZ_DEFAULT = 16 * 1024 * 1024;

    /**
     * Default term buffer size max.
     */
    public static final int TERM_BUFFER_SZ_MAX_DEFAULT = 16 * 1024 * 1024;

    /**
     * Default buffer size for command buffers between threads
     */
    public static final int COMMAND_BUFFER_SZ_DEFAULT = 1024 * 1024;
    public static final int COMMAND_BUFFER_SZ = getInteger(COMMAND_BUFFER_SZ_PROP_NAME, COMMAND_BUFFER_SZ_DEFAULT);

    /**
     * Default buffer size for conductor buffers between the media driver and the client
     */
    public static final int CONDUCTOR_BUFFER_SZ_DEFAULT = 1024 * 1024 + RingBufferDescriptor.TRAILER_LENGTH;
    public static final int CONDUCTOR_BUFFER_SZ = getInteger(CONDUCTOR_BUFFER_SZ_PROP_NAME, CONDUCTOR_BUFFER_SZ_DEFAULT);

    /**
     * Default buffer size for broadcast buffers from the media driver to the clients
     */
    public static final int TO_CLIENTS_BUFFER_SZ_DEFAULT = 1024 * 1024 + BroadcastBufferDescriptor.TRAILER_LENGTH;
    public static final int TO_CLIENTS_BUFFER_SZ = getInteger(TO_CLIENTS_BUFFER_SZ_PROP_NAME, TO_CLIENTS_BUFFER_SZ_DEFAULT);

    /**
     * Size of the memory mapped buffers for the counters file
     */
    public static final int COUNTERS_BUFFER_SZ_DEFAULT = 64 * 1024 * 1024;
    public static final int COUNTER_BUFFERS_SZ = getInteger(COUNTER_BUFFERS_SZ_PROP_NAME, COUNTERS_BUFFER_SZ_DEFAULT);

    /**
     * Default group size estimate for NAK delay randomization
     */
    public static final int NAK_GROUPSIZE_DEFAULT = 10;

    /**
     * Default group RTT estimate for NAK delay randomization in msec
     */
    public static final int NAK_GRTT_DEFAULT = 10;

    /**
     * Default max backoff for NAK delay randomization in msec
     */
    public static final long NAK_MAX_BACKOFF_DEFAULT = TimeUnit.MILLISECONDS.toNanos(60);

    /**
     * Default Unicast NAK delay in nanoseconds
     */
    public static final long NAK_UNICAST_DELAY_DEFAULT_NS = TimeUnit.MILLISECONDS.toNanos(60);

    /**
     * Default delay for retransmission of data for unicast
     */
    public static final long RETRANS_UNICAST_DELAY_DEFAULT_NS = TimeUnit.NANOSECONDS.toNanos(0);

    /**
     * Default delay for linger for unicast
     */
    public static final long RETRANS_UNICAST_LINGER_DEFAULT_NS = TimeUnit.MILLISECONDS.toNanos(60);

    /**
     * Default max number of active retransmissions per Term
     */
    public static final int MAX_RETRANSMITS_DEFAULT = 16;

    /**
     * Default initial window size for flow control sender to receiver purposes
     * <p>
     * Sizing of Initial Window
     * <p>
     * RTT (LAN) = 100 usec
     * Throughput = 10 Gbps
     * <p>
     * Buffer = Throughput * RTT
     * Buffer = (10*1000*1000*1000/8) * 0.0001 = 125000
     * Round to 128KB
     */
    public static final int INITIAL_WINDOW_SIZE_DEFAULT = 128 * 1024;

    /**
     * Max timeout between SMs.
     */
    public static final long STATUS_MESSAGE_TIMEOUT_DEFAULT_NS = TimeUnit.MILLISECONDS.toNanos(200);

    /**
     * 0 means use OS default.
     */
    public static final int SOCKET_RCVBUF_SZ_DEFAULT = 128 * 1024;
    public static final int SOCKET_RCVBUF_SZ = getInteger(SOCKET_RCVBUF_SZ_PROP_NAME, SOCKET_RCVBUF_SZ_DEFAULT);

    /**
     * 0 means use OS default.
     */
    public static final int SOCKET_SNDBUF_SZ_DEFAULT = 0;
    public static final int SOCKET_SNDBUF_SZ = getInteger(SOCKET_SNDBUF_SZ_PROP_NAME, SOCKET_SNDBUF_SZ_DEFAULT);

    /**
     * Time for publications to linger before cleanup
     */
    public static final long PUBLICATION_LINGER_DEFAULT_NS = TimeUnit.SECONDS.toNanos(5);
    public static final long PUBLICATION_LINGER_NS = getLong(PUBLICATION_LINGER_PROP_NAME, PUBLICATION_LINGER_DEFAULT_NS);

    /**
     * Timeout for client liveness in nanoseconds
     */
    public static final long CLIENT_LIVENESS_TIMEOUT_DEFAULT_NS = TimeUnit.MILLISECONDS.toNanos(5000);
    public static final long CLIENT_LIVENESS_TIMEOUT_NS = getLong(
        CLIENT_LIVENESS_TIMEOUT_PROP_NAME, CLIENT_LIVENESS_TIMEOUT_DEFAULT_NS);
    /**
     * Timeout for connection liveness in nanoseconds
     */
    public static final long CONNECTION_LIVENESS_TIMEOUT_DEFAULT_NS = TimeUnit.SECONDS.toNanos(10);
    public static final long CONNECTION_LIVENESS_TIMEOUT_NS = getLong(
        CONNECTION_LIVENESS_TIMEOUT_PROP_NAME, CONNECTION_LIVENESS_TIMEOUT_DEFAULT_NS);

    /**
     * ticksPerWheel for TimerWheel in conductor thread
     */
    public static final int CONDUCTOR_TICKS_PER_WHEEL = 1024;

    /**
     * tickDuration (in MICROSECONDS) for TimerWheel in conductor thread
     */
    public static final int CONDUCTOR_TICK_DURATION_US = 10 * 1000;

    /**
     * {@link IdleStrategy} to be employed by agents.
     */
    public static final String AGENT_IDLE_STRATEGY_PROP_NAME = "aeron.agent.idle.strategy";
    public static final String AGENT_IDLE_STRATEGY = getProperty(
        AGENT_IDLE_STRATEGY_PROP_NAME, "uk.co.real_logic.aeron.common.BackoffIdleStrategy");

    public static final long AGENT_IDLE_MAX_SPINS = 20;
    public static final long AGENT_IDLE_MAX_YIELDS = 50;
    public static final long AGENT_IDLE_MIN_PARK_NS = TimeUnit.NANOSECONDS.toNanos(1);
    public static final long AGENT_IDLE_MAX_PARK_NS = TimeUnit.MICROSECONDS.toNanos(100);

    /** Capacity for the command queues used between driver agents. */
    public static final int CMD_QUEUE_CAPACITY = 1024;

    /** Timeout on cleaning up pending SETUP state on subscriber */
    public static final long PENDING_SETUPS_TIMEOUT_NS = TimeUnit.MILLISECONDS.toNanos(1000);

    /** Timeout between SETUP frames for publications during initial setup phase */
    public static final long PUBLICATION_SETUP_TIMEOUT_NS = TimeUnit.MILLISECONDS.toNanos(100);

    /** Timeout between heartbeats for publications */
    public static final long PUBLICATION_HEARTBEAT_TIMEOUT_NS = TimeUnit.MILLISECONDS.toNanos(200);

    /**
     * {@link SenderFlowControl} to be employed for unicast channels.
     */
    public static final String SENDER_UNICAST_FLOW_CONTROL_STRATEGY_PROP_NAME =
        "aeron.sender.unicast.flow.control.strategy";
    public static final String SENDER_UNICAST_FLOW_CONTROL_STRATEGY = getProperty(
        SENDER_UNICAST_FLOW_CONTROL_STRATEGY_PROP_NAME, "uk.co.real_logic.aeron.driver.UnicastSenderFlowControl");

    /**
     * {@link SenderFlowControl} to be employed for multicast channels.
     */
    public static final String SENDER_MULTICAST_FLOW_CONTROL_STRATEGY_PROP_NAME =
        "aeron.sender.multicast.flow.control.strategy";
    public static final String SENDER_MULTICAST_FLOW_CONTROL_STRATEGY = getProperty(
        SENDER_MULTICAST_FLOW_CONTROL_STRATEGY_PROP_NAME, "uk.co.real_logic.aeron.driver.UnicastSenderFlowControl");

    /** Length of the maximum transport unit of the media driver's protocol */
    public static final String MTU_LENGTH_PROP_NAME = "aeron.mtu.length";
    public static final int MTU_LENGTH_DEFAULT = 4096;

    /**
     * How far ahead the receiver can get from the subscriber position.
     *
     * @param termCapacity to be used when {@link #SUBSCRIPTION_TERM_WINDOW_SZ} is not set.
     * @return the size to be used for the subscription window.
     */
    public static int subscriptionTermWindowSize(final int termCapacity)
    {
        return 0 != SUBSCRIPTION_TERM_WINDOW_SZ ? SUBSCRIPTION_TERM_WINDOW_SZ : termCapacity / 2;
    }

    /**
     * How far ahead the publisher can get from the sender position.
     *
     * @param termCapacity to be used when {@link #PUBLICATION_TERM_WINDOW_SZ} is not set.
     * @return the size to be used for the publication window.
     */
    public static int publicationTermWindowSize(final int termCapacity)
    {
        return 0 != PUBLICATION_TERM_WINDOW_SZ ? PUBLICATION_TERM_WINDOW_SZ : termCapacity / 2;
    }

    /**
     * Validate the the term buffer size is a power of two.
     *
     * @param size of the term buffer
     */
    public static void validateTermBufferSize(final int size)
    {
        if (!BitUtil.isPowerOfTwo(size))
        {
            throw new IllegalStateException("Term buffer size must be a positive power of 2: " + size);
        }
    }

    /**
     * Validate that the initial window size is suitably greater than MTU.
     *
     * @param initialWindowSize to be validated.
     * @param mtuLength against which to validate.
     */
    public static void validateInitialWindowSize(final int initialWindowSize, final int mtuLength)
    {
        if (mtuLength > initialWindowSize)
        {
            throw new IllegalStateException("Initial window size must be >= to MTU length: " + mtuLength);
        }
    }

    public static IdleStrategy eventReaderIdleStrategy()
    {
        return new BackoffIdleStrategy(0, 0, AGENT_IDLE_MIN_PARK_NS, AGENT_IDLE_MAX_PARK_NS);
    }

    public static IdleStrategy agentIdleStrategy()
    {
        switch (AGENT_IDLE_STRATEGY)
        {
            case "uk.co.real_logic.aeron.common.BackoffIdleStrategy":
                return new BackoffIdleStrategy(
                    AGENT_IDLE_MAX_SPINS, AGENT_IDLE_MAX_YIELDS, AGENT_IDLE_MIN_PARK_NS, AGENT_IDLE_MAX_PARK_NS);

            default:
                try
                {
                    return (IdleStrategy)Class.forName(AGENT_IDLE_STRATEGY).newInstance();
                }
                catch (final Exception ex)
                {
                    throw new RuntimeException(ex);
                }
        }
    }

    public static SenderFlowControl unicastSenderFlowControlStrategy()
    {
        try
        {
            return (SenderFlowControl)Class.forName(SENDER_UNICAST_FLOW_CONTROL_STRATEGY).newInstance();
        }
        catch (final Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public static SenderFlowControl multicastSenderFlowControlStrategy()
    {
        try
        {
            return (SenderFlowControl)Class.forName(SENDER_MULTICAST_FLOW_CONTROL_STRATEGY).newInstance();
        }
        catch (final Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public static TimerWheel newConductorTimerWheel()
    {
        return new TimerWheel(CONDUCTOR_TICK_DURATION_US, TimeUnit.MICROSECONDS, CONDUCTOR_TICKS_PER_WHEEL);
    }

    public static int termBufferSize()
    {
        return getInteger(TERM_BUFFER_SZ_PROP_NAME, TERM_BUFFER_SZ_DEFAULT);
    }

    public static int termBufferSizeMax()
    {
        return getInteger(TERM_BUFFER_SZ_MAX_PROP_NAME, TERM_BUFFER_SZ_MAX_DEFAULT);
    }

    public static int initialWindowSize()
    {
        return getInteger(INITIAL_WINDOW_SIZE_PROP_NAME, INITIAL_WINDOW_SIZE_DEFAULT);
    }

    public static long statusMessageTimeout()
    {
        return getLong(STATUS_MESSAGE_TIMEOUT_PROP_NAME, STATUS_MESSAGE_TIMEOUT_DEFAULT_NS);
    }

    public static long dataLossSeed()
    {
        return getLong(DATA_LOSS_SEED_PROP_NAME, -1);
    }

    public static long controlLossSeed()
    {
        return getLong(CONTROL_LOSS_SEED_PROP_NAME, -1);
    }

    public static double dataLossRate()
    {
        return Double.parseDouble(getProperty(DATA_LOSS_RATE_PROP_NAME, "0.0"));
    }

    public static double controlLossRate()
    {
        return Double.parseDouble(getProperty(CONTROL_LOSS_RATE_PROP_NAME, "0.0"));
    }

    public static LossGenerator createLossGenerator(final double lossRate, final long lossSeed)
    {
        if (0 == lossRate)
        {
            return (address, length) -> false;
        }

        return new RandomLossGenerator(lossRate, lossSeed);
    }
}
