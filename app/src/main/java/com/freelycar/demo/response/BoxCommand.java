package com.freelycar.demo.response;

public class BoxCommand {
	
	public static final int BOARD_TYPE_DEFAULT = 0x68;
	

	public  String deviceId;
	public  String boardId = "0";
	public  String boxId = "0";
	public int boardType = BOARD_TYPE_DEFAULT;
	
	public BoxCommand(String deviceId,String boardId, String boxId) {
		this.deviceId = deviceId;
		this.boardId = boardId;
		this.boxId = boxId;
	}

	@Override
	public String toString() {
		return "BoxCommand [deviceId=" + deviceId + ", boardId=" + boardId + ", boxId=" + boxId + ", boardType="
				+ boardType + "]";
	}

}
