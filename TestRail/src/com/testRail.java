package com;

import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.sql.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class testRail {

	String planSql, runSql, resultSql;
	String testPlan_id, testRun_id, testResult_id;
	ArrayList arrTestRun = new ArrayList();
	
	public static void main(String args[]) throws Exception{
		testRail tr = new testRail();
		tr.uploadPlanRun();
		tr.dbConnect();
		tr.uploadResults();
		tr.uploadIterations();
		tr.updateErrorCode();
	
	}
	
	public String getStatus(String intStatus){
		
		String strStatus=null;
		
		switch(intStatus){
		
		case "1" : strStatus = "Passed";
		break;
		
		case "2" : strStatus = "Blocked";
		break;
		
		case "3" : strStatus = "Untested";
		break;
		
		case "4" : strStatus = "Retest";
		break;
		
		case "5" : strStatus = "failed";
		break;
		
		case "6" : strStatus = "Not Applicable";
		break;
		
		case "7" : strStatus = "automated";
		break;
		
		case "8" : strStatus = "Can't Automate";
		break;
		
		case "9" : strStatus = "Can't Automate";
		break;
		
		}
		
		return strStatus;
	}
	
	public String getJson(String url){
		String json_response = new String();
		 
		try{
			StringBuffer response;
			
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			String userCredentials = "sudheer.mudumala.contractor@weather.com:@lt12345";
			
			String basicAuth = "Basic " + Base64.getEncoder().encodeToString((userCredentials).getBytes());
		
			con.setRequestProperty ("Authorization", basicAuth);
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			
			 con.setRequestProperty("User-Agent", "Mozilla/5.0");
			 		     
		     BufferedReader in = new BufferedReader(
		             new InputStreamReader(con.getInputStream()));
		     String inputLine;
		     response = new StringBuffer();
		     while ((inputLine = in.readLine()) != null) {
		     	response.append(inputLine);
		     }
		     in.close();
		     
		   json_response = response.toString();
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return json_response;
	}
	
	public void dbConnect(){
		
		System.out.println("Started uploading testplan and testrun details into DB");
		
		ResultSet rs;
		
		try{
	
			Class.forName("com.mysql.jdbc.Driver");
			Connection con=DriverManager.getConnection("jdbc:mysql://18.221.129.98/","bigweb","cash-power-flies");  
			
			Statement stmt=con.createStatement();  
			
			stmt.executeUpdate(planSql);
			System.out.println("Inserted into Plan Table");
			
			rs=stmt.executeQuery("select max(id) from bigweb_automation.test_plan;");
			rs.first();
			testPlan_id=rs.getString(1);

			
			runSql = runSql.replace("testPlanid", testPlan_id);
			System.out.println(runSql);
			stmt.executeUpdate(runSql);
			
			con.close();  
			
		}catch(Exception e){System.out.println(e);}
		
		System.out.println("Completed uploading testplan and testrun details into DB");
		
	}
	
	public void dbInsert(String sqlQuery){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con=DriverManager.getConnection("jdbc:mysql://18.221.129.98/","bigweb","cash-power-flies");  
			Statement stmt=con.createStatement();  
			stmt.executeUpdate(sqlQuery);
			con.close();	
		}catch(Exception e){System.out.println(e);}
	}
	
	public String dbGetId(String sqlQuery){
		
		ResultSet rs;
		String dbId=null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con=DriverManager.getConnection("jdbc:mysql://18.221.129.98/","bigweb","cash-power-flies");  
			Statement stmt=con.createStatement();  
			rs = stmt.executeQuery(sqlQuery);
			rs.first();
			dbId = rs.getString(1);
			con.close();	
		}catch(Exception e){System.out.println(e);}
		
		return dbId;
	}
	public ArrayList dbGetTestCases(String sqlQuery){
		ResultSet rsTestCases = null;
		ArrayList<String> alTestCases = new ArrayList<String>();
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con=DriverManager.getConnection("jdbc:mysql://18.221.129.98/","bigweb","cash-power-flies");  
			Statement stmt=con.createStatement();  
			rsTestCases = stmt.executeQuery(sqlQuery);
			while(rsTestCases.next()){
				alTestCases.add(String.valueOf(rsTestCases.getInt(1)));
			}
			rsTestCases.close();
			con.close();	
		}catch(Exception e){System.out.println(e);}
		return alTestCases;
	}
	public List<String[]> dbGetErrorCode(String sqlQuery){
		
		ResultSet rsErrorCode = null;
		List<String[]> strErrorCode = new ArrayList<String []>();
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con=DriverManager.getConnection("jdbc:mysql://18.221.129.98/","bigweb","cash-power-flies");  
			Statement stmt=con.createStatement();  
			rsErrorCode = stmt.executeQuery(sqlQuery);
			
			int i=0;
			while(rsErrorCode.next()){
				
				strErrorCode.add(new String [] {rsErrorCode.getString(1).toUpperCase(),rsErrorCode.getString(2).toUpperCase()});
			}
			rsErrorCode.close();
			con.close();	
		}catch(Exception e){System.out.println(e);}
		return strErrorCode;	
	}
	
	public void uploadPlanRun() {
		
		System.out.println("Retrieving testplan and test run details");
		
		try{
			
			String url = "https://twcqa.testrail.com/index.php?/api/v2/get_plan/16383";
			     
		    String json_response = getJson(url);
		    
		    JSONObject planObj = new JSONObject(json_response);
		    
		    planSql = "INSERT INTO bigweb_automation.test_plan (test_plan_id,test_plan_url,name) VALUES (";
		    
		    planSql += planObj.get("id").toString() + ",";
		    planSql += "\"" + planObj.get("url").toString() + "\"" + ",";
		    planSql += "\"" + planObj.get("name").toString() + "\"" ;
		    planSql +=  ");";

		    
		    runSql = "INSERT INTO bigweb_automation.test_run (test_run_id, test_run_url, name,test_plan_id) VALUES";
		    JSONArray arrEntries = (JSONArray) planObj.get("entries");
		    for(int i=0;i<arrEntries.length();i++){
		    	
		    	JSONObject objEntries = arrEntries.getJSONObject(i);
		    	
		    	JSONArray arrRuns = (JSONArray) objEntries.get("runs");
		    	
		    	JSONObject objRuns = arrRuns.getJSONObject(0);
		    	runSql += "("  + objRuns.get("id")  + ",";
		    	runSql += "\""  + objRuns.get("url") + "\"" + ",";
		    	runSql += "\"" + objRuns.get("name") + "\"" + "," ;
		    	runSql += "testPlanid" + ")";
		    	
		    	arrTestRun.add(objRuns.get("id"));
		    	
		    }
		    
		    runSql = runSql.replace(")(","),(") + ";" ;
		    //System.out.println(runSql);
		    	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Completed Retrieving testplan and test run details");
		
	}
	
	public void uploadResults(){
		System.out.println("Started retreiving and uploading test results in db");
		String run_id = null;
		try{
			
			Iterator<Integer> itrTestRun = arrTestRun.iterator();
			while (itrTestRun.hasNext()){
				
				int runId = itrTestRun.next();
				
				String url = "https://twcqa.testrail.com/index.php?/api/v2/get_tests/" + runId;
				String json_response = getJson(url);
				JSONArray testArr = new JSONArray(json_response);
				
				resultSql = "INSERT INTO bigweb_automation.test_result (test_case_id,name,status,test_run_id) VALUES ";
				
				for(int j=0;j<testArr.length();j++){
					JSONObject testObj = testArr.getJSONObject(j);
					
					resultSql+= "(" + testObj.get("case_id").toString();
					resultSql+= "," + "\"" + testObj.get("title").toString().replace("\"", "'") + "\"";
					resultSql+= "," + "\"" + getStatus(testObj.get("status_id").toString()) + "\"";
					resultSql+= "," + "runId";
					resultSql+= ")";
				}
				resultSql = resultSql.replace(")(","),(") + ";" ;	
				run_id = dbGetId("select max(id) from bigweb_automation.test_run where test_run_id = "+ runId + ";");
				resultSql = resultSql.replace("runId", run_id);
			
				dbInsert(resultSql);
			}
		}catch(Exception e){e.printStackTrace();}
		System.out.println("Completed retreiving and uploading test results in db");
	}

	public void uploadIterations(){
		System.out.println("Started retrieving and uploading test iteration details into DB");
		
		String strGetTestCase, intResultId, intTestCaseId, strJsonTestCase,trComment, arrComment[], strIterationSQL, strParameterSQL,intIterationId, strItrStatus, strParams; 
		Integer intTestRunId, intCnt, intIterationCnt;
		
		
		ArrayList<String> alTestCase = new ArrayList<String>();
		try{
			Iterator<Integer> itrTestRun = arrTestRun.iterator();
			while (itrTestRun.hasNext()){
				intTestRunId = itrTestRun.next();
				strGetTestCase = "select test_case_id from bigweb_automation.test_result where test_run_id in (select max(id) from bigweb_automation.test_run where test_run_id = " + intTestRunId + ")";
				alTestCase = dbGetTestCases(strGetTestCase);
				Iterator<String> itrTestCase = alTestCase.iterator();
				while (itrTestCase.hasNext()){
					
					intTestCaseId = itrTestCase.next();
					//System.out.println("select max(id) from bigweb_automation.test_result where test_case_id = " + intTestCaseId + " and test_run_id in (select max(id) from bigweb_automation.test_run where test_run_id = " +  intTestRunId + ");");
					intResultId = dbGetId("select max(id) from bigweb_automation.test_result where test_case_id = " + intTestCaseId + " and test_run_id in (select max(id) from bigweb_automation.test_run where test_run_id = " +  intTestRunId + ");");
					
					String resultURL = "https://twcqa.testrail.com/index.php?/api/v2/get_results_for_case/" +  intTestRunId + "/" + intTestCaseId;
					
					strJsonTestCase = getJson(resultURL);
			    	JSONArray resultArr = new JSONArray(strJsonTestCase);
			    	intCnt = resultArr.length()-1;
			    	
			    	JSONObject resObj = resultArr.getJSONObject(intCnt);
			    	trComment = resObj.get("comment").toString();
		    		arrComment = trComment.split("JSON Output:");	
		    		JSONObject commentObj = new JSONObject(arrComment[1]);
		    		
		    		JSONArray arrIteration = new JSONArray(commentObj.get("iterations").toString());
		    		
		    		for(int q=0;q<arrIteration.length();q++){

			    		strIterationSQL = "insert into bigweb_automation.test_iteration (test_result_id,status,error_message,video_url) VALUES";
			    		strParameterSQL = "insert into bigweb_automation.test_parameter (test_iteration_id,name,value) VALUES";
		    			
		    			JSONObject objIteration = arrIteration.getJSONObject(q);
		    			JSONArray arrTries = new JSONArray(objIteration.get("tries").toString());
		    				
		    			ArrayList<JSONObject> objTries = new ArrayList<JSONObject>();

		    			for (int g=0;g<arrTries.length();g++){
		    				objTries.add(arrTries.getJSONObject(g));
		    			}
		    			int idxTries=objTries.size() - 1;
		    			
		    			if(objTries.get(idxTries).get("status").toString() == "SKIPPED"){strItrStatus = "FAILED";}
		    			else{strItrStatus = objTries.get(idxTries).get("status").toString();}

		    			strIterationSQL += "(" + intResultId;
		    			strIterationSQL += "," + "\"" + strItrStatus + "\"";
		    			strIterationSQL += "," + "\"" + objTries.get(idxTries).get("message").toString().replaceAll("\"", "'") + "\"";
		    			strIterationSQL += "," + "\"" + objTries.get(idxTries).get("link").toString() + "\"" + ");";
		    			
		    			System.out.println("Inserting Iteration : ");
		    			
			    		dbInsert(strIterationSQL);
			    		
			    		System.out.println("Done Inserting Iteration : ");
			    		
			    		strParams= objIteration.get("parameters").toString();
			    		
			    		if(strParams == "null"){
//			    			strParameterSQL += "((select max(id) from bigweb_automation.test_iteration),null,null);";
//			    			strParameterSQL = strParameterSQL.replace(")(","),(") + ";" ;
//			    			dbInsert(strParameterSQL);
		    				
			    		}
			    		else
			    		{
			    			JSONArray arrParams = new JSONArray(strParams);
			    		
			    			for (int g=0;g<arrParams.length();g++){
			    				
			    				JSONObject objParams = arrParams.getJSONObject(g);
			    				strParameterSQL += "((select max(id) from bigweb_automation.test_iteration)";
			    				strParameterSQL += "," + "\"" + objParams.get("name").toString() + "\"" ;
			    				strParameterSQL += "," + "\"" + objParams.get("value").toString() + "\"" ;
			    				strParameterSQL  += ")";
			    			}
			    			strParameterSQL = strParameterSQL.replace(")(","),(") + ";" ;
			    			
			    			System.out.println("Inserting Parameter : ");
			    			
			    			System.out.println(System.currentTimeMillis());
			    			dbInsert(strParameterSQL);
			    			System.out.println(System.currentTimeMillis());
			    			
			    			System.out.println("Done Inserting Parameter : ");
			    			
			    			//System.out.println(strParameterSQL);
			    		}
		    			
		    		}
		    		
				}
				System.out.println("Completed uploading iterations details for run " + intTestRunId);
			}
		}catch(Exception e) {e.printStackTrace();}
		
		System.out.println("Completed retrieving and uploading test iteration details into DB");
		
	}

	public void updateErrorCode(){
		
		System.out.println("Started updating error code for failures");
		
		List<String[]> strErrorCode = new ArrayList<String []>();
		List<String[]> strErrorMsg = new ArrayList<String []>();
		int intErrCodeSize, intErroMsgSize;
		String strErrorCodeSql;
		
		strErrorCode = dbGetErrorCode("select * from bigweb_automation.test_error");
		
		strErrorMsg = dbGetErrorCode("select id,error_message from bigweb_automation.test_iteration where status=\"failed\" and error_code is null;");
		
		intErrCodeSize = strErrorCode.size();
		intErroMsgSize = strErrorMsg.size();
		
		for(int i=0; i < intErrCodeSize;i++){
			
			for(int j=0; j < intErroMsgSize;j++){
			
				if (strErrorMsg.get(j)[1].contains(strErrorCode.get(i)[1])){
					
					strErrorCodeSql = "update bigweb_automation.test_iteration set error_code=" + strErrorCode.get(i)[0] + " where id =" + strErrorMsg.get(j)[0] + ";";
					dbInsert(strErrorCodeSql);
				}
			}
		}
		dbInsert("update bigweb_automation.test_iteration set error_code=900 where status=\"failed\" and error_code is null;");
		System.out.println("Completed updating error code for failures");
	}
	
}
