package m.fasion.ai.base

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import m.fasion.ai.R

/**
 * @author shao_g
 * 2021年11月11日10:51:46
 * 页面空数据和无网络的加载页面
 */
class StateView : ConstraintLayout, View.OnClickListener {

    private var llNoInternet: ConstraintLayout? = null
    private var tvRetry: TextView? = null
    private var tvStatus: TextView? = null
    private var llNoData: ConstraintLayout? = null
    private var tvEmpty: TextView? = null
    private var ivBg: ImageView? = null
    private var mContext: Context? = null
    private var mState: State? = null

    enum class State {
        loading, error, done, empty
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        mContext = context
        val inflate = LayoutInflater.from(context).inflate(R.layout.layout_ui_state, this)
        //没有网络
        llNoInternet = inflate.findViewById(R.id.uiState_noInternet)
        tvRetry = inflate.findViewById(R.id.uiState_tvRetry)
        tvStatus = inflate.findViewById(R.id.uiState_tv)

        //暂无内容
        llNoData = inflate.findViewById(R.id.uiState_noData)
        tvEmpty = inflate.findViewById(R.id.uiState_tvEmpty)
        ivBg = inflate.findViewById(R.id.uiState_iv2)

        tvRetry?.setOnClickListener(this)
        setStateView(State.done)
    }

    /**
     * @param state  loading 加载中, error 网络异常, done  加载结束, empty  空数据
     * @param textTip  没有返回数据时候的提示语
     * @param emptyImgResource 没有数据或者没有网络时候的提示图片
     */
    fun setStateView(state: State, textTip: String? = null, emptyImgResource: Int? = null) {
        this.mState = state
        when (mState) {
            State.error -> {
                visibility = View.VISIBLE
                llNoData?.visibility = View.GONE
                llNoInternet?.visibility = View.VISIBLE
                if (!TextUtils.isEmpty(textTip)) {
                    tvStatus?.text = textTip
                }
            }
            State.empty -> {
                visibility = View.VISIBLE
                llNoInternet?.visibility = View.GONE
                llNoData?.visibility = View.VISIBLE
                if (!textTip.isNullOrEmpty()) {
                    tvEmpty?.text = textTip
                }
                if (emptyImgResource != null && emptyImgResource > 0 && emptyImgResource != 0) {
                    ivBg?.setImageResource(emptyImgResource)
                }
            }
            State.loading -> {
                visibility = View.GONE
            }
            State.done -> {
                visibility = View.GONE
            }
            else -> {
                visibility = View.GONE
            }
        }
    }

    override fun onClick(v: View?) {
        if (listener != null && mState == State.error) {
            listener?.onRetry()
        }
    }

    var listener: OnRetryListener? = null

    interface OnRetryListener {
        fun onRetry()
    }
}