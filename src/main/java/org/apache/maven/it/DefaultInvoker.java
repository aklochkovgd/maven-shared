package org.apache.maven.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.it.util.FileUtils;
import org.apache.maven.it.util.StringUtils;
import org.apache.maven.it.util.cli.CommandLineException;
import org.apache.maven.it.util.cli.CommandLineUtils;
import org.apache.maven.it.util.cli.Commandline;
import org.apache.maven.it.util.cli.StreamConsumer;
import org.apache.maven.it.util.cli.WriterStreamConsumer;

/**
 * @author Jason van Zyl
 */
public class DefaultInvoker
    implements Invoker
{
    private static final String LOG_FILENAME = "log.txt";
    public String localRepo;
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    private PrintStream originalOut;
    private boolean autoclean = true;
    private boolean debug;
    private String defaultMavenHome;

    public void invoke( InvocationRequest request )
        throws VerificationException
    {
        List goals = Arrays.asList( StringUtils.split( request.getGoals(), ","  ) );        
        
        if ( goals.size() == 0 )
        {
            throw new VerificationException( "No goals specified" );
        }

        List allGoals = new ArrayList();

        if ( autoclean )
        {
            allGoals.add( "clean:clean" );
        }

        allGoals.addAll( goals );

        int ret;

        File logFile = new File( request.getBasedir(), LOG_FILENAME );
        try
        {
            Commandline cli = createCommandLine();

            for ( Iterator i = request.getEnvars().keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();

                cli.addEnvironment( key, (String) request.getEnvars().get( key ) );
            }

            if ( request.getEnvars().get( "JAVA_HOME" ) == null )
            {
                cli.addEnvironment( "JAVA_HOME", System.getProperty( "java.home" ) );
            }

            cli.setWorkingDirectory( request.getBasedir() );

            for ( Iterator it = request.getCliOptions().iterator(); it.hasNext(); )
            {
                String key = String.valueOf( it.next() );

                String resolvedArg = resolveCommandLineArg( key, request.getBasedir() );

                cli.createArgument().setLine( resolvedArg );
            }

            cli.createArgument().setValue( "-e" );

            cli.createArgument().setValue( "--no-plugin-registry" );

            cli.createArgument().setValue( "--batch-mode" );

            for ( Iterator i = request.getSystemProperties().keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();
                cli.createArgument().setLine( "-D" + key + "=" + request.getSystemProperties().getProperty( key ) );
            }

            for ( Iterator i = allGoals.iterator(); i.hasNext(); )
            {
                cli.createArgument().setValue( (String) i.next() );
            }

            // System.out.println( "Command: " + Commandline.toString( cli.getCommandline() ) );

            ret = runCommandLine( System.getProperty( "maven.home" ), cli, logFile );
        }
        catch ( CommandLineException e )
        {
            throw new VerificationException( e );
        }
        catch ( IOException e )
        {
            throw new VerificationException( e );
        }

        if ( ret > 0 )
        {
            System.err.println( "Exit code: " + ret );

            throw new VerificationException( "Exit code was non-zero: " + ret + "; log = \n" + getLogContents( logFile ) );
        }
    }

    public String getMavenVersion()
        throws VerificationException
    {
        Commandline cmd = createCommandLine();
        cmd.addArguments( new String[] { "--version" } );

        Writer writer = new StringWriter();

        try
        {
            runCommandLine( System.getProperty( "maven.home" ), cmd, writer );
        }
        catch ( CommandLineException e )
        {
            throw new VerificationException( "Error running commandline " + cmd.toString(), e );
        }
        catch ( IOException e )
        {
            throw new VerificationException( "IO Error communicating with commandline " + cmd.toString(), e );
        }

        String separator = System.getProperty( "line.separator" );

        // Maven version: 2.1-SNAPSHOT
        // Java version: 1.6.0_05
        // Default locale: en_US, platform encoding: MacRoman
        // OS name: "mac os x" version: "10.5.4" arch: "x86_64" family: "mac"

        String version = StringUtils.split( writer.toString(), separator )[0];
        version = version.substring( version.indexOf( ":" ) + 2 );

        if ( version == null )
        {
            throw new VerificationException( "Illegal maven output: String 'Maven version: ' not found in the following output: " + writer.toString() );
        }
        else
        {
            return version;
        }
    }

    public String getExecutable()
    {
        String mavenHome = System.getProperty( "maven.home" );

        if ( mavenHome != null )
        {
            return mavenHome + "/bin/mvn";
        }
        else
        {
            mavenHome = defaultMavenHome;

            if ( mavenHome != null )
            {
                return mavenHome + "/bin/mvn";
            }

            File f = new File( System.getProperty( "user.home" ), "m2/bin/mvn" );

            if ( f.exists() )
            {
                return f.getAbsolutePath();
            }
            else
            {
                return "mvn";
            }
        }
    }

    private Commandline createCommandLine()
    {
        Commandline cmd = new Commandline();
        String executable = getExecutable();
        if ( executable.endsWith( "/bin/mvn" ) )
        {
            cmd.addEnvironment( "M2_HOME", executable.substring( 0, executable.length() - 8 ) );
        }
        cmd.setExecutable( executable );
        return cmd;
    }

    private int runCommandLine( String mavenHome, Commandline cli, Writer logWriter )
        throws CommandLineException, IOException
    {
        StreamConsumer out = new WriterStreamConsumer( logWriter );

        StreamConsumer err = new WriterStreamConsumer( logWriter );

        try
        {
            return CommandLineUtils.executeCommandLine( cli, out, err );
        }
        finally
        {
            logWriter.close();
        }
    }

    private int runCommandLine( String mavenHome, Commandline cli, File logFile )
        throws CommandLineException, IOException
    {
        Writer logWriter = new FileWriter( logFile );

        return runCommandLine( mavenHome, cli, logWriter );
    }

    private static String getLogContents( File logFile )
    {
        try
        {
            return FileUtils.fileRead( logFile );
        }
        catch ( IOException e )
        {
            // ignore
            return "(Error reading log contents: " + e.getMessage() + ")";
        }
    }

    private String resolveCommandLineArg( String key, String basedir )
    {
        String result = key.replaceAll( "\\$\\{basedir\\}", basedir );
        if ( result.indexOf( "\\\\" ) >= 0 )
        {
            result = result.replaceAll( "\\\\", "\\" );
        }
        result = result.replaceAll( "\\/\\/", "\\/" );

        return result;
    }

    public boolean isAutoclean()
    {
        return autoclean;
    }

    public void setAutoclean( boolean autoclean )
    {
        this.autoclean = autoclean;
    }
}