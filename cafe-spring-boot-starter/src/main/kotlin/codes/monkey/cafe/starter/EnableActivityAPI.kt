package codes.monkey.cafe.starter

import codes.monkey.cafe.starter.activity.ActivityConfig
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(ActivityConfig::class)
annotation class EnableActivityAPI
