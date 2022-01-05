package m.fasion.ai.util.javaVideo

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object VideoUtil {

    /**
     * 检测本地文件是否存在
     */
    fun checkFileExist(path: String): Boolean {
        if (path.isEmpty() || !File(path).exists()) {
            return false
        }
        if (File(path).exists()) {
            return true
        }
        return false
    }

    /**
     * 时间戳
     */
    fun getFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return String.format("_%s.mp4", timeStamp)
    }

    /**
     * 截取文件名字最后的名字，
     * @return 格式如：xxx.mp4
     */
    fun getVideoName(videoPath: String): String {
        if (checkFileExist(videoPath)) {
            return videoPath.substring(videoPath.lastIndexOf("/") + 1, videoPath.length) //截取视频最后面的名字
        }
        return ""
    }

    /**
     * 截取文件名字最后的名字，
     * @return 格式如：xxx.mp4
     */
    fun getVideoName1(videoPath: String): String {
        if (checkFileExist(videoPath)) {
            return videoPath.substring(videoPath.lastIndexOf("\\") + 1, videoPath.length) //截取视频最后面的名字
        }
        return ""
    }


    /**
     * 根据视频名字去没有音频文件夹中找到它
     *
     * @param videoName 音频的名字 如：c4e2449ffa981add9c5deed7d1145ebd.mp4
     * @param filePath  存放没有音频的文件夹路径
     */
    fun findNoAudioVideoPath(videoName: String, filePath: String): String? {
        val file = File(filePath)
        var cachePath: String? = null
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files == null || files.isEmpty()) {
                return null
            }
            for (fileTemp in files) {
                if (fileTemp.exists() && fileTemp.name.isNotEmpty() && fileTemp.name == videoName) {
                    cachePath = fileTemp.absolutePath
                    break
                }
            }
        }
        return cachePath
    }

    /**
     * 秒转时分秒
     * @param second
     * @return
     */
    fun getFormatTime(second: Int): String {
        if (second > 0) {
            val num0 = NumFormat(0)
            if (second < 60) { //秒
                return num0 + ":" + num0 + ":" + NumFormat(second)
            }
            if (second < 3600) { //分
                return num0 + ":" + NumFormat(second / 60) + ":" + NumFormat(second % 60)
            }
            if (second < 3600 * 24) { //时
                return NumFormat(second / 60 / 60) + ":" + NumFormat(second / 60 % 60) + ":" + NumFormat(second % 60)
            }
            if (second >= 3600 * 24) { //天
                return NumFormat(second / 60 / 60 / 24) + "天" + NumFormat(second / 60 / 60 % 24) + ":" + NumFormat(
                    second / 60 % 60
                ) + ":" + NumFormat(second % 60)
            }
        }
        return "--"
    }

    /**
     * 格式化时间
     * @param sec
     * @return
     */
    private fun NumFormat(sec: Int): String {
        return if (sec.toString().length < 2) {
            "0$sec"
        } else {
            sec.toString()
        }
    }

    /**
     * 查询时间中间的冒号有几个并添加进去
     * @param times 当前的时间秒
     * @return 00:00:00
     */
    fun selectSymbol(times: Double): String {
        if (times > 0) {
            val string = times.toString()
            if (string.contains(".")) {
                //如10.47
                val last = string.substring(string.indexOf(".") + 1).toInt()    //47
                val before = string.substring(0, string.indexOf(".")).toInt()   //10
                val formatTime = getFormatTime(before)
                return formatTime + ".$last"
            } else {
                val formatTime = getFormatTime(string.toInt())
                return formatTime
            }
        }
        return "00:00:00"
    }

    /**
     * 检测文件是否存在，存在就删除
     */
    fun deleteExistsFile(path:String){
        val file = File(path)
        if(file.exists()){
            file.delete()
        }
    }
}