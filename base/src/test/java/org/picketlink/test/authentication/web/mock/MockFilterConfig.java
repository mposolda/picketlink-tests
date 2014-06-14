package org.picketlink.test.authentication.web.mock;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;


public class MockFilterConfig implements FilterConfig {

    private final MockServletContext owningContext;
    public Map<String, String> initParams = new HashMap<String, String>();

    public MockFilterConfig( MockServletContext owningContext ) {
        this.owningContext = owningContext;
    }

    @Override
    public String getFilterName() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public ServletContext getServletContext() {
        return owningContext;
    }

    @Override
    public String getInitParameter( String name ) {
        return initParams.get( name );
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration( initParams.keySet() );
    }

    public void setContextPath( String contextPath ) {
        owningContext.setContextPath( contextPath );
    }

}
