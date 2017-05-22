/*
 * Copyright (c) 2017.  Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.ui.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

/**
 * A {@link FloatingActionButton} subclass that shows a counter badge on right top corner.
 *
 * @author André Mion (andremion)
 */
public class CounterFab extends FloatingActionButton {

    private static final int MAX_COUNT = 99;
    private static final String MAX_COUNT_TEXT = "99+";
    private static final int TEXT_SIZE_DP = 11;
    private static final int TEXT_PADDING_DP = 2;
    private static final int MASK_COLOR = Color.parseColor("#33000000");
    private static final Interpolator ANIMATION_INTERPOLATOR = new OvershootInterpolator();
    private final Rect mContentBounds;
    private final Paint mTextPaint;
    private final float mTextSize;
    private final Paint mCirclePaint;
    private final Rect mCircleBounds;
    private final Paint mMaskPaint;
    private final int mAnimationDuration;
    private float mAnimationFactor;
    private final Property<CounterFab, Float> ANIMATION_PROPERTY =
            new Property<CounterFab, Float>(Float.class, "animation") {

                @Override
                public void set(CounterFab object, Float value) {
                    mAnimationFactor = value;
                    postInvalidateOnAnimation();
                }

                @Override
                public Float get(CounterFab object) {
                    return 0f;
                }
            };
    private int mCount;
    private String mText;
    private float mTextHeight;
    private ObjectAnimator mAnimator;

    public CounterFab(Context context) {
        this(context, null, 0);
    }

    public CounterFab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CounterFab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUseCompatPadding(true);

        final float density = getResources().getDisplayMetrics().density;

        mTextSize = TEXT_SIZE_DP * density;
        float textPadding = TEXT_PADDING_DP * density;

        mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mAnimationFactor = 1;

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.SANS_SERIF);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        ColorStateList colorStateList = getBackgroundTintList();
        if (colorStateList != null) {
            mCirclePaint.setColor(colorStateList.getDefaultColor());
        } else {
            Drawable background = getBackground();
            if (background instanceof ColorDrawable) {
                ColorDrawable colorDrawable = (ColorDrawable) background;
                mCirclePaint.setColor(colorDrawable.getColor());
            }
        }

        mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMaskPaint.setStyle(Paint.Style.FILL);
        mMaskPaint.setColor(MASK_COLOR);

        Rect textBounds = new Rect();
        mTextPaint.getTextBounds(MAX_COUNT_TEXT, 0, MAX_COUNT_TEXT.length(), textBounds);
        mTextHeight = textBounds.height();

        float textWidth = mTextPaint.measureText(MAX_COUNT_TEXT);
        float circleRadius = Math.max(textWidth, mTextHeight) / 2f + textPadding;
        mCircleBounds = new Rect(0, 0, (int) (circleRadius * 2), (int) (circleRadius * 2));
        mContentBounds = new Rect();

        onCountChanged();
    }

    /**
     * @return The current count value
     */
    public int getCount() {
        return mCount;
    }

    /**
     * Set the count to show on badge
     *
     * @param count The count value starting from 0
     */
    public void setCount(@IntRange(from = 0) int count) {
        if (count == mCount) return;
        mCount = count > 0 ? count : 0;
        onCountChanged();
        if (ViewCompat.isLaidOut(this)) {
            startAnimation();
        }
    }

    /**
     * Increase the current count value by 1
     */
    public void increase() {
        setCount(mCount + 1);
    }

    /**
     * Decrease the current count value by 1
     */
    public void decrease() {
        setCount(mCount > 0 ? mCount - 1 : 0);
    }

    private void onCountChanged() {
        if (mCount > MAX_COUNT) {
            mText = String.valueOf(MAX_COUNT_TEXT);
        } else {
            mText = String.valueOf(mCount);
        }
    }

    private void startAnimation() {
        float start = 0f;
        float end = 1f;
        if (mCount == 0) {
            start = 1f;
            end = 0f;
        }
        if (isAnimating()) {
            mAnimator.cancel();
        }
        mAnimator = ObjectAnimator.ofObject(this, ANIMATION_PROPERTY, null, start, end);
        mAnimator.setInterpolator(ANIMATION_INTERPOLATOR);
        mAnimator.setDuration(mAnimationDuration);
        mAnimator.start();
    }

    private boolean isAnimating() {
        return mAnimator != null && mAnimator.isRunning();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCount > 0 || isAnimating()) {
            if (getContentRect(mContentBounds)) {
                mCircleBounds.offsetTo(mContentBounds.left + mContentBounds.width() -
                        mCircleBounds.width(), mContentBounds.top);
            }
            float cx = mCircleBounds.centerX();
            float cy = mCircleBounds.centerY();
            float radius = mCircleBounds.width() / 2f * mAnimationFactor;
            // Solid circle
            canvas.drawCircle(cx, cy, radius, mCirclePaint);
            // Mask circle
            canvas.drawCircle(cx, cy, radius, mMaskPaint);
            // Count text
            mTextPaint.setTextSize(mTextSize * mAnimationFactor);
            canvas.drawText(mText, cx, cy + mTextHeight / 2f, mTextPaint);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.count = mCount;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCount(ss.count);
        requestLayout();
    }

    private static class SavedState extends BaseSavedState {

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        private int count;

        /**
         * Constructor called from {@link CounterFab#onSaveInstanceState()}
         */
        private SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            count = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(count);
        }

        @Override
        public String toString() {
            return CounterFab.class.getSimpleName() + "." + SavedState.class
                    .getSimpleName() + "{"
                    + Integer.toHexString(System.identityHashCode(this)) + " count=" + count + "}";
        }
    }

}