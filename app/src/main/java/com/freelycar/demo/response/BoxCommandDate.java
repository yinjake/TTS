package com.freelycar.demo.response;

public class BoxCommandDate {

	public static final int BOARD_TYPE_DEFAULT = 0x68;


	public  String deviceId;
	public  int boardId = 0;//myData
	public  int boxId = 0;//hour
	public  int year = 0;
	public  int month = 0;
	//public  int myData = 0;
	//public  int hour = 0;
	//public  int minute = 0;
	public int boardType = BOARD_TYPE_DEFAULT;

	public BoxCommandDate(String deviceId, int boardId, int boxId, int year, int month) {
		this.deviceId = deviceId;
		this.boxId = boxId;
		this.boardId = boardId;
		this.year = year;
		this.month = month;
		//this.myData = myData;
		//this.hour = hour;
	}

	@Override
	public String toString() {
		return "BoxCommand [deviceId=" + deviceId + ", boardId=" + boardId + ", boxId=" + boxId +", year=" + year +", month=" + month
				 + ", boardType=" + boardType + "]";
	}

}
