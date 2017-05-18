package com.xu.toggleview.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义开关
 * @author poplar
 * 
 * Android 的界面绘制流程
 * 测量			 摆放		绘制
 * measure	->	layout	->	draw
 * 	  | 		  |			 |
 * onMeasure -> onLayout -> onDraw 重写这些方法, 实现自定义控件
 * 
 * onResume()之后执行
 * 
 * View
 * onMeasure() (在这个方法里指定自己的宽高) -> onDraw() (绘制自己的内容)
 * 
 * ViewGroup
 * onMeasure() (指定自己的宽高, 所有子View的宽高)-> onLayout() (摆放所有子View) -> onDraw() (绘制内容)
 */
public class ToggleView extends View {

	private Bitmap switchBackgroupBitmap; // 背景图片
	private Bitmap slideButtonBitmap; // 滑块图片
	private Paint paint; // 画笔
	private boolean mSwitchState = false; // 开关状态, 默认false
	private float currentX;

	/**
	 * 用于代码创建控件
	 * @param context
	 */
	public ToggleView(Context context) {
		super(context);
		//在每个构造方法里里面初始化一下画笔
		init();
	}

	/**
	 * 用于在xml里使用, 可指定自定义属性
	 * @param context
	 * @param attrs
	 */
	public ToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//在每个构造方法里里面初始化一下画笔
		init();

		// 获取配置的自定义属性
		String namespace = "http://schemas.android.com/apk/res/com.xu.toggleview";
		int switchBackgroundResource = attrs.getAttributeResourceValue(namespace , "switch_background", -1);
		int slideButtonResource = attrs.getAttributeResourceValue(namespace , "slide_button", -1);

		//自定义名空间。
		mSwitchState = attrs.getAttributeBooleanValue(namespace, "switch_state", false);
		//设置资源
		setSwitchBackgroundResource(switchBackgroundResource);
		setSlideButtonResource(slideButtonResource);
	}

	/**
	 * 用于在xml里使用, 可指定自定义属性, 如果指定了样式, 则走此构造函数
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ToggleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//在每个构造方法里里面初始化一下画笔
		init();
	}
	
	private void init() {
		paint = new Paint();
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//设置测量的一个值（使用自己测量的一个值，宽高）
		setMeasuredDimension(switchBackgroupBitmap.getWidth(), switchBackgroupBitmap.getHeight());
	}

	// Canvas 画布, 画板. 在上边绘制的内容都会显示到界面上.

	@Override
	protected void onDraw(Canvas canvas) {
		// 1. 绘制背景
		//先测量后绘制，宽高设置完之后容器就这么大，所依坐标系就是从左上角开始的0
		canvas.drawBitmap(switchBackgroupBitmap, 0, 0, paint);
		
		// 2. 绘制滑块
		if(isTouchMode){
			// 根据当前用户触摸到的位置画滑块
			
			// 让滑块向左移动自身一半大小的位置
			float newLeft = currentX - slideButtonBitmap.getWidth() / 2.0f;

			//求出左边滑块的大小（背景宽度减去滑动块的宽度）
			int maxLeft = switchBackgroupBitmap.getWidth() - slideButtonBitmap.getWidth();
			
			// 限定滑块范围
			if(newLeft < 0){
				newLeft = 0; // 左边范围
			}else if (newLeft > maxLeft) {
				newLeft = maxLeft; // 右边范围
			}
			
			canvas.drawBitmap(slideButtonBitmap, newLeft, 0, paint);
		}else {
			// 根据开关状态boolean, 直接设置图片位置
			if(mSwitchState){// 开
				int newLeft = switchBackgroupBitmap.getWidth() - slideButtonBitmap.getWidth();
				canvas.drawBitmap(slideButtonBitmap, newLeft, 0, paint);
			}else {// 关
				canvas.drawBitmap(slideButtonBitmap, 0, 0, paint);
			}
		}
		
	}
	
	boolean isTouchMode = false;
	private OnSwitchStateUpdateListener onSwitchStateUpdateListener;

	// 重写触摸事件, 响应用户的触摸.
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isTouchMode = true;
			System.out.println("event: ACTION_DOWN: " + event.getX());
			currentX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			System.out.println("event: ACTION_MOVE: " + event.getX());
			currentX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			isTouchMode = false;
			System.out.println("event: ACTION_UP: " + event.getX());
			currentX = event.getX();
			//求出背景的中心位置
			float center = switchBackgroupBitmap.getWidth() / 2.0f;
			
			// 根据当前按下的位置, 和控件中心的位置进行比较. 
			boolean state = currentX > center;
			
			// 如果开关状态变化了, 通知界面. 里边开关状态更新了.
			if(state != mSwitchState && onSwitchStateUpdateListener != null){
				// 把最新的boolean, 状态传出去了
				onSwitchStateUpdateListener.onStateUpdate(state);
			}
			mSwitchState = state;
			break;

		default:
			break;
		}

		//每次按下移动都会调用这个方法
		// 重绘界面
		invalidate(); // 会引发onDraw()被调用, 里边的变量会重新生效.界面会更新
		
		return true; // 消费了用户的触摸事件, 才可以收到其他的事件.
	}

	/**
	 * 设置背景图
	 * @param switchBackground
	 * 肯定这个方法在onresume之后执行的
	 */
	public void setSwitchBackgroundResource(int switchBackground) {
		//创建背景图片(开关的基本图片)
		switchBackgroupBitmap = BitmapFactory.decodeResource(getResources(), switchBackground);
	}

	/**
	 * 设置滑块图片资源
	 * @param slideButton
	 * 肯定这个方法在onresume之后执行的
	 */
	public void setSlideButtonResource(int slideButton) {
		//设置前景图片（开关的滑块）
		slideButtonBitmap = BitmapFactory.decodeResource(getResources(), slideButton);
	}

	/**
	 * 设置开关状态
	 * @param
	 */
	public void setSwitchState(boolean mSwitchState) {
		this.mSwitchState = mSwitchState;
	}

	//接口监听回调
	public interface OnSwitchStateUpdateListener{
		// 状态回调, 把当前状态传出去
		void onStateUpdate(boolean state);
	}
	//和上面的方法相关联和调用的方法关联
	//设置监听快关状态
	public void setOnSwitchStateUpdateListener(OnSwitchStateUpdateListener onSwitchStateUpdateListener) {
		this.onSwitchStateUpdateListener = onSwitchStateUpdateListener;
	}

}
