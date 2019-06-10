package com.example.getheight;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.textView);
        printString("屏幕 --宽度：" + getScreenWidth(this));
        printString("屏幕 --高度：" + getScreenHeight(this));
        getViewHeightByMeasure();
        getViewHeightByOnPreDrawListener();
        getViewHeightByOnLayoutChangeListener();
        getViewHeightByOnOnGlobalLayoutListener();
        getViewHeightByPost();
    }

    /*使用 View.measure 测量 View
    * 该方法测量的宽度和高度可能与视图绘制完成后的真实的宽度和高度不一致。
    * */
    private void getViewHeightByMeasure() {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mTextView.measure(width, height);
        printString("getViewHeightByMeasure --宽度：" + mTextView.getMeasuredWidth());
        printString("getViewHeightByMeasure --高度：" + mTextView.getMeasuredWidth());
    }

    /*使用 ViewTreeObserver. OnPreDrawListener 监听事件*/
    /*
     * 在视图将要绘制时调用该监听事件，会被调用多次，因此获取到视图的宽度和高度后要移除该监听事件。
     * */
    private void getViewHeightByOnPreDrawListener() {
        mTextView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mTextView.getViewTreeObserver().removeOnPreDrawListener(this);
                printString("getViewHeightByOnPreDrawListener --宽度：" + mTextView.getWidth());
                printString("getViewHeightByOnPreDrawListener --高度：" + mTextView.getHeight());
                return true;
            }
        });
    }

    /*
     * 使用 ViewTreeObserver. OnGlobalLayoutListener 监听事件
     *
     * 在布局发生改变或者某个视图的可视状态发生改变时调用该事件，会被多次调用，因此需要在获取到视图的宽度和高度后执行 remove 方法移除该监听事件。
     * */
    private void getViewHeightByOnOnGlobalLayoutListener() {
        mTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= 16) {
                    mTextView.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                } else {
                    mTextView.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                }
                printString("getViewHeightByOnOnGlobalLayoutListener --宽度：" + mTextView.getWidth());
                printString("getViewHeightByOnOnGlobalLayoutListener --高度：" + mTextView.getHeight());
            }
        });
    }

    /*
     *重写 View 的 onSizeChanged 方法
     *
     * 在视图的大小发生改变时调用该方法，会被多次调用，因此获取到宽度和高度后需要考虑禁用掉代码。
     *该实现方法需要继承 View，且多次被调用，不建议使用。
     * */
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//
//        view.getWidth(); // 获取宽度
//        view.getHeight(); // 获取高度
//    }

    /*
     *
     *重写 View 的 onLayout 方法
     * 该方法会被多次调用，获取到宽度和高度后需要考虑禁用掉代码。
     *该实现方法需要继承 View，且多次被调用，不建议使用。
     *
     *
     * */

//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
//
//        view.getWidth(); // 获取宽度
//        view.getHeight(); // 获取高度
//    }

    /*
    * 使用 View.OnLayoutChangeListener 监听事件（API >= 11）
在视图的 layout 改变时调用该事件，会被多次调用，因此需要在获取到视图的宽度和高度后执行 remove 方法移除该监听事件。
    * */
    private void getViewHeightByOnLayoutChangeListener() {
        mTextView.addOnLayoutChangeListener(
                new View.OnLayoutChangeListener() {

                    @Override
                    public void onLayoutChange(View v, int l, int t, int r, int b,
                                               int oldL, int oldT, int oldR, int oldB) {
                        mTextView.removeOnLayoutChangeListener(this);
                        printString("getViewHeightByOnLayoutChangeListener --宽度：" + mTextView.getWidth());
                        printString("getViewHeightByOnLayoutChangeListener --高度：" + mTextView.getHeight());
                    }
                });
    }


    /*使用 View.post() 方法
Runnable 对象中的方法会在 View 的 measure、layout 等事件完成后触发。
UI 事件队列会按顺序处理事件，在 setContentView() 被调用后，事件队列中会包含一个要求重新 layout 的 message，所以任何 post 到队列中的 Runnable 对象都会在 Layout 发生变化后执行。
该方法只会执行一次，且逻辑简单，建议使用。*/

    private void getViewHeightByPost() {
        mTextView.post(new Runnable() {
            @Override
            public void run() {
                printString("getViewHeightByPost --宽度：" + mTextView.getWidth());
                printString("getViewHeightByPost --高度：" + mTextView.getHeight());
            }
        });
    }

    protected int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        return display.getWidth();
    }

    protected int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        return display.getHeight();
    }

    private void printString(String string) {
        Log.d("MainActivity", string);
    }
}
