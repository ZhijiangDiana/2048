package com.example.a2048;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Random;
import static com.example.a2048.MainActivity.mainActivity;

public class GameView extends GridLayout{
    public static GameView gameView;

    private Card[][] cards = new Card[4][4];

    public GameView(Context context) {
        super(context);
        gameView = this;
        initGame();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gameView = this;
        initGame();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gameView = this;
        initGame();
    }

    public void initGame(){
        this.setBackgroundColor(getResources().getColor(R.color.gameViewBackgroundColor));
        setColumnCount(4);

        int cardWidth = GetCardWidth();
        addCards(cardWidth, cardWidth);

        randomCreateCard(2);

        setListener();
    }

    public void replayGame(){
        mainActivity.clearScore();
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                cards[i][j].setNum(0);
            }
        }
        randomCreateCard(2);
    }

    /*
     * 监听Touch事件
     */
    private void setListener(){
        setOnTouchListener(new OnTouchListener() {
            private float staX,  staY, endX, endY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        staX = event.getX();
                        staY = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        endX = event.getX();
                        endY = event.getY();

                        boolean swiped = false;//记录是否有效滑动了

                        //水平移动更多
                        if(Math.abs(endX - staX) > Math.abs(endY - staY)){
                            if(endX - staX > 10){
                                if(swipeRight()){
                                    swiped = true;
                                }
                            }
                            else if(endX - staX < -10){
                                if(swipeLeft()){
                                    swiped = true;
                                }
                            }
                        }
                        else{
                            if(endY - staY < -10){
                                if(swipeUp()){
                                    swiped = true;
                                }
                            }
                            else if(endY - staY > 10){
                                if(swipeDown()){
                                    swiped = true;
                                }
                            }
                        }
                        //滑动后创建新块，并检查当前状态是否能滑动
                        if(swiped){
                            randomCreateCard(1);
                            if(!canSwipe()){
                                gameOver();
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    /*
     * 返回该次滑动是否有效（有卡片移动或合并）
     */
    private boolean swipeUp(){
        boolean flag = false;
        for(int j = 0; j < 4; ++j){
            int ind = 0;
            //从上往下依次处理
            for(int i = 1; i < 4; ++i){
                //如果是存在数字的，往上遍历
                if(cards[i][j].getNum() != 0){
                    for(int ii = i - 1; ii >= ind; --ii){
                        //如果这块是空的，将数字上移
                        if(cards[ii][j].getNum() == 0){
                            cards[ii][j].setNum(cards[i][j].getNum());
                            cards[i][j].setNum(0);
                            i--;//上移
                            flag = true;
                        }
                        //如果这块是相同的数，合并，合并的块不能一下合并两次，更新ind，不再遍历合并的块
                        else if(cards[ii][j].getNum() == cards[i][j].getNum()){
                            cards[ii][j].setNum((cards[i][j].getNum() * 2));
                            cards[i][j].setNum(0);
                            flag = true;
                            ind = ii + 1;//已经合过，该点不再合成
                            mainActivity.addScore(cards[ii][j].getNum() / 2);
                            break;
                        }
                        //上面的块数字不同，退出循环
                        else break;
                    }
                }
            }
        }
        return flag;
    }

    private boolean swipeDown(){
        boolean flag = false;
        for(int j = 0; j < 4; ++j){
            int ind = 4;
            for(int i = 2; i >= 0; --i){
                if(cards[i][j].getNum() != 0){
                    for(int ii = i + 1; ii < ind; ++ii){
                        if(cards[ii][j].getNum() == 0){
                            cards[ii][j].setNum(cards[i][j].getNum());
                            cards[i][j].setNum(0);
                            flag = true;
                            i++;
                        }
                        else if(cards[ii][j].getNum() == cards[i][j].getNum()){
                            cards[ii][j].setNum((cards[i][j].getNum() * 2));
                            cards[i][j].setNum(0);
                            flag = true;
                            ind = ii;
                            mainActivity.addScore(cards[ii][j].getNum() / 2);
                            break;
                        }
                        else break;
                    }
                }
            }
        }
        return flag;
    }

    private boolean swipeLeft(){
        boolean flag = false;
        for(int i = 0; i < 4; ++i){
            int ind = 0;
            for(int j = 1; j < 4; ++j){
                if(cards[i][j].getNum() != 0){
                    for(int jj = j - 1; jj >= ind; --jj){
                        if(cards[i][jj].getNum() == 0){
                            cards[i][jj].setNum(cards[i][j].getNum());
                            cards[i][j].setNum(0);
                            flag = true;
                            j--;
                        }
                        else if(cards[i][jj].getNum() == cards[i][j].getNum()){
                            cards[i][jj].setNum((cards[i][j].getNum() * 2));
                            cards[i][j].setNum(0);
                            flag = true;
                            ind = jj + 1;
                            mainActivity.addScore(cards[i][jj].getNum() / 2);
                            break;
                        }
                        else break;
                    }
                }
            }
        }
        return flag;
    }

    private boolean swipeRight(){
        boolean flag = false;
        for(int i = 0; i < 4; ++i){
            int ind = 4;
            for(int j = 2; j >= 0; --j){
                if(cards[i][j].getNum() != 0){
                    for(int jj = j + 1; jj < ind; ++jj){
                        if(cards[i][jj].getNum() == 0){
                            cards[i][jj].setNum(cards[i][j].getNum());
                            cards[i][j].setNum(0);
                            flag = true;
                            j++;
                        }
                        else if(cards[i][jj].getNum() == cards[i][j].getNum()){
                            cards[i][jj].setNum((cards[i][j].getNum() * 2));
                            cards[i][j].setNum(0);
                            flag = true;
                            ind = jj;
                            mainActivity.addScore(cards[i][jj].getNum() / 2);
                            break;
                        }
                        else break;
                    }
                }
            }
        }
        return flag;
    }

    /**
     *如果存在空白块，或者相邻的数字相同的块，则可以继续滑动
     */
    private boolean canSwipe(){
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                if(cards[i][j].getNum() == 0){
                    return true;
                }
                else if(i != 3 && cards[i][j].getNum() == cards[i + 1][j].getNum()){
                    return true;
                }
                else if(j != 3 && cards[i][j].getNum() == cards[i][j + 1].getNum()){
                    return true;
                }
            }
        }
        return false;
    }

    private void addCards(int width, int height){
        Card c;
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                c = new Card(getContext());
                addView(c, width, height);
                cards[i][j] = c;
            }
        }
    }

    public void gameOver(){
//        AlertDialog.Builder upload = new AlertDialog.Builder(getContext());
//        upload.setTitle("上传成绩中...")
//                .setView(new ProgressBar(getContext()))
//                .setMessage("正在上传成绩")
//                .create()
//                .show();
        Toast t = Toast.makeText(getContext(), "游戏结束，正在上传成绩", Toast.LENGTH_LONG);
        t.show();


        String name = Varible.et.getText().toString();
        if(name.isEmpty())
            name = "Anonymous";

        AlertDialog.Builder top10 = new AlertDialog.Builder(getContext());
        // GET请求：
        String responseText = "";
        Response response;
        try{
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url("http://" + Varible.ip
                            + ":" + Varible.port + "/" + Varible.war + "/top2048?op=getTop10"
                            + "&name=" + name + "&score=" + Varible.score)
                    .build();
            final Call call = okHttpClient.newCall(request);
            response = call.execute();
            responseText = new String(response.body().bytes(), "UTF-8");
        } catch (Exception e){
            e.printStackTrace();
            top10.setTitle("网络错误")
                    .setMessage("发生连接错误，请检查互联网")
                    .create()
                    .show();
            return;
        }

        // 处理response
        StringBuilder message = new StringBuilder();
        if(responseText.contains("HTTP Status")){
            top10.setTitle("发生错误")
                    .setMessage(responseText)
                    .create()
                    .show();
            return;
        }
        String[] entity = responseText.trim().split("&&");
        for(int i=1;i<entity.length;i++){
            String[] temp = entity[i].split(":");
            message.append(i).append(". ").append(temp[0]).append("：").append(temp[1]).append("\n");
        }

        // 展示窗口
        t.cancel();
        if(entity[0].equals("true"))
            top10.setTitle("恭喜您破纪录：");
        else
            top10.setTitle("排行榜：");
        top10.setMessage(message)
                .create()
                .show();

        // 重新开局
        replayGame();
    }

    private int GetCardWidth() {
        //获取屏幕信息
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //根据布局，GameView是占屏幕宽度的90%，除以4就是卡片边长
        return (int)((displayMetrics.widthPixels * 0.9f) / 4);
    }

    /*
     * 递归随机，玄学复杂度，期望递归次数小于 16 次，偷了个懒
     * 最好是把可用方块加入到一个列表中，然后在列表中随机
     */
    private void randomCreateCard(int cnt){
        Random random = new Random();
        int r = random.nextInt(4);
        int c = random.nextInt(4);

        //该处已经存在数字，重新随机r, c
        if(cards[r][c].getNum() != 0){
            randomCreateCard(cnt);
            return;
        }

        int rand = random.nextInt(10);

        if(rand >= 2) rand = 2;
        else rand = 4;

        cards[r][c].setNum(rand);

        if(cnt >= 2){
            randomCreateCard(cnt - 1);
        }
    }
}
