/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.scheduler.spi.quartz;

import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.deployer.spi.core.AppDefinition;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class QuartsSchedulerJob extends QuartzJobBean {

	private static final Logger logger = LoggerFactory.getLogger(QuartsSchedulerJob.class);

	private TaskLauncher taskLauncher;

	private AppDefinition appDefinition;

	private Resource resource;

	private String taskName;

	private Map<String, String> taskDeploymentProperties;

	private List<String> commandLineArgs;

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setTaskDeploymentProperties(Map<String, String> taskDeploymentProperties) {
		this.taskDeploymentProperties = taskDeploymentProperties;
	}

	public void setCommandLineArgs(List<String> commandLineArgs) {
		this.commandLineArgs = commandLineArgs;
	}

	public void setTaskLauncher(TaskLauncher taskLauncher) {
		this.taskLauncher = taskLauncher;
	}

	public void setAppDefinition(AppDefinition appDefinition) {
		this.appDefinition = appDefinition;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		logger.debug("launching scheduled quartz job {}", taskName);
		AppDeploymentRequest appDeploymentRequest = new AppDeploymentRequest(appDefinition,	resource,
				taskDeploymentProperties);
		taskLauncher.launch(appDeploymentRequest);
	}
}
