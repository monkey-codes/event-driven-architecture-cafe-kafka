package codes.monkey.cafe.starter

import codes.monkey.cafe.starter.kafka.KafkaConfig
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(KafkaConfig::class)
annotation class EnableKafkaEventIntegration
