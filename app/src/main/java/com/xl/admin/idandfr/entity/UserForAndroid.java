package com.xl.admin.idandfr.entity;


public class UserForAndroid{
	private String userId;
	private String userName;
	private String passWord;
	private String macId;
	private String result;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getMacId() {
		return macId;
	}
	public void setMacId(String macId) {
		this.macId = macId;
	}

	@Override
	public String toString() {
		return "UserForAndroid{" +
				"userId='" + userId + '\'' +
				", userName='" + userName + '\'' +
				", passWord='" + passWord + '\'' +
				", macId='" + macId + '\'' +
				", result='" + result + '\'' +
				'}';
	}
}