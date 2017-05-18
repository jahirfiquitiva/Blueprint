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

import android.app.WallpaperManager;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
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
import jahirfiquitiva.libs.iconshowcase.models.DrawerItem;
import jahirfiquitiva.libs.iconshowcase.ui.layouts.CustomCoordinatorLayout;
import jahirfiquitiva.libs.iconshowcase.ui.layouts.FixedElevationAppBarLayout;
import jahirfiquitiva.libs.iconshowcase.ui.views.CounterFab;
import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils;
import jahirfiquitiva.libs.iconshowcase.utils.CoreUtils;
import jahirfiquitiva.libs.iconshowcase.utils.IconUtils;
import jahirfiquitiva.libs.iconshowcase.utils.NetworkUtils;
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils;
import jahirfiquitiva.libs.iconshowcase.utils.preferences.Preferences;
import jahirfiquitiva.libs.iconshowcase.utils.themes.ThemeUtils;
import jahirfiquitiva.libs.iconshowcase.utils.themes.TintUtils;

import static android.support.v4.widget.TextViewCompat.setTextAppearance;

public class ShowcaseActivity extends ThemedActivity {

    private CustomCoordinatorLayout coordinatorLayout;
    private FixedElevationAppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private TabLayout tabs;
    private Toolbar toolbar;
    private BottomNavigationView bottomBar;
    private CounterFab fab;
    private Preferences prefs;

    private long currentItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Preferences(this);
        setContentView(R.layout.activity_showcase);
        initToolbar();
        initBottomBar();
        initFAB();
        initCollapsingToolbar();
        performBottomBarClick(0);
        // initDrawer(savedInstanceState);
    }

    protected Bundle getInitialConfiguration() {
        return null;
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(item -> {
            int i = item.getItemId();
            if (i == R.id.changelog) {
                showChangelog();
                return true;
            }
            return false;
        });
    }

    private void initFAB() {
        fab = (CounterFab) findViewById(R.id.fab);
        if (bottomBar == null) return;
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                fab.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin,
                params.bottomMargin + bottomBar.getHeight());
        fab.setLayoutParams(params);
    }

    private void initBottomBar() {
        bottomBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomBar.setOnNavigationItemSelectedListener(
                item -> {
                    long id = -1;
                    if (item.getItemId() == R.id.home) {
                        id = 0;
                    } else if (item.getItemId() == R.id.previews) {
                        id = 1;
                    } else if (item.getItemId() == R.id.wallpapers) {
                        id = 2;
                    } else if (item.getItemId() == R.id.apply) {
                        id = 3;
                    } else if (item.getItemId() == R.id.requests) {
                        id = 4;
                    } else {
                        return false;
                    }
                    clickDrawerItem(id);
                    return true;
                });
    }

    private void performBottomBarClick(int id) {
        if (bottomBar == null) return;
        int itemId = -1;
        switch (id) {
            case 0:
                itemId = R.id.home;
                break;
            case 1:
                itemId = R.id.previews;
                break;
            case 2:
                itemId = R.id.wallpapers;
                break;
            case 3:
                itemId = R.id.apply;
                break;
            case 4:
                itemId = R.id.requests;
                break;
        }
        if (itemId != -1) {
            bottomBar.setSelectedItemId(itemId);
            /*
            View view = bottomBar.findViewById(itemId);
            if (view != null) {
                view.performClick();
            }
            */
        }
    }

    private void initCollapsingToolbar() {
        appBarLayout = (FixedElevationAppBarLayout) findViewById(R.id.appBar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        ImageView wallpaper = (ImageView) findViewById(R.id.toolbarHeader);
        WallpaperManager wManager = WallpaperManager.getInstance(this);
        wallpaper.setImageDrawable(wManager.getFastDrawable());
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
        if (currentItemId == id) return;
        try {
            currentItemId = id;
            changeFABVisibility(id == DrawerItem.HOME.getId() ||
                    id == DrawerItem.REQUESTS.getId());
            changeFABAction(id == DrawerItem.HOME.getId());
            if (appBarLayout != null) {
                appBarLayout.setExpanded(id == DrawerItem.HOME.getId(),
                        prefs != null && prefs.getAnimationsEnabled());
            }
            if (collapsingToolbar != null) {
                collapsingToolbar.setTitle(
                        ResourceUtils.getString(this, id == DrawerItem.HOME.getId()
                                ? R.string.app_name
                                : DrawerItem.getItemWithId(id).getText()));
            }
            loadFragment(DrawerItem.getItemWithId(id));
            Toast.makeText(this, DrawerItem.getItemWithId(id).toString(), Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFragment(DrawerItem item) {
        Fragment newFragment = item.getFragment();
        if (newFragment == null) return;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragments_container,
                item.getFragment(), item.getStringId());
        if (prefs != null && prefs.getAnimationsEnabled()) {
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
        }
        fragmentTransaction.commit();
    }

    private void showChangelog() {
        Toast.makeText(this, "Changelog", Toast.LENGTH_SHORT).show();
    }

    public void changeFABVisibility(boolean visible) {
        if (fab == null) return;
        if (visible) fab.show();
        else fab.hide();
    }

    public void changeFABAction(boolean home) {
        if (fab == null) return;
        fab.setImageDrawable(TintUtils.createTintedDrawable(
                IconUtils.getDrawableWithName(this, home ? "ic_rate" : "ic_email"),
                ColorUtils.getMaterialActiveIconsColor(
                        !ColorUtils.isLightColor(
                                ContextCompat.getColor(this,
                                        ThemeUtils.darkOrLight(
                                                R.color.dark_theme_accent,
                                                R.color.light_theme_accent))))));
        if (home) {
            fab.setOnClickListener(view -> NetworkUtils.openLink(view.getContext(),
                    NetworkUtils.PLAY_STORE_LINK_PREFIX + CoreUtils.getAppPackageName(this)));
        } else {
            fab.setOnClickListener(view ->
                    Toast.makeText(view.getContext(), "Creating request", Toast.LENGTH_SHORT)
                            .show());
        }
    }

}