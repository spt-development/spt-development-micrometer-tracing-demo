package com.spt.development.demo.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

@EnableJms
@Configuration
public class JmsConfig {
    public static final String AUDIT_EVENT_QUEUE = "jms.queue.audit-event-queue";

    public static final String JMS_CONTAINER_FACTORY = "auditJmsContainerFactory";

    private static final String CONCURRENCY = "5";

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) throws JMSException {
        final JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);

        return jmsTemplate;
    }
}
