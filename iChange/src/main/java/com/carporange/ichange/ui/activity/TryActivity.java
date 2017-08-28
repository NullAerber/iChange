package com.carporange.ichange.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.carporange.ichange.R;
import com.carporange.ichange.ui.base.AerberBaeeActivity;

public class TryActivity extends AerberBaeeActivity implements View.OnClickListener {
    protected static final String TAG = "TryActivity";
    // 当前显示的bitmap对象
    private static Bitmap bitmap;
    //framlayout
    private FrameLayout frameLayout;
    // 图片容器
    private ImageView imageView;
    //选择背景的按钮
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;

    boolean toastTag;
    boolean postionTag;

    // 开始按下位置
    private int startX;
    // 当前位置
    private int currentX;
    // 当前图片的编号
    private int scrNum;
    // 图片的总数
    private static int maxNum = 52;
    // 资源图片集合
    private int[] srcs = new int[]{R.drawable.p1, R.drawable.p2,
            R.drawable.p3, R.drawable.p4, R.drawable.p5, R.drawable.p6,
            R.drawable.p7, R.drawable.p8, R.drawable.p9, R.drawable.p10,
            R.drawable.p11, R.drawable.p12, R.drawable.p13, R.drawable.p14,
            R.drawable.p15, R.drawable.p16, R.drawable.p17, R.drawable.p18,
            R.drawable.p19, R.drawable.p20, R.drawable.p21, R.drawable.p22,
            R.drawable.p23, R.drawable.p24, R.drawable.p25, R.drawable.p26,
            R.drawable.p27, R.drawable.p28, R.drawable.p29, R.drawable.p30,
            R.drawable.p31, R.drawable.p32, R.drawable.p33, R.drawable.p34,
            R.drawable.p35, R.drawable.p36, R.drawable.p37, R.drawable.p38,
            R.drawable.p39, R.drawable.p40, R.drawable.p41, R.drawable.p42,
            R.drawable.p43, R.drawable.p44, R.drawable.p45, R.drawable.p46,
            R.drawable.p47, R.drawable.p48, R.drawable.p49, R.drawable.p50,
            R.drawable.p51, R.drawable.p52};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_try);
        initBar(this.getIntent().getStringExtra("bar_title"));

        //drag
        DisplayMetrics dm = getResources().getDisplayMetrics();
        final int screenWidth = dm.widthPixels;
        final int screenHeight = dm.heightPixels - 50;

        initListen();

        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        imageView = (ImageView) findViewById(R.id.imageView);

        // 初始化当前显示图片编号
        scrNum = 1;
        toastTag = true;
        postionTag = true;

        imageView.setOnTouchListener(new View.OnTouchListener() {
            int lastX, lastY; //记录移动的最后的位置

            public boolean onTouch(View v, MotionEvent event) {
                int ea = event.getAction();
                int x = (int) event.getX();
                int y = (int) event.getY();
                int lastTime = (int) event.getDownTime();
                Log.i("zmy", String.valueOf(lastTime));

                boolean mIsLongPressed = isLongPressed(event.getX(), event.getY(), x, y, lastTime, event.getEventTime(), 1500);

                if (mIsLongPressed) {
                    if (toastTag){
                        toastTag = false;
                        Toast.makeText(getApplicationContext(), R.string.try_move_char,Toast.LENGTH_SHORT).show();
                    }

                    //长按模式所做的事
                    switch (ea) {
                        case MotionEvent.ACTION_DOWN:   //按下
                            lastX = (int) event.getRawX();
                            lastY = (int) event.getRawY();
                            break;
                        /**
                         * layout(l,t,r,b)
                         * l  Left position, relative to parent
                         t  Top position, relative to parent
                         r  Right position, relative to parent
                         b  Bottom position, relative to parent
                         * */
                        case MotionEvent.ACTION_MOVE:  //移动
                            //移动中动态设置位置
                            if (postionTag){
                                lastX = screenWidth / 2;
                                lastY = screenHeight / 2;
                                postionTag = false;
                            }
                            Log.i("TAG", String.valueOf(event.getRawX()));
                            int dx = (int) event.getRawX() - lastX;
                            int dy = (int) event.getRawY() - lastY;
                            int left = v.getLeft() + dx;
                            int top = v.getTop() + dy;
                            int right = v.getRight() + dx;
                            int bottom = v.getBottom() + dy;
                            if (left < 0) {
                                left = 0;
                                right = left + v.getWidth();
                            }
                            if (right > screenWidth) {
                                right = screenWidth;
                                left = right - v.getWidth();
                            }
                            if (top < 0) {
                                top = 0;
                                bottom = top + v.getHeight();
                            }
                            if (bottom > screenHeight) {
                                bottom = screenHeight;
                                top = bottom - v.getHeight();
                            }
                            v.layout(left, top, right, bottom);
                            Log.i("", "position：" + left + ", " + top + ", " + right + ", " + bottom);
                            //将当前的位置再次设置
                            lastX = (int) event.getRawX();
                            lastY = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:   //脱离
                            toastTag = true;
                            break;
                    }
                } else {
                    //触碰模式所做的事
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = (int) event.getX();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            currentX = (int) event.getX();
                            // 判断手势滑动方向，并切换图片
                            if (currentX - startX > 10) {
                                modifySrcR();
                            } else if (currentX - startX < -10) {
                                modifySrcL();
                            }
                            // 重置起始位置
                            startX = (int) event.getX();
                            break;
                    }
                }
                return false;
            }
        });


    }

    // 向右滑动修改资源
    private void modifySrcR() {

        if (scrNum > maxNum) {
            scrNum = 1;
        }

        if (scrNum > 0) {
            bitmap = BitmapFactory.decodeResource(getResources(),
                    srcs[scrNum - 1]);
            imageView.setImageBitmap(bitmap);
            scrNum++;
        }

    }

    // 向左滑动修改资源
    private void modifySrcL() {
        if (scrNum <= 0) {
            scrNum = maxNum;
        }

        if (scrNum <= maxNum) {
            bitmap = BitmapFactory.decodeResource(getResources(),
                    srcs[scrNum - 1]);
            imageView.setImageBitmap(bitmap);
            scrNum--;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_one:
                frameLayout.setBackground(getResources().getDrawable(R.drawable.bg1));
                break;
            case R.id.btn_two:
                frameLayout.setBackground(getResources().getDrawable(R.drawable.bg2));
                break;
            case R.id.btn_three:
                frameLayout.setBackground(getResources().getDrawable(R.drawable.bg3));
                break;
            case R.id.btn_four:
                frameLayout.setBackground(getResources().getDrawable(R.drawable.bg4));
                break;
            default:
                break;
        }
    }

    private void initListen() {
        btn1 = (Button) findViewById(R.id.btn_one);
        btn2 = (Button) findViewById(R.id.btn_two);
        btn3 = (Button) findViewById(R.id.btn_three);
        btn4 = (Button) findViewById(R.id.btn_four);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
    }

    /**
     * * 判断是否有长按动作发生 * @param lastX 按下时X坐标 * @param lastY 按下时Y坐标 *
     *
     * @param thisX         移动时X坐标 *
     * @param thisY         移动时Y坐标 *
     * @param lastDownTime  按下时间 *
     * @param thisEventTime 移动时间 *
     * @param longPressTime 判断长按时间的阀值
     */
    static boolean isLongPressed(float lastX, float lastY, float thisX,
                                 float thisY, long lastDownTime, long thisEventTime,
                                 long longPressTime) {
        Log.i("zmy", String.valueOf(thisEventTime));
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
            return true;
        }
        return false;
    }
}
