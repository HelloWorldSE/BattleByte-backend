package com.battlebyte.battlebyte.websocket;

import com.alibaba.fastjson.JSONObject;
import com.battlebyte.battlebyte.config.BeanContext;
import com.battlebyte.battlebyte.entity.Game;
import com.battlebyte.battlebyte.entity.Question;
import com.battlebyte.battlebyte.entity.Room;
import com.battlebyte.battlebyte.entity.UserGameRecord;
import com.battlebyte.battlebyte.entity.dto.UserGameDTO;
import com.battlebyte.battlebyte.entity.dto.UserProfileDTO;
import com.battlebyte.battlebyte.service.GameService;
import com.battlebyte.battlebyte.service.OJService;
import com.battlebyte.battlebyte.service.RoomService;
import com.battlebyte.battlebyte.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;


import static com.battlebyte.battlebyte.websocket.WebSocketServer.*;

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
        Map<String, Integer> playerMap = currentGameMap.get(uid).getPlayerMap();

        if (type.equals("global")) { //全局聊天
            for (Integer player : playerMap.values()) {
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
                sendMsg(player, output.toJSONString());
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
        Map<String, Integer> playerMap = currentGameMap.get(uid).getPlayerMap();

        for (Integer player : playerMap.values()){
            if (player != uid) {
                //输出逻辑
                JSONObject output = new JSONObject();
                JSONObject dataOutput = new JSONObject();

                dataOutput.put("row", row);
                dataOutput.put("col", col);
                dataOutput.put("total_rows", total_rows);
                dataOutput.put("user_id", uid);

                output.put("type", "POS_SYNC");
                output.put("data", dataOutput);

                sendMsg(player, output.toJSONString());
            }
        }
    }

    public void onMessage_SURRENDER(JSONObject data, int id, int uid) throws IOException {
        //获取同局人员
        Map<String, Integer> playerMap = currentGameMap.get(uid).getPlayerMap();
        //获取赢的队伍
        if (currentGameMap.get(uid).getGameType().equals(1)) {//如果是单人模式
            for (Integer player : playerMap.values()) {
                //如果是赢
                if (player == uid) {
                    returnGameEnd(player, "lose");
                } else {//假如是输
                    returnGameEnd(player, "win");
                }
                //清除当前比赛
                currentGameMap.remove(player);
            }
        } else if (currentGameMap.get(uid).getGameType().equals(2)) {//如果是大逃杀模式
            currentGameMap.get(uid).getHPMAP().put(uid, 0);
            for (Integer player : playerMap.values()) {
                //输出逻辑
                JSONObject output = new JSONObject();
                JSONObject dataOutput = new JSONObject();

                dataOutput.put("change_id", uid);
                dataOutput.put("hp", 0);

                output.put("type", "HP_CHANGE");
                output.put("data", dataOutput);
                sendMsg(player, output.toJSONString());
            }
        }
    }

    //处理道具
    public void onMessage_ITEM_SEND(JSONObject data, int id, int uid) throws IOException {
        //读取json文件
        String type = data.getString("type");

        //获取同局人员
        Map<String, Integer> playerMap = currentGameMap.get(uid).getPlayerMap();

        for (Integer player : playerMap.values()) {
            //如果不同队
            if (player != uid) {
                //输出逻辑
                JSONObject output = new JSONObject();
                JSONObject dataOutput = new JSONObject();
                dataOutput.put("type", type);

                output.put("type", "ITEM_USED");
                output.put("data", dataOutput);

                sendMsg(player, output.toJSONString());
            }
        }
    }

    //房间更新
    public void onMessage_ROOM_REQUEST(JSONObject data, int id, int uid) throws IOException {
        //读取json文件
        Integer roomid = data.getInteger("roomid");
        String type = data.getString("type");

        //修改人
        if (type.equals("in")) {
            addUserInRoom(roomid, uid);
        } else if (type.equals("out")) {
            delUserInRoom(roomid, uid);
        }

        //输出
        JSONObject output = new JSONObject();
        JSONObject dataOutput = new JSONObject();

        dataOutput.put("roomid", roomid);
        ArrayList<Integer> users = getRoomUsersId(roomid);
        dataOutput.put("userid", users);
        dataOutput.put("username", getRoomUsersName(roomid));
        dataOutput.put("avatarUrl", getRoomUsersAvatar(roomid));

        output.put("type", "ROOM_REFRESH");
        output.put("data", dataOutput);
        for (Integer userId : users) {
            sendMsg(userId, output.toJSONString());
        }
    }

    //获取房间信息
    public void onMessage_ROOM_GET_INFO(JSONObject data, int id, int uid) throws IOException {
        //读取json文件
        Integer roomid = data.getInteger("roomid");
        String type = data.getString("type");

        //输出
        JSONObject output = new JSONObject();
        JSONObject dataOutput = new JSONObject();

        dataOutput.put("roomid", roomid);
        dataOutput.put("users", getRoomUsersId(roomid));
        dataOutput.put("username", getRoomUsersName(roomid));
        dataOutput.put("avatarUrl", getRoomUsersAvatar(roomid));

        output.put("type", "ROOM_REFRESH");
        output.put("data", dataOutput);

        sendMsg(uid, output.toJSONString());
    }

    //房间开始游戏
    public void onMessage_ROOM_START(JSONObject data, int id) throws IOException {
        //读取json文件
        Integer roomid = data.getInteger("roomid");
        String type = data.getString("type");

        //获取房间
        Room room = roomService.findRoomById(roomid);
        int gameId = room.getGameId();

        //todo:修改currentGameMap+返回MatchEnter,数据库增加teamId
        ArrayList<Integer> users = getRoomUsersId(roomid);
        if (users.size() == 7) {
            //增加teamId到数据库
            for (int i = 0; i < users.size(); i++) {
                gameService.setTeam(gameId, users.get(i), i);
            }

            //创建比赛

            Map<String, Integer> playerMap = new HashMap<>();
            for (int i = 0; i < 7; i++) {
                playerMap.put(Integer.toString(i), users.get(i));
            }

            CurrentGame currentGame = new CurrentGame();
            currentGame.setGameId(gameId);
            currentGame.setQuestionId(getRoomQuestionId(roomid));
            if (playerMap.size() == 2) {
                currentGame.setGameType(1);
            } else {
                currentGame.setGameType(2);
            }
            currentGame.setPlayerMap(playerMap);
            currentGame.setCurrentTime(LocalDateTime.now());
            Map<Integer, Integer> HPMAP = new HashMap<>();
            for (Integer eachUser : playerMap.values()) {
                HPMAP.put(eachUser, 100);
            }
            currentGame.setHPMAP(HPMAP);
            Map<Integer, Integer> acMAP = new HashMap<>();
            for (Integer eachUser : playerMap.values()) {
                acMAP.put(eachUser, 0);
            }
            currentGame.setAcMAP(acMAP);


            //返回MatchEnter
            for (int userdId : users) {
                return_MATCH_ENTER(userdId, getRoomQuestionId(roomid), playerMap, currentGame);
            }

        } else {
            System.out.println("Room " + roomid + " size is not 7");
        }
    }

    //返回房间内所有userId
    public ArrayList<Integer> getRoomUsersId(int roomId) {
        Room room = roomService.findRoomById(roomId);
        int gameId = room.getGameId();
        List<UserGameDTO> players = gameService.getPlayer(gameId);

        ArrayList<Integer> users = new ArrayList<>();
        for (UserGameDTO userGameDTO : players) {
            users.add(userGameDTO.getId());
        }
        System.out.println("There are " + users.size() + " people in room " + roomId);
        return users;
    }

    //返回房间内所有userId
    public ArrayList<String> getRoomUsersName(int roomId) {
        Room room = roomService.findRoomById(roomId);
        int gameId = room.getGameId();
        List<UserGameDTO> players = gameService.getPlayer(gameId);

        ArrayList<String> users = new ArrayList<>();
        for (UserGameDTO userGameDTO : players) {
            users.add(userGameDTO.getUserName());
        }
        return users;
    }

    //返回房间内所有userId
    public ArrayList<String> getRoomUsersAvatar(int roomId) {
        Room room = roomService.findRoomById(roomId);
        int gameId = room.getGameId();
        List<UserGameDTO> players = gameService.getPlayer(gameId);

        ArrayList<String> users = new ArrayList<>();
        for (UserGameDTO userGameDTO : players) {
            users.add(userGameDTO.getAvatar());
        }
        return users;
    }

    //返回房间内所有userId
    public ArrayList<Integer> getRoomQuestionId(int roomId) {
        Room room = roomService.findRoomById(roomId);
        int gameId = room.getGameId();
        List<Question> questions = gameService.findByGameId(gameId);

        ArrayList<Integer> questionId = new ArrayList<>();
        for (Question question : questions) {
            questionId.add(question.getId());
        }
        return questionId;
    }

    //给房间新增用户
    public void addUserInRoom(int roomId, int userId) {
        Room room = roomService.findRoomById(roomId);
        int gameId = room.getGameId();
        UserGameRecord userGameRecord = new UserGameRecord();

        userGameRecord.setGameId(gameId);
        userGameRecord.setUserId(userId);

        gameService.save(userGameRecord);

        System.out.println("Room " + roomId + " add user " + userId);
    }

    public void delUserInRoom(int roomId, int userId) {
        Room room = roomService.findRoomById(roomId);
        int gameId = room.getGameId();
        gameService.deleteByGameIdAndUserId(gameId, userId);
        System.out.println("Room " + roomId + " delete user " + userId);
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