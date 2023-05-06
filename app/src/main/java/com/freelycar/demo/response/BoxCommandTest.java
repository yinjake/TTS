package com.freelycar.demo.response;

public class BoxCommandTest {

	public static final int BOARD_TYPE_DEFAULT = 0x68;
	public  String deviceId;
	public  String boxTmp = "0";
	public int boardType = BOARD_TYPE_DEFAULT;

	public BoxCommandTest(String deviceId, String boxTmp) {
		this.deviceId = deviceId;
		this.boxTmp = boxTmp;
	}

	@Override
	public String toString() {
		return "BoxCommand [deviceId=" + deviceId  + ", boxTmp=" + boxTmp + "]";
	}

}
