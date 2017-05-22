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

package jahirfiquitiva.libs.iconshowcase.fragments;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import jahirfiquitiva.libs.iconshowcase.R;
import jahirfiquitiva.libs.iconshowcase.adapters.HomeCardsAdapter;
import jahirfiquitiva.libs.iconshowcase.models.HomeCard;
import jahirfiquitiva.libs.iconshowcase.ui.views.EmptyViewRecyclerView;
import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils;
import jahirfiquitiva.libs.iconshowcase.utils.CoreUtils;
import jahirfiquitiva.libs.iconshowcase.utils.IconUtils;
import jahirfiquitiva.libs.iconshowcase.utils.NetworkUtils;
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils;
import jahirfiquitiva.libs.iconshowcase.utils.themes.ThemeUtils;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View content = inflater.inflate(R.layout.section_home, container, false);
        initRV(content);
        return content;
    }

    private void initRV(@NonNull View view) {
        EmptyViewRecyclerView rv = (EmptyViewRecyclerView) view.findViewById(R.id.home_rv);
        rv.setEmptyView(view.findViewById(R.id.empty_view));
        rv.setLoadingView(view.findViewById(R.id.loading_view));
        rv.setTextView((TextView) view.findViewById(R.id.empty_text));
        rv.setState(EmptyViewRecyclerView.STATE_LOADING);
        rv.updateStateViews();
        ArrayList<HomeCard> cards = new ArrayList<>();
        String[] titles = ResourceUtils.getStringArray(getContext(), R.array.home_list_titles);
        String[] descriptions = ResourceUtils.getStringArray(getContext(),
                R.array.home_list_descriptions);
        String[] icons = ResourceUtils.getStringArray(getContext(), R.array.home_list_icons);
        String[] urls = ResourceUtils.getStringArray(getContext(), R.array.home_list_links);
        if (titles.length == descriptions.length && descriptions.length == icons.length
                && icons.length == urls.length) {
            for (int i = 0; i < titles.length; i++) {
                String url = urls[i];
                boolean isAnApp = url.toLowerCase().startsWith(NetworkUtils.PLAY_STORE_LINK_PREFIX);
                boolean isInstalled = false;
                Intent intent = null;
                if (isAnApp) {
                    String packageName = url.substring(url.lastIndexOf("="));
                    isInstalled = CoreUtils.isAppInstalled(getContext(), packageName);
                    intent = getContext().getPackageManager().getLaunchIntentForPackage
                            (packageName);
                }
                cards.add(new HomeCard(titles[i], descriptions[i], urls[i],
                        IconUtils.getDrawableWithName(getContext(), icons[i]), isAnApp,
                        isInstalled, intent));
            }
        }
        rv.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        decoration.setDrawable(new ColorDrawable(
                ColorUtils.getMaterialDividerColor(ThemeUtils.isDarkTheme())));
        rv.addItemDecoration(decoration);
        rv.setAdapter(new HomeCardsAdapter(cards));
        rv.setState(EmptyViewRecyclerView.STATE_NORMAL);
        rv.updateStateViews();
    }
}