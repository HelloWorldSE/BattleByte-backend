package com.battlebyte.battlebyte.util;

import com.battlebyte.battlebyte.websocket.CurrentGame;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

public class FakePlayer extends Thread{
    private final CurrentGame game;
    private ReentrantLock lock = new ReentrantLock();
    public FakePlayer(CurrentGame game){
        this.game=game;
    }
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                lock.lock();
                LocalDateTime start=game.getCurrentTime();
                LocalDateTime end=LocalDateTime.now();
                Duration duration = Duration.between(start, end);
                long seconds = duration.getSeconds();
                System.out.println("时间差：" + seconds + "秒");
                if (seconds>180) {
                    //TODO
                    lock.unlock();
                    break;
                }else {
                    if(seconds%5==0){
                        //TODO
                    }
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }


}
