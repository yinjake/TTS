package com.freelycar.voice.response;

public class BoxResponseTest {

	public static final int BOARD_TYPE_DEFAULT = 0x68;


	public  String deviceId;
//	public  int boardId = 0;
//	public  int boxId = 0;

	public  String boxTmp = "0";
	//public int boardType = BOARD_TYPE_DEFAULT;

	public BoxResponseTest(String deviceId,String boxTmp) {
		this.deviceId = deviceId;
//		this.boardId = boardId;
//		this.boxId = boxId;
		this.boxTmp = boxTmp;
	}

	@Override
	public String toString() {
		return "BoxCommand [deviceId=" + deviceId  + ", boxTmp=" + boxTmp  + "]";
	}

}
