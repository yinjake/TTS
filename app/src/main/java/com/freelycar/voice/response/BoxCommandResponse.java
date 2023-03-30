package com.freelycar.voice.response;

import java.util.Arrays;

public class BoxCommandResponse {
	public int code;
	public String msg;
	public String deviceId;
	public String boardId;
	public String boxId;
	public boolean is_open;
	public boolean[] open_array;
	@Override
	public String toString() {
		return "BoxCommandResponse [code=" + code + ", msg=" + msg + ", deviceId=" + deviceId + ", boardId=" + boardId
				+ ", boxId=" + boxId + ", open_array=" + Arrays.toString(open_array) + ", is_open=" + is_open + "]";
	}
}
