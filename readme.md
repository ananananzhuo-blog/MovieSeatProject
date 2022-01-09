



新年要来了，看电影是我们新年中必不可少的娱乐项目，那么看电影的时候你是否有想过选座位的空间是如何实现的呢，今天就带你研究。
## 起因
compose的绘制系列在两个月前就已经学习完成了，但是仅限于api的熟练，因此今天打算做一个仿淘票票选电影座位的自定义ui。

## 效果查看

先看一下支付宝客户端的实现吧

![](https://files.mdnice.com/user/15648/27f2231e-90f8-4ee0-a2f2-d9a1d5c1df66.png)

我们发现选座位的主要点就是座位的绘制、选择座位的逻辑、双指手势缩放、单指长按后拖动效果


既然说到这里了提取吧我们实现的效果也放出来吧：

![](https://files.mdnice.com/user/15648/57e42a13-0deb-4043-80da-6369b62e1a92.gif)

## 实现要点

#### 绘制座位
本身绘制座位我么可以使用drawRoundRect api来实现，不过为了增加难度本例中我是使用path拼接而成的座位。这样做的目标也是为了方便将来将座位扩展成其它形状

构建path的代码：


```kt
val path = Path().apply {
        moveTo(boundStart.x, boundStart.y + radius)
        addArc(//绘制圆弧
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
```

drawpath：


```kt
drawPath(
        path,
        if (seat.type == 0) Color(0xfff4d9bd) else Color(0xffc3d9e9),
        style = Stroke(strokeWidth)//使用Stroke可以保证我们的path绘制出来是线妆的效果
    )
```

#### 双指操作手势
手势的使用可以参考之前我写过的文章，[传送门](https://juejin.cn/post/7045443999523405860)

使用双指手势需要使用如下两个Modifier操作符：

```kt
.graphicsLayer
.transformable(state = state)
```

实现代码如下：



```kt
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
       内容部分
       ....
    }
```

>代码：















