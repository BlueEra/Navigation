package base.foxizz.util

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间工具类
 */
object TimeUtil {
    const val FORMATION_yMdHms = "yy-MM-dd HH:mm:ss"
    const val FORMATION_yMd = "yy-MM-dd"
    const val FORMATION_yM = "yy-MM"
    const val FORMATION_Hms = "HH:mm:ss"
    const val FORMATION_Hm = "HH:mm"
    const val FORMATION_ms = "mm:ss"

    //临时的Date对象
    @get:Synchronized
    private val temporaryDate = Date()

    /**
     * 格式化日期
     *
     * @param date      日期
     * @param formation 格式
     * @return 格式化的日期
     */
    @SuppressLint("SimpleDateFormat")
    fun format(date: Date, formation: String): String =
        SimpleDateFormat(formation).format(date)

    /**
     * 反格式化日期
     *
     * @param formatString 格式化的日期
     * @param formation    格式
     * @return 日期
     */
    @SuppressLint("SimpleDateFormat")
    fun parse(formatString: String, formation: String): Date = try {
        SimpleDateFormat(formation).parse(formatString)
    } catch (e: ParseException) {
        e.printStackTrace()
        Date()
    }

    /**
     * 格式化日期
     *
     * @param timeMillis 时间戳
     * @param formation  格式
     * @return 格式化的日期
     */
    @SuppressLint("SimpleDateFormat")
    @Synchronized
    fun format(timeMillis: Long, formation: String): String =
        format(temporaryDate.apply { time = timeMillis }, formation)

    /**
     * 判断是否在时间内
     *
     * @param nowTime   现在的时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return Boolean
     */
    fun isInTime(nowTime: Date, startTime: Date, endTime: Date): Boolean {
        if (nowTime.time == startTime.time || nowTime.time == endTime.time) {
            return true
        }
        while (startTime.time > endTime.time) { //结束时间超过24点时进入下一天
            endTime.time += 24 * 60 * 60 * 1000
        }
        return nowTime.after(startTime) && nowTime.before(endTime)
    }
}