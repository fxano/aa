package com.xl.admin.idandfr.entity;


public class uploadForAndroid{

	private String serialNumber;//记录流水号（用于实时调取本条调取数据）--必传
	private String serialUserName;//用户--必传
	private String resultType;//结果(成功:yes失败:no)--如不需要对比传yes
	private String serialType;//业务类型（比如住院登记对比：1出院时对比：2参合对比：3……自行添加）--必传
	private String serialInpatient;//业务流水号（比如住院对比时此字段内容为本次住院流水号）--必传
	private String orderNumber;//其他流水号--必传
	private String serialTime;//对比时间--不需要传入



	private String resultName;//身份证姓名
	private String resultSex;//身份证性别
	private String resultBirthday;//身份证出生日期
	private String resultNation;//身份证民族
	private String resultAddress;//身份证地址
	private String resultValidityPeriod;//身份证身份证有效期
	private String resultCardno;//身份证号


	/*
	 *
	 *上传照片与指纹时只需要传入 serialPicture  resultPicture  serialFingerprint  resultFingerprint
	 *获取照片与指纹时使用  resultPicture  resultFingerprint
	 *
	 */

	private String serialPicture;//本次对比照片base64解码处理时使用
	private String resultPicture;//身份证照片base64解码处理时使用
	private String serialPictureName;//对比照片文件名
	private String resultPictureName;//身份证照片文件名
	private String serialPictureUrl;//对比证照片实际地址
	private String resultPictureUrl;//身份证照片实际地址
	private String resultPictureFictitiousPath;//对比照片虚拟路径
	private String serialPictureFictitiousPath;//身份证照片虚拟路径
	private String dataType;//数据来源1，身份证2，终端录入





	private String serialFingerprint;//对比指纹数据byte[]转string后的结果
	private String resultFingerprint;//身份证指纹数据byte[]转string后的结果
	private String serialFingerprintName;//对比指纹数据文件名
	private String resultFingerprintName;//身份证指纹数据文件名
	private String serialFingerprintUrl;//对比指纹数据实际地址
	private String resultFingerprintUrl;//身份证指纹数据实际地址
	private String serialFingerprintFictitiousPath;//对比指纹数据虚拟路径
	private String resultFingerprintFictitiousPath;//身份证指纹数据虚拟路径
	private String resultPictureStencilUrl;//模版地址
	private String resultPictureStencil;//模版

	public String getResultPictureStencilUrl() {
		return resultPictureStencilUrl;
	}

	public void setResultPictureStencilUrl(String resultPictureStencilUrl) {
		this.resultPictureStencilUrl = resultPictureStencilUrl;
	}

	public String getResultPictureStencil() {
		return resultPictureStencil;
	}

	public void setResultPictureStencil(String resultPictureStencil) {
		this.resultPictureStencil = resultPictureStencil;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getSerialFingerprintName() {
		return serialFingerprintName;
	}
	public void setSerialFingerprintName(String serialFingerprintName) {
		this.serialFingerprintName = serialFingerprintName;
	}
	public String getResultFingerprintName() {
		return resultFingerprintName;
	}
	public void setResultFingerprintName(String resultFingerprintName) {
		this.resultFingerprintName = resultFingerprintName;
	}
	public String getSerialFingerprintUrl() {
		return serialFingerprintUrl;
	}
	public void setSerialFingerprintUrl(String serialFingerprintUrl) {
		this.serialFingerprintUrl = serialFingerprintUrl;
	}
	public String getResultFingerprintUrl() {
		return resultFingerprintUrl;
	}
	public void setResultFingerprintUrl(String resultFingerprintUrl) {
		this.resultFingerprintUrl = resultFingerprintUrl;
	}
	public String getSerialFingerprintFictitiousPath() {
		return serialFingerprintFictitiousPath;
	}
	public void setSerialFingerprintFictitiousPath(
			String serialFingerprintFictitiousPath) {
		this.serialFingerprintFictitiousPath = serialFingerprintFictitiousPath;
	}
	public String getResultFingerprintFictitiousPath() {
		return resultFingerprintFictitiousPath;
	}
	public void setResultFingerprintFictitiousPath(
			String resultFingerprintFictitiousPath) {
		this.resultFingerprintFictitiousPath = resultFingerprintFictitiousPath;
	}
	public String getSerialPictureFictitiousPath() {
		return serialPictureFictitiousPath;
	}
	public void setSerialPictureFictitiousPath(String serialPictureFictitiousPath) {
		this.serialPictureFictitiousPath = serialPictureFictitiousPath;
	}
	public String getResultPictureFictitiousPath() {
		return resultPictureFictitiousPath;
	}
	public void setResultPictureFictitiousPath(String resultPictureFictitiousPath) {
		this.resultPictureFictitiousPath = resultPictureFictitiousPath;
	}
	public String getResultPictureUrl() {
		return resultPictureUrl;
	}
	public void setResultPictureUrl(String resultPictureUrl) {
		this.resultPictureUrl = resultPictureUrl;
	}
	public String getSerialPictureUrl() {
		return serialPictureUrl;
	}
	public void setSerialPictureUrl(String serialPictureUrl) {
		this.serialPictureUrl = serialPictureUrl;
	}
	public String getResultPictureName() {
		return resultPictureName;
	}
	public void setResultPictureName(String resultPictureName) {
		this.resultPictureName = resultPictureName;
	}
	public String getSerialPictureName() {
		return serialPictureName;
	}
	public void setSerialPictureName(String serialPictureName) {
		this.serialPictureName = serialPictureName;
	}
	public String getResultPicture() {
		return resultPicture;
	}
	public void setResultPicture(String resultPicture) {
		this.resultPicture = resultPicture;
	}
	public String getSerialFingerprint() {
		return serialFingerprint;
	}
	public void setSerialFingerprint(String serialFingerprint) {
		this.serialFingerprint = serialFingerprint;
	}
	public String getSerialPicture() {
		return serialPicture;
	}
	public void setSerialPicture(String serialPicture) {
		this.serialPicture = serialPicture;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getSerialUserName() {
		return serialUserName;
	}
	public void setSerialUserName(String serialUserName) {
		this.serialUserName = serialUserName;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getResultName() {
		return resultName;
	}
	public void setResultName(String resultName) {
		this.resultName = resultName;
	}
	public String getResultSex() {
		return resultSex;
	}
	public void setResultSex(String resultSex) {
		this.resultSex = resultSex;
	}
	public String getResultBirthday() {
		return resultBirthday;
	}
	public void setResultBirthday(String resultBirthday) {
		this.resultBirthday = resultBirthday;
	}
	public String getResultNation() {
		return resultNation;
	}
	public void setResultNation(String resultNation) {
		this.resultNation = resultNation;
	}
	public String getResultAddress() {
		return resultAddress;
	}
	public void setResultAddress(String resultAddress) {
		this.resultAddress = resultAddress;
	}
	public String getResultValidityPeriod() {
		return resultValidityPeriod;
	}
	public void setResultValidityPeriod(String resultValidityPeriod) {
		this.resultValidityPeriod = resultValidityPeriod;
	}
	public String getResultCardno() {
		return resultCardno;
	}
	public void setResultCardno(String resultCardno) {
		this.resultCardno = resultCardno;
	}
	public String getResultFingerprint() {
		return resultFingerprint;
	}
	public void setResultFingerprint(String resultFingerprint) {
		this.resultFingerprint = resultFingerprint;
	}
	public String getSerialType() {
		return serialType;
	}
	public void setSerialType(String serialType) {
		this.serialType = serialType;
	}
	public String getSerialInpatient() {
		return serialInpatient;
	}
	public void setSerialInpatient(String serialInpatient) {
		this.serialInpatient = serialInpatient;
	}
	public String getSerialTime() {
		return serialTime;
	}
	public void setSerialTime(String serialTime) {
		this.serialTime = serialTime;
	}
	public void setresultPictureData(String[] a ){
		this.resultPictureName = a[0];
		this.resultPictureUrl = a[1];
		this.resultPictureFictitiousPath = a[2];
	}
	public void setserialPictureData(String[] a) {
		this.serialPictureName = a[0];
		this.serialPictureUrl = a[1];
		this.serialPictureFictitiousPath = a[2];
	}
	public void setresultFingerprintData(String[] a ){
		this.resultFingerprintName = a[0];
		this.resultFingerprintUrl = a[1];
		this.resultFingerprintFictitiousPath = a[2];
	}
	public void setserialFingerprintData(String[] a) {
		this.serialFingerprintName = a[0];
		this.serialFingerprintUrl = a[1];
		this.serialFingerprintFictitiousPath = a[2];
	}
	@Override
	public String toString() {
		return "uploadForAndroid [resultAddress=" + resultAddress
				+ ", resultBirthday=" + resultBirthday + ", resultCardno="
				+ resultCardno + ", resultFingerprint=" + resultFingerprint
				+ ", resultName=" + resultName + ", resultNation="
				+ resultNation + ", resultPicture=" + resultPicture
				+ ", resultPictureName=" + resultPictureName
				+ ", resultPictureUrl=" + resultPictureUrl + ", resultSex="
				+ resultSex + ", resultType=" + resultType
				+ ", resultValidityPeriod=" + resultValidityPeriod
				+ ", serialFingerprint=" + serialFingerprint
				+ ", serialInpatient=" + serialInpatient + ", serialNumber="
				+ serialNumber + ", serialPicture=" + serialPicture
				+ ", serialPictureName=" + serialPictureName
				+ ", serialPictureUrl=" + serialPictureUrl + ", serialTime="
				+ serialTime + ", serialType=" + serialType
				+ ", serialUserName=" + serialUserName + "]";
	}



}