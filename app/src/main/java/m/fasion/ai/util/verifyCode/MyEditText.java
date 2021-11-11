package m.fasion.ai.util.verifyCode;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.Objects;

public class MyEditText extends AppCompatEditText {

    private long lastTime = 0;

    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        this.setSelection(Objects.requireNonNull(this.getText()).length());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime < 500) {
                lastTime = currentTime;
                return true;
            } else {
                lastTime = currentTime;
            }
        }
        return super.onTouchEvent(event);
    }
}
