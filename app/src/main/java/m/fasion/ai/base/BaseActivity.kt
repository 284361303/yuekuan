package m.fasion.ai.base

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import m.fasion.ai.R
import m.fasion.ai.login.LoginActivity
import m.fasion.core.util.SPUtil

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission()

        initTitle()
        m.fasion.ai.util.ActivityManager.instance?.addActivity(this)
    }

    private fun initTitle() {
        val includeTitleBack = findViewById<AppCompatImageView>(R.id.inCludeTitle_ivBack)
        includeTitleBack?.let {
            it.setOnClickListener {
                finish()
            }
        }
    }

    /**
     * 请求权限
     */
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val newList: MutableList<String> = mutableListOf()  //里面装未允许的权限列表

        val permList = arrayOf(
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION
        )

        for (perm in permList) {
            //未允许的权限
            if (ActivityCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                newList.add(perm)
            }
        }
        if (newList.size > 0) {
            ActivityCompat.requestPermissions(this, newList.toTypedArray(), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * 判断登录，没有登录就跳转到登录页面
     */
    protected open fun checkLogin(callback: () -> Unit) {
        if (!SPUtil.getToken().isNullOrEmpty()) {
            callback.invoke()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}