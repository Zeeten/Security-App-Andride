package com.acmatics.securityguardexchange.bean;



public class UserBean {
	private String name;
	private String email;
	private String companyName;
	private String phone;
	private String gcmRegistrationId;
	private AddressBean addressBean;
	private int userId;
	private boolean userCreated;
	private boolean userRegistered;
	private boolean scratchCodeVerified;

	public boolean isUserRegistered() {
		return userRegistered;
	}

	public void setUserRegistered(boolean userRegistered) {
		this.userRegistered = userRegistered;
	}

	public boolean isScratchCodeVerified() {
		return scratchCodeVerified;
	}

	public void setScratchCodeVerified(boolean scratchCodeVerified) {
		this.scratchCodeVerified = scratchCodeVerified;
	}

	public boolean isUserCreated() {
		return userCreated;
	}

	public void setUserCreated(boolean userCreated) {
		this.userCreated = userCreated;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}


	/**
	 * @return the name
	 */

	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}
	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the gcmRegistrationId
	 */
	public String getGcmRegistrationId() {
		return gcmRegistrationId;
	}
	/**
	 * @param gcmRegistrationId the gcmRegistrationId to set
	 */
	public void setGcmRegistrationId(String gcmRegistrationId) {
		this.gcmRegistrationId = gcmRegistrationId;
	}
	/**
	 * @return the addressBean
	 */
	public AddressBean getAddressBean() {
		return addressBean;
	}
	/**
	 * @param addressBean the addressBean to set
	 */
	public void setAddressBean(AddressBean addressBean) {
		this.addressBean = addressBean;
	}
}
