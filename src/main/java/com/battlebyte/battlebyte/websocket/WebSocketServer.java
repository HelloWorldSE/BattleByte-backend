package com.battlebyte.battlebyte.websocket;

import com.battlebyte.battlebyte.config.BeanContext;
import com.battlebyte.battlebyte.entity.dto.UserGameDTO;
import com.battlebyte.battlebyte.entity.dto.UserProfileDTO;
import com.battlebyte.battlebyte.service.GameService;
import com.battlebyte.battlebyte.service.MatchService;
import com.battlebyte.battlebyte.service.OJService;
import com.battlebyte.battlebyte.service.UserService;
import com.battlebyte.battlebyte.service.match.Player;
import io.micrometer.common.util.StringUtils;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import static com.battlebyte.battlebyte.service.MatchService.removePlayer;
import static com.battlebyte.battlebyte.util.JwtUtil.getUserId;

@Component
@ServerEndpoint("/server")
@Slf4j
public class WebSocketServer {

    /**
     * 记录当前在线连接数
     */
    private static int onlineCount = 0;

    /**
     * 使用线程安全的ConcurrentHashMap来存放每个客户端对应的WebSocket对象
     */
    private static ConcurrentHashMap<Integer, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, CurrentGame> currentGameMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收客户端消息的uid
     */
    private Integer uid = 0;
    /**
     * OJ服务
     */
    private OJService ojService = new OJService();
    private GameService gameService;
    private UserService userService;

    public WebSocketServer() {
        ojService = BeanContext.getApplicationContext().getBean(OJService.class);
        gameService = BeanContext.getApplicationContext().getBean(GameService.class);
        userService = BeanContext.getApplicationContext().getBean(UserService.class);
    }

    @OnOpen
    public void onOpen(Session session) {
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(uid)) {
            webSocketMap.remove(uid);
            //取消匹配
            removePlayer(uid);
            //从set中删除
            subOnlineCount();
            log.info("用户【" + uid + "】退出，当前在线人数为:" + getOnlineCount());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        // 处理信息
        if (StringUtils.isNotBlank(message)) {
            try {
                log.info("Session【" + session + "】发送报文:" + message);

                //读取json文件
                JSONObject mssageObj = JSON.parseObject(message);
                String type = mssageObj.getString("type");
                JSONObject data = mssageObj.getJSONObject("data");
                int id = mssageObj.getIntValue("id");
                //设置session
                this.session = session;
                if (type.equals("LOGIN_REQ")) {
                    onMessage_LOGIN_REQ(data, id);
                } else if (type.equals("MATCH_REQ")) {
                    onMessage_MATCH_REQ(data, id);
                } else if (type.equals("CHAT_REQ")) {
                    onMessage_CHAT_REQ(data, id);
                } else if (type.equals("ANSWER_REFRESH")) {
                    onMessage_ANSWER_REFRESH(data, id);
                } else if (type.equals("POS_UPDATE")) {
                    onMessage_POS_UPDATE(data, id);
                } else if (type.equals("SURRENDER")) {
                    onMessage_SURRENDER(data, id);
                } else if (type.equals("ITEM_SEND")) {
                    onMessage_ITEM_SEND(data, id);
                }

            } catch (Exception e) {
                log.error("用户【" + uid + "】发送消息异常！", e);
            }
        }
    }

    //处理登录
    private void onMessage_LOGIN_REQ(JSONObject data, int id) throws IOException {
        String token = data.getString("token");
        String filePath = "/home/ubuntu";
        File file = new File(filePath);
        Integer uid;
        if (file.exists()) {
            //获取uid
            uid = getUserId(token);
        } else {
            //获取uid 测试
            uid = Integer.valueOf(token);
        }

        this.uid = uid;
        if (webSocketMap.containsKey(uid)) {
            //断掉之前的
            WebSocketServer beforeSession = webSocketMap.get(uid);
            beforeSession.getSession().close();
            webSocketMap.remove(uid);
        }
        //加入set中
        webSocketMap.put(uid, this);
        //在线数加1
        addOnlineCount();
        log.info("用户【" + uid + "】连接成功，当前在线人数为:" + getOnlineCount());
        try {
            JSONObject output_LOGIN_ACK = new JSONObject();
            JSONObject dataOutput_LOGIN_ACK = new JSONObject();

            dataOutput_LOGIN_ACK.put("code", 0);

            output_LOGIN_ACK.put("type", "LOGIN_ACK");
            output_LOGIN_ACK.put("data", dataOutput_LOGIN_ACK);
            sendMsg(output_LOGIN_ACK.toJSONString());
        } catch (IOException e) {
            log.error("用户【" + uid + "】网络异常!", e);
        }
        //如果上局比赛没结束
        if (currentGameMap.containsKey(uid)) {
            CurrentGame currentGame = currentGameMap.get(uid);
            JSONObject output_MATCH_ENTER = new JSONObject();
            JSONObject dataOutput_MATCH_ENTER = new JSONObject();
            JSONObject infoOutput_MATCH_ENTER = new JSONObject();

            infoOutput_MATCH_ENTER.put("questionId", currentGame.getQuestionId());

            dataOutput_MATCH_ENTER.put("info", infoOutput_MATCH_ENTER);
            dataOutput_MATCH_ENTER.put("playerMap", currentGame.getPlayerMap());

            output_MATCH_ENTER.put("type", "MATCH_ENTER");
            output_MATCH_ENTER.put("data", dataOutput_MATCH_ENTER);
            //webSocketMap.get(uid).sendMsg(output_MATCH_ENTER.toJSONString());
            sendMsg(uid, output_MATCH_ENTER.toJSONString());
        }
    }

    //处理匹配
    private void onMessage_MATCH_REQ(JSONObject data, int id) throws IOException {
        Integer type = data.getInteger("type");

        //如果上局比赛没结束
        if (currentGameMap.containsKey(uid)) {
            JSONObject output = new JSONObject();
            JSONObject dataOutput = new JSONObject();

            dataOutput.put("ack", id);
            dataOutput.put("msg", "已在匹配对局中");

            output.put("type", "ERROR");
            output.put("data", dataOutput);

            sendMsg(output.toJSONString());
        } else {
            //todo:根据rating进行匹配


            if (type==1) {
                MatchService.addPlayer1(uid, 1000);
                //输出逻辑
                JSONObject output_MATCH_START = new JSONObject();
                JSONObject dataOutput_MATCH_START = new JSONObject();

                dataOutput_MATCH_START.put("type", type);

                output_MATCH_START.put("type", "MATCH_START");
                output_MATCH_START.put("data", dataOutput_MATCH_START);
                sendMsg(output_MATCH_START.toJSONString());
            } else if (type==2) {
                MatchService.addPlayer2(uid, 1000);
                //输出逻辑
                JSONObject output_MATCH_START = new JSONObject();
                JSONObject dataOutput_MATCH_START = new JSONObject();

                dataOutput_MATCH_START.put("type", type);

                output_MATCH_START.put("type", "MATCH_START");
                output_MATCH_START.put("data", dataOutput_MATCH_START);
                sendMsg(output_MATCH_START.toJSONString());
            } else {
                JSONObject output = new JSONObject();
                JSONObject dataOutput = new JSONObject();

                dataOutput.put("ack", id);
                dataOutput.put("msg", "没有该游戏模式");

                output.put("type", "ERROR");
                output.put("data", dataOutput);

                sendMsg(output.toJSONString());
            }

        }
    }

    //匹配成功
    public static void return_MATCH_ENTER(int userId, int questionId, Map<String, Integer> playerMap, int gameId) throws IOException {
        //更新信息
        //输出逻辑
        JSONObject output_MATCH_ENTER = new JSONObject();
        JSONObject dataOutput_MATCH_ENTER = new JSONObject();
        JSONObject infoOutput_MATCH_ENTER = new JSONObject();

        infoOutput_MATCH_ENTER.put("questionId", questionId);

        dataOutput_MATCH_ENTER.put("info", infoOutput_MATCH_ENTER);
        dataOutput_MATCH_ENTER.put("playerMap", playerMap);

        output_MATCH_ENTER.put("type", "MATCH_ENTER");
        output_MATCH_ENTER.put("data", dataOutput_MATCH_ENTER);
        webSocketMap.get(userId).sendMsg(output_MATCH_ENTER.toJSONString());

        //更新当前比赛信息
        CurrentGame currentGame = new CurrentGame();
        currentGame.setGameId(gameId);
        currentGame.setQuestionId(questionId);
        currentGame.setPlayerMap(playerMap);
        currentGameMap.put(userId, currentGame);
    }

    // 处理聊天
    private void onMessage_CHAT_REQ(JSONObject data, int id) throws IOException {
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
                    //webSocketMap.get(userGameDTO.getId()).sendMsg(output.toJSONString());
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
                //webSocketMap.get(userGameDTO.getId()).sendMsg(output.toJSONString());
                sendMsg(userGameDTO.getId(), output.toJSONString());
            }
        }
    }

    // 刷新评测结果
    private void onMessage_ANSWER_REFRESH(JSONObject data, int id) throws IOException {
        String submit_id = data.getString("submit_id");

        //输出逻辑
        JSONObject output = new JSONObject();
        JSONObject dataOutput = new JSONObject();
        JSONObject result = ojService.getResult(submit_id);

        dataOutput.put("result", result);
        output.put("type", "ANSWER_RESULT");
        output.put("data", dataOutput);
        sendMsg(output.toJSONString());

        //判断是否结束
        JSONObject dataResult = result.getJSONObject("data");
        JSONObject statistic_info = dataResult.getJSONObject("statistic_info");
        JSONObject info = dataResult.getJSONObject("info");
        //已评测完
        if (!(info.isEmpty() && statistic_info.isEmpty())) {
            //已结束
            if (dataResult.getInteger("result") == 0) {
                Integer gameId = currentGameMap.get(uid).getGameId();
                List<UserGameDTO> players = gameService.getPlayer(gameId);
                //获取赢的队伍
                int winTeamId = 0;
                //todo:多人模式记得修改这部分逻辑
                for (UserGameDTO userGameDTO : players) {
                    if (userGameDTO.getId() == uid) {
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
        }
    }

    public void returnGameEnd(int userId, String result) throws IOException {
        JSONObject output = new JSONObject();
        JSONObject dataOutput = new JSONObject();

        dataOutput.put("result", result);
        output.put("type", "GAME_END");
        output.put("data", dataOutput);

        //webSocketMap.get(userId).sendMsg(output.toJSONString());
        sendMsg(userId, output.toJSONString());
    }

    //处理光标移动
    private void onMessage_POS_UPDATE(JSONObject data, int id) throws IOException {
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
                //webSocketMap.get(userGameDTO.getId()).sendMsg(output.toJSONString());
                sendMsg(userGameDTO.getId(), output.toJSONString());
            }
        }
    }

    public void onMessage_SURRENDER(JSONObject data, int id) throws IOException {
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
    private void onMessage_ITEM_SEND(JSONObject data, int id) throws IOException {
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

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户【" + this.uid + "】处理消息错误，原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     *
     * @param msg
     * @throws IOException
     */
    private void sendMsg(String msg) throws IOException {
        if (this.session != null)
            this.session.getBasicRemote().sendText(msg);
        else
            System.out.println("no session here");
    }

    private void sendMsg(int uid, String msg) throws IOException {
        if (webSocketMap.containsKey(uid))
            webSocketMap.get(uid).session.getBasicRemote().sendText(msg);
        else
            System.out.println("no session here");
    }

    public Session getSession() {
        return this.session;
    }

    /**
     * 发送自定义消息
     *
     * @param message
     * @param uid
     * @throws IOException
     */
    public static void sendInfo(String message, @PathParam("uid") String uid) throws IOException {
        log.info("发送消息到用户【" + uid + "】发送的报文:" + message);
        if (!StringUtils.isEmpty(uid) && webSocketMap.containsKey(uid)) {
            webSocketMap.get(uid).sendMsg(message);
        } else {
            log.error("用户【" + uid + "】不在线！");
        }
    }

    public static synchronized int getCurrentMatch() {
        return MatchService.matchingPool.getCurrentMatch();
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}