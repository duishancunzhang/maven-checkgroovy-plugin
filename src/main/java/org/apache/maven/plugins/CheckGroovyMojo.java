package org.apache.maven.plugins;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jodd.util.StringUtil;

/**
 * @author zhuhw
 * @date 2018/12/17 9:06.
 */
@Mojo(name="checkgroovy",defaultPhase= LifecyclePhase.VALIDATE)
public class CheckGroovyMojo extends AbstractMojo {

    @Parameter ( defaultValue = "${project}", readonly = true, required = true )
    protected MavenProject project;

    /**
     * 源代码路径，默认为当前项目
     */
    @Parameter
    private List<String> path;

    /**
     * 自定义规则文件的父目录
     */
    @Parameter
    private String configLocation;

    /**
     * 是否启用，默认false
     */
    @Parameter( property = "checkgroovy.enable", defaultValue = "false" )
    private boolean enableCheckGroovy;

    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( enableCheckGroovy )
        {
            getLog().warn( "start check groovy......" );
            checkGroovy( getGroovyPath() );
        }
    }

    private void checkGroovy( String groovyPath )
            throws MojoFailureException
    {
        try
        {
            if ( groovyPath == null || "".equals( groovyPath ) )
            {
                throw new MojoFailureException( "checkGroovy config file not found" );
            }
            List<String> sourceRoot = getSourceDirectories();
            if( sourceRoot == null || sourceRoot.size() == 0 )
            {
                throw new MojoFailureException( "maven:checkgroovy sourceroot cannot be null " );
            }
            Binding binding = new Binding();

            binding.setProperty( "sourceDirectories", sourceRoot );
            GroovyScriptEngine engine = new GroovyScriptEngine( "E:\\ideaworkspace\\maven-checkgroovy-plugin\\src\\main\\resources" );
//            GroovyScriptEngine engine = new GroovyScriptEngine( groovyLocation );
//            GroovyScriptEngine engine = new GroovyScriptEngine( getGroovyPath() );
            engine.run( "check.groovy", binding );
        }
        catch ( IOException e )
        {
            throw new MojoFailureException( "GroovyScriptEngine load failed, groovyPath:" + groovyPath );
        }
        catch ( ResourceException e )
        {
            throw new MojoFailureException( "GroovyScriptEngine run script failed, resource not founded:" + groovyPath );
        }
        catch ( ScriptException e )
        {
            throw new MojoFailureException( "GroovyScriptEngine run script, groovyPath:" + groovyPath );
        }
    }

    private String getGroovyPath()
    {
        if( StringUtil.isAllEmpty( configLocation ) ){
//            configLocation = this.getClass().getResource("" ).getPath().replaceAll("/org/apache/maven/plugins","" );
            configLocation = this.getClass().getResource("" ).getPath() + "style" + File.separator;
        }
        return configLocation;
    }

    private List<String> getSourceDirectories()
    {
        if ( path == null )
        {
            path = project.getCompileSourceRoots();
        }
        return path;
    }
}
