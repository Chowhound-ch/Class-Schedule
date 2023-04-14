package per.chowhound.plugin.mirai.academic

import cn.hutool.crypto.digest.DigestUtil
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import org.apache.http.entity.StringEntity
import per.chowhound.plugin.mirai.ClassSchedule
import per.chowhound.plugin.mirai.util.*
import java.io.File
import java.sql.Date
import java.util.stream.Collectors

/**
 * Academic公共接口
 */
interface Academic{
    fun refresh(username: String, password: String)
}

/**
 * @Author: Chowhound
 * @Date: 2023/4/13 - 19:59
 * @School: HFUT
 * @Description: 在线从教务系统获取课表， 对本地课表信息进行更改
 */
@Suppress("unused")
class HFUTAcademic: HttpBase(), Academic{
    override fun refresh(username: String, password: String){

        logInfo("开始刷新hfut 学号:${username} 课表中内容...")

            try {
                //访问中间网址，获取会话cookie和加密密钥
                val encode = DigestUtil.sha1Hex("${doGetStr(GET_SALT)}-${password}") //密码加密

                //登录验证


                val entity = StringEntity(
                    JacksonUtil.toJsonString(
                        mutableMapOf(Pair("username", username), Pair("password", encode), Pair("captcha", ""))
                    ), "UTF-8"
                )
                entity.setContentType("application/json")
//                val loginRes =
                val res = doPostJson(LOGIN_URL, entity) //登录结果

                if (res["needCaptcha"].asBoolean()){ // 需要验证码
                    logWarn("登录中 needCaptcha: ${res["needCaptcha"]}")
                }
                if (!res["result"].asBoolean()) { //登录失败
                    logError("登录失败: ${res["message"]}")
                    return
                }
                //访问我的课程表，获取课程表中lessons的id
                val lessonIds = doGetJson( LESSON_FOR_ID )["lessonIds"]
                //TODO 待确认lessonIds.toString()结果

                //再次请求，根据lessonIds请求得到具体lesson信息
                val entityForRes = StringEntity(
                    JacksonUtil.toJsonString(
                        mutableMapOf(Pair("lessonIds", lessonIds), Pair("studentId", 152113), Pair("weekIndex", ""))
                    ), "UTF-8"
                )

                entityForRes.setContentType("application/json")
                val lessonsRes = doPostJson(LESSON_URL, entityForRes)
                //对返回的lesson信息进行解析
                try {
                    val hfutScheduleList = ArrayList<HFUTSchedule>().apply {
                        lessonsRes["result"]["scheduleList"].forEach { res: JsonNode ->
                            try {
                                val schedule = JacksonUtil.readValue(res.toString(), HFUTSchedule::class.java)
                                schedule.room = res["room"]["nameZh"].asText()
                                this.add(schedule)
                            } catch (e: JsonProcessingException) {
                                logError("解析lesson信息错误: ${e.message?: ""}")
                                e.printStackTrace()
                            }
                        }
                    }

                    // key: lessonId, value: courseName
                    val classMap = HashMap<Long, String>().apply {
                        lessonsRes["result"]["lessonList"].forEach { res ->
                            this[res["id"].asLong()] = res["courseName"].asText()
                        }
                    }

                    // 组装Schedule对象
                    val schedules = hfutScheduleList.stream().map {
                        val schedule = BeanUtil.copyProperties(it, Schedule())
                        schedule.room = classMap[it.lessonId]!!
                        return@map schedule
                    }.collect(Collectors.toList())

                    // 保存到本地
                    JacksonUtil.objectMapper.writeValue(File(ClassSchedule.academicFolder + "/hfut-${username}.json"), schedules)

                } catch (e: Exception) {

                    logError("数据解析错误: ${e.message}")

                    e.printStackTrace()
                }
            } catch (e: Exception) {
                logError("访问网址错误: ${e.message}")

                e.printStackTrace()
            }
    }

    //抑制HTTP links are not secure警告
    @Suppress("SpellCheckingInspection", "HttpUrlsUsage")
    companion object{
        const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 SLBrowser/8.0.0.3161 SLBChan/103"
        const val REFERER = "http://jxglstu.hfut.edu.cn/eams5-student/login?refer=http://jxglstu.hfut.edu.cn/eams5-student/for-std/course-table/info/152113"
        const val GET_SALT = "http://jxglstu.hfut.edu.cn/eams5-student/login-salt"
        const val LOGIN_URL = "http://jxglstu.hfut.edu.cn/eams5-student/login"
        const val LESSON_URL = "http://jxglstu.hfut.edu.cn/eams5-student/ws/schedule-table/datum"
        const val LESSON_FOR_ID = "http://jxglstu.hfut.edu.cn/eams5-student/for-std/course-table/get-data?bizTypeId=23&semesterId=214&dataId=152113"
    }

    // region 课表数据类
    data class ClassMap(
        val id: String? = null,
        val lessonId: Long? = null,
        val className: String
    ){
        constructor(): this(null, null, "课程")
    }


    @JsonIgnoreProperties("room")
    data class HFUTSchedule(
        @field:JsonProperty("lessonId")
        var lessonId: Long? = null,
        var date: Date? = null,
        var room: String? = null,
        @field:JsonProperty("weekday")
        var day: Int? = null,
        @field:JsonProperty("startTime")
        var start: Int? = null,
        @field:JsonProperty("endTime")
        var end: Int? = null,
        @field:JsonProperty("personName")
        var teacher: String? = null,
        @field:JsonProperty("weekIndex")
        var week: Int? = null
    ) {
        companion object {
            fun instance(): HFUTSchedule = HFUTSchedule().apply { this.date = Date(System.currentTimeMillis()) }
        }
    }
    // endregion
}