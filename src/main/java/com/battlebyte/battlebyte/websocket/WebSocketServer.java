package com.battlebyte.battlebyte.websocket;

import com.battlebyte.battlebyte.service.MatchService;
import com.battlebyte.battlebyte.service.OJService;
import io.micrometer.common.util.StringUtils;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

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
        }
        log.info("用户【" + uid + "】退出，当前在线人数为:" + getOnlineCount());
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
                    onMessage_LOGIN_REQ(data,id);
                }else if(type.equals("MATCH_REQ")){
                    onMessage_MATCH_REQ(data,id);
                }else if(type.equals("CHAT_REQ")){
                    onMessage_CHAT_REQ(data,id);
                }else if(type.equals("ANSWER_REFRESH")){
                    onMessage_ANSWER_REFRESH(data,id);
                }

            } catch (Exception e) {
                log.error("用户【" + uid + "】发送消息异常！", e);
            }
        }
    }

    //处理登录
    private void onMessage_LOGIN_REQ(JSONObject data,int id) throws IOException {
        String token = data.getString("token");
        //获取uid 测试
//        Integer uid = Integer.valueOf(token);
        //获取uid
        Integer uid = getUserId(token);
        this.uid = uid;
        if (webSocketMap.containsKey(uid)) {
            //断掉之前的
            WebSocketServer beforeSession=webSocketMap.get(uid);
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
            JSONObject dataOutput_LOGIN_ACK=new JSONObject();
            
            dataOutput_LOGIN_ACK.put("code",0);

            output_LOGIN_ACK.put("type","LOGIN_ACK");
            output_LOGIN_ACK.put("data",dataOutput_LOGIN_ACK);
            sendMsg(output_LOGIN_ACK.toJSONString());
        } catch (IOException e) {
            log.error("用户【" + uid + "】网络异常!", e);
        }
    }
    //处理匹配
    private void onMessage_MATCH_REQ(JSONObject data,int id) throws IOException {
        String type = data.getString("type");

        //todo:根据rating进行匹配
        MatchService.addPlayer(uid,1000);

        //输出逻辑
        JSONObject output_MATCH_START = new JSONObject();
        JSONObject dataOutput_MATCH_START=new JSONObject();

        dataOutput_MATCH_START.put("type",type);

        output_MATCH_START.put("type","MATCH_START");
        output_MATCH_START.put("data",dataOutput_MATCH_START);
        sendMsg(output_MATCH_START.toJSONString());
    }
    //匹配成功
    public static void return_MATCH_ENTER(int userId) throws IOException {
        //输出逻辑
        JSONObject output_MATCH_ENTER = new JSONObject();
        JSONObject dataOutput_MATCH_ENTER=new JSONObject();

        dataOutput_MATCH_ENTER.put("opponents","to be continue");
        dataOutput_MATCH_ENTER.put("team_side","to be continue");

        output_MATCH_ENTER.put("type","MATCH_ENTER");
        output_MATCH_ENTER.put("data",dataOutput_MATCH_ENTER);
        webSocketMap.get(userId).sendMsg(output_MATCH_ENTER.toJSONString());
    }
    // 处理聊天
    private void onMessage_CHAT_REQ(JSONObject data,int id) throws IOException{
        //读取json文件
        String type = data.getString("type");
        String message = data.getString("message");
        Integer gameId = data.getInteger("gameId");

        Integer toId=0;
        //todo:根据gameId获取所有人参与的id for循环遍历输出
        //输出逻辑
        JSONObject output = new JSONObject();
        JSONObject dataOutput=new JSONObject();

        dataOutput.put("fromId",uid);
        dataOutput.put("message",message);

        output.put("type","CHAT_MSG");
        output.put("data",dataOutput);
        webSocketMap.get(toId).sendMsg(output.toJSONString());
    }
    // 刷新评测结果
    private void onMessage_ANSWER_REFRESH(JSONObject data,int id) throws IOException{
        Integer submit_id = data.getInteger("submit_id");

        //输出逻辑
        JSONObject output = new JSONObject();
        JSONObject dataOutput=new JSONObject();
        JSONObject result = JSON.parseObject(ojService.getProblem(submit_id));

        dataOutput.put("result",result);
        output.put("type","ANSWER_RESULT");
        output.put("data",dataOutput);
        sendMsg(output.toJSONString());
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
    private  void sendMsg(String msg) throws IOException {
        this.session.getBasicRemote().sendText(msg);
    }

    public Session getSession(){
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

    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

}