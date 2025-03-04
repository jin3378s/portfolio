package test.portfolio.eventsourcing

import autoparams.BrakeBeforeAnnotation
import autoparams.kotlin.AutoKotlinSource
import org.springframework.beans.factory.annotation.Autowired

@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION
)
@AutoKotlinSource
@BrakeBeforeAnnotation(Autowired::class)
annotation class AutoJpaEventSourcingSource
