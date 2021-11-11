package com.aisway.faceswap.utils

import android.os.CountDownTimer
import android.widget.TextView
import m.fasion.ai.R

/**
 * 倒计时
 */
class TimeCountdown {

    fun time(btn: TextView): CountDownTimer {
        btn.isEnabled = false
        val tm: CountDownTimer = object : CountDownTimer(60000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished / 1000
                btn.text = btn.context.resources.getString(R.string.second, second)
            }

            override fun onFinish() {
                btn.isEnabled = true
                btn.text = "重新发送"
            }
        }
        tm.start()
        return tm
    }
}