wATL open source parts 
==========

licensed under GPL v3 

*wATL* is a library for android applications with support for formatted text justification, image wrapping and more 

UPDATE 03 Apr 15
 added *Soft-Hyphenation LineBreaker*  to sample application - so, TextViewEx render justified text using HyphenLineBreaker.class
    HyphenLineBreaker takes time to initialize (it's adapted third-party code), but initialization required once per launch, and
    may be optimized. (Indeed it's just demo of wATL feature *customizable LineBreaker*)
    screenshots: <a href="https://github.com/suwhs/wATL/blob/master/screenshots/HyphenTextViewEx1.png">screenshot 3</a>,    <a href="https://github.com/suwhs/wATL/blob/master/screenshots/HyphenTextViewEx2.png">screenshot 4</a>

Published Classes
========
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



