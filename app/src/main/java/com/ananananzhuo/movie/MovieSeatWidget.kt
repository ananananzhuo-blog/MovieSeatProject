package com.ananananzhuo.movie

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ananananzhuo.movie.ui.theme.textColor666

/**
 * author  :mayong
 * function:
 * date    :2022/1/5
 **/
@Composable
fun MovieSeatWidget() {

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    Text(
                        text = "保利国际影城-北京龙旗广场店",
                        style = TextStyle(color = Color.White, fontSize = 16.sp),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            })
        },
        backgroundColor = Color.White,
    ) {
        Column(Modifier.fillMaxSize()) {
            SeatDesc()
            Box(
                Modifier
                    .weight(1f)
                    .background(Color(0xfff6f6f6))) {
                MovieSeats()
            }
            BottomMovieInfo()
        }
    }
}

@Composable
fun BottomMovieInfo() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.voice_receiver),
                contentDescription = "",
                modifier = Modifier.weight(0.1f)
            )
            Text(
                text = "疫情期间，影城将不提供公用3D眼镜，请自备",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(0.8f),
                style = TextStyle(color = textColor666)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "",
                modifier = Modifier.weight(0.1f)
            )
        }
        Row(
            Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(0.7f), verticalArrangement = Arrangement.Center) {
                Text(text = "穿过寒冬拥抱你丫的", style = TextStyle(color = Color(0xff333333), fontSize = 16.sp, fontWeight = FontWeight.Bold))
                Text(text = "今天 01月07日 22:00 01:04 国语 3D", style = TextStyle(color = textColor666, fontSize = 16.sp))
            }
            Text(text = "切换场次", style = TextStyle(color = Color(0xff09acfb), fontWeight = FontWeight.Bold, fontSize = 16.sp))
        }
        Button(
            onClick = {

            }, modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "请先选座", style = TextStyle(fontSize = 18.sp))
        }
    }
}

@Composable
fun SeatDesc() {
    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MovieDesc(false, "已售")
        MovieDesc(false, "45元")
        MovieDesc(false, "45元")
    }
}

@Composable
fun MovieDesc(hasSelect: Boolean, desc: String) {
    val selectedSeat = ImageBitmap.imageResource(id = R.drawable.boygirl)
    Row(
        Modifier
            , verticalAlignment = Alignment.CenterVertically
    ) {
//        Canvas(modifier = Modifier.width(50.dp), onDraw = {
//            seatItem(
//                Seat(
//                    Offset(0f, 0f),
//                    end = Offset(50f, 50f),
//                    hasSealed = hasSelect,
//                    type = Seat.SEAT_ORDINARY
//                ), selectedSeat
//            )
//        })
        Box(modifier = Modifier.size(15.dp).background(shape = RoundedCornerShape(4.dp), color = Color.White).border(width = 2.dp, color = Color(0xfff4d9bd)))
        Text(text = desc, style = TextStyle(color = Color.Black), modifier = Modifier.padding(start = 5.dp))
    }

}

@Composable
fun MovieSeats() {
    val selectedSeat = ImageBitmap.imageResource(id = R.drawable.boygirl)
    val model = viewModel<MovieSeatViewModel>()

    val movieSeats = model.dataFlow.collectAsState()
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        offset += offsetChange
    }
    Box(
        Modifier
            .fillMaxSize(),

        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .transformable(state = state)
                .pointerInput(Unit) {
                    detectTapGestures {
                        model.click(it.x, it.y)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress { change, dragAmount ->
                        offset += Offset(dragAmount.x * 2, 0f)
                    }
                }
                .fillMaxSize()
        ) {
            model.initSeat(size)
            movieSeats.value.matrix.forEach {
                it.forEach { seat ->
                    seatItem(seat, selectedSeat)
                }
            }
        }
    }
}

private fun DrawScope.seatItem(seat: Seat, selectedSeat: ImageBitmap) {
    if (seat.type == 3) {//非座位
        return
    }
    val width = (seat.end.x - seat.start.x) * 0.8f
    val padding = ((seat.end.x - seat.start.x) - width) / 2
    val boundStart = seat.start + Offset(padding, padding)
    val boundEnd = seat.end - Offset(padding, padding)
    val radius = width * 0.2f
    val strokeWidth = 5f

    if (seat.hasSealed) {
        log("绘制头像")
        drawImage(
            selectedSeat,
            boundStart + Offset((width - selectedSeat.width) / 2, (width - selectedSeat.width) / 2)
        )
    }
    val path = Path().apply {
        moveTo(boundStart.x, boundStart.y + radius)
        addArc(
            Rect(Offset(boundStart.x + radius, boundStart.y + radius), radius),
            180f,
            90f,
        )
        lineTo(boundEnd.x - radius, boundStart.y)
        arcTo(
            Rect(Offset(boundEnd.x - radius, boundStart.y + radius), radius),
            270f,
            90f,
            false
        )
        lineTo(boundEnd.x, boundEnd.y - radius)
        arcTo(Rect(Offset(boundEnd.x - radius, boundEnd.y - radius), radius), 0f, 90f, false)
        lineTo(boundStart.x + radius, boundEnd.y)
        arcTo(Rect(Offset(boundStart.x + radius, boundEnd.y - radius), radius), 90f, 90f, false)
        close()
    }
    drawPath(
        path,
        if (seat.type == 0) Color(0xfff4d9bd) else Color(0xffc3d9e9),
        style = Stroke(strokeWidth)
    )
}

@Preview
@Composable
fun MoviePreview() {
    val density = LocalDensity.current.density
    Canvas(modifier = Modifier.size(100.dp), onDraw = {
//        seatItem(Offset(0f, 0f), Offset(100 * density, 100 * density))
    })
}

fun log(msg: String) {
    Log.e("tag", msg)
}