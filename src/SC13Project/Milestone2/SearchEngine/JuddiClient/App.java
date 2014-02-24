package SC13Project.Milestone2.SearchEngine.JuddiClient;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.v3_service.DispositionReportFaultMessage;
public class App {
	
	public static void main(String[] args) {
		CreateBusinessServicePOJO config = new CreateBusinessServicePOJO();
		List<CreateBusinessServicePOJO> listservices = new ArrayList<CreateBusinessServicePOJO>();
		config.setBusinessEntityName("g45_Business");
		config.setBusinessServiceName("BookStoreImpl");
		config.setModelName("BookStore");

		config.setBusinessServiceDescription("BookStore Web service");
		config.setBindingServiceDescription("binding service for Bookstore");
		config.setModelDescription("Simple BookStore Web Service implementation");

		config.setUrl("http://vmjacobsen4.informatik.tu-muenchen.de:8080/axis2/services/g45/BookStoreImpl?wsdl");
		config.setEndpoint("http://vmjacobsen4.informatik.tu-muenchen.de:8080/axis2/services/g45/BookStoreImpl.BookStoreImplHttpSoap12Endpoint/");
		
		listservices.add(config);
		config = new CreateBusinessServicePOJO();
		config.setBusinessEntityName("g45_Business");
		config.setBusinessServiceName("PaymentImpl");
		config.setModelName("Payment");

		config.setBusinessServiceDescription("Payment Web Service");
		config.setBindingServiceDescription("binding service for Payment Web Service");
		config.setModelDescription("Simple Payment Web Service implementation");

		config.setUrl("http://vmjacobsen4.informatik.tu-muenchen.de:8080/axis2/services/g45/PaymentImpl?wsdl");
		config.setEndpoint("http://vmjacobsen4.informatik.tu-muenchen.de:8080/axis2/services/g45/PaymentImpl.PaymentImplHttpSoap12Endpoint/");
		listservices.add(config);
		config = new CreateBusinessServicePOJO();
		
		config.setBusinessEntityName("g45_Business");
		config.setBusinessServiceName("FlightTicketImp");
		config.setModelName("FlightTicket");

		config.setBusinessServiceDescription("Fligth Ticket Web Service");
		config.setBindingServiceDescription("binding service for Flight Ticket Web Service");
		config.setModelDescription("Simple Flight Ticket Web Service implementation");

		config.setUrl("http://vmjacobsen4.informatik.tu-muenchen.de:8080/axis2/services/g45/FlightTicketImpl?wsdl");
		config.setEndpoint("http://vmjacobsen4.informatik.tu-muenchen.de:8080/axis2/services/g45/FlightTicketImpl.FlightTicketImplHttpSoap12Endpoint/");
		listservices.add(config);
		try {
			Transport transport = new BuildClient().buildClearkManager();
			String authToken = new Authenticate(transport).getRootAuthToken();
			DeleteBusiness delBusiness = new DeleteBusiness(transport);
			delBusiness.deleteBusiness(authToken, "g45_Business");
			RegisterService register = new RegisterService(transport);
			register.registerService(authToken, listservices);
		} catch (ConfigurationException e1) {
			e1.printStackTrace();
		} catch (DispositionReportFaultMessage e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		}
	}
}
