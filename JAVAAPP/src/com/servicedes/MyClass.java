package com.servicedes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.zip.DataFormatException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import Decoder.BASE64Encoder;
import com.techm.integration.jira.JIRAConnection;
import com.techm.integration.jira.util.Constant;

public class MyClass {

	public static void main(String[] args) throws JSONException, IOException, NumberFormatException, DataFormatException {
		JSONObject obj=getResult(Constant.SERVICEDESKURL);
		ArrayList<HashMap<String, String>> finalData=parseData(obj);
		createJson(finalData);
	}

	@SuppressWarnings("deprecation")
	public static JSONObject getResult(final String pUrl) {
        String result=null;
		InputStream is = null;
		 JSONObject resultObj = null;
        try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
			HttpConnectionParams.setSoTimeout(httpParameters, 30000);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			URI uri = new URI(pUrl);
			HttpGet httppost = new HttpGet(uri);
			httppost.setHeader("X-ExperimentalApi", "true");
			httppost.setHeader("Content-Type", "application/json");
			String str = "admin:admin";
			byte[] b = str.getBytes();
			BASE64Encoder e = new BASE64Encoder();
	        String encoding = e.encode(b);
			httppost.addHeader("Authorization", "Basic " + encoding);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			result=getStringfromIS(is);
			resultObj = new JSONObject(result);
			httpclient.getConnectionManager().shutdown();
			if (entity != null) {
				entity.consumeContent();
			}
			 System.out.println("output="+resultObj);
			return resultObj;
		}
		catch(Exception e) {
			System.out.println("Exception while calling API. Exception=" + e.getMessage());
		} 
        return resultObj;
    }
	
	public static String getStringfromIS(final InputStream pIs){
		String result=null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(pIs, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)	{
				sb.append(line + "\n");
			}
			pIs.close();
			result = sb.toString();
		}
		catch (Exception e){ 
			System.out.println("Error while geting data from Input String="+ e.getMessage());
		}
		return result;
	}
	
	public static ArrayList <HashMap<String, String>> parseData(JSONObject jsonData) {
		ArrayList <HashMap<String, String>> data=null;
		if(jsonData != null){
			 data=new ArrayList<HashMap<String, String>>();
			try {
				JSONArray array = (JSONArray) jsonData.get("values");
				System.out.println(array);

				for (int i = 0; i < array.length(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					JSONObject obj1 = (JSONObject) array.get(i);
					JSONObject obj2 = (JSONObject) obj1.get("fields");

					map.put("summary", obj2.get("summary").toString());
					map.put("updated", obj2.get("updated").toString());
					
					data.add(map);
					System.out.println("map=" + data);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else {
			System.out.println(" get action Response null");
		}
		return data;
	}
	
	private static void createJson(final ArrayList<HashMap<String, String>> finalData) throws JSONException, IOException, NumberFormatException, DataFormatException {
		if (finalData != null) {
			ArrayList<String>jsonData=new ArrayList<String>();
			for (int i = 0; i < finalData.size(); i++) {
				String yesterdayDate=finalData.get(i).get("updated").substring(0, 10);
				if(!getCurrentDate().equals(yesterdayDate)){
					
					String post = "{\"fields\":{\"project\":{\"key\":\""+Constant.PROJECTKEY+"\"},\"summary\":\""+finalData.get(i).get("summary")+"\",\"issuetype\":{\"name\":\"Task\"}}}";
					jsonData.add(post);
				}
			}
			new JIRAConnection().createIssue(jsonData);

		}else {
			System.out.println("Exception while parseData API. Exception="+finalData);
		}
	}
	
	public static String getCurrentDate() throws DataFormatException,NumberFormatException {
		String PATTERN="yyyy-MM-dd";
		SimpleDateFormat dateFormat=new SimpleDateFormat();
		dateFormat.applyPattern(PATTERN);
		String currentdate=dateFormat.format(Calendar.getInstance().getTime());
		System.out.print(currentdate);
		return currentdate;
	}
}
