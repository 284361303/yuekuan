package m.fasion.ai.util.javaVideo

//import it.sauronsoftware.jave.Encoder
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.regex.Pattern

/**
 * 2022年1月4日17:20:05
 * 在idea上运行此类，因为在idea上没有此小工具的上传git地址，我也不能上传自己的git账户上，
 * 不能一个git多个账号进行登录，会影响主项目的代码地址，所以上传到这里面
 */
/*
class MakeVideo {
    //ffmpeg安装路径
    private val mFFmpeg = "C:/Users/shaog/Desktop/ffmpeg/ffmpeg-2021-11-22-git-203b0e3561-essentials_build/bin/ffmpeg"
    private val ffprobe = "C:/Users/shaog/Desktop/ffmpeg/ffmpeg-2021-11-22-git-203b0e3561-essentials_build/bin/ffprobe"

    companion object {
        private var start: Long = 0
        private const val mVideoPath = "C:/Users/shaog/Desktop/videos/start/4/aa-1.mp4"
        private const val BG_PATH = "C:/Users/shaog/Desktop/videos/start/bg.mov"
        private const val OUT_PATH = "C:/Users/shaog/Desktop/videos/no_audio/"
        private const val MERGE_PATH = "C:/Users/shaog/Desktop/videos/no_audio/mergeVideo/" //合并之后的视频
        private const val SPEED_PATH = "C:/Users/shaog/Desktop/videos/no_audio/speed/" //调速后的视频
        private const val CROPPED_BG_PATH = "C:/Users/shaog/Desktop/videos/cropped_bg/"   //背景裁剪和主视频同长度后的存放路径
        private const val CROPPED_HEIGHT_PATH = "C:/Users/shaog/Desktop/videos/no_audio/cropHeight/"    //裁剪顶底部存放的路径
        private const val REMOVE_FRAMES_PATH = "C:/Users/shaog/Desktop/videos/no_audio/removeFrames/"   //删除关键帧保存的路径
        private const val RESULT_PATH =
            "C:/Users/shaog/Desktop/videos/no_audio/resultVideo/4/"   //最后的合成之后的视频路径，可供运营人员直接使用的视频路径
        private const val Error401 = 401  //命令行为空
        private const val Error404 = 404  //文件不存在
        private const val START_TIME = "00:00:00"   //默认的起始时间

        @JvmStatic
        fun main(args: Array<String>) {
            start = System.currentTimeMillis()
            val make = MakeVideo()
            make.speedVideo(make)
        }
    }

    */
/**
     * 不需要删除音频的操作
     *//*

    private fun speedVideo(make: MakeVideo) {
        println("开始调速")
        var mainVideoTime = 0.0
        var bgVideoTime = 0.0
        make.setVideoSpeed(
            1.1f,
            mVideoPath,
            SPEED_PATH + VideoUtil.getVideoName(mVideoPath)
        ) { speedNum ->
            when (speedNum) {
                0 -> {  //成功
                    println("调速完成")
                    //裁剪主视频的顶底部
                    make.cropTopAndBottom(
                        SPEED_PATH + VideoUtil.getVideoName(mVideoPath),
                        CROPPED_HEIGHT_PATH + VideoUtil.getVideoName(mVideoPath),
                        90
                    ) { cropHeightNum ->
                        if (cropHeightNum == 0) {
                            //调速完成获取调速后的视频的时长
                            mainVideoTime = make.getVideoTime(
                                CROPPED_HEIGHT_PATH + VideoUtil.getVideoName(mVideoPath)
                            )
                            //获取背景视频的时长
                            val bgPath =
                                VideoUtil.findNoAudioVideoPath(VideoUtil.getVideoName(BG_PATH), OUT_PATH)
                            bgPath?.let { bgNoAudioPath ->
                                bgVideoTime = make.getVideoTime(bgNoAudioPath)
                                println("获取的背景视频时长-- " + bgVideoTime + " ，路径-- " + bgNoAudioPath)
                                //背景视频长度大于主视频长度 就裁剪主视频长度一样大小
                                if (mVideoPath.isNotEmpty() && bgPath.isNotEmpty()) {
                                    println("2个视频的时长各：主视频-- $mainVideoTime  背景时长-- $bgVideoTime")
                                    if (bgVideoTime > mainVideoTime) {
                                        println("--------开始裁剪--------")
                                        //如果背景视频长度大于主视频就裁剪背景视频的长度与主视频长度一致
                                        make.cropVideo(
                                            bgNoAudioPath,
                                            START_TIME,
                                            VideoUtil.selectSymbol(mainVideoTime),
                                            CROPPED_BG_PATH + VideoUtil.getVideoName(BG_PATH)
                                        ) { cropNums ->
                                            when (cropNums) {
                                                0 -> {
                                                    //裁剪完成开始把背景和主视频合并处理
                                                    println("--------开始合并----")
                                                    make.merageFunction(
                                                        CROPPED_BG_PATH + VideoUtil.getVideoName(
                                                            BG_PATH
                                                        )
                                                    )
                                                }
                                                404 -> {
                                                    println("裁剪视频文件不存在")
                                                }
                                            }
                                        }
                                    } else {
                                        //println("主视频长度大于背景视频长度")
                                        make.merageFunction(bgNoAudioPath)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    */
/**
     * 删除音频的
     * 去除主视频和背景视频的音频后进行主视频变速，后面都基于变速后的视频进行生成
     *//*

    private fun removeAudio(make: MakeVideo) {
        make.removeAudio(mVideoPath, OUT_PATH + VideoUtil.getVideoName(mVideoPath)) {
            if (it == 0) {
                //1、删除主视频音频 2、删除背景视频的音频 3、主视频进行调速 4、去除音频后的主、背景视频进行长度对比，把背景视频裁剪成主视频相等的长度
                //5、裁剪后的背景视频和主视频进行合并  6、进行删除关键帧（每4秒删除一帧）会缩短视频时间  7、导出对应格式的视频
                make.removeAudio(BG_PATH, OUT_PATH + VideoUtil.getVideoName(BG_PATH)) { bgResult ->
                    if (bgResult == 0) {
                        var mainVideoTime = 0.0
                        var bgVideoTime = 0.0

                        val mainPath = VideoUtil.findNoAudioVideoPath(VideoUtil.getVideoName(mVideoPath), OUT_PATH)
                        mainPath?.let {
                            //去除主视频和背景视频的音频后进行主视频变速，后面都基于变速后的视频进行生成
                            println("开始调速")
                            make.setVideoSpeed(
                                1.1f,
                                mainPath,
                                SPEED_PATH + VideoUtil.getVideoName(mVideoPath)
                            ) { speedNum ->
                                when (speedNum) {
                                    0 -> {  //成功
                                        println("调速完成")
                                        //裁剪主视频的顶底部
                                        make.cropTopAndBottom(
                                            SPEED_PATH + VideoUtil.getVideoName(mVideoPath),
                                            CROPPED_HEIGHT_PATH + VideoUtil.getVideoName(mVideoPath),
                                            90
                                        ) { cropHeightNum ->
                                            if (cropHeightNum == 0) {
                                                //调速完成获取调速后的视频的时长
                                                mainVideoTime = make.getVideoTime(
                                                    CROPPED_HEIGHT_PATH + VideoUtil.getVideoName(mVideoPath)
                                                )
                                                //获取背景视频的时长
                                                val bgPath =
                                                    VideoUtil.findNoAudioVideoPath(VideoUtil.getVideoName(BG_PATH), OUT_PATH)
                                                bgPath?.let { bgNoAudioPath ->
                                                    bgVideoTime = make.getVideoTime(bgNoAudioPath)
                                                    println("获取的背景视频时长-- " + bgVideoTime + " ，路径-- " + bgNoAudioPath)
                                                    //背景视频长度大于主视频长度 就裁剪主视频长度一样大小
                                                    if (mainPath.isNotEmpty() && bgPath.isNotEmpty()) {
                                                        println("2个视频的时长各：主视频-- $mainVideoTime  背景时长-- $bgVideoTime")
                                                        if (bgVideoTime > mainVideoTime) {
                                                            println("--------开始裁剪--------")
                                                            //如果背景视频长度大于主视频就裁剪背景视频的长度与主视频长度一致
                                                            make.cropVideo(
                                                                bgNoAudioPath,
                                                                START_TIME,
                                                                VideoUtil.selectSymbol(mainVideoTime),
                                                                CROPPED_BG_PATH + VideoUtil.getVideoName(BG_PATH)
                                                            ) { cropNums ->
                                                                when (cropNums) {
                                                                    0 -> {
                                                                        //裁剪完成开始把背景和主视频合并处理
                                                                        println("--------开始合并----")
                                                                        make.merageFunction(
                                                                            CROPPED_BG_PATH + VideoUtil.getVideoName(
                                                                                BG_PATH
                                                                            )
                                                                        )
                                                                    }
                                                                    404 -> {
                                                                        println("裁剪视频文件不存在")
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            //println("主视频长度大于背景视频长度")
                                                            make.merageFunction(bgNoAudioPath)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    */
/**
     * 合并的方法提取出来
     *//*

    private fun merageFunction(bgPath: String) {
        mergeVideo(
            0.01f,
            CROPPED_HEIGHT_PATH + VideoUtil.getVideoName(mVideoPath),
            bgPath,
            MERGE_PATH + VideoUtil.getVideoName(mVideoPath)
        ) { nums ->
            when (nums) {
                0 -> {
                    println("合并完成")
                    //合并完成开始进行删除关键帧，每4秒删除一帧
                    */
/*make.removeFrames(
                        MERGE_PATH + VideoUtil.getVideoName(
                            mVideoPath
                        ),
                        REMOVE_FRAMES_PATH + VideoUtil.getVideoName(
                            mVideoPath
                        ), 4
                    ) { removeNums ->
                        if (removeNums == 0) {
                            //删除关键帧成功就开始导出指定的格式文件，如：缩放比例、fps、饱和度
                    println("开始导出指定格式")
                    make.setingOutFormat(
                        REMOVE_FRAMES_PATH + VideoUtil.getVideoName(
                            mVideoPath
                        ),
                        RESULT_PATH + VideoUtil.getVideoName(
                            mVideoPath
                        )
                    ) { outNum ->
                        if (outNum == 0) {
                            println("导出指定格式成功--finish")
                        }
                    }
                        }
                    }*//*

                    println("开始导出指定格式")
                    setingOutFormat(
                        MERGE_PATH + VideoUtil.getVideoName(
                            mVideoPath
                        ),
                        RESULT_PATH + VideoUtil.getVideoName(
                            mVideoPath
                        )
                    ) { outNum ->
                        if (outNum == 0) {
                            println("导出指定格式成功--finish")
                        }
                    }
                }
                404 -> {
                    println("文件不存在")
                }
            }
            val end = System.currentTimeMillis()
            val last = (end - start) / 1000
            println("总时间-- " + last + " 秒")
        }
    }

    */
/**
     * 顶部和底部进行裁剪像素
     * @param   cropHeight  裁剪的高度（顶部+底部的和）默认90（传参90），就是顶底各45像素
     *//*

    fun cropTopAndBottom(videoPath: String, outPath: String, cropHeight: Int, observer: ((Int?) -> Unit?)? = null) {
        //        ffmpeg -i in.mp4 -filter:v      "crop=in_w:in_h-40" -c:a copy out.mp4     //顶底部各裁剪20像素共40像素
        val cropCmd = "ffmpeg -i $videoPath -filter:v \"crop=in_w:in_h-$cropHeight\" -c:a copy $outPath"  //各45,共90
//        val aa = "ffmpeg -i $videoPath -filter:v \"crop=in_w:in_h-100:0:out_h\" -c:a copy $outPath" //只有顶部裁剪了100像素
        cmdRequest(videoPath, outPath, cropCmd) { nums ->
            println("裁剪顶部和底部- $nums")
            observer?.invoke(nums)
        }
    }

    */
/**
     * 设置视频输出格式参数
     * -r fps=60 显示格式每秒60帧播放
     * -vf scale=720:1280 缩放到指定的宽高
     * -ar 44100 （采样率是给音频设置的，视频不能设置上去）
     * contrast  值必须是一个-2.0-2.0间的浮点数，默认为1
     * brightness 亮度   必须是一个-1.0-1.0间的浮点数，默认为0
     * saturation 饱和   设置saturation表达式. 值必须是一个0-3.0间的浮点数，默认为1
     *//*

    fun setingOutFormat(videoPath: String, outPath: String, observer: ((Int?) -> Unit?)? = null) {
        if (!File(videoPath).exists()) {
            println("输出指定格式文件不存在")
            observer?.invoke(404)
            return
        }
        val videoSize = Encoder().getInfo(File(videoPath)).video.size
        val mainWidth = videoSize.width
        val mainHeight = videoSize.height
        val formatCmd: String

        if (mainHeight > mainWidth) {   //竖屏
            formatCmd =
                "ffmpeg -i $videoPath -r 60 -ar 44100 -vf scale=720:1280,eq=contrast=1:brightness=0:saturation=1.1 $outPath"
        } else {  //横屏就不用进行缩放视频宽高了
            formatCmd = "ffmpeg -i $videoPath -r 60 -ar 44100 -vf eq=contrast=1:brightness=0:saturation=1.1 $outPath"
        }
        println("导出的指令 $formatCmd")
        cmdRequest(videoPath, outPath, formatCmd) { nums ->
            println("输出格式设置完成- $nums")
            observer?.invoke(nums)
        }
    }

    */
/**
     * 视频添加噪点
     *//*

    fun filterVideo(videoPath: String, outPath: String, observer: ((Int?) -> Unit?)? = null) {
//        val filterCmd = "ffmpeg -i $videoPath -vf smartblur=5:0.5:0 $outPath"
        val filterCmd1 = "ffmpeg -i $videoPath -vf boxblur=2:1 $outPath"
        cmdRequest(videoPath, outPath, filterCmd1) { nums ->
            println("噪点添加完成- $nums")
            observer?.invoke(nums)
        }
    }

    */
/**
     * 视频水平翻转
     * TODO://目前翻转完字幕会产生相反的镜像，适合没有字符的video
     *//*

    fun levelHflip(videoPath: String, outPath: String, observer: ((Int?) -> Unit?)? = null) {
        //[root@blog 1]# ffmpeg -i 8_9f6fa300bacded7b.mp4 -vf "hflip" /data/dev/think_file/html/8_hflip.mp4
        val hflipCmd =
            "ffmpeg -i $videoPath -vf \"hflip\" $outPath"
        cmdRequest(videoPath, outPath, hflipCmd) { nums ->
            println("视频翻转完成$nums")
            observer?.invoke(nums)
        }
    }

    */
/**
     * 删除特定间隔时间的帧，视频长度会缩减
     * @param   videoPath   原视频路径地址
     * @param   outPath     抽帧之后的保存路径
     * @param   intervals   间隔时间    如：4(默认) 就是每4秒删除一帧
     *//*

    fun removeFrames(videoPath: String, outPath: String, intervals: Int, observer: ((Int?) -> Unit?)? = null) {
        val intervalsCmd =
            "ffmpeg -i $videoPath -filter:v \"select=mod(n+1\\,$intervals),setpts=N/FRAME_RATE/TB\" $outPath"
        cmdRequest(videoPath, outPath, intervalsCmd) { nums ->
            observer?.invoke(nums)
        }
    }

    */
/**
     * 设置加速播放
     * @param   speed 取值范围 0.1~1
     * 1原速度不变  0.5 加速200%   0.9 加速110%
     * demo中要求是110%，所以是传0.9
     * @param   videoPath   要变速的视频地址
     * @param   outPath 输出地址
     *//*

    fun setVideoSpeed(
        speed: Float, videoPath: String, outPath: String,
        observer: ((Int?) -> Unit?)? = null
    ) {
//        val speedCmd = "ffmpeg -i $videoPath -filter:v \"setpts=$speed*PTS\" $outPath"
        val speedCmd =
            "ffmpeg -y -i ${videoPath} -filter_complex [0:v]setpts=PTS/$speed[v];[0:a]atempo=$speed[a] -map [v] -map [a] -preset superfast $outPath"
        println("调速指令- $speedCmd")
        cmdRequest(videoPath, outPath, speedCmd) { nums ->
            observer?.invoke(nums)
        }
    }

    */
/**
     * 命令请求封装
     *//*

    fun cmdRequest(
        videoPath: String, outPath: String, cmdKey: String, observer: ((Int?) -> Unit?)? = null
    ) {
        if (!File(videoPath).exists()) {
            println("主方法内，{文件不存在}")
            observer?.invoke(Error404)
            return
        }
        if (cmdKey.isEmpty()) {
            println("主方法内，{指令为空}")
            observer?.invoke(Error401)
            return
        }
        VideoUtil.deleteExistsFile(outPath)
        val exec = Runtime.getRuntime().exec(cmdKey)
        //加上下面2行是为了防止死锁发生，一直阻塞在那里没有响应
        val errorStream = BufferedReader(InputStreamReader(exec.errorStream))
        while ((errorStream.readLine()) != null) {
        }
        val waitFor = exec.waitFor()
        if (waitFor == 0) {
            exec.destroy()
            observer?.invoke(0)
        } else {
            exec.destroy()
            println("主方法内，{异常结束}")
            observer?.invoke(waitFor)
        }
    }

    */
/**
     * 合并视频（背景视频与主视频合并，并设置透明度）
     * @param   transparent 0.1~0.9 数值越大背景越透明
     * @param   videoPath1  主视频地址
     * @param   bgVideoPath 背景视频地址
     * @param   outPath 导出路径地址
     *//*

    fun mergeVideo(
        transparent: Float,
        videoPath1: String,
        bgVideoPath: String,
        outPath: String,
        observer: ((Int?) -> Unit?)? = null
    ) {
        if (!File(videoPath1).exists()) {
            println("主视频不存在")
            observer?.invoke(404)
            return
        }
        if (!File(bgVideoPath).exists()) {
            println("背景视频不存在")
            observer?.invoke(404)
            return
        }
        VideoUtil.deleteExistsFile(outPath)
        val timeCmd = "ffmpeg -i $videoPath1 -i $bgVideoPath -filter_complex " +
                "\"[1]format=yuva444p,colorchannelmixer=aa=$transparent[$bgVideoPath];[0][$bgVideoPath]overlay\" $outPath"
        println("合并的指令-- $timeCmd")
        val exec = Runtime.getRuntime().exec(timeCmd)
        println("合并中-----")
        //加上下面2行是为了防止死锁发生，一直阻塞在那里没有响应
        val errorStream = BufferedReader(InputStreamReader(exec.errorStream))
        while ((errorStream.readLine()) != null) {
        }
        val waitFor = exec.waitFor()
        println("合并完成 $waitFor 了")
        if (waitFor == 0) {
            exec.destroy()
            observer?.invoke(0)
        } else {
            exec.destroy()
            observer?.invoke(waitFor)
        }
    }

    fun testRemoveAudio(videoPath: String, outPath: String) {
        val dwelete = "ffmpeg -i $videoPath -c copy -an $outPath"
        cmdRequest(videoPath, outPath, dwelete) {
            println("结束了 $it")
        }
    }

    */
/**
     * 去除音频
     * @param videoPath 视频的路径 C:/Users/shaog/Desktop/videos/xx/xx.mp4
     * @param outPath   输出路径   C:/Users/shaog/Desktop/videos/xx/xx.mp4
     *//*

    fun removeAudio(videoPath: String, outPath: String, observer: ((Int?) -> Unit?)? = null) {
        if (VideoUtil.checkFileExist(videoPath)) {
            VideoUtil.deleteExistsFile(outPath)
//            val timeCmd = "$mFFmpeg -i $videoPath -map 0:0 -vcodec copy $outPath"
            val timeCmd = "ffmpeg -i $videoPath -c copy -an $outPath"
            println("删除音频指令-- $timeCmd")
            val exec = Runtime.getRuntime().exec(timeCmd)
            val waitFor = exec.waitFor()
            println("删除音频指令状态-- $waitFor")
            if (waitFor == 0) {
                exec.destroy()
                observer?.invoke(0)
            } else {
                exec.destroy()
                observer?.invoke(waitFor)
            }
        } else {
            println("文件不存在")
        }
    }

    */
/**
     * 获取视频时长
     *//*

    fun getVideoTime(videoPath: String): Double {
        val timeCmd = "$mFFmpeg -i $videoPath"
        val exec = Runtime.getRuntime().exec(timeCmd)
        val br = BufferedReader(InputStreamReader(exec.errorStream))
        val sb = StringBuffer()
        br.lineSequence().forEach {
            sb.append(it)
        }
        br.close()
        //从视频信息中解析时长
        val regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb/s"
        val pattern = Pattern.compile(regexDuration)
        val matcher = pattern.matcher(sb.toString())
        if (matcher.find()) {
            exec.destroy()
            return getTimelen(matcher.group(1))
        }
        return 0.0
    }

    */
/**
     * 裁剪视频
     * @param videoPath 要裁剪的视频的地址
     * @param   startTime   开始时间默认 00:00:00
     * @param   endTime 截取的截止时间 （传截止时间） 比如：从00:00:00开始  截取到  00:00:10结束(要传的时间)  共10秒钟时间
     * @return  保存中间的视频 从00 -- 结束时间 之间的长度
     *//*

    fun cropVideo(
        videoPath: String,
        startTime: String,
        endTime: String,
        outPath: String,
        observer: ((Int?) -> Unit?)? = null
    ) {
        if (VideoUtil.checkFileExist(videoPath)) {
            VideoUtil.deleteExistsFile(outPath)
            val timeCmd =
                "$mFFmpeg -ss $startTime -i $videoPath -to $endTime -c copy $outPath"
            val exec = Runtime.getRuntime().exec(timeCmd)
            println("裁剪中----")
            val waitFor = exec.waitFor()
            println("裁剪完成  $waitFor 了")
            if (waitFor == 0) {
                exec.destroy()
                observer?.invoke(0)
            } else {
                exec.destroy()
                observer?.invoke(waitFor)
            }
        } else {
            observer?.invoke(404)
        }
    }

    */
/**
     * @param timelen   格式:"00:00:10.68"
     * @return 如：10.01  //保留2位小数
     *//*

    private fun getTimelen(timelen: String): Double {
        var min = 0.0
        val strs = timelen.split(":").toTypedArray()
        if (strs[0] > "0") {
            min += Integer.valueOf(strs[0]) * 60 * 60 //秒
        }
        if (strs[1] > "0") {
            min += Integer.valueOf(strs[1]) * 60
        }
        if (strs[2] > "0") {
//            min += Math.round(java.lang.Float.valueOf(strs[2]))   //取整数不保留任何小数
            //保留2位小数
            val toDouble = BigDecimal(strs[2]).setScale(2, RoundingMode.HALF_UP).toDouble()
            min += toDouble
        }
        return min
    }
}*/
