package SC13Project.Milestone2.SearchEngine;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.v3_service.DispositionReportFaultMessage;

import SC13Project.Milestone2.SearchEngine.JuddiClient.Authenticate;
import SC13Project.Milestone2.SearchEngine.JuddiClient.BuildClient;
import SC13Project.Milestone2.SearchEngine.JuddiClient.QueryJuddi;

public class WSSearchEngineImpl implements WSSearchEngine {
	private Transport transport;
	private String authToken;
	private QueryJuddi queryJuddi;
	
	public WSSearchEngineImpl() {

		try {
			System.out.println("Building Transport");
			transport = new BuildClient().buildClearkManager();
			System.out.println("Transport Builded: "+transport.toString());
			System.out.println("Getting Authtoken");
			authToken = new Authenticate(transport).getRootAuthToken();
			System.out.println("Authtoken: "+authToken);
			queryJuddi= new QueryJuddi(transport);
	
			

		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (DispositionReportFaultMessage e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		}
		

	}

	@Override
	public List<ServiceInfo> search(String keywords) {
		List<ServiceInfo> result = new ArrayList<ServiceInfo>();
//		String[] queries = keywords.split(" ");
		ArrayList<ArrayList<String>> queryResult = new ArrayList<ArrayList<String>>();
		try {
			System.out.println("Starting Search...");
			queryResult=queryJuddi.query(authToken, keywords);
//			for (String query : queries) {
//				queryResult.addAll(queryJuddi.query(authToken, query));
//				System.out.println("Keyword: "+ query+"ResultSize: "+queryResult.size());
//			}
			List<String> urls = new ArrayList<String>();
			for (ArrayList<String> arrayList : queryResult) {
				System.out.println("Appending Result..");
				ServiceInfo sinfo = new ServiceInfo();
				String serviceName = arrayList.get(0);
				System.out.println("Service found: " + serviceName);
				String currentUrl = arrayList.get(1);
				if (urls.contains(currentUrl)) {
					// we've found this service url already
					continue;
				}
				urls.add(currentUrl);
				sinfo.setUrl(currentUrl);
				sinfo.setDescription(arrayList.get(2));
				result.add(sinfo);
			}
			/*List<String> names= new ArrayList<String>();
			for (ArrayList<String> arrayList : queryResult) {
				System.out.println("Appending Result..");
				ServiceInfo sinfo = new ServiceInfo();
				String serviceName = arrayList.get(0);
				if(names.contains(serviceName)){
					continue;
				}
				names.add(serviceName);
				System.out.println("Service found: " + serviceName);
				sinfo.setUrl(arrayList.get(1));
				sinfo.setDescription(arrayList.get(2));
				result.add(sinfo);
			}*/
			if (queryResult.size() == 0) {
				System.out.println("nothing found");
				return result;
			}

		} catch (DispositionReportFaultMessage e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		}
		System.out.println("Returned result!");
		return result;
	}


}
