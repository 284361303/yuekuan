package m.fasion.ai.util.verifyCode;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import m.fasion.ai.R;

/**
 * Author: shaoguang
 */
public class VerifyCodeView extends RelativeLayout {
    private final EditText editText;
    private final TextView[] textViews;
    private final View[] mLineViews;
    private static final int MAX = 4;
    private String inputContent;
    private MessageHandler messageHandler;

    public VerifyCodeView(Context context) {
        this(context, null);
    }

    public VerifyCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_verify_code, this);

        textViews = new TextView[MAX];
        textViews[0] = findViewById(R.id.tv_0);
        textViews[1] = findViewById(R.id.tv_1);
        textViews[2] = findViewById(R.id.tv_2);
        textViews[3] = findViewById(R.id.tv_3);


        mLineViews = new View[MAX];
        mLineViews[0] = findViewById(R.id.view_0);
        mLineViews[1] = findViewById(R.id.view_1);
        mLineViews[2] = findViewById(R.id.view_2);
        mLineViews[3] = findViewById(R.id.view_3);
        editText = findViewById(R.id.edit_text_view);

        editText.setCursorVisible(false);//隐藏光标
        initClick();
        setEditTextListener();
    }

    /**
     * 模拟触摸输入框让弹出软键盘
     */
    private void initClick() {
        Looper looper = Looper.myLooper();
        messageHandler = new MessageHandler(looper);
        //此处的作用是延迟1秒，然后激活点击事件
        new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(300); //延迟一会
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message = Message.obtain();
                message.what = 1;
                messageHandler.sendMessage(message);
            }
        }.start();
    }

    private void setEditTextListener() {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                inputContent = editText.getText().toString();

                if (inputCompleteListener != null) {
                    if (inputContent.length() >= MAX) {
                        inputCompleteListener.inputComplete();
                    } else {
                        inputCompleteListener.invalidContent();
                    }
                }

                for (int i = 0; i < MAX; i++) {
                    if (i < inputContent.length()) {
                        textViews[i].setText(String.valueOf(inputContent.charAt(i)));
                        mLineViews[i].setBackgroundColor(getResources().getColor(R.color.color_111111));
                    } else {
                        textViews[i].setText("");
                        mLineViews[i].setBackgroundColor(getResources().getColor(R.color.color_EEEEEE));
                    }
                }
            }
        });
    }

    private InputCompleteListener inputCompleteListener;

    public void setInputCompleteListener(InputCompleteListener inputCompleteListener) {
        this.inputCompleteListener = inputCompleteListener;
    }

    public interface InputCompleteListener {

        void inputComplete();

        void invalidContent();
    }

    /**
     * 获取输入框里面的内容
     */
    public String getEditContent() {
        return inputContent;
    }

    /**
     * 清楚输入框的内容
     */
    public void clearContent() {
        if (editText != null) {
            editText.setText("");
        }
    }

    private class MessageHandler extends Handler {
        public MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {    //以下代码模拟点击文本编辑框
                editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, editText.getLeft() + 5, editText.getTop() + 5, 0));
                editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, editText.getLeft() + 5, editText.getTop() + 5, 0));
            }
        }
    }
}
