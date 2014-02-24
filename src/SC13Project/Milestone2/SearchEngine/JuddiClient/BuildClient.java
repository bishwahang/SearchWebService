package SC13Project.Milestone2.SearchEngine.JuddiClient;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.transport.Transport;

public class BuildClient {

	public Transport buildClearkManager() throws ConfigurationException {
		UDDIClerkManager uddiClerkManager = new UDDIClerkManager("META-INF/uddi.xml");
		uddiClerkManager.start();
		return uddiClerkManager.getTransport();
	}
}
