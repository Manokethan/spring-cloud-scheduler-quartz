/*
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.dataflow.autoconfigure.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.dataflow.server.config.OnLocalPlatform;
import org.springframework.cloud.dataflow.server.config.features.SchedulerConfiguration;
import org.springframework.cloud.deployer.spi.local.LocalDeployerProperties;
import org.springframework.cloud.deployer.spi.local.LocalTaskLauncher;
import org.springframework.cloud.deployer.spi.scheduler.ScheduleInfo;
import org.springframework.cloud.deployer.spi.scheduler.ScheduleRequest;
import org.springframework.cloud.deployer.spi.scheduler.Scheduler;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.cloud.scheduler.spi.quartz.QuartzScheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.List;

/**
 * @author Publio Estupiñán
 */
@Configuration
@EnableConfigurationProperties(LocalDeployerProperties.class)
@Conditional({ OnLocalPlatform.class, SchedulerConfiguration.SchedulerConfigurationPropertyChecker.class })
public class LocalSchedulerAutoConfiguration {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public TaskLauncher taskLauncher(LocalDeployerProperties properties) {
		return new LocalTaskLauncher(properties);
	}

	@Autowired
	TaskLauncher taskLauncher;

	@Bean
	public Scheduler localScheduler() {
		QuartzScheduler quartzScheduler = new QuartzScheduler(
				applicationContext.getBean(SchedulerFactoryBean.class).getScheduler()
		);

		return new Scheduler() {
			@Override
			public void schedule(ScheduleRequest scheduleRequest) {
				quartzScheduler.schedule(scheduleRequest);
			}

			@Override
			public void unschedule(String scheduleName) {
				quartzScheduler.unschedule(scheduleName);
			}

			@Override
			public List<ScheduleInfo> list(String taskDefinitionName) {
				return quartzScheduler.list(taskDefinitionName);
			}

			@Override
			public List<ScheduleInfo> list() {
				return quartzScheduler.list();
			}
		};
	}
}
