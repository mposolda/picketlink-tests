/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketlink.test.integration.federation.saml;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import static org.junit.Assert.assertTrue;
import static org.picketlink.test.integration.federation.saml.QuickstartArchiveUtil.resolveFromFederation;

/**
 * @author Pedro Igor
 */
@RunWith (Arquillian.class)
@RunAsClient
public class RestoreOriginalRequestTestCase extends AbstractFederationTestCase {

    @Deployment(name = "picketlink-federation-saml-idp-basic")
    public static WebArchive deployIdp() {
        return resolveFromFederation("picketlink-federation-saml-idp-basic");
    }

    @Deployment(name = "picketlink-federation-saml-sp-redirect-basic")
    public static WebArchive deployEmployee() {
        WebArchive serviceProvider = resolveFromFederation("picketlink-federation-saml-sp-redirect-basic");

        serviceProvider.add(new StringAsset("Back to the original requested resource."), "savedRequest/savedRequest.html");

        return serviceProvider;
    }

    @Deployment(name = "picketlink-federation-saml-sp-post-basic")
    public static WebArchive deploySales() {
        WebArchive serviceProvider = resolveFromFederation("picketlink-federation-saml-sp-post-basic");

        serviceProvider.add(new StringAsset("Back to the original requested resource."), "savedRequest/savedRequest.html");
        serviceProvider.add(new StringAsset("<%= request.getParameter(\"SAVED_PARAM\") %>."), "savedRequest/savedRequest.jsp");

        return serviceProvider;
    }

    @Test
    @OperateOnDeployment("picketlink-federation-saml-sp-post-basic")
    public void testPostOriginalRequest(@ArquillianResource URL url) throws Exception {
        WebRequest request = new GetMethodWebRequest(formatUrl(url) + "savedRequest/savedRequest.html");
        WebConversation conversation = new WebConversation();
        WebResponse response = conversation.getResponse(request);

        WebForm webForm = response.getForms()[0];

        webForm.setParameter("j_username", "tomcat");
        webForm.setParameter("j_password", "tomcat");

        webForm.getSubmitButtons()[0].click();

        response = conversation.getCurrentPage();

        assertTrue(response.getText().contains("Back to the original requested resource."));
    }

    @Test
    @OperateOnDeployment("picketlink-federation-saml-sp-post-basic")
    public void testPostOriginalRequestWithParams(@ArquillianResource URL url) throws Exception {
        WebRequest request = new GetMethodWebRequest(formatUrl(url) + "savedRequest/savedRequest.jsp");

        request.setParameter("SAVED_PARAM", "Param was saved.");

        WebConversation conversation = new WebConversation();
        WebResponse response = conversation.getResponse(request);

        WebForm webForm = response.getForms()[0];

        webForm.setParameter("j_username", "tomcat");
        webForm.setParameter("j_password", "tomcat");

        webForm.getSubmitButtons()[0].click();

        response = conversation.getCurrentPage();

        assertTrue(response.getText().contains("Param was saved."));
    }

    @Test
    @OperateOnDeployment("picketlink-federation-saml-sp-redirect-basic")
    public void testRedirectOriginalRequest(@ArquillianResource URL url) throws Exception {
        WebRequest request = new GetMethodWebRequest(formatUrl(url) + "/savedRequest/savedRequest.html");
        WebConversation conversation = new WebConversation();
        WebResponse response = conversation.getResponse(request);

        WebForm webForm = response.getForms()[0];

        webForm.setParameter("j_username", "tomcat");
        webForm.setParameter("j_password", "tomcat");

        webForm.getSubmitButtons()[0].click();

        response = conversation.getCurrentPage();

        assertTrue(response.getText().contains("Back to the original requested resource."));
    }

}
