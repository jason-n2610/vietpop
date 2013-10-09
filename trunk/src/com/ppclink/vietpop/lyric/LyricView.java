/**
 * 
 */
package com.ppclink.vietpop.lyric;

import com.ppclink.vietpop.activity.R;
import com.ppclink.vietpop.widget.DisplayMode;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author CHIEN NGUYEN
 * 
 */
public class LyricView extends View {
	public int count = 0;
	String[] text = new String[13];
	private Paint mTextLight;
	private Paint mTextPaint;
	private String mText;
	private int mAscent;

	/**
	 * Constructor. This version is only needed if you will be instantiating the
	 * object manually (not from a layout XML file).
	 * 
	 * @param context
	 */
	public LyricView(Context context) {
		super(context);
		initLabelView();
	}

	/**
	 * Construct object, initializing with any attributes we understand from a
	 * layout file. These attributes are defined in
	 * SDK/assets/res/any/classes.xml.
	 * 
	 * @see android.view.View#View(android.content.Context,
	 *      android.util.AttributeSet)
	 */
	public LyricView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLabelView();
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.LyricView);

		CharSequence s = a.getString(R.styleable.LyricView_text);
		if (s != null) {
			setText(s.toString());
		}

		// Retrieve the color(s) to be used for this view and apply them.
		// Note, if you only care about supporting a single color, that you
		// can instead call a.getColor() and pass that to setTextColor().
		setTextColor(a.getColor(R.styleable.LyricView_textColor, Color.GRAY));

		int textSize = a.getDimensionPixelOffset(
				R.styleable.LyricView_textSize, 0);
		if (textSize > 0) {
			setTextSize(textSize);
		}
		a.recycle();
	}

	private final void initLabelView() {
		for (int i = 0; i < 13; i++) {
			text[i] = " ";
		}
		mTextPaint = new Paint();
		mTextLight = new Paint();
		mTextLight.setTextAlign(Paint.Align.CENTER);
		mTextLight.setAntiAlias(true);
		if (DisplayMode.mode ==1) {
			mTextLight.setTextSize(20);
			mTextPaint.setTextSize(18);
		}
		else if(DisplayMode.mode ==2){
			mTextLight.setTextSize(22);
			mTextPaint.setTextSize(20);
		}
		
		mTextLight.setColor(Color.WHITE);
		mTextLight.setStyle(Paint.Style.FILL_AND_STROKE);
		
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setAntiAlias(true);
		
		setPadding(3, 3, 3, 3);
	}

	/**
	 * Sets the text to display in this label
	 * 
	 * @param text
	 *            The text to display. This will be drawn as one line.
	 */
	public void setText(String text) {
		mText = text;
		requestLayout();
		invalidate();
	}

	public void setText(String[] text) {
		this.text = text;
		requestLayout();
		invalidate();

	}

	/**
	 * Sets the text size for this label
	 * 
	 * @param size
	 *            Font size
	 */
	public void setTextSize(int size) {
		mTextPaint.setTextSize(size);
		requestLayout();
		invalidate();
	}

	/**
	 * Sets the text color for this label.
	 * 
	 * @param color
	 *            ARGB value for the text
	 */
	public void setTextColor(int color) {
		mTextPaint.setColor(color);
		invalidate();
	}

	public void setCount(int count) {
		this.count = count;
		requestLayout();
		invalidate();
	}

	/**
	 * @see android.view.View#measure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
					+ getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		mAscent = (int) mTextPaint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = (int) (-mAscent + mTextPaint.descent()) + getPaddingTop()
					+ getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * Render the text
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	int i = 0;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (DisplayMode.mode ==1) {
			canvas.translate(160, 35);
		}
		else if(DisplayMode.mode ==2){
			canvas.translate(250, 160);
		}
		
		for (int i = 0; i < text.length; i++) {
			if (i == 6) {
				canvas.drawText(text[i], getPaddingLeft(), getPaddingTop()
						- mAscent + i * 20 - count, mTextLight);
			} else {
				canvas.drawText(text[i], getPaddingLeft(), getPaddingTop()
						- mAscent + i * 20 - count, mTextPaint);
			}

		}
		
	}
	public void translate(int x,int y){
		
	}
}