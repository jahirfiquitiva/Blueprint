<img src="https://github.com/jahirfiquitiva/Blueprint/raw/master/art/app_logo.png" width="192" align="right" hspace="20" />

Blueprint
======

![API](https://img.shields.io/badge/API-16%2B-34bf49.svg)
[![JitPack](https://jitpack.io/v/jahirfiquitiva/Blueprint.svg)](https://jitpack.io/#jahirfiquitiva/Blueprint)
[![Build Status](https://travis-ci.org/jahirfiquitiva/Blueprint.svg?branch=master)](https://travis-ci.org/jahirfiquitiva/Blueprint)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9747a594949f49b9a8146909868adfba)](https://www.codacy.com/app/jahirfiquitiva/Blueprint?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jahirfiquitiva/Blueprint&amp;utm_campaign=Badge_Grade)
[![Crowdin](https://d322cqt584bo4o.cloudfront.net/blueprint/localized.svg)](http://j.mp/BlueprintTranslations)

A full-of-features, easy-to-customize, free and open source, light-weight, Android dashboard library for icon packs.

<a target="_blank" href="http://j.mp/BlueprintAPK">
<img src="http://jahirfiquitiva.me/share/download_sample.svg?maxAge=432000" width="200"/>
</a>

<a target="_blank" href="http://www.jahirfiquitiva.me/support/">
<img src="http://jahirfiquitiva.me/share/support_my_work.svg?maxAge=432000" width="200"/>
</a>

## Show some  :blue_heart:
[![GitHub stars](https://img.shields.io/github/stars/jahirfiquitiva/Blueprint.svg?style=social&label=Star)](https://github.com/jahirfiquitiva/Blueprint)
[![GitHub forks](https://img.shields.io/github/forks/jahirfiquitiva/Blueprint.svg?style=social&label=Fork)](https://github.com/jahirfiquitiva/Blueprint/fork)
[![GitHub watchers](https://img.shields.io/github/watchers/jahirfiquitiva/Blueprint.svg?style=social&label=Watch)](https://github.com/jahirfiquitiva/Blueprint)

[![Follow on GitHub](https://img.shields.io/github/followers/jahirfiquitiva.svg?style=social&label=Follow)](https://github.com/jahirfiquitiva)
[![Twitter Follow](https://img.shields.io/twitter/follow/jahirfiquitiva.svg?style=social)](https://twitter.com/jahirfiquitiva)
[![Google+](https://img.shields.io/badge/Follow-Google%2B-ea4335.svg)](https://plus.google.com/+JahirFiquitivaR)

---

# Previews

### Customizable styles

### Home / Icons / Changelog
<p align="center">
<img src="https://github.com/jahirfiquitiva/Blueprint/raw/master/art/1.png" height="350"/>
<img src="https://github.com/jahirfiquitiva/Blueprint/raw/master/art/3.png" height="350"/>
<img src="https://github.com/jahirfiquitiva/Blueprint/raw/master/art/4.png" height="350"/>
<img src="https://github.com/jahirfiquitiva/Blueprint/raw/master/art/9.png" height="350"/>
</p>

### Wallpapers / Apply / Requests / Templates (Zooper & Kustom)
<p align="center">
<img src="https://github.com/jahirfiquitiva/Blueprint/raw/master/art/5.png" height="350"/>
<img src="https://github.com/jahirfiquitiva/Blueprint/raw/master/art/6.png" height="350"/>
<img src="https://github.com/jahirfiquitiva/Blueprint/raw/master/art/7.png" height="350"/>
<img src="https://github.com/jahirfiquitiva/Blueprint/raw/master/art/8.png" height="350"/>
</p>

### Help / Credits / Settings / Legacy Navigation Drawer
<p align="center">
<img src="https://github.com/jahirfiquitiva/Blueprint/raw/master/art/10.png" height="350"/>
<img src="https://github.com/jahirfiquitiva/Blueprint/raw/master/art/11.png" height="350"/>
<img src="https://github.com/jahirfiquitiva/Blueprint/raw/master/art/12.png" height="350"/>
<img src="https://github.com/jahirfiquitiva/Blueprint/raw/master/art/13.png" height="350"/>
</p>

---

# Features:
- Material Design dashboard.
- License Checker.
- Donations.
- OneSignal notifications ready.
- Changelog shown with every update.
- Previews section, where user can see and search for themed icons which organized by categories.
- In-app icon request tool. Fully functional. Without duplicates and incredibly fast. This tool generates 'appfilter.xml', 'theme_resources.xml' and 'appmap.xml' which are the needed files for icon packs designers to be able to provide support the most of launchers.
- Support for [Arctic Manager](https://arcticmanager.com/)
- Apply section with 26 supported launchers. Launchers are sorted by installed first, and alphabetically.
- Help section, to answer the questions your users have.
- Analog clock widget.
- Widget/Shortcut to open the app. (In case users hide the icon).
- App works as a gallery so users are able to pick pictures to use them in other apps.
- Cloud based (only) wallpapers.
- Support for Zooper templates, Kustom Wallpapers, Widgets, Lockscreens and Komponents.
- App can work offline.
- Deep search (users can search wallpapers by name, author and/or collection at the same time, and icons by name and category at the same time, too).
- Wallpapers can be applied and downloaded.
- Wallpapers include a full-screen viewer with zooming capabilities and detailed info viewer.
- [Muzei Live Wallpaper](http://muzei.co/) support.
- Credits section.
- Settings section with these options:
	- Option to change app theme (Light, Dark, Amoled, Transparent, Auto-Dark, Auto-Amoled).
	- Option to color navigation bar (Lollipop+).
	- Option to change the columns amount in wallpapers section.
	- Option to clear app cache.
	- Option to change where to download wallpapers.
	- Option to hide icon.
- Tablet layouts.
- Lots of customizations.
- Works with Android 4.1 and newer.

## Help translating
:page_facing_up: Help making Blueprint available in more languages. [Click here to go to the translation site](http://j.mp/BlueprintTranslations)

## Changelog
:radio_button: You can find it in the [Releases page](https://github.com/jahirfiquitiva/Blueprint/releases)

---

# Including in your project
Blueprint is available via JitPack, so getting it as simple as adding it as a dependency, like this:

1. Add JitPack repository to your root `build.gradle` file
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
2. Add the dependency in your project `build.gradle` file
```gradle
dependencies {
    compile('me.jahirfiquitiva:Blueprint:{latest version}@aar') {
        transitive = true
    }
}
```
where `{latest version}` corresponds to published version in   [![JitPack](https://jitpack.io/v/jahirfiquitiva/Blueprint.svg)](https://jitpack.io/#jahirfiquitiva/Blueprint)

## How to implement
:page_with_curl: Everything you need to know can be found in the **[Wiki Docs](https://github.com/jahirfiquitiva/Blueprint/wiki/)**

## Still need help :question:
Just join our community and make a post. We'll help you as soon as possible. [![Google+ Community](https://img.shields.io/badge/Google%2B-Community-ea4335.svg)](https://plus.google.com/communities/117748118619432374563)

---

# Developed by

### [Jahir Fiquitiva](https://www.jahirfiquitiva.me/)

[![Follow on GitHub](https://img.shields.io/github/followers/jahirfiquitiva.svg?style=social&label=Follow)](https://github.com/jahirfiquitiva)
[![Twitter Follow](https://img.shields.io/twitter/follow/jahirfiquitiva.svg?style=social)](https://twitter.com/jahirfiquitiva)
[![Google+](https://img.shields.io/badge/Follow-Google%2B-ea4335.svg)](https://plus.google.com/+JahirFiquitivaR)

If you found this app/library helpful and want to thank me, you can:

<a target="_blank" href="http://www.jahirfiquitiva.me/support/">
<img src="http://jahirfiquitiva.me/share/support_my_work.svg?maxAge=432000" width="200"/>
</a>

**Thanks in advance!** :pray:

## Special thanks 🙌

- [Sherry Sabatine](https://plus.google.com/+SherrySabatine) 💵
- [Allan Wang](https://www.allanwang.ca/) 💻
- [James Fenn](https://theandroidmaster.github.io/) 🔌
- [Maximilian Keppeler](https://plus.google.com/+MaxKeppeler) 🔌
- [Sasi Kanth](https://plus.google.com/+Sasikanth) 🔌
- [Alexandre Piveteau](https://github.com/alexandrepiveteau) 💻
- [Lukas Koller](https://github.com/kollerlukas) 🔌
- [Patryk Goworowski](https://plus.google.com/+PatrykGoworowski) 🎨
- [Lumiq Creative](https://plus.google.com/+LumiqCreative) 🎨
- [Jackson Hayes](https://jacksonhayes.xyz/) 📖
- [Kevin Aguilar](http://kevaguilar.com/) 🎨
- [Eduardo Pratti](https://plus.google.com/+EduardoPratti) 🎨
- [Anthony Nguyen](https://plus.google.com/+AHNguyen) 🎨

---

# License

This app is shared under the CreativeCommons Attribution-ShareAlike license.

	Copyright © 2018 Jahir Fiquitiva

	Licensed under the CreativeCommons Attribution-ShareAlike 
	4.0 International License. You may not use this file except in compliance 
	with the License. You may obtain a copy of the License at

	   http://creativecommons.org/licenses/by-sa/4.0/legalcode

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

## Library source

As you may know, the [library source](https://github.com/jahirfiquitiva/Blueprint/tree/master) is open-source. This means that you can fork it and do your own modifications, but it has some conditions:

When using the [library source](https://github.com/jahirfiquitiva/Blueprint/tree/master), anything from it: errors, crashes, issues, etc. including successful builds, must be done completely by yourself and under your own risk and responsibility. I **will not** provide any help/support when using the [library source](https://github.com/jahirfiquitiva/Blueprint/tree/master).

Finally, be sure your projects comply with the [license previously mentioned](https://github.com/jahirfiquitiva/Blueprint#license). Otherwise I will be taking the required legal actions. I hope you understand.