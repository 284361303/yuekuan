package m.fasion.ai.util

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import com.google.android.material.tabs.TabLayout

class CustomTabLayout : TabLayout {

    constructor(context: Context) : super(context) {
        initMinWidth()
    }

    constructor(context: Context, attributes: AttributeSet?) : super(context, attributes) {
        initMinWidth()
    }

    constructor(context: Context, attributes: AttributeSet?, @AttrRes defSty: Int) : super(context, attributes, defSty) {
        initMinWidth()
    }

    private fun initMinWidth() {
        try {
            val field = TabLayout::class.java.getDeclaredField("scrollableTabMinWidth")
            field.isAccessible = true
            field.set(this, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}