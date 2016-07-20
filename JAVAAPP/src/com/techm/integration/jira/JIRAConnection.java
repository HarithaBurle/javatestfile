package com.techm.integration.jira;

import java.util.ArrayList;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.core.util.Base64;
import com.techm.integration.jira.util.Constant;


public class JIRAConnection {

	private  Client client = Client.create();
	
	public void createIssue( final ArrayList<String> pJsonData) {
		try {
			String url = Constant.JIRAURL;
			String credentials = Constant.CREDENTIALS;
			CreateJIRAIssue issue = new CreateJIRAIssue();
			String auth = new String(Base64.encode(credentials));

			String issueCreateURL = url + "/rest/api/2/issue";
			for(int i=0;i<pJsonData.size();i++){
				issue.createIssue(client, auth, issueCreateURL, pJsonData.get(i));
			}
		} catch (Exception e) {
			System.out.println("Exception="+e.getMessage());
		} 
	}
}
