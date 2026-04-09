package com.debpro.linkedin.posts_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.internals.Topic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic postCreatedTopic(){
        return new NewTopic("post-created-topic", 3, (short)1);
    }

    @Bean
    public NewTopic postLikedTopic(){
        return new NewTopic("post-liked-topic", 3, (short)1);
    }
}
