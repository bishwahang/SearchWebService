package SC13Project.Milestone2.SearchEngine.JuddiClient;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIPublicationPortType;

public class RegisterService {

	private Transport transport;

	public RegisterService(Transport transport) {
		this.transport = transport;
	}

	public void registerService(String authToken,
			List<CreateBusinessServicePOJO> listservices)
			throws DispositionReportFaultMessage, RemoteException,
			TransportException {

		String businessEntityKey = createBusinessEntity(authToken, listservices.get(0));
		System.out.println("created BusinessEntity:  " + businessEntityKey);
		for (CreateBusinessServicePOJO pojocontext : listservices) {
			System.out.println("test: "+pojocontext.getBusinessServiceName());
			// Creating a service to save. Only adding the minimum data: the parent
			// business key retrieved from saving the business
			// above and a single name.
			BusinessService businessService = new BusinessService();
			businessService.setBusinessKey(businessEntityKey); // group name
			Name myServName = new Name();
			myServName.setValue(pojocontext.getBusinessServiceName());
			businessService.getName().add(myServName);
			// Add service description
			Description servDescription = new Description();
			servDescription.setLang("en");
			servDescription.setValue(pojocontext.getBusinessServiceDescription());
			businessService.getDescription().add(servDescription);

			// Adding keys
			String serviceKey = createBusinessService(authToken, businessService);
			System.out.println("created Business Service:  " + serviceKey);

			String bindingKey = createBinding(authToken, pojocontext, businessService,
					serviceKey);
			System.out.println("Binding created:  " + bindingKey);

			// update the service to the "save" structure, using our publisher's
			// authentication info and saving away.
			SaveService updates = new SaveService();
			businessService.setServiceKey(serviceKey);
			updates.getBusinessService().add(businessService);
			updates.setAuthInfo(authToken);

			UDDIPublicationPortType uddiPublish2 = transport
					.getUDDIPublishService();
			ServiceDetail sd = uddiPublish2.saveService(updates); // update the
																	// Service
			serviceKey = sd.getBusinessService().get(0).getServiceKey();
			System.out.println("updated Business Service:  " + serviceKey);
		}
		
	}

	private String createBinding(String authToken,
			CreateBusinessServicePOJO config, BusinessService myService,
			String myServiceKey) throws TransportException,
			DispositionReportFaultMessage, RemoteException {
		// Add binding templates
		BindingTemplates templates = new BindingTemplates();
		myService.setBindingTemplates(templates);
		
		BindingTemplate myTemplate = new BindingTemplate();
		//Create description for binding
		Description templateDescription = new Description();
		templateDescription.setLang("en");
		templateDescription.setValue(config.getBindingServiceDescription());
		
		myTemplate.getDescription().add(templateDescription);
		myTemplate.setServiceKey(myServiceKey);
		UDDIPublicationPortType uddiPublish = transport.getUDDIPublishService();

		TModelDetail tModelDetail = createtModel(authToken, config);
		List<TModel> tModelList = tModelDetail.getTModel();

		TModelInstanceDetails tModelInstanceDetails = new TModelInstanceDetails();
		List<TModelInstanceInfo> tModelInstanceInfoList = tModelInstanceDetails
				.getTModelInstanceInfo();

		for (TModel tm : tModelList) {
			
			TModelInstanceInfo tModelInstanceInfo = new TModelInstanceInfo();
			tModelInstanceInfoList.add(tModelInstanceInfo);
			tModelInstanceInfo.setTModelKey(tm.getTModelKey());
			InstanceDetails instanceDetails = new InstanceDetails();
			instanceDetails.setInstanceParms("g45 tModel instance");
			tModelInstanceInfo.setInstanceDetails(instanceDetails);

		}

		SaveBinding saveBinding = new SaveBinding();
		saveBinding.setAuthInfo(authToken);

		List<BindingTemplate> templateList = templates.getBindingTemplate();
		templateList.add(myTemplate);
		myTemplate.setTModelInstanceDetails(tModelInstanceDetails);
		// String url=config.getEndpoint();
		AccessPoint g45AccessPoint = createAccessPoint(config.getEndpoint());
		myTemplate.setAccessPoint(g45AccessPoint);
		myTemplate.setCategoryBag(tModelList.get(0).getCategoryBag());
		saveBinding.getBindingTemplate().add(myTemplate);

		BindingDetail binddetail = uddiPublish.saveBinding(saveBinding);
		String bindingKey = binddetail.getBindingTemplate().get(0)
				.getBindingKey();
		System.out.println("My binding key:  " + bindingKey);
		return bindingKey;
	}

	/*
	 * Creats the Business Service
	 */
	private String createBusinessService(String authToken,
			BusinessService myService) throws TransportException,
			DispositionReportFaultMessage, RemoteException {

		BusinessServices businessServices = new BusinessServices();
		List<BusinessService> bslist = businessServices.getBusinessService();
		bslist.add(myService);

		// Adding the service to the "save" structure, using our publisher's
		// authentication info and saving away.
		SaveService ss = new SaveService();
		ss.getBusinessService().add(myService);
		ss.setAuthInfo(authToken);

		UDDIPublicationPortType publish = transport.getUDDIPublishService();
		ServiceDetail sd = publish.saveService(ss);
		String myServKey = sd.getBusinessService().get(0).getServiceKey();
		System.out.println("myService key:  " + myServKey);
		return myServKey;
	}

	/*
	 * Creates a Business Entity and publishes it on JUDDI
	 */
	private String createBusinessEntity(String authToken,
			CreateBusinessServicePOJO config)
			throws DispositionReportFaultMessage, RemoteException,
			TransportException {

		UDDIPublicationPortType uddiPublishService = transport
				.getUDDIPublishService();

		BusinessEntity myBusEntity = new BusinessEntity();
		Name myBusName = new Name();
		myBusName.setValue(config.getBusinessEntityName());
		myBusEntity.getName().add(myBusName);

		// Adding the business entity to the "save" structure, using our
		// publisher's authentication info and saving away.
		SaveBusiness sb = new SaveBusiness();
		sb.getBusinessEntity().add(myBusEntity);
		sb.setAuthInfo(authToken);

		BusinessDetail bd = uddiPublishService.saveBusiness(sb);
		String myBusKey = bd.getBusinessEntity().get(0).getBusinessKey();
		System.out.println("myBusiness key:  " + myBusKey);
		return myBusKey;
	}

	/**
	 * Creates a tModel, identifying WSDL endpoint. This is the best practices
	 * model for registering a WSDL describing a business service. Applications
	 * searching registries for WSDL will be searching registries for this data
	 * structure.
	 * 
	 * The data structure contains an endpoint for the Service's WSDL document,
	 * so each service will need a unique tModel.
	 * 
	 * Best Practices are outlined in the document
	 * http://www.oasis-open.org/committees
	 * /uddi-spec/doc/bp/uddi-spec-tc-bp-using-wsdl-v108-20021110.htm
	 * 
	 * @throws TransportException
	 * @throws RemoteException
	 * @throws DispositionReportFaultMessage
	 * 
	 */
	private TModelDetail createtModel(String authinfo,
			CreateBusinessServicePOJO config) throws TransportException,
			DispositionReportFaultMessage, RemoteException {

		UDDIPublicationPortType uddiPublishService = transport
				.getUDDIPublishService();

		TModel helloworldWSDL_tModel = new TModel();

		// set the name of tModel
		Name tModelName = new Name();
		tModelName.setLang("en");
		tModelName.setValue(config.getModelName());
		helloworldWSDL_tModel.setName(tModelName);

		//Set the model description
		Description modelDescription= new Description();
		modelDescription.setLang("en");
		modelDescription.setValue(config.getModelDescription());
		helloworldWSDL_tModel.getDescription().add(modelDescription);
		
		helloworldWSDL_tModel.setDeleted(false);

		// set Overview Docs
		OverviewDoc overviewDoc = new OverviewDoc();
		OverviewURL overviewurl = new OverviewURL();
		overviewurl.setUseType("WSDL source document");
		overviewurl.setValue(config.getUrl());
		overviewDoc.setOverviewURL(overviewurl);

		List<OverviewDoc> overviewdoclist = helloworldWSDL_tModel
				.getOverviewDoc();
		overviewdoclist.add(overviewDoc);

		TModelDetail tModelDetail = new TModelDetail();
		List<TModel> tModelList = tModelDetail.getTModel();
		tModelList.add(helloworldWSDL_tModel);

		// Adding the tModel to the "save" structure, using our publisher's
		// authentication info and saving away.
		SaveTModel saveTModel = new SaveTModel();
		saveTModel.getTModel().addAll(tModelList);
		saveTModel.setAuthInfo(authinfo);
		tModelDetail = uddiPublishService.saveTModel(saveTModel);

		String tModelKey = tModelDetail.getTModel().get(0).getTModelKey();
		System.out.println("tModel key: " + tModelKey);
		helloworldWSDL_tModel.setTModelKey(tModelKey);

		// set CategoryBag
		CategoryBag catBag = new CategoryBag();
		List<KeyedReference> krlist = catBag.getKeyedReference();
		KeyedReference kr = new KeyedReference();
		kr.setKeyName("uuid-org:types");
		kr.setKeyValue("wsdlSpec");
		kr.setTModelKey(tModelKey);
		krlist.add(kr);

		helloworldWSDL_tModel.setCategoryBag(catBag);

		// set the IdentifierBag
		IdentifierBag idBag = new IdentifierBag();
		List<KeyedReference> idkeylist = idBag.getKeyedReference();
		KeyedReference idKey = new KeyedReference();
		idKey.setKeyName("service name");
		idKey.setKeyValue(config.getBusinessServiceName()); // Flightticket
		idKey.setTModelKey(tModelKey);
		idkeylist.add(idKey);

		helloworldWSDL_tModel.setIdentifierBag(idBag);

		// udpate the tModel to the "save" structure, using our publisher's
		// authentication info and saving away.
		SaveTModel updateTModel = new SaveTModel();
		updateTModel.getTModel().addAll(tModelList);
		updateTModel.setAuthInfo(authinfo);
		tModelDetail = uddiPublishService.saveTModel(updateTModel);

		tModelKey = tModelDetail.getTModel().get(0).getTModelKey();
		System.out.println("tModel key: " + tModelKey);

		return tModelDetail;
	}

	private AccessPoint createAccessPoint(String url) {
		AccessPoint accessPoint = new AccessPoint();
		accessPoint.setUseType("http");
		accessPoint.setValue(url);
		return accessPoint;
	}
}
