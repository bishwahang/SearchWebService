package SC13Project.Milestone2.SearchEngine.JuddiClient;

public class CreateBusinessServicePOJO {
	private String url;
	private String businessEntityName;
	private String businessServiceName;
	private String modelName;
	private String endpoint;
	private String businessServiceDescription;
	private String bindingServiceDescription;
	private String modelDescription;
	
	public String getBusinessEntityName() {
		return businessEntityName;
	}

	public void setBusinessEntityName(String businessEntityName) {
		this.businessEntityName = businessEntityName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBusinessServiceName() {
		return businessServiceName;
	}

	public void setBusinessServiceName(String businessServiceName) {
		this.businessServiceName = businessServiceName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String businessModelName) {
		this.modelName = businessModelName;
	}
	
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getEndpoint() {
		return endpoint;
	}

	
	public String getModelDescription() {
		return modelDescription;
	}

	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}

	public String getBusinessServiceDescription() {
		return businessServiceDescription;
	}

	public void setBusinessServiceDescription(String businessServiceDescription) {
		this.businessServiceDescription = businessServiceDescription;
	}

	public String getBindingServiceDescription() {
		return bindingServiceDescription;
	}

	public void setBindingServiceDescription(String bindingServiceDescription) {
		this.bindingServiceDescription = bindingServiceDescription;
	}
}