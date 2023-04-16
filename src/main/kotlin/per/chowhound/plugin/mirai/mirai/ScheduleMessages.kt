package per.chowhound.plugin.mirai.mirai

import cn.hutool.core.date.DateUtil
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.toPlainText
import per.chowhound.plugin.mirai.academic.Schedule
import per.chowhound.plugin.mirai.util.WeekUtil
import java.sql.Date
import java.time.temporal.ChronoField

/**
 * @Author: Chowhound
 * @Date: 2023/4/16 - 14:42
 * @Description:
 */
class ScheduleMessages private constructor(): ArrayList<PlainText>(){

    companion object{
        // 获得课表的消息集合,可为某一周也可为某一日
        // 暂你不支持多周
        fun buildMsg(scheduleList: List<Schedule>): ScheduleMessages{

            val messagesList = ScheduleMessages()
            var now: Date? = null // 当前正在处理的日期
            var msg = StringBuilder()

            // 先根据date升序排，再根据start升序排
            scheduleList.sortedWith { o1, o2 ->
                if (o1.date == o2.date) {
                    o1.start.compareTo(o2.start)
                } else {
                    o1.date.compareTo(o2.date)
                }
            }.forEach{
                val th = it.date // 当前schedule消息的日期

                if (now == null || th != now){ // today为空或者th不等于today，说明是新的一天
                    if (now != null){ // today不为空，说明不是第一次进入循环，需要将上一天的消息添加到消息列表中，然后清空msg
                        messagesList.add(msg.toString().toPlainText())
                        msg = StringBuilder()
                    }

                    val i: Int = it.date.toLocalDate().get(ChronoField.DAY_OF_WEEK)
                    msg.append(DateUtil.format(th, "MM-dd") + " 周" + WeekUtil.WEEK_DAY[i - 1] + ":")
                    now = th
                }
                if (th == now) {
                    msg.append(it.toMessage())
                }
            }
            return messagesList
        }

        fun empty(): ScheduleMessages{
            return ScheduleMessages().apply { this.add("暂无信息".toPlainText()) }
        }

    }

}