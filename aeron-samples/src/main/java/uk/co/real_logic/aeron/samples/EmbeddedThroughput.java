/*
 * Copyright 2014 - 2015 Real Logic Ltd.
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
package uk.co.real_logic.aeron.samples;

import uk.co.real_logic.aeron.Aeron;
import uk.co.real_logic.aeron.Publication;
import uk.co.real_logic.aeron.Subscription;
import uk.co.real_logic.aeron.CommonContext;
import uk.co.real_logic.aeron.driver.RateReporter;
import uk.co.real_logic.aeron.logbuffer.FragmentHandler;
import uk.co.real_logic.aeron.driver.MediaDriver;
import uk.co.real_logic.aeron.driver.ThreadingMode;
import uk.co.real_logic.agrona.concurrent.BusySpinIdleStrategy;
import uk.co.real_logic.agrona.concurrent.IdleStrategy;
import uk.co.real_logic.agrona.concurrent.NoOpIdleStrategy;
import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;
import uk.co.real_logic.agrona.console.ContinueBarrier;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static uk.co.real_logic.aeron.samples.SamplesUtil.rateReporterHandler;

public class EmbeddedThroughput
{
    private static final String CHANNEL = SampleConfiguration.CHANNEL;
    private static final int STREAM_ID = SampleConfiguration.STREAM_ID;
    private static final int MESSAGE_LENGTH = SampleConfiguration.MESSAGE_LENGTH;
    private static final long NUMBER_OF_MESSAGES = SampleConfiguration.NUMBER_OF_MESSAGES;
    private static final long LINGER_TIMEOUT_MS = SampleConfiguration.LINGER_TIMEOUT_MS;
    private static final int FRAGMENT_COUNT_LIMIT = SampleConfiguration.FRAGMENT_COUNT_LIMIT;

    private static final UnsafeBuffer ATOMIC_BUFFER = new UnsafeBuffer(ByteBuffer.allocateDirect(MESSAGE_LENGTH));
    private static final IdleStrategy OFFER_IDLE_STRATEGY = new BusySpinIdleStrategy();

    public static void main(final String[] args) throws Exception
    {
        final MediaDriver.Context ctx = new MediaDriver.Context()
            .threadingMode(ThreadingMode.DEDICATED)
            .conductorIdleStrategy(new NoOpIdleStrategy())
            .receiverIdleStrategy(new NoOpIdleStrategy())
            .senderIdleStrategy(new NoOpIdleStrategy())
            .dirsDeleteOnExit(true);

        final RateReporter reporter = new RateReporter(TimeUnit.SECONDS.toNanos(1), SamplesUtil::printRate);
        final FragmentHandler rateReporterHandler = rateReporterHandler(reporter);
        final ExecutorService executor = Executors.newFixedThreadPool(2);

        final Aeron.Context context = new Aeron.Context();
        final String embeddedDirName = CommonContext.generateEmbeddedDirName();
        ctx.dirName(embeddedDirName);
        context.dirName(embeddedDirName);

        System.out.format("Aeron dir '%s'\n", embeddedDirName);

        final AtomicBoolean running = new AtomicBoolean(true);

        try (final MediaDriver ignore = MediaDriver.launch(ctx);
             final Aeron aeron = Aeron.connect(context);
             final Publication publication = aeron.addPublication(CHANNEL, STREAM_ID);
             final Subscription subscription = aeron.addSubscription(CHANNEL, STREAM_ID))
        {
            executor.execute(reporter);
            executor.execute(
                () -> SamplesUtil.subscriberLoop(rateReporterHandler, FRAGMENT_COUNT_LIMIT, running).accept(subscription));

            final ContinueBarrier barrier = new ContinueBarrier("Execute again?");

            do
            {
                System.out.format(
                    "\nStreaming %,d messages of size %d bytes to %s on stream Id %d\n",
                    NUMBER_OF_MESSAGES, MESSAGE_LENGTH, CHANNEL, STREAM_ID);

                long backPressureCount = 0;

                for (long i = 0; i < NUMBER_OF_MESSAGES; i++)
                {
                    ATOMIC_BUFFER.putLong(0, i);

                    while (publication.offer(ATOMIC_BUFFER, 0, ATOMIC_BUFFER.capacity()) < 0)
                    {
                        backPressureCount++;
                        OFFER_IDLE_STRATEGY.idle(0);
                    }
                }

                System.out.println("Done streaming. backPressureRatio=" + ((double)backPressureCount / NUMBER_OF_MESSAGES));

                if (0 < LINGER_TIMEOUT_MS)
                {
                    System.out.println("Lingering for " + LINGER_TIMEOUT_MS + " milliseconds...");
                    Thread.sleep(LINGER_TIMEOUT_MS);
                }

            }
            while (barrier.await());

            running.set(false);
            reporter.halt();
            executor.shutdown();
        }
    }
}
