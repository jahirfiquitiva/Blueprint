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

package jahirfiquitiva.libs.iconshowcase.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jahirfiquitiva.libs.iconshowcase.R;
import jahirfiquitiva.libs.iconshowcase.holders.HomeCardsHolders;
import jahirfiquitiva.libs.iconshowcase.models.HomeCard;

public class HomeCardsAdapter extends RecyclerView.Adapter<HomeCardsHolders.ExtraCardHolder> {

    private Context context;
    private ArrayList<HomeCard> extraCards;

    public HomeCardsAdapter(Context context, ArrayList<HomeCard> extraCards) {
        this.context = context;
        this.extraCards = extraCards;
    }

    @Override
    public HomeCardsHolders.ExtraCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_extra_card, parent, false);
        return new HomeCardsHolders.ExtraCardHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeCardsHolders.ExtraCardHolder holder, int position) {
        holder.setItem(extraCards.get(position));
    }

    @Override
    public int getItemCount() {
        return extraCards != null && extraCards.size() > 0 ? extraCards.size() : 0;
    }
}