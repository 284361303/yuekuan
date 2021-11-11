package m.fasion.core.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody

data class ErrorDataModel(val message: String)

/**
 * @author  shao_g
 * 2021年11月9日20:08:45
 * errorBody()?string() 会报 Inappropriate blocking method call 警告所有采用此方法来规避警告
 */
@Suppress("BlockingMethodInNonBlockingContext")
suspend fun ResponseBody.stringSuspending() =
    withContext(Dispatchers.IO) {
        string()
    }