package com.event.discovery.agent.task.realtime;

import com.event.discovery.agent.apps.eventDiscovery.EventDiscoveryContainer;
import com.event.discovery.agent.framework.exception.DiscoverySupportCode;
import com.event.discovery.agent.framework.exception.EventDiscoveryAgentException;
import com.event.discovery.agent.framework.model.AppStatus;
import com.event.discovery.agent.framework.model.JobStatus;
import com.event.discovery.agent.framework.model.NormalizedEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class AsynchronousMessageProcessor extends RealtimeMessage implements Runnable {
    public AsynchronousMessageProcessor(final EventDiscoveryContainer eventDiscoveryContainer,
                                        final LinkedBlockingQueue<NormalizedEvent> normalizedMessageProcessingQueue) {
        super(eventDiscoveryContainer, normalizedMessageProcessingQueue);
    }

    @Override
    public void run() {
        try {
            poll();
        } catch (final Exception e) {
            String errorMsg = "Failed to process messages: " + e.getMessage();
            log.error(errorMsg, e);
            getJob().setStatus(JobStatus.ERROR);
            getJob().setError(errorMsg);
            getEventDiscoveryApplication().updateAppStatus(AppStatus.ERROR);
            throw new EventDiscoveryAgentException(DiscoverySupportCode.DISCOVERY_ERROR_101, e.getMessage());
        }
    }

    public void poll() {
        NormalizedEvent event;
        while (!exit) {
            if (normalizedMessageProcessingQueue.peek() != null) {
                event = normalizedMessageProcessingQueue.poll();
                // Infer schema and add to message
                getEventDiscoveryApplication().mapNormalizedMessageToNormalizedEvent(event);
            }
        }

    }
}
