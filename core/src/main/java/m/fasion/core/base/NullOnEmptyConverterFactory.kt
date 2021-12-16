package m.fasion.core.base

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * @author  shao_g 2021年12月15日19:40:39
 * 避免后台服务器返回200状态码但返回的结果为null 出现闪退  Retrofit2 error java.io.EOFException: End of input at line 1 column 1
 * 解决地址：https://stackoverflow.com/questions/35744795/retrofit2-error-java-io-eofexception-end-of-input-at-line-1-column-1
 */
class NullOnEmptyConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *> {
        val delegate: Converter<ResponseBody, Any> = retrofit.nextResponseBodyConverter(this, type, annotations)
        return Converter { body -> if (body.contentLength() == 0L) null else delegate.convert(body) }
    }
}