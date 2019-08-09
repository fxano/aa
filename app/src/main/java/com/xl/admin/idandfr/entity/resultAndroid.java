package com.xl.admin.idandfr.entity;


public class resultAndroid{
	private String resultType;//yes or no
	private String resultMessage;//失败原因
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	@Override
	public String toString() {
		return "resultAndroid{" +
				"resultType='" + resultType + '\'' +
				", resultMessage='" + resultMessage + '\'' +
				'}';
	}
}