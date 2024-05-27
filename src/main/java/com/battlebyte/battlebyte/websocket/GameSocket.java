package com.battlebyte.battlebyte.websocket;

import com.alibaba.fastjson.JSONObject;
import com.battlebyte.battlebyte.config.BeanContext;
import com.battlebyte.battlebyte.entity.Room;
import com.battlebyte.battlebyte.entity.dto.UserGameDTO;
import com.battlebyte.battlebyte.entity.dto.UserProfileDTO;
import com.battlebyte.battlebyte.service.GameService;
import com.battlebyte.battlebyte.service.OJService;
import com.battlebyte.battlebyte.service.RoomService;
import com.battlebyte.battlebyte.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.battlebyte.battlebyte.websocket.WebSocketServer.currentGameMap;
import static com.battlebyte.battlebyte.websocket.WebSocketServer.sendMsg;

public class GameSocket {
    private OJService ojService;
    private GameService gameService;
    private UserService userService;
    private RoomService roomService;

    public GameSocket() {
        ojService = BeanContext.getApplicationContext().getBean(OJService.class);
        gameService = BeanContext.getApplicationContext().getBean(GameService.class);
        userService = BeanContext.getApplicationContext().getBean(UserService.class);
        roomService = BeanContext.getApplicationContext().getBean(RoomService.class);
    }

    // 处理聊天
    public void onMessage_CHAT_REQ(JSONObject data, int id, int uid) throws IOException {
        //读取json文件
        String type = data.getString("type");
        String message = data.getString("message");

        //获取同局人员
        Integer gameId = currentGameMap.get(uid).getGameId();
        List<UserGameDTO> players = gameService.getPlayer(gameId);
        //获取当前uid的队号
        int teamId = 0;
        for (UserGameDTO userGameDTO : players) {
            if (userGameDTO.getId() == uid) {
                teamId = userGameDTO.getTeam();
                break;
            }
        }
        if (type.equals("team")) { //队内聊天
            for (UserGameDTO userGameDTO : players) {
                if (userGameDTO.getTeam() == teamId) { //如果是同队的
                    //输出逻辑
                    JSONObject output = new JSONObject();
                    JSONObject dataOutput = new JSONObject();

                    //通过uid读取名字
                    UserProfileDTO userProfileDTO = userService.findByUserId(uid);

                    dataOutput.put("fromName", userProfileDTO.getUserName());
                    dataOutput.put("fromId", uid);
                    dataOutput.put("message", message);

                    output.put("type", "CHAT_MSG");
                    output.put("data", dataOutput);
                    sendMsg(userGameDTO.getId(), output.toJSONString());
                }
            }
        } else if (type.equals("global")) { //全局聊天
            for (UserGameDTO userGameDTO : players) {
                //输出逻辑
                JSONObject output = new JSONObject();
                JSONObject dataOutput = new JSONObject();

                //通过uid读取名字
                UserProfileDTO userProfileDTO = userService.findByUserId(uid);

                dataOutput.put("fromName", userProfileDTO.getUserName());
                dataOutput.put("fromId", uid);
                dataOutput.put("message", message);

                output.put("type", "CHAT_MSG");
                output.put("data", dataOutput);
                sendMsg(userGameDTO.getId(), output.toJSONString());
            }
        }
    }

    // 刷新评测结果
    public void onMessage_ANSWER_REFRESH(JSONObject data, int id, int uid) throws IOException {
        String submit_id = data.getString("submit_id");

        //输出逻辑
        JSONObject output = new JSONObject();
        JSONObject dataOutput = new JSONObject();
        JSONObject result = ojService.getResult(submit_id);

        dataOutput.put("result", result);
        output.put("type", "ANSWER_RESULT");
        output.put("data", dataOutput);
        sendMsg(uid, output.toJSONString());

        //判断是否结束
        JSONObject dataResult = result.getJSONObject("data");
        JSONObject statistic_info = dataResult.getJSONObject("statistic_info");
        JSONObject info = dataResult.getJSONObject("info");
        //已评测完
        if (!(info.isEmpty() && statistic_info.isEmpty())) {
            //已结束
            if (dataResult.getInteger("result") == 0) {
                if (currentGameMap.get(uid).getGameType().equals(1)) {//如果是单人模式
                    winTeam(uid);
                } else if (currentGameMap.get(uid).getGameType().equals(2)) {//如果是大逃杀模式
                    acQuestion(uid);
                }
            }
        }
    }

    //处理光标移动
    public void onMessage_POS_UPDATE(JSONObject data, int id, int uid) throws IOException {
        int row = data.getInteger("row");
        int col = data.getInteger("col");
        int total_rows = data.getInteger("total_rows");

        //获取同局人员
        Integer gameId = currentGameMap.get(uid).getGameId();
        List<UserGameDTO> players = gameService.getPlayer(gameId);

        for (UserGameDTO userGameDTO : players) {
            if (userGameDTO.getId() != uid) {
                //输出逻辑
                JSONObject output = new JSONObject();
                JSONObject dataOutput = new JSONObject();

                dataOutput.put("row", row);
                dataOutput.put("col", col);
                dataOutput.put("total_rows", total_rows);
                dataOutput.put("user_id", uid);

                output.put("type", "POS_SYNC");
                output.put("data", dataOutput);

                sendMsg(userGameDTO.getId(), output.toJSONString());
            }
        }
    }

    public void onMessage_SURRENDER(JSONObject data, int id, int uid) throws IOException {
        Integer gameId = currentGameMap.get(uid).getGameId();
        List<UserGameDTO> players = gameService.getPlayer(gameId);
        //获取赢的队伍
        int surrenderTeamId = 0;
        //todo:多人模式记得修改这部分逻辑
        for (UserGameDTO userGameDTO : players) {
            if (userGameDTO.getId() == uid) {
                surrenderTeamId = userGameDTO.getTeam();
                break;
            }
        }
        for (UserGameDTO userGameDTO : players) {
            //如果是赢
            if (userGameDTO.getTeam() == surrenderTeamId) {
                returnGameEnd(userGameDTO.getId(), "lose");
            } else {//假如是输
                returnGameEnd(userGameDTO.getId(), "win");
            }
            //清楚当前比赛
            currentGameMap.remove(userGameDTO.getId());
        }
    }

    //处理道具
    public void onMessage_ITEM_SEND(JSONObject data, int id, int uid) throws IOException {
        //读取json文件
        String type = data.getString("type");

        //获取同局人员
        Integer gameId = currentGameMap.get(uid).getGameId();
        List<UserGameDTO> players = gameService.getPlayer(gameId);
        //获取当前uid的队号
        int teamId = 0;
        for (UserGameDTO userGameDTO : players) {
            if (userGameDTO.getId() == uid) {
                teamId = userGameDTO.getTeam();
                break;
            }
        }

        for (UserGameDTO userGameDTO : players) {
            //如果不同队
            if (userGameDTO.getTeam() != teamId) {
                //输出逻辑
                JSONObject output = new JSONObject();
                JSONObject dataOutput = new JSONObject();
                dataOutput.put("type", type);

                output.put("type", "ITEM_USED");
                output.put("data", dataOutput);

                sendMsg(userGameDTO.getId(), output.toJSONString());
            }
        }
    }

    //房间更新
    public void onMessage_ROOM_REQUEST(JSONObject data, int id, int uid) throws IOException {
        //读取json文件
        Integer roomid = data.getInteger("roomid");
        String type = data.getString("type");

        //获取房间
        Room room = roomService.findRoomById(roomid);

        //修改人
        if (type.equals("in")) {

        } else if (type.equals("out")) {

        }

        Integer gameId = room.getGameId();
        List<UserGameDTO> players = gameService.getPlayer(gameId);

        //输出
        JSONObject output = new JSONObject();
        JSONObject dataOutput = new JSONObject();

        dataOutput.put("roomid", roomid);
        ArrayList<Integer> users = new ArrayList<>();
        for (UserGameDTO userGameDTO : players) {
            users.add(userGameDTO.getId());
        }
        dataOutput.put("users", users);

        output.put("type", "ROOM_REFRESH");
        output.put("data", dataOutput);
        for (UserGameDTO userGameDTO : players) {
            sendMsg(userGameDTO.getId(), output.toJSONString());
        }
    }

    //某个玩家赢了。
    public void winTeam(int userId) throws IOException {
        Integer gameId = currentGameMap.get(userId).getGameId();
        List<UserGameDTO> players = gameService.getPlayer(gameId);
        //获取赢的队伍
        int winTeamId = 0;
        for (UserGameDTO userGameDTO : players) {
            if (userGameDTO.getId() == userId) {
                winTeamId = userGameDTO.getTeam();
                break;
            }
        }
        for (UserGameDTO userGameDTO : players) {
            //如果是赢
            if (userGameDTO.getTeam() == winTeamId) {
                returnGameEnd(userGameDTO.getId(), "win");
            } else {//假如是输
                returnGameEnd(userGameDTO.getId(), "lose");
            }
            //清楚当前比赛
            currentGameMap.remove(userGameDTO.getId());
        }
    }

    //某个玩家AC了。
    public void acQuestion(int userId) throws IOException {
        CurrentGame currentGame = currentGameMap.get(userId);
        currentGame.getAcMAP().put(userId, currentGame.getAcMAP().get(userId) + 1);

        //currentGame.getAcMAP().values().stream().max(Integer::compareTo);
    }

    public void returnGameEnd(int userId, String result) throws IOException {
        JSONObject output = new JSONObject();
        JSONObject dataOutput = new JSONObject();

        dataOutput.put("result", result);
        output.put("type", "GAME_END");
        output.put("data", dataOutput);

        sendMsg(userId, output.toJSONString());
    }
}
