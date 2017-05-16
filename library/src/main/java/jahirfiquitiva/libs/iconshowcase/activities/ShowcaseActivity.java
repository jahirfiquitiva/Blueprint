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

package jahirfiquitiva.libs.iconshowcase.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import jahirfiquitiva.libs.iconshowcase.R;
import jahirfiquitiva.libs.iconshowcase.activities.base.ThemedActivity;
import jahirfiquitiva.libs.iconshowcase.ui.layouts.CustomCoordinatorLayout;
import jahirfiquitiva.libs.iconshowcase.ui.layouts.FixedElevationAppBarLayout;
import jahirfiquitiva.libs.iconshowcase.ui.views.CounterFab;
import jahirfiquitiva.libs.iconshowcase.utils.CoreUtils;
import jahirfiquitiva.libs.iconshowcase.utils.DrawerItem;
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils;

import static android.support.v4.widget.TextViewCompat.setTextAppearance;

public class ShowcaseActivity extends ThemedActivity {

    private CustomCoordinatorLayout coordinatorLayout;
    private FixedElevationAppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private TabLayout tabs;
    private Toolbar toolbar;
    private CounterFab fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showcase_activity);
        initToolbar();
        initDrawer(savedInstanceState);
    }

    protected Bundle getInitialConfiguration() {
        return null;
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(item -> {
            int i = item.getItemId();
            if (i == R.id.changelog) {
                showChangelog();
                return true;
            }
            return false;
        });
    }

    private void initDrawer(Bundle savedInstance) {
        AccountHeaderBuilder accountHeaderBuilder = new AccountHeaderBuilder().withActivity(this);
        accountHeaderBuilder.withHeaderBackground(R.drawable.drawer_header);
        if (ResourceUtils.getBoolean(this, R.bool.with_drawer_texts)) {
            accountHeaderBuilder.withSelectionFirstLine(
                    ResourceUtils.getString(this, R.string.app_long_name));
            accountHeaderBuilder.withSelectionSecondLine("v " + CoreUtils.getAppVersion(this));
        }
        accountHeaderBuilder.withProfileImagesClickable(false)
                .withResetDrawerOnProfileListClick(false)
                .withSelectionListEnabled(false)
                .withSelectionListEnabledForSingleProfile(false)
                .withSavedInstance(savedInstance);
        AccountHeader accountHeader = accountHeaderBuilder.build();

        TextView drawerTitle = (TextView)
                accountHeader.getView().findViewById(R.id.material_drawer_account_header_name);
        TextView drawerSubtitle = (TextView)
                accountHeader.getView().findViewById(R.id.material_drawer_account_header_email);
        setTextAppearance(drawerTitle, R.style.DrawerTextsWithShadow);
        setTextAppearance(drawerSubtitle, R.style.DrawerTextsWithShadow);

        DrawerBuilder drawerBuilder = new DrawerBuilder().withActivity(this);
        if (toolbar != null)
            drawerBuilder.withToolbar(toolbar);
        drawerBuilder.withAccountHeader(accountHeader)
                .withDelayOnDrawerClose(-1)
                .withShowDrawerOnFirstLaunch(true);

        drawerBuilder.withOnDrawerItemClickListener((view, position, drawerItem) -> {
            if (drawerItem != null) {
                clickDrawerItem(drawerItem.getIdentifier());
            }
            return false;
        });

        DrawerItem home = DrawerItem.HOME;
        drawerBuilder.addDrawerItems(
                new PrimaryDrawerItem().withIdentifier(home.getId())
                        .withName(home.getText()).withIcon(home.getIcon())
                        .withIconTintingEnabled(true));
        for (String itemId : ResourceUtils.getStringArray(this, R.array.drawer_sections)) {
            try {
                DrawerItem item = DrawerItem.getItemWithId(itemId);
                if (item.getIcon() != -1) {
                    drawerBuilder.addDrawerItems(
                            new PrimaryDrawerItem().withIdentifier(item.getId())
                                    .withName(item.getText())
                                    .withIcon(item.getIcon())
                                    .withIconTintingEnabled(true));
                } else {
                    drawerBuilder.addDrawerItems(
                            new PrimaryDrawerItem().withIdentifier(item.getId())
                                    .withName(item.getText())
                                    .withIconTintingEnabled(true));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        drawerBuilder.addDrawerItems(new DividerDrawerItem());
        DrawerItem about = DrawerItem.ABOUT;
        drawerBuilder.addDrawerItems(
                new SecondaryDrawerItem().withIdentifier(about.getId())
                        .withName(about.getText()));
        DrawerItem settings = DrawerItem.SETTINGS;
        drawerBuilder.addDrawerItems(
                new SecondaryDrawerItem().withIdentifier(settings.getId())
                        .withName(settings.getText()));

        drawerBuilder.withHasStableIds(true)
                // .withFireOnInitialOnClick(true)
                .withShowDrawerUntilDraggedOpened(true)
                .withSavedInstance(savedInstance);


        drawerBuilder.build();
    }

    private void clickDrawerItem(long id) {
        try {
            Toast.makeText(this, DrawerItem.getItemWithId(id).toString(), Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showChangelog() {
        Toast.makeText(this, "Changelog", Toast.LENGTH_SHORT).show();
    }

}