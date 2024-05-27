package com.battlebyte.battlebyte.websocket;

import com.alibaba.fastjson.JSONObject;
import com.battlebyte.battlebyte.service.MatchService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.battlebyte.battlebyte.websocket.WebSocketServer.currentGameMap;
import static com.battlebyte.battlebyte.websocket.WebSocketServer.sendMsg;

public class MatchSocket {


    //处理匹配
    public void onMessage_MATCH_REQ(JSONObject data, int id, int uid) throws IOException {
        Integer type = data.getInteger("type");

        //如果上局比赛没结束
        if (currentGameMap.containsKey(uid)) {
            JSONObject output = new JSONObject();
            JSONObject dataOutput = new JSONObject();

            dataOutput.put("ack", id);
            dataOutput.put("msg", "已在匹配对局中");

            output.put("type", "ERROR");
            output.put("data", dataOutput);

            sendMsg(uid, output.toJSONString());
        } else {
            //todo:根据rating进行匹配


            if (type == 1) {
                MatchService.addPlayer1(uid, 1000);
                //输出逻辑
                JSONObject output_MATCH_START = new JSONObject();
                JSONObject dataOutput_MATCH_START = new JSONObject();

                dataOutput_MATCH_START.put("type", type);

                output_MATCH_START.put("type", "MATCH_START");
                output_MATCH_START.put("data", dataOutput_MATCH_START);
                sendMsg(uid, output_MATCH_START.toJSONString());
            } else if (type == 2) {
                MatchService.addPlayer2(uid, 1000);
                //输出逻辑
                JSONObject output_MATCH_START = new JSONObject();
                JSONObject dataOutput_MATCH_START = new JSONObject();

                dataOutput_MATCH_START.put("type", type);

                output_MATCH_START.put("type", "MATCH_START");
                output_MATCH_START.put("data", dataOutput_MATCH_START);
                sendMsg(uid, output_MATCH_START.toJSONString());
            } else {
                JSONObject output = new JSONObject();
                JSONObject dataOutput = new JSONObject();

                dataOutput.put("ack", id);
                dataOutput.put("msg", "没有该游戏模式");

                output.put("type", "ERROR");
                output.put("data", dataOutput);

                sendMsg(uid, output.toJSONString());
            }
        }
    }

    //匹配成功
    public static void return_MATCH_ENTER(int userId, ArrayList<Integer> questionId, Map<String, Integer> playerMap, int gameId, CurrentGame currentGame) throws IOException {
        //更新信息
        //输出逻辑
        JSONObject output_MATCH_ENTER = new JSONObject();
        JSONObject dataOutput_MATCH_ENTER = new JSONObject();
        JSONObject infoOutput_MATCH_ENTER = new JSONObject();

        infoOutput_MATCH_ENTER.put("questionId", questionId);
        infoOutput_MATCH_ENTER.put("currentQuestion", 0);

        dataOutput_MATCH_ENTER.put("info", infoOutput_MATCH_ENTER);
        dataOutput_MATCH_ENTER.put("playerMap", playerMap);

        output_MATCH_ENTER.put("type", "MATCH_ENTER");
        output_MATCH_ENTER.put("data", dataOutput_MATCH_ENTER);
        sendMsg(userId, output_MATCH_ENTER.toJSONString());

        currentGameMap.put(userId, currentGame);
    }
}
