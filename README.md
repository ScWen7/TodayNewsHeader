# TodayNewsHeader
仿今日头条下拉刷新  SmartRefreshLayout

###前言
前两天在玩今日头条，觉得今日头条的下拉刷新蛮有意思的，就自己实现了一下，整体上实现了同样的效果。无图无真相，效果图如下：
今日头条效果：
![image](http://upload-images.jianshu.io/upload_images/6113442-e1a2786cc5c4c974.gif?imageMogr2/auto-orient/strip)
实现效果：
![image](http://upload-images.jianshu.io/upload_images/6113442-f492cdb786732e6a.gif?imageMogr2/auto-orient/strip)
实现过程分为两部分：
- **图形绘制**
- **结合下拉刷新动起来**



###图形绘制
#### 测量，坐标计算
实现过程中图形的绘制全部是通过Path 完成，需要精确计算 path 各个部分的坐标值

对Path不熟悉的请看[Path使用详解](https://blog.csdn.net/u013831257/article/details/50784565)

这里需要注意的是：**在绘制时坐标不能从 0 开始，绘制线条是通过Paint.setStyle(Paint.Style.STROKE)方法，如果从0开始绘制 会出现左侧，顶部线条只能绘制一半的情况**
主要参数：
```java
  private int strokeWidth;  //线宽
    //绘制不能从  坐标0 开始 会有 stroke*1 的偏移量
    private int contentWidth, contentHeight;  //内容宽度 内容高度
    private float roundCorner;  //外层 圆角矩形 圆角半径
    private float lineWidth;  // 线条宽度
    private float rectWidth;  //小矩形宽度
    private float shortLineWidth; //短线宽度
    private float spaceRectLine;  //小矩形距 断线距离
```
坐标说明图：
![image](http://upload-images.jianshu.io/upload_images/6113442-763f2206c4acfff1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
特地说明一下 roundCorner： 为 圆角矩形的圆角
在这里将 contentHeight 分为 7等份，roundCorner 为 1/7的contentHeight 
    之后每个线条之间间距 一个 roundCorner 

测量计算关键变量代码：
```java

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);


        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        int height = MeasureSpec.getSize(heightMeasureSpec);

        
        //设定最小值时，增加 stoke 的偏移保证 边界绘制完整
        int minWidth = dip2px(30) + strokeWidth * 2;

        int minHeight = dip2px(35) + strokeWidth * 2;

        //判断 测量模式  如果是  wrap_content 需要对 宽高进行限定
        //同时确定 高度 也对 最小值进行限定

        if (widthMode == MeasureSpec.AT_MOST) {
            width = minWidth;
        } else if (widthMode == MeasureSpec.EXACTLY && width < minWidth) {
            width = minWidth;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            height = minHeight;
        } else if (heightMode == MeasureSpec.EXACTLY && height < minHeight) {
            height = minHeight;
        }

        // 在确定宽高之后 对内容 宽高再次进行计算，留出 stroke 的偏移
        contentWidth = width - strokeWidth * 2;

        contentHeight = height - strokeWidth * 2;

        setMeasuredDimension(width, height);

        initNeedParamn();
        //初始化最外层 圆角矩形 path
        initPath();
    }

    /**
     * 初始化绘制所需要的参数
     */
    private void initNeedParamn() {
        //圆角半径
        roundCorner = contentHeight / 7f;
        //线条宽度
        lineWidth = contentWidth - roundCorner * 2;
        //小矩形宽度
        rectWidth = lineWidth / 2f;
        //短线宽度
        shortLineWidth = (lineWidth / 8f) * 3f;  //短线条宽度
        //矩形与 断线之间的间距
        spaceRectLine = (lineWidth / 8f) * 1f; //矩形与线条之间间距
    }
```

#####绘制
通过观察进入头条gif 效果，图形的绘制分为两部分
- **拖拽过程绘制**
- **刷新过程绘制**

>在两个过程中，最外层圆角矩形是不变的，先来绘制这个圆角矩形。最外层圆角矩形Path初始化：
```java
private Path roundPath; //最外层 圆形Path
/**
     * 初始化 path
     */
    private void initPath() {


        roundPath = new Path();
        //从右侧第一个圆角作为起点
        roundPath.moveTo(contentWidth, roundCorner);
        roundPath.arcTo(contentWidth - roundCorner * 2, 0, contentWidth, roundCorner * 2, 0, -90, false);
        roundPath.lineTo(roundCorner, 0);
        roundPath.arcTo(0, 0, roundCorner * 2, roundCorner * 2, -90, -90, false);
        roundPath.lineTo(0, contentHeight - roundCorner);
        roundPath.arcTo(0, contentHeight - roundCorner * 2, roundCorner * 2, contentHeight, -180, -90, false);
        roundPath.lineTo(contentWidth - roundCorner, contentHeight);
        roundPath.arcTo(contentWidth - roundCorner * 2, contentHeight - roundCorner * 2, contentWidth, contentHeight, -270, -90, false);
  //path闭合 自动 lineTo(contentWidth, roundCorner)
        roundPath.close();
    }
```
>小矩形与线条Path创建

测量完成后，需要的参数已经计算完成，我们可以根据指定坐标提供小矩形和线条的Path
```java
 /**
     * 根据 左上 坐标 创建 矩形 Path
     *
     * @param left 左坐标
     * @param top  上坐标
     * @return
     */
    public Path provideRectPath(float left, float top) {
        Path path = new Path();
        path.moveTo(left + rectWidth, top);
        path.lineTo(left, top);
        path.lineTo(left, top + roundCorner * 2f);
        path.lineTo(left + rectWidth, top + roundCorner * 2f);
        path.close();
        return path;
    }


    /**
     * 根据线条 左上 坐标和线宽创建线条 Path
     *
     * @param left 左坐标
     * @param top  上坐标
     * @param lineWidth  线宽
     * @return
     */
    public Path provideLinePath(float left, float top, float lineWidth) {
        Path path = new Path();
        path.moveTo(left, top);
        path.lineTo(left + lineWidth, top);
        return path;
    }
```


>每个图形都是通过Path绘制，对每个绘制的状态进行封装
```java
 /**
     * 绘制的状态
     */
    public abstract class State {

        protected List<PathWrapper> mPathList;

        public State() {
            mPathList = new ArrayList<>();
            initStatePath();
        }
         //初始化 PathWrapper集合
        protected abstract void initStatePath();
        
        //将绘制分配给 PathWrapper执行
        void onDraw(Canvas canvas, Paint paint) {
            for (PathWrapper path : mPathList) {
                path.onDraw(canvas, paint);
            }
        }
    }
```
这里的 PathWrapper 会在下面的拖拽过程进行解释

##### 拖拽过程
 
下拉拖拽过程：
![image](http://upload-images.jianshu.io/upload_images/6113442-38368417b3dbc9f8.gif?imageMogr2/auto-orient/strip)
头部刷新View 跟随手指下拉显示，当下拉高度超过了一定距离，Path图形开始绘制，手指继续下拉 ，图形绘制完全，并且可以看到会有一个渐进绘制的效果。这里需要根据下拉率 **fraction**来计算绘制比例 

 渐进绘制分析
每个图形根据 fraction 的绘制比例是不同的，我在这里设计的映射关系如下表：
图形     | fraction  | 绘制比例
-------- | ---|------
外层圆角矩形 |0~1|0~1
矩形    |0~0.25 |0~1
短线条1     | 0.25~0.33|0~1
短线条2     | 0.33~0.41|0~1
短线条3     | 0.41~0.5|0~1
长线条1     | 0.5~0.66|0~1
长线条2    | 0.66~0.82|0~1
长线条3     | 0.82~1|0~1
这里需要公式去计算每个图形的绘制比例，并且需要一个容器去保存每个图形的path 和绘制比例，PathWrapper 就应运而生。
```java
public class PathWrapper {
    protected Path mPath; //图形 Path
    protected float fraction;  //绘制的比例


    public PathWrapper(Path path, float fraction) {
        mPath = path;
        this.fraction = fraction;
    }


    public void onDraw(Canvas canvas, Paint paint) {
        if(fraction<=0) {
            return;
        }

        Path dst = new Path();
        PathMeasure measure = new PathMeasure(mPath, false);         // 将 Path 与 PathMeasure 关联

        float length = measure.getLength();

        // 截取一部分 并使用 moveTo 保持截取得到的 Path 第一个点的位置不变
        measure.getSegment(0, length*fraction, dst, true);

        canvas.drawPath(dst, paint);
    }
}
```
PathWrapper 保存了path 和 绘制比例。
这里有一个巧妙的设计是将 图形的绘制 封装到了 PathWrapper中，这么早的好处在哪里呢？不要急，接下来会分析到。而关于绘制代码有问题的可以参考 [Path使用详解](https://blog.csdn.net/u013831257/article/details/50784565)

![image](http://upload-images.jianshu.io/upload_images/6113442-3fdc527b08dde296.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
这个图形可以看到 小矩形有一个灰色的填充效果，与其他图形的绘制有所分别，就不能使用通用的绘制方法进行绘制，需要特殊对待。
这时候PathWrapper 封装 **绘制代码**的作用就提现了出来，对于线条图形使用通用的方法，对于矩形图形，创建单独的 RectPathWrapper 继承自PathWrapper对 public void onDraw(Canvas canvas, Paint paint)方法进行重写，自定义绘制规则。
```java
public class RectPathWrapper extends PathWrapper {

    Paint mPaint;

    public RectPathWrapper(Path path, float fraction) {
        super(path, fraction);
        //创建新的画笔  设置填充样式    颜色
         mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0x32000000);
    }


    public void onDraw(Canvas canvas, Paint paint) {
        if (fraction <= 0) {
            return;
        }

        Path dst = new Path();
        PathMeasure measure = new PathMeasure(mPath, false);         // 将 Path 与 PathMeasure 关联

        float length = measure.getLength();

        measure.getSegment(0, length * fraction, dst, true);                   // 截取一部分 并使用 moveTo 保持截取得到的 Path 第一个点的位置不变
        //绘制线条
        canvas.drawPath(dst, paint);
         //绘制填充
        canvas.drawPath(dst, mPaint);
    }
}
```
**关于绘制比例计算**
再看一次映射关系
图形     | fraction  | 绘制比例
-------- | ---|------
外层圆角矩形 |0~1|0~1
矩形    |0~0.25 |0~1
短线条1     | 0.25~0.33|0~1
短线条2     | 0.33~0.41|0~1
短线条3     | 0.41~0.5|0~1
长线条1     | 0.5~0.66|0~1
长线条2    | 0.66~0.82|0~1
长线条3     | 0.82~1|0~1
直接贴出 DragState的代码
```java
 class DragState extends State {


        private float fraction = 0f;


        public void setFraction(float fraction) {
            this.fraction = fraction;
            mPathList.clear();
            initStatePath();
        }
        
        @Override
        protected void initStatePath() {
            //圆角 矩形 
            PathWrapper pathWrapper = new PathWrapper(roundPath, fraction);
            mPathList.add(pathWrapper);

            //小矩形
            Path rectPath = provideRectPath(roundCorner, roundCorner);
            pathWrapper = new RectPathWrapper(rectPath, Math.min(1, 4 * fraction));
            mPathList.add(pathWrapper);

            //短线条1
            float shortLeft = roundCorner + rectWidth + spaceRectLine;
            Path shortLine1 = provideLinePath(shortLeft, roundCorner, shortLineWidth);
            pathWrapper = new PathWrapper(shortLine1, Math.min(1, 12.5f * (fraction - 0.25f)));
            mPathList.add(pathWrapper);

//

             //短线条2
            Path shortLine2 = provideLinePath(shortLeft, roundCorner * 2f, shortLineWidth);
            pathWrapper = new PathWrapper(shortLine2, Math.min(1, 12.5f * (fraction - 0.33f)));
            mPathList.add(pathWrapper);
//
             //短线条3
            Path shortLine3 = provideLinePath(shortLeft, roundCorner * 3f, shortLineWidth);
            pathWrapper = new PathWrapper(shortLine3, Math.min(1, 12.5f * (fraction - 0.41f)));
            mPathList.add(pathWrapper);
//
            //长线条1
            Path longLine1 = provideLinePath(roundCorner, roundCorner * 4f, lineWidth);
            pathWrapper = new PathWrapper(longLine1, Math.min(1, 6.25f * (fraction - 0.5f)));
            mPathList.add(pathWrapper);
            //长线条2
            Path longLine2 = provideLinePath(roundCorner, roundCorner * 5f, lineWidth);
            pathWrapper = new PathWrapper(longLine2, Math.min(1, 6.25f * (fraction - 0.66f)));
            mPathList.add(pathWrapper);
          //长线条3
            Path longLine3 = provideLinePath(roundCorner, roundCorner * 6f, lineWidth);
            pathWrapper = new PathWrapper(longLine3, Math.min(1, 6.25f * (fraction - 0.82f)));
            mPathList.add(pathWrapper);
        }
    }
```
接下里就可以写个按钮不断改变 fraction 来观察绘制效果了

##### 刷新过程
 刷新的过程可以分为四中状态：
![image](http://upload-images.jianshu.io/upload_images/6113442-34c8a5f992a7c4dc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)![image](http://upload-images.jianshu.io/upload_images/6113442-b4d8606eb94aac11.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)![image](http://upload-images.jianshu.io/upload_images/6113442-7b8e80074de4f937.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image](http://upload-images.jianshu.io/upload_images/6113442-6d57b96b0e51560c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
刷新过程显示就是四中状态图形在一定时间间隔内循环切换显示
这部分就比较简单了，确定好图形直接绘制即可，这里贴出 第二个状态的代码
```java
 class RefreshState2 extends State {


        @Override
        protected void initStatePath() {

            PathWrapper pathWrapper = new PathWrapper(roundPath, 1);
            mPathList.add(pathWrapper);


            Path rectPath = provideRectPath(contentWidth-roundCorner-rectWidth, roundCorner);
            pathWrapper = new RectPathWrapper(rectPath, 1);
            mPathList.add(pathWrapper);


            float shortLeft = roundCorner;
            Path shortLine1 = provideLinePath(shortLeft, roundCorner, shortLineWidth);
            pathWrapper = new RectPathWrapper(shortLine1, 1);
            mPathList.add(pathWrapper);


            Path shortLine2 = provideLinePath(shortLeft, roundCorner * 2f, shortLineWidth);
            pathWrapper = new PathWrapper(shortLine2, 1);
            mPathList.add(pathWrapper);
//

            Path shortLine3 = provideLinePath(shortLeft, roundCorner * 3f, shortLineWidth);
            pathWrapper = new PathWrapper(shortLine3, 1);
            mPathList.add(pathWrapper);
//
//
            Path longLine1 = provideLinePath(roundCorner, roundCorner * 4f, lineWidth);
            pathWrapper = new PathWrapper(longLine1, 1);
            mPathList.add(pathWrapper);
//
            Path longLine2 = provideLinePath(roundCorner, roundCorner * 5f, lineWidth);
            pathWrapper = new PathWrapper(longLine2, 1);
            mPathList.add(pathWrapper);
//
            Path longLine3 = provideLinePath(roundCorner, roundCorner * 6f, lineWidth);
            pathWrapper = new PathWrapper(longLine3, 1);
            mPathList.add(pathWrapper);
        }

    }
```
代码比较简单就是 计算坐标，创建Path 然后绘制交由公共的PathWrapper 完成
**状态的切换**
```java
public void setDragState() {
        if (mDragState instanceof DragState) {
            mDragState = new RefreshState1();
        } else if (mDragState instanceof RefreshState1) {
            mDragState = new RefreshState2();
        } else if (mDragState instanceof RefreshState2) {
            mDragState = new RefreshState3();
        } else if (mDragState instanceof RefreshState3) {
            mDragState = new RefreshState4();
        } else if (mDragState instanceof RefreshState4) {
            mDragState = new RefreshState1();
        }
        postInvalidate();
    }
```


### 结合下拉刷新动起来

下拉刷新使用 [SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout)，正如它的介绍所说 SmartRefreshLayout是一个“聪明”或者“智能”的下拉刷新布局，并且支持自定义多种Header，Footer。[自定义Header文档说明](https://github.com/scwang90/SmartRefreshLayout/blob/master/art/md_custom.md)

代码直接贴出来
```java
public class TodayNewsHeader extends LinearLayout implements RefreshHeader {

    public static String REFRESH_HEADER_PULLDOWN = "下拉推荐";
    public static String REFRESH_HEADER_REFRESHING = "推荐中...";
    public static String REFRESH_HEADER_RELEASE = "松开推荐";
    private NewRefreshView mNewRefreshView;
    private TextView releaseText;


    public TodayNewsHeader(Context context) {
        this(context, null);
    }

    public TodayNewsHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TodayNewsHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.setGravity(Gravity.CENTER_HORIZONTAL);
        this.setOrientation(LinearLayout.VERTICAL);

        mNewRefreshView = new NewRefreshView(context);

        LinearLayout.LayoutParams lpNewRefresh = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpNewRefresh.setMargins(30, dip2px(context,30), 30, 0);

        this.addView(mNewRefreshView, lpNewRefresh);


        LinearLayout.LayoutParams lpReleaseText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpReleaseText.setMargins(0, 30, 0, 30);

        releaseText = new TextView(context);
        releaseText.setText(REFRESH_HEADER_PULLDOWN);
        releaseText.setTextColor(0xff666666);
        addView(releaseText, lpReleaseText);
        
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mNewRefreshView.setDragState();
            mHandler.sendEmptyMessageDelayed(0, 250);
        }
    };

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onPulling(float percent, int offset, int height, int extendHeight) {
        Log.e("TAG", "fraction:" + percent);
        mNewRefreshView.setFraction((percent - 0.8f) * 6f);
    }

    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {
        onPulling(percent, offset, height, extendHeight);
    }

    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int extendHeight) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {

    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        mHandler.removeCallbacksAndMessages(null);
        mNewRefreshView.setDrag();
        return 0;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
                break;
            case PullDownToRefresh:
                releaseText.setText(REFRESH_HEADER_PULLDOWN);
                break;
            case PullUpToLoad:
                break;
            case ReleaseToRefresh:
                releaseText.setText(REFRESH_HEADER_RELEASE);
                break;
            case Refreshing:
                releaseText.setText(REFRESH_HEADER_REFRESHING);
                break;
            case Loading:
                break;
        }
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context,float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}
```
- TodayNewsHeader继承自 LinearLayout 在 initView()  方法中 创建 NewRefreshView 和下方显示文字，并添加到自身中。
- 关于getSpinnerStyle() 方法说明，参考官方说明
> 变换方式
Translate 平行移动 特点: 最常见，HeaderView高度不会改变，
Scale 拉伸形变 特点：在下拉和上弹（HeaderView高度改变）时候，会自动触发OnDraw事件
FixedFront 固定在前面 特点：不会上下移动，HeaderView高度不会改变
FixedBehind 固定在后面 特点：不会上下移动，HeaderView高度不会改变（类似微信浏览器效果）
Screen 全屏幕 特点：固定在前面，尺寸充满整个布局
- onPulling 与 onReleasoing 拖拽过程与下拉放回过程，执行 mNewRefreshView.setFraction();操作，修改 绘制比例
- onReleased 出发下拉刷新，开启刷新动画，我们在上面分析刷新过程是 四中状态图形在一定时间间隔内循环切换显示，这里我采用可Handler 的形式
```java
 private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mNewRefreshView.setDragState();
            mHandler.sendEmptyMessageDelayed(0, 250);
        }
    };
...
//使用handler 的好习惯，先清除消息再发送
  mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessage(0);

```
-  onFinish刷新完成会调用，返回值为 头部延迟收回的时间 在这个方法里 需要清除 handler 并且重置 NewRefreshView 的状态为拖拽状态
- onStateChanged方法 刷新状态变化时回调，在这里完成下方文本的切换显示





