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

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jahirfiquitiva.libs.iconshowcase.R;
import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils;
import jahirfiquitiva.libs.iconshowcase.utils.CoreUtils;
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils;
import jahirfiquitiva.libs.iconshowcase.utils.themes.ThemeUtils;

public class EmptyViewRecyclerView extends RecyclerView {

    @Nullable
    private View loadingView;
    @Nullable
    private View emptyView;
    @Nullable
    private TextView textView;
    @StringRes
    private int loadingTextRes = -1;
    @StringRes
    private int emptyTextRes = -1;

    @IntDef({STATE_LOADING, STATE_NORMAL, STATE_EMPTY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public static final int STATE_LOADING = 0;
    public static final int STATE_NORMAL = 1;
    public static final int STATE_EMPTY = 2;

    @State
    private int state = STATE_LOADING;

    public EmptyViewRecyclerView(Context context) {
        super(context);
    }

    public EmptyViewRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyViewRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void updateStateViews() {
        switch (state) {
            default:
            case STATE_LOADING:
                if (loadingView != null)
                    loadingView.setVisibility(VISIBLE);
                if (emptyView != null)
                    emptyView.setVisibility(GONE);
                setVisibility(GONE);
                break;
            case STATE_NORMAL:
                if (getAdapter() != null) {
                    int items = getAdapter().getItemCount();
                    if (items > 0) {
                        if (loadingView != null)
                            loadingView.setVisibility(GONE);
                        if (emptyView != null)
                            emptyView.setVisibility(GONE);
                        setVisibility(VISIBLE);
                    } else {
                        setState(STATE_EMPTY);
                    }
                } else {
                    Log.e(CoreUtils.LOG_TAG, "RecyclerView does not have an adapter");
                    setState(STATE_LOADING);
                }
                break;
            case STATE_EMPTY:
                if (loadingView != null)
                    loadingView.setVisibility(GONE);
                if (emptyView != null)
                    emptyView.setVisibility(VISIBLE);
                setVisibility(GONE);
                break;
        }
        if (textView != null) {
            if (state == STATE_LOADING) {
                textView.setText(ResourceUtils.getString(getContext(),
                        loadingTextRes != -1 ? loadingTextRes : R.string.loading_section));
            } else if (state == STATE_EMPTY) {
                textView.setText(ResourceUtils.getString(getContext(),
                        emptyTextRes != -1 ? emptyTextRes : R.string.empty_section));
            }
            textView.setVisibility(state != STATE_NORMAL ? VISIBLE : GONE);
        }
    }

    final
    @NonNull
    AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateStateViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            updateStateViews();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            updateStateViews();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            updateStateViews();
        }
    };

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        updateStateViews();
    }

    @State
    public int getState() {
        return state;
    }

    public void setState(@State int state) {
        this.state = state;
        updateStateViews();
    }

    public void setLoadingView(@Nullable View loadingView) {
        this.loadingView = loadingView;
        updateStateViews();
    }

    public void setEmptyView(@Nullable View emptyView) {
        this.emptyView = emptyView;
        updateStateViews();
    }

    public void setTextView(@Nullable TextView textView) {
        this.textView = textView;
        if (this.textView != null) {
            this.textView.setTextColor(
                    ColorUtils.getMaterialSecondaryTextColor(ThemeUtils.isDarkTheme()));
        }
    }

    public void setLoadingTextRes(@StringRes int loadingTextRes) {
        this.loadingTextRes = loadingTextRes;
    }

    public void setEmptyTextRes(@StringRes int emptyTextRes) {
        this.emptyTextRes = emptyTextRes;
    }
}