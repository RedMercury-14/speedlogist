package by.base.main.model;

import com.google.gson.annotations.SerializedName;

/**
 * ClientRequest который используется для управляния двором
 */
public class ClientRequest {

	 @SerializedName("action")
	 private String action;

	 @SerializedName("data")
	 private String data;
	 
	 

	/**
	 * @param action
	 * @param data
	 */
	public ClientRequest(String action, String data) {
		super();
		this.action = action;
		this.data = data;
	}
	
	public ClientRequest() {
		super();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ClientRequest [action=" + action + ", data=" + data + "]";
	}
	 
	
}
