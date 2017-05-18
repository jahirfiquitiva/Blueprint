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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jahirfiquitiva.libs.iconshowcase.R;
import jahirfiquitiva.libs.iconshowcase.adapters.HomeCardsAdapter;
import jahirfiquitiva.libs.iconshowcase.models.HomeCard;
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils;

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
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.home_rv);
        ArrayList<HomeCard> cards = new ArrayList<>();
        String[] titles = ResourceUtils.getStringArray(getContext(), R.array.home_list_titles);
        String[] descriptions = ResourceUtils.getStringArray(getContext(),
                R.array.home_list_descriptions);
        String[] icons = ResourceUtils.getStringArray(getContext(), R.array.home_list_icons);
        String[] urls = ResourceUtils.getStringArray(getContext(), R.array.home_list_links);
        if (titles.length == descriptions.length && descriptions.length == icons.length
                && icons.length == urls.length) {
            for (int i = 0; i < titles.length; i++) {
                cards.add(new HomeCard(getContext(), titles[i], descriptions[i], urls[i],
                        icons[i]));
            }
            rv.setAdapter(new HomeCardsAdapter(getContext(), cards));
        }
    }
}