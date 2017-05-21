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

package jahirfiquitiva.libs.iconshowcase.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jahirfiquitiva.libs.iconshowcase.R;
import jahirfiquitiva.libs.iconshowcase.models.HomeCard;
import jahirfiquitiva.libs.iconshowcase.utils.NetworkUtils;

public class HomeCardsHolders {

    public class DescriptionCardHolder extends RecyclerView.ViewHolder {
        public DescriptionCardHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ExtraCardHolder extends RecyclerView.ViewHolder {
        private LinearLayout root;
        private final TextView title;
        private final TextView description;
        private final ImageView icon;

        public ExtraCardHolder(View itemView) {
            super(itemView);
            this.root = itemView.findViewById(R.id.home_extra_card);
            this.title = itemView.findViewById(R.id.home_extra_card_title);
            this.description = itemView.findViewById(R.id.home_extra_card_description);
            this.icon = itemView.findViewById(R.id.home_extra_card_image);
        }

        public void setItem(final HomeCard item) {
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchIntent(view.getContext(), item);
                }
            });
            title.setText(item.getTitle());
            description.setText(item.getDescription());
            icon.setImageDrawable(item.getIcon());
        }

        private void launchIntent(Context context, HomeCard item) {
            if (item.getIntent() != null) {
                context.startActivity(item.getIntent());
            } else {
                NetworkUtils.openLink(context, item.getUrl());
            }
        }
    }
}