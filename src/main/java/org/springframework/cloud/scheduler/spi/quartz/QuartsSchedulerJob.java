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

import org.quartz.JobExecutionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.deployer.spi.core.AppDefinition;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class QuartsSchedulerJob extends QuartzJobBean {

	@Autowired
	ApplicationContext applicationContext;

	private static final Logger logger = LoggerFactory.getLogger(QuartsSchedulerJob.class);
	private String taskName;
	private String definitionName;
	private Map definitionProperties;
	private Map deploymentProperties;
	private List commandlineArguments;
	private String resourceDescription;
	private String resourceFilename;
	private File resourceFile;
	private long resourceContentLength;
	private boolean resourceExists;
	private URI resourceURI;
	private URL resourceURL;
	private boolean resourceIsOpen;
	private boolean resourceIsReadable;
	private long resourceLastModified;

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setDefinitionName(String definitionName) {
		this.definitionName = definitionName;
	}

	public void setDefinitionProperties(Map definitionProperties) {
		this.definitionProperties = definitionProperties;
	}

	public void setDeploymentProperties(Map deploymentProperties) {
		this.deploymentProperties = deploymentProperties;
	}

	public void setCommandlineArguments(List commandlineArguments) {
		this.commandlineArguments = commandlineArguments;
	}

	public void setResourceDescription(String resourceDescription) {
		this.resourceDescription = resourceDescription;
	}

	public void setResourceFilename(String resourceFilename) {
		this.resourceFilename = resourceFilename;
	}

	public void setResourceFile(File resourceFile) {
		this.resourceFile = resourceFile;
	}

	public void setResourceContentLength(long resourceContentLength) {
		this.resourceContentLength = resourceContentLength;
	}

	public void setResourceExists(boolean resourceExists) {
		this.resourceExists = resourceExists;
	}

	public void setResourceURI(URI resourceURI) {
		this.resourceURI = resourceURI;
	}

	public void setResourceURL(URL resourceURL) {
		this.resourceURL = resourceURL;
	}

	public void setResourceIsOpen(boolean resourceIsOpen) {
		this.resourceIsOpen = resourceIsOpen;
	}

	public void setResourceIsReadable(boolean resourceIsReadable) {
		this.resourceIsReadable = resourceIsReadable;
	}

	public void setResourceLastModified(long resourceLastModified) {
		this.resourceLastModified = resourceLastModified;
	}

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		logger.debug("launching scheduled quartz job {}", taskName);

		AppDefinition appDefinition = new AppDefinition(definitionName, definitionProperties);

		Resource resource = new Resource() {
			@Override
			public boolean exists() {
				return resourceExists;
			}

			@Override
			public boolean isReadable() {
				return resourceIsReadable;
			}

			@Override
			public boolean isOpen() {
				return resourceIsOpen;
			}

			@Override
			public URL getURL() throws IOException {
				return resourceURL;
			}

			@Override
			public URI getURI() throws IOException {
				return resourceURI;
			}

			@Override
			public File getFile() throws IOException {
				return resourceFile;
			}

			@Override
			public long contentLength() throws IOException {
				return resourceContentLength;
			}

			@Override
			public long lastModified() throws IOException {
				return resourceLastModified;
			}

			@Override
			public Resource createRelative(String s) throws IOException {
				return null;
			}

			@Override
			public String getFilename() {
				return resourceFilename;
			}

			@Override
			public String getDescription() {
				return resourceDescription;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return null;
			}
		};

		AppDeploymentRequest appDeploymentRequest = new AppDeploymentRequest(
				appDefinition,
				resource,
				deploymentProperties,
				commandlineArguments);

		applicationContext.getBean(TaskLauncher.class).launch(appDeploymentRequest);
	}
}
