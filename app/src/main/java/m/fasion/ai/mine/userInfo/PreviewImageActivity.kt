package m.fasion.ai.mine.userInfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import m.fasion.ai.databinding.ActivityPreviewImageBinding

/**
 * 预览页面
 */
class PreviewImageActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPreviewImageBinding.inflate(layoutInflater) }

    companion object {
        const val PIC_PATH = "pic_image"
        fun startActivity(context: Context, imgPath: String) {
            val intent = Intent(context, PreviewImageActivity::class.java)
            val bundle = Bundle()
            bundle.putString(PIC_PATH, imgPath)
            context.startActivity(intent.putExtras(bundle))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intent.extras?.containsKey(PIC_PATH)?.let {
            if (it) {
                Glide.with(this).load(intent.extras?.getString(PIC_PATH)).into(binding.previewIv)
            }
        }
        binding.previewIvBack.setOnClickListener {
            finish()
        }
    }
}