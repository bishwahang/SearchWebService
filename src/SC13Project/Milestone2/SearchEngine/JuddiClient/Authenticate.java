package SC13Project.Milestone2.SearchEngine.JuddiClient;

import java.rmi.RemoteException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

public class Authenticate {
	
	private Transport transport;

	public Authenticate(Transport uclManager) {
		this.transport = uclManager;
		
	}

	public String getRootAuthToken() throws ConfigurationException, TransportException, DispositionReportFaultMessage, RemoteException {
		GetAuthToken getAuthToken = new GetAuthToken();
        getAuthToken.setUserID("user");
        getAuthToken.setCred("rtj2m4saqu2ee14kaev3aj99i1");
        UDDISecurityPortType uddiSecurityService = transport.getUDDISecurityService();
        return uddiSecurityService.getAuthToken(getAuthToken).getAuthInfo();
    }	
}
