package SC13Project.Milestone2.SearchEngine.JuddiClient;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;

public class QueryJuddi {

	private Transport transport;

	public QueryJuddi(Transport transport) {
		this.transport = transport;

	}

	public ArrayList<ArrayList<String>> query(String authToken, String query) throws TransportException, DispositionReportFaultMessage,
			RemoteException {

		ArrayList<ArrayList<String>> services = new ArrayList<ArrayList<String>>();
		services = findService(authToken, query);
		return services;
	}

	private ArrayList<ArrayList<String>> findService(String authToken, String query) throws DispositionReportFaultMessage, RemoteException,
			TransportException {
		String[] keywords = query.trim().split("\\s*;\\s*|\\s*,\\s*|\\s*[+]\\s*|\\s+");
		ArrayList<ArrayList<String>> serviceInfos = new ArrayList<ArrayList<String>>();
		List<org.uddi.api_v3.ServiceInfo> services = new ArrayList<org.uddi.api_v3.ServiceInfo>();
		FindService fs = new FindService();
		fs.setAuthInfo(authToken);
		fs.getName().add(getWildcardName());
		fs.setFindQualifiers(approximateQualifier());

		UDDIInquiryPortType uddiInquiryService = transport.getUDDIInquiryService();
		ServiceList foundServices = uddiInquiryService.findService(fs);
		services = foundServices.getServiceInfos().getServiceInfo();
		System.out.println("Got the services list..");
		GetServiceDetail gsd = new GetServiceDetail();
		for (org.uddi.api_v3.ServiceInfo serviceInfo : services) {
			System.out.println("Looping through each services...");
			String serviceKey = "";
			String modelkey = "";
			String bindingkey = "";

			String wsdlurl = "";
			String accesspoint = "";
			String servicename = "";

			String serviceDescription = "";
			String bindingdescription = "";
			String modeldescription = "";
			// Service
			ArrayList<String> templist = new ArrayList<>();
			gsd.getServiceKey().add(serviceInfo.getServiceKey());
			serviceKey = serviceInfo.getServiceKey();

			GetServiceDetail getServiceDetail = new GetServiceDetail();
			getServiceDetail.setAuthInfo(authToken);
			getServiceDetail.getServiceKey().add(serviceKey);
			ServiceDetail serviceDetail = uddiInquiryService.getServiceDetail(getServiceDetail);
			// Service
			BusinessService businessservice = new BusinessService();
			if (serviceDetail.getBusinessService().size() > 0) {
				businessservice = serviceDetail.getBusinessService().get(0);
				servicename = businessservice.getName().get(0).getValue().trim();
				// System.out.println("fetched service name:" + servicename);
				if (businessservice.getDescription().size() > 0) {
					serviceDescription = businessservice.getDescription().get(0).getValue().trim();
				}

				// Binding Service
				BindingTemplate bindingtemplate = new BindingTemplate();
				BindingTemplates bindingTemplates = businessservice.getBindingTemplates();
				if (bindingTemplates != null) {
					if (bindingTemplates.getBindingTemplate().size() > 0) {
						bindingkey = businessservice.getBindingTemplates().getBindingTemplate().get(0).getBindingKey();
						GetBindingDetail gbd = new GetBindingDetail();
						gbd.setAuthInfo(authToken);
						gbd.getBindingKey().add(bindingkey);
						BindingDetail bindingdetail = uddiInquiryService.getBindingDetail(gbd);
						bindingtemplate = bindingdetail.getBindingTemplate().get(0);
						if(bindingtemplate != null){
							accesspoint = bindingtemplate.getAccessPoint().getValue().trim();
						}
						

						if (bindingtemplate.getDescription().size() > 0) {
							bindingdescription = bindingtemplate.getDescription().get(0).getValue().trim();
						}
					}

				}

				// TModel
				TModel tmodel = new TModel();
				TModelInstanceDetails tmodeldetails = bindingtemplate.getTModelInstanceDetails();
				if (tmodeldetails != null) {
					if (tmodeldetails.getTModelInstanceInfo().size() > 0) {
						modelkey = bindingtemplate.getTModelInstanceDetails().getTModelInstanceInfo().get(0).getTModelKey();
						GetTModelDetail gtd = new GetTModelDetail();
						gtd.setAuthInfo(authToken);
						gtd.getTModelKey().add(modelkey);
						TModelDetail tmodelDetails = uddiInquiryService.getTModelDetail(gtd);
						tmodel = tmodelDetails.getTModel().get(0);
						if (tmodel.getDescription().size() > 0) {
							modeldescription = tmodel.getDescription().get(0).getValue().trim();
						}
						if(tmodel.getOverviewDoc().size() > 0){
							wsdlurl = tmodel.getOverviewDoc().get(0).getOverviewURL().getValue().trim();
						}
					}

				}

			}
			String description = serviceDescription +" "+ bindingdescription + " "+modeldescription + " "+accesspoint;
			for (String key : keywords) {
				key = key.toLowerCase();
				if (servicename.toLowerCase().contains(key) || wsdlurl.toLowerCase().contains(key) || description.toLowerCase().contains(key)) {
					System.out.println("Caught keyword: "+ key);
					templist.add(0, servicename);
					templist.add(1, wsdlurl.trim());
					templist.add(2, "ServiceName: "+servicename+"\nDescription: "+serviceDescription +"\n"+ bindingdescription + "\n"+modeldescription + "\nAccesspoint: "+accesspoint);
					serviceInfos.add(templist);
				}
			}

		}

		return serviceInfos;
	}

	private Name getWildcardName() {
		Name name = new Name();
		name.setValue("%");
		return name;
	}

	private FindQualifiers approximateQualifier() {
		FindQualifiers fq = new FindQualifiers();
		fq.getFindQualifier().add("approximateMatch");
		return fq;
	}
}
