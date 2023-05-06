package com.freelycar.demo.constants;

import com.freelycar.demo.response.BoxCommand;
import com.freelycar.demo.response.BoxCommandDate;
import com.freelycar.demo.response.BoxCommandResponse;
import com.freelycar.demo.response.BoxResponseTest;
import com.freelycar.demo.util.MyLogUtils;
import com.freelycar.demo.util.SafeConnection;

import java.io.IOException;
import java.util.Arrays;

public class TestApi {

    public static final String SERVER_BASE_URL = "http://123.60.169.72:8089";

    public static final String URL_OPEN_BOX = SERVER_BASE_URL + "/api/box/open";//2
    public static final String URL_QUERY_BOX = SERVER_BASE_URL + "/api/box/query";//4
    public static final String URL_QUERY_BOARD = SERVER_BASE_URL + "/api/board/query";//1


    public static void testQueryBoxX(String conDev, String conBoard) throws IOException {
        MyLogUtils.file("testQueryBoxX content",conDev+" - "+conBoard);
        BoxResponseTest cmd = new BoxResponseTest(conDev, conBoard);
        BoxCommandResponse response = SafeConnection.postAndGetResponse(URL_QUERY_BOARD, cmd, BoxCommandResponse.class);
        System.out.println("testQueryBoxX  content:  " + Arrays.toString(response.open_array) + "  -  " + response.is_open + "  -  " + response.msg + "  -  " + response.code + "  - ");
        //logNotMatchTest(cmd,response);
        //assertTrue(response.code == 0);
        //assertTrue(response.deviceId.equals(cmd.deviceId));
        //assertTrue(response.boardId.equals(cmd.boardId));
        //assertTrue(response.boxId.equals(cmd.boxId));
    }

    static void testQueryBoxX(String conDev, String conBox, String conBoard) throws IOException {
        MyLogUtils.file("testQueryBoxX box",conDev+" - "+conBoard+"  -  "+conBox);
        BoxCommand cmd = new BoxCommand(conDev, conBoard, conBox);
        BoxCommandResponse response = SafeConnection.postAndGetResponse(URL_OPEN_BOX, cmd, BoxCommandResponse.class);
        System.out.println("testQueryBoxX   box:  " + Arrays.toString(response.open_array) + "  -  " + response.is_open + "  -  " + response.msg + "  -  " + response.code + "  - ");
        //logNotMatch(cmd,response);
        //assertTrue(response.code == 0);
        //assertTrue(response.deviceId.equals(cmd.deviceId));
        //	assertTrue(response.boardId==cmd.boardId);
        //	assertTrue(response.boxId==cmd.boxId);
    }

    public static void testQueryBoxX(String conDev,int boxId) throws IOException {
        BoxCommandDate cmd = new BoxCommandDate(conDev, 14, boxId, 23, 1);
        BoxCommandResponse response = SafeConnection.postAndGetResponse(URL_QUERY_BOX, cmd, BoxCommandResponse.class);
        System.out.println("testQueryBoxX:  " + Arrays.toString(response.open_array) + "  -  " + response.is_open + "  -  " + response.msg + "  -  " + response.code + "  - ");
        //BoxCommandDate(cmd,response);
        //assertTrue(response.code == 0);
        //assertTrue(response.deviceId.equals(cmd.deviceId));
        //assertTrue(response.boardId.equals(cmd.boardId));
        //assertTrue(response.boxId.equals(cmd.boxId));
    }

    void logNotMatch(BoxCommand cmd, BoxCommandResponse response) {
        if (!cmd.deviceId.equals(response.deviceId)) {
            //	logger.error("CMD = "+cmd);
            //logger.error("RESPONSE="+response);
        }
    }
}
