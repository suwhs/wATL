wATL open source parts 
==========

licensed under GPL v3 

*wATL* is a library for android applications with support for formatted text justification, image wrapping and more 

Demo Application available on Google Play

<a href="https://play.google.com/store/apps/details?id=su.whs.watl.samples">
<img src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" alt="Get it on Google Play" />
</a>

Quick Start
======
Usage:

1. add maven repository url to build.gradle (top-level)
<pre>
allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven {
            url  'https://raw.githubusercontent.com/suwhs/maven-repository/master/'
        }
    }
}
</pre>

2. add dependency to module's builed.gradle
<pre>
dependencies {
....
     compile 'su.whs.watl:watllibrarylite:+'
}
</pre>


News
======

UPDATE 06 Apr 15
 added MultiColumnTextViewEx example - widget, that supports automatic columns count calculating depends on screen resolution. limits sets via setColumnLimits(minColumnWidth,maxColumnWidth) or forcing columns count via setColumnsCount(int numOfColumns)

UPDATE 05 Apr 15
 added <a href="https://appetize.io/app/3tcue9p594yzm7z1dap0md41tc">online app demo (via appetize.io)</a>

UPDATE 04 Apr 15
 hyphenation rules loading optimization
 added icons and 'about' activity
 added image wrap demo activity

UPDATE 03 Apr 15
 added *Soft-Hyphenation LineBreaker*  to sample application - so, TextViewEx render justified text using HyphenLineBreaker.class
    HyphenLineBreaker takes time to initialize (it's adapted third-party code), but initialization required once per launch, and
    may be optimized. (Indeed it's just demo of wATL feature *customizable LineBreaker*)
    screenshots: <a href="https://github.com/suwhs/wATL/blob/master/screenshots/HyphenTextViewEx3.png">screenshot 3</a>,    <a href="https://github.com/suwhs/wATL/blob/master/screenshots/HyphenTextViewEx2.png">screenshot 4</a>

Published Classes
========

some description on <a href="https://github.com/suwhs/wATL/wiki">Wiki</a>

- *su.whs.watl.ui.TextViewWS* - base class with methods for handling text selection
    <a href="https://github.com/suwhs/wATL/blob/master/screenshots/TextViewWS1.png">screenshot 1</a>
- *su.whs.watl.ui.ClickableSpanListener* - interface for easy handle clicks on drawable 
onClick() method receive view, span position, and coordinates of image within view

- *su.whs.watl.ui.TextViewEx* - class (replacement for stock TextView) with full text justification support (enabled by default)
    <a href="https://github.com/suwhs/wATL/blob/master/screenshots/TextViewExScrollView1.png">screenshot 2</a>


Contacts
========
<a href="http://whs.su/?p=33">wATL Home</a><br/>
<a href="mailto:info@whs.su">info@whs.su</a>



