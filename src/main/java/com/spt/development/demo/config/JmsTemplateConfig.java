package com.spt.development.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

@Configuration
public class JmsTemplateConfig {

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) throws JMSException {
        final JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);

        return jmsTemplate;
    }
}
