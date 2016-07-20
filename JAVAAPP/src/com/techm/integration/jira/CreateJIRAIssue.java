package com.techm.integration.jira;

import javax.security.sasl.AuthenticationException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CreateJIRAIssue {

	public void createIssue(Client client, String auth, String url, String string) throws AuthenticationException
	{
		 	WebResource webResource = client.resource(url);
		 	ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json").accept("application/json").post(ClientResponse.class, string);
		    int statusCode = response.getStatus();
		    if (statusCode == 401) 
		    {
		        throw new AuthenticationException("Invalid Username or Password");
		    }	
		    System.out.println(response);
	}
}
