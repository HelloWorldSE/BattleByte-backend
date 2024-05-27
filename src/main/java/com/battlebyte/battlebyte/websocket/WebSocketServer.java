package com.battlebyte.battlebyte.websocket;

import com.battlebyte.battlebyte.config.BeanContext;
import com.battlebyte.battlebyte.entity.Game;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import static com.battlebyte.battlebyte.service.MatchService.removePlayer;
import static com.battlebyte.battlebyte.util.JwtUtil.getUserId;
import static java.lang.Integer.max;


@ServerEndpoint("/server")
@Slf4j
@Component
public class WebSocketServer {

    /**
     * 记录当前在线连接数
     */
    private static int onlineCount = 0;

    /**
     * 使用线程安全的ConcurrentHashMap来存放每个客户端对应的WebSocket对象
     */
    public static ConcurrentHashMap<Integer, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, CurrentGame> currentGameMap = new ConcurrentHashMap<>();

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
    private OJService ojService;
    private GameService gameService;
    private UserService userService;
    private static MatchSocket matchSocket = new MatchSocket();
    private static GameSocket gameSocket = new GameSocket();

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
                    matchSocket.onMessage_MATCH_REQ(data, id, uid);
                } else if (type.equals("CHAT_REQ")) {
                    gameSocket.onMessage_CHAT_REQ(data, id, uid);
                } else if (type.equals("ANSWER_REFRESH")) {
                    gameSocket.onMessage_ANSWER_REFRESH(data, id, uid);
                } else if (type.equals("POS_UPDATE")) {
                    gameSocket.onMessage_POS_UPDATE(data, id, uid);
                } else if (type.equals("SURRENDER")) {
                    gameSocket.onMessage_SURRENDER(data, id, uid);
                } else if (type.equals("ITEM_SEND")) {
                    gameSocket.onMessage_ITEM_SEND(data, id, uid);
                } else if (type.equals("TEST_AC_QUESTION")) {
                    test_AC_QUESTION(data, id);
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
            infoOutput_MATCH_ENTER.put("currentQuestion", currentGame.getCurrentQuestion());

            dataOutput_MATCH_ENTER.put("info", infoOutput_MATCH_ENTER);
            dataOutput_MATCH_ENTER.put("playerMap", currentGame.getPlayerMap());

            output_MATCH_ENTER.put("type", "MATCH_ENTER");
            output_MATCH_ENTER.put("data", dataOutput_MATCH_ENTER);
            //webSocketMap.get(uid).sendMsg(output_MATCH_ENTER.toJSONString());
            sendMsg(uid, output_MATCH_ENTER.toJSONString());
        }
    }

    //匹配成功
    public static void return_MATCH_ENTER(int userId, ArrayList<Integer> questionId, Map<String, Integer> playerMap, int gameId, CurrentGame currentGame) throws IOException {
        matchSocket.return_MATCH_ENTER(userId, questionId, playerMap, gameId, currentGame);
    }


    private void test_AC_QUESTION(JSONObject data, int id) throws IOException {
        gameSocket.acQuestion(uid);
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

    public static void sendMsg(int uid, String msg) throws IOException {
        if (webSocketMap.containsKey(uid))
            webSocketMap.get(uid).session.getBasicRemote().sendText(msg);
        else
            System.out.println("no session here");
    }

    public Session getSession() {
        return this.session;
    }

    //处理大逃杀血量
    public void manageGame() throws IOException {
        // 使用Set来记录已经处理过的值
        Set<CurrentGame> seenValues = new HashSet<>();

        // 遍历Map的值
        for (CurrentGame currentGame : currentGameMap.values()) {
            if (!seenValues.contains(currentGame)) {
                if (currentGame.getGameType() == 2) {
                    Map<Integer, Integer> HPMAP = currentGame.getHPMAP();
                    Map<Integer, Integer> acMAP = currentGame.getAcMAP();

                    // 获取acMAP的最大值
                    int maxAcValue = Collections.max(acMAP.values());

                    // 遍历HPMAP并进行计算
                    for (Map.Entry<Integer, Integer> entry : HPMAP.entrySet()) {
                        Integer userId = entry.getKey();
                        Integer hpValue = entry.getValue();
                        Integer acValue = acMAP.get(userId);

                        if (acValue != null) {
                            // 计算差距
                            int difference = maxAcValue - acValue;
                            // 更新HPMAP的值
                            HPMAP.put(userId, max(hpValue - difference, 0));

                            //获取同局人员
                            Integer gameId = currentGameMap.get(userId).getGameId();
                            List<UserGameDTO> players = gameService.getPlayer(gameId);

                            for (UserGameDTO userGameDTO : players) {
                                if (userGameDTO.getId() != userId) {
                                    //输出逻辑
                                    JSONObject output = new JSONObject();
                                    JSONObject dataOutput = new JSONObject();

                                    dataOutput.put("change_id", userId);
                                    dataOutput.put("hp", max(hpValue - difference, 0));

                                    output.put("type", "HP_CHANGE");
                                    output.put("data", dataOutput);
                                    sendMsg(userGameDTO.getId(), output.toJSONString());
                                }
                            }
                        }
                    }

                    // 打印更新后的HPMAP
                    for (Map.Entry<Integer, Integer> entry : HPMAP.entrySet()) {
                        System.out.println("id: " + entry.getKey() + ", hp: " + entry.getValue());
                    }

                    //判断是否结束
                    int tmp = -100;
                    int count = 0;
                    for (Map.Entry<Integer, Integer> entry : HPMAP.entrySet()) {
                        if (entry.getValue() > 0) {
                            count++;
                            tmp = entry.getKey();
                        }
                    }
                    if (count == 1) {
                        gameSocket.winTeam(tmp);
                    }
                }
                seenValues.add(currentGame);
            }
        }
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