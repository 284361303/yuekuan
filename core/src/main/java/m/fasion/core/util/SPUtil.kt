package m.fasion.core.util

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import m.fasion.core.base.ConstantsKey
import m.fasion.core.model.UserModel

object SPUtil {

    var defaultMMKV: MMKV? = null

    init {
        defaultMMKV = MMKV.defaultMMKV()
    }

    fun put(key: String, value: Any): Boolean {
        return when (value) {
            is String -> defaultMMKV?.encode(key, value)!!
            is Int -> defaultMMKV?.encode(key, value)!!
            is Long -> defaultMMKV?.encode(key, value)!!
            is Double -> defaultMMKV?.encode(key, value)!!
            is Float -> defaultMMKV?.encode(key, value)!!
            is Boolean -> defaultMMKV?.encode(key, value)!!
            is Parcelable -> defaultMMKV?.encode(key, value)!!
            else -> false
        }
    }

    fun getString(key: String): String? {
        return defaultMMKV?.decodeString(key, null)
    }

    fun getInt(key: String): Int? {
        return defaultMMKV?.decodeInt(key, 0)
    }

    fun getLong(key: String): Long? {
        return defaultMMKV?.decodeLong(key, 0)
    }

    fun getDouble(key: String): Double? {
        return defaultMMKV?.decodeDouble(key, 0.0)
    }

    fun getFloat(key: String): Float? {
        return defaultMMKV?.decodeFloat(key, 0f)
    }

    fun getBoolean(key: String): Boolean? {
        return defaultMMKV?.decodeBool(key, false)
    }

    inline fun <reified T : Parcelable> getParcelable(key: String): T? {
        return defaultMMKV?.decodeParcelable(key, T::class.java)
    }

    fun getToken(): String? {
        getParcelable<UserModel>(ConstantsKey.USER_KEY)?.apply {
            return token
        }
        return null
    }

    fun removeKey(key: String) {
        defaultMMKV?.removeValueForKey(key)
    }

    fun clearAll() {
        defaultMMKV?.clearAll()
        defaultMMKV?.clearMemoryCache()
    }
}