package SC13Project.Milestone2.SearchEngine;

import java.util.ArrayList;
import java.util.List;

public class MainApp {
	public static void main(String[] args) {
		WSSearchEngine sengine = new WSSearchEngineImpl();
		List <ServiceInfo> result = new ArrayList<ServiceInfo>();
		result=sengine.search("saroj test test");
		if(result.isEmpty()){
			System.out.println("Empty List");
			
		}else{
			for (ServiceInfo serviceInfo : result) {
				System.out.println( serviceInfo.getDescription());
				System.out.println(serviceInfo.getUrl());
			}
		}
		
		
	}

}
