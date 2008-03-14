package org.apache.maven.shared.dependency.analyzer;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Tests <code>ProjectDependencyAnalysis</code>.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see ProjectDependencyAnalysis
 */
public class ProjectDependencyAnalysisTest extends TestCase
{
    // tests ------------------------------------------------------------------

    public void testConstructor()
    {
        Set usedDeclaredArtifacts = new HashSet();
        Set usedUndeclaredArtifacts = new HashSet();
        Set unusedDeclaredArtifacts = new HashSet();

        ProjectDependencyAnalysis analysis =
            new ProjectDependencyAnalysis( usedDeclaredArtifacts, usedUndeclaredArtifacts, unusedDeclaredArtifacts );

        assertEquals( usedDeclaredArtifacts, analysis.getUsedDeclaredArtifacts() );
        assertEquals( usedUndeclaredArtifacts, analysis.getUsedUndeclaredArtifacts() );
        assertEquals( unusedDeclaredArtifacts, analysis.getUnusedDeclaredArtifacts() );
    }
}