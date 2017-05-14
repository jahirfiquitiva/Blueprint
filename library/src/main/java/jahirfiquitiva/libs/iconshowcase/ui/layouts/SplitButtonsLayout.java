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

package jahirfiquitiva.libs.iconshowcase.ui.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import jahirfiquitiva.libs.iconshowcase.R;

/**
 * @author Aidan Follestad (afollestad)
 */
public class SplitButtonsLayout extends LinearLayout {

    private int buttonCount;

    public SplitButtonsLayout(Context context) {
        super(context);
        init();
    }

    public SplitButtonsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SplitButtonsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        if (isInEditMode()) {
            buttonCount = 2;
            addButton("Website", "https://www.jahirfiquitiva.me");
            addButton("Google+", "https://www.google.com/+JahirFiquitivaJDev");
        }
    }

    /**
     * Sets how many buttons the layout will have.
     */
    public void setButtonCount(int buttonCount) {
        this.buttonCount = buttonCount;
        setWeightSum(buttonCount);
    }

    public void addButton(String text, String link) {
        if (getChildCount() == buttonCount)
            throw new IllegalStateException(buttonCount + " buttons have already been added.");
        final Button newButton = (Button) LayoutInflater.from(getContext())
                .inflate(R.layout.item_credits_button, this, false);
        // width can be 0 since weight is used
        final LayoutParams lp = new LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        newButton.setText(text);
        newButton.setTag(link);
        addView(newButton, lp);
    }

    public boolean hasAllButtons() {
        return getChildCount() == buttonCount;
    }
}