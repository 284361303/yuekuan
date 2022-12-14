package m.fasion.core

object Config {
    /**
     * 友盟账号：aisway   WUdi0922
     */
    const val BASE_DEBUG_URL = "http://192.168.50.113:56100"

//    const val BASE_DEBUG_URL = "https://play.fasionai.com"
    const val BASE_URL = "https://play.fasionai.com"

    //微信appId
    const val APP_ID = "wx485a5432191e5d37"

    //在微博开发平台为应用申请的App Key
    const val APP_KEY: String = "2045436852"

    //在微博开放平台设置的授权回调页
    const val REDIRECT_URL = "http://www.sina.com"

    //友盟统计埋点
    const val UMENG_APP_KEY = "61c96f8ce014255fcbcbecc2"

    //在微博开放平台为应用申请的高级权限
    const val SCOPE = ("email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write")

    //美洽app_key
    const val MQ_KEY = "328a2d473b86df2c8aec689776156765"

    //腾讯bugly
    const val BUGLY_APP_ID = "6bc74abb9e"

    //官网地址
    const val WEBSIT_ADDRESS = "https://www.fasionai.com"

    //企业服务邮箱
    const val WEBSIT_EMAIL = "yuekuan@fasion.ai"

    //隐私政策
    const val PRIVACY_URL = "file:///android_asset/privacy.html"

    //用户协议
    const val PROTOCOL_URL = "file:///android_asset/protocol.html"
}