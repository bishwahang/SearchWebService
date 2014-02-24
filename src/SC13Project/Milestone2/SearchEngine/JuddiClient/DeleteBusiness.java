package SC13Project.Milestone2.SearchEngine.JuddiClient;

import java.rmi.RemoteException;

import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.Name;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;

public class DeleteBusiness {
	
	private Transport transport;

	public DeleteBusiness(Transport transport) {
		this.transport = transport;
		
	}
	
	
	public void deleteBusiness(String authToken, String businessName) throws TransportException, DispositionReportFaultMessage, RemoteException {
		
		UDDIInquiryPortType uddiInquiryService = transport.getUDDIInquiryService();
		
		Name name = new Name();
		name.setValue(businessName);
		
		FindBusiness fb = new FindBusiness();
		fb.setAuthInfo(authToken);
		fb.getName().add(name);
		fb.setMaxRows(999);
		
		BusinessList foundBusinesses = uddiInquiryService.findBusiness(fb);
		
		
		if(foundBusinesses.getBusinessInfos() != null) {		
			for(BusinessInfo business : foundBusinesses.getBusinessInfos().getBusinessInfo()) {
				System.out.println("delete business: " + business.getName() + " - " + business.getBusinessKey());
				org.uddi.api_v3.DeleteBusiness db = new org.uddi.api_v3.DeleteBusiness();
				db.setAuthInfo(authToken);
				db.getBusinessKey().add(business.getBusinessKey());
				transport.getUDDIPublishService().deleteBusiness(db);
			}
		}
		else {
			System.out.println("didn't found any business");
		}
	}
}
