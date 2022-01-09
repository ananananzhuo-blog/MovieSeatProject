package com.ananananzhuo.movie

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * author  :mayong
 * function:
 * date    :2022/1/8
 **/
class MovieSeatViewModel : ViewModel() {
    private val movieSeats = mutableListOf<MutableList<Seat>>()
    val dataFlow = MutableStateFlow(MovieData(movieSeats))
    private val movieWidth = 0.9f//整个影院的宽高百分比
    private val column = 16//列数
    private val row = 10//行数
    fun initSeat(size: Size) {
        if (movieSeats.size > 0) {
            return
        }
        val start = Offset(size.width * (1 - movieWidth) / 2, size.height * (1 - movieWidth) / 2)
        val end = Offset(size.width * (1 + movieWidth) / 2, size.height * (1 + movieWidth) / 2)
        val step = (end.x - start.x) / column
        for (i in (0 until row)) {
            val rowData = mutableListOf<Seat>()
            for (j in (0 until column))
                rowData.add(
                    Seat(
                        Offset(step * j, i * step) + start,
                        end = Offset(step * (j + 1), step * (i + 1)) + start,
                        hasSealed = false,
                        type = if (i in 2..8 && j in 4..9) 1 else if (i in 0..6 && j in 12..13) 3 else 0
                    )
                )
            movieSeats.add(rowData)
        }
        dataFlow.value = dataFlow.value.copy(movieSeats)
    }

    fun click(x: Float, y: Float) {
        log("点击事件：  $x   $y")
        movieSeats.forEach {
            it.forEach { seat ->
                if (x in seat.start.x..seat.end.x && y in seat.start.y..seat.end.y) {
                    if (seat.type != Seat.SEAT_NO) {//未出售的座位
                        seat.hasSealed = !seat.hasSealed
                    }
                }
            }
        }
        val value = dataFlow.value
        dataFlow.value = value.copy(change = !value.change)
    }
}

data class MovieData(
    val matrix: MutableList<MutableList<Seat>>,//座位数组
    val change: Boolean = false,//因为数组的引用是不变的，所以如我我们想让数据更新一定要更改这个字段
)

data class Seat(
    var start: Offset,
    var end: Offset,
    var hasSealed: Boolean,//是否已售
    var enabled: Boolean = false,
    var type: Int = 0//0:普通座位 1:高级座位  2:已售 3:非座位
) {
    companion object {
        const val SEAT_ORDINARY = 0
        const val SEAT_SENIOR = 1
        const val SEAT_SEALED = 2
        const val SEAT_NO = 3

    }
}