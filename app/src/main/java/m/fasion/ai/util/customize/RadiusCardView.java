package m.fasion.ai.util.customize;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import m.fasion.ai.R;

/**
 * 自定义CardView其中一个或几个圆角
 * 2021年11月8日13:41:53
 *
 * @author shaog
 */
public class RadiusCardView extends CardView {

    private final float tlRadiu, trRadiu, brRadiu, blRadiu;
    private Path path;
    private RectF rectF;

    public RadiusCardView(@NonNull Context context) {
        this(context, null);
    }

    public RadiusCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.materialCardViewStyle);
    }

    public RadiusCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setRadius(0);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RadiusCardView);
        tlRadiu = array.getDimension(R.styleable.RadiusCardView_rcv_topLeftRadiu, 0);
        trRadiu = array.getDimension(R.styleable.RadiusCardView_rcv_topRightRadiu, 0);
        brRadiu = array.getDimension(R.styleable.RadiusCardView_rcv_bottomRightRadiu, 0);
        blRadiu = array.getDimension(R.styleable.RadiusCardView_rcv_bottomLeftRadiu, 0);
        setBackground(new ColorDrawable());
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (path == null) {
            path = new Path();
        }
        if (rectF == null) {
            rectF = getRectF();
        }
        float[] readius = {tlRadiu, tlRadiu, trRadiu, trRadiu, brRadiu, brRadiu, blRadiu, blRadiu};
        path.addRoundRect(rectF, readius, Path.Direction.CW);
        canvas.clipPath(path, Region.Op.INTERSECT);
        super.onDraw(canvas);
    }

    private RectF getRectF() {
        Rect rect = new Rect();
        getDrawingRect(rect);
        return new RectF(rect);
    }
}
