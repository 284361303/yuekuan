package m.fasion.ai.mine.userInfo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import m.fasion.ai.R
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityEditingUserPicBinding
import m.fasion.ai.util.customize.GlideEngine
import m.fasion.core.base.ConstantsKey
import java.io.File

/**
 * 修改用户头像
 */
class EditingUserPicActivity : BaseActivity() {

    private val binding by lazy { ActivityEditingUserPicBinding.inflate(layoutInflater) }
    private val viewModel: EditingUserViewModel by viewModels()

    private var mPath: String? = null

    companion object {
        const val PIC_PATH = "pic_image"
        fun startActivity(context: Context, imgPath: String) {
            val intent = Intent(context, EditingUserPicActivity::class.java)
            val bundle = Bundle()
            bundle.putString(PIC_PATH, imgPath)
            context.startActivity(intent.putExtras(bundle))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.activity_in_from_right, 0)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        intent.extras?.containsKey(PIC_PATH)?.let {
            if (it) {
                mPath = intent.extras?.getString(PIC_PATH)
            }
        }

        //拍照并裁剪
        binding.editUserPicTvCamera.setOnClickListener {
            PictureSelector.create(this).openCamera(PictureMimeType.ofImage()) //单独使用相机
                .imageEngine(GlideEngine.createGlideEngine()).isEnableCrop(true).rotateEnabled(false)
                .withAspectRatio(1, 1).isGif(false).showCropGrid(false).showCropGrid(true)
                .forResult(PictureConfig.REQUEST_CAMERA)
        }
        //相册选择
        binding.editUserPicTvPhoto.setOnClickListener {
            PictureSelector.create(this).openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine()).maxSelectNum(1)        // 最大图片选择数量
                .minSelectNum(1)        // 最小选择数量
                .imageSpanCount(5)      // 每行显示个数 int
                .isCamera(false)    //是否展示拍照按钮
                .isEnableCrop(true).rotateEnabled(false).withAspectRatio(1, 1).isGif(false)
                .showCropGrid(false).showCropGrid(true).forResult(PictureConfig.CHOOSE_REQUEST)
        }
        //查看大图
        binding.editUserPicTvBigPic.setOnClickListener {
            PreviewImageActivity.startActivity(this, mPath!!)
        }
        //取消
        binding.editUserPicTvCancel.setOnClickListener {
            finish()
        }
        binding.editUserPicViewTop.setOnClickListener {
            finish()
        }

        //上传图片 回调云地址
        viewModel.uploadImageOk.observe(this, { path ->
            path?.let {
                LiveEventBus.get(ConstantsKey.EDIT_USER_PIC, String::class.java).post(path)
                finish()
            }
        })

        viewModel.errorCallback.observe(this, { errorStr ->
            errorStr?.let {
                finish()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST, PictureConfig.REQUEST_CAMERA -> {
                    val result = PictureSelector.obtainMultipleResult(data)
                    if (result != null && result.size > 0) {
                        if (result[0].isCut) {
                            viewModel.uploadImage(File(result[0].cutPath))
                        } else {
                            viewModel.uploadImage(File(result[0].path))
                        }
                    }
                }
            }
        }
    }
}