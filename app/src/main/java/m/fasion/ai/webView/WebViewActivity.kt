package m.fasion.ai.webView

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import m.fasion.ai.base.BaseActivity
import m.fasion.ai.databinding.ActivityWebViewBinding

/**
 * 加载H5页面
 */
class WebViewActivity : BaseActivity() {

    private var mUrl: String? = null

    private val binding by lazy { ActivityWebViewBinding.inflate(layoutInflater) }

    companion object {
        const val URL = "url"
        const val TITLE_NAME = "title_name"
        fun startActivity(context: Context, url: String, titleName: String?) {
            val intent = Intent(context, WebViewActivity::class.java)
            val bundle = Bundle()
            bundle.putString(URL, url)
            bundle.putString(TITLE_NAME, titleName)
            context.startActivity(intent.putExtras(bundle))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.webViewTitle.inCludeTitleIvBack.setOnClickListener {
            finish()
        }

        with(intent.extras?.getString(TITLE_NAME)) {
            binding.webViewTitle.inCludeTitleTvCenterTitle.text = this
        }

        intent.extras?.containsKey(URL)?.apply {
            mUrl = intent.extras?.getString(URL)
        }

        initWebView()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            mUrl?.let {
                binding.webViewWebView.clearCache(true)
                binding.webViewWebView.loadUrl(it)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun initWebView() {
        val settings = binding.webViewWebView.settings
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.defaultTextEncodingName = "utf-8"
        settings.domStorageEnabled = true
        settings.blockNetworkImage = false
        settings.setGeolocationEnabled(true)

        binding.webViewWebView.apply {
            webViewClient = WebViewClient()
            webChromeClient = ChromeClient()
            removeJavascriptInterface("searchBoxJavaBridge_")
            removeJavascriptInterface("accessibilityTraversal")
            removeJavascriptInterface("accessibility")

            addJavascriptInterface(WebAppInterface(this@WebViewActivity), "android")
        }
    }

    inner class ChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            with(title) {
                binding.webViewTitle.inCludeTitleTvCenterTitle.text = this
            }
            super.onReceivedTitle(view, title)
        }
    }

    /**
     * html代码调用原生方法块
     */
    class WebAppInterface(private val mContext: Context) {

        @JavascriptInterface
        fun showToast(toast: String) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webViewWebView.removeAllViews()
        binding.webViewWebView.clearCache(true)
        binding.webViewWebView.clearHistory()
        binding.webViewWebView.pauseTimers()
        binding.webViewWebView.destroy()
    }
}