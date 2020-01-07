wATL library Demo sources
==========

NB: library itself hosted now in separate repository - <a href="https://github.com/suwhs/wATLlib">github.com/suwhs/wATLlib</a>

License
========= 
APACHE LICENSE 2.0

Description
======
*wATL* is a library for android applications with features:
  - formatted text full justification,
  - wrap text around images, 
  - auto-hyphenation,
  - adapter for show paginated article with stock ViewPager
  - supported Android version - 2.3 - 5.1+ (both runtimes - ART and DALVIK)
 

Demo Application available on Google Play

<a href="https://play.google.com/store/apps/details?id=su.whs.watl.samples">
<img src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" alt="Get it on Google Play" />
</a>

[![Demo Video on Youtube](http://img.youtube.com/vi/ZtXvyS6GHGo/0.jpg)](https://youtu.be/ZtXvyS6GHGo)


Quick Start
======
Usage:

wATLlib published on jcenter repository, so just

add to dependencies :
```gradle
compile 'su.whs:wATLlib:1.2.3'
```


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



