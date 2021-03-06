package org.apache.maven.shared.runtime;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.List;

import org.apache.maven.project.MavenProject;

/**
 * Provides methods to introspect the current Maven runtime environment.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public interface MavenRuntime
{
    // fields -----------------------------------------------------------------

    /**
     * The Plexus role for this component.
     */
    String ROLE = MavenRuntime.class.getName();

    // public methods ---------------------------------------------------------

    /**
     * Gets the properties for the specified class's Maven project.
     * 
     * @param klass
     *            the class to introspect
     * @return the project properties for the specified class's Maven project
     * @throws MavenRuntimeException
     *             if an error occurred introspecting the Maven runtime environment
     */
    MavenProjectProperties getProjectProperties( Class<?> klass ) throws MavenRuntimeException;

    /**
     * Obtains a list of basic properties for each Maven project accessible from the specified class loader.
     * 
     * @param classLoader
     *            the class loader to introspect
     * @return a list of project properties for each Maven project found within the class path
     * @throws MavenRuntimeException
     *             if an error occurred introspecting the Maven runtime environment
     */
    List<MavenProjectProperties> getProjectsProperties( ClassLoader classLoader ) throws MavenRuntimeException;

    /**
     * Gets the specified class's Maven project.
     * 
     * @param klass
     *            the class to introspect
     * @return the specified class's Maven project
     * @throws MavenRuntimeException
     *             if an error occurred introspecting the Maven runtime environment
     */
    MavenProject getProject( Class<?> klass ) throws MavenRuntimeException;

    /**
     * Obtains a list of Maven projects accessible from the specified class loader.
     * 
     * @param classLoader
     *            the class loader to introspect
     * @return a list of projects for each Maven project found within the class path
     * @throws MavenRuntimeException
     *             if an error occurred introspecting the Maven runtime environment
     */
    List<MavenProject> getProjects( ClassLoader classLoader ) throws MavenRuntimeException;

    /**
     * Obtains a list of Maven projects accessible from the specified class loader ordered by their dependencies.
     * 
     * @param classLoader
     *            the class loader to introspect
     * @return a list of projects for each Maven project found within the class path ordered by their dependencies
     * @throws MavenRuntimeException
     *             if an error occurred introspecting the Maven runtime environment
     */
    List<MavenProject> getSortedProjects( ClassLoader classLoader ) throws MavenRuntimeException;
}
