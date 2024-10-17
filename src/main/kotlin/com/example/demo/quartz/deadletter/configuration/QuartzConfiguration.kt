package com.example.demo.quartz.deadletter.configuration

import com.example.demo.quartz.deadletter.CheckDeadLetterEventJob
import org.quartz.*
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.scheduling.quartz.SpringBeanJobFactory

@Configuration
class QuartzConfiguration(private val applicationContext: ApplicationContext) {

    @Bean
    fun jobDetail(): JobDetail {
        return JobBuilder.newJob(CheckDeadLetterEventJob::class.java)
            .withIdentity("checkDeadLetterEventJob")
            .storeDurably()
            .build()
    }

    @Bean
    fun trigger(jobDetail: JobDetail): Trigger {
        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity("checkDeadLetterEventTrigger")
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(1)
                    .repeatForever()
            )
            .build()
    }

    @Bean
    fun springBeanJobFactory(): SpringBeanJobFactory {
        val jobFactory = SpringBeanJobFactory()
        jobFactory.setApplicationContext(applicationContext)
        return jobFactory
    }

    @Bean
    fun schedulerFactoryBean(jobDetail: JobDetail, trigger: Trigger, springBeanJobFactory: SpringBeanJobFactory): SchedulerFactoryBean {
        val factory = SchedulerFactoryBean()
        factory.setJobDetails(jobDetail)
        factory.setTriggers(trigger)
        factory.setJobFactory(springBeanJobFactory)
        return factory
    }
}