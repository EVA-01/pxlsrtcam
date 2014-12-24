pxlsrtcam
=========

Pixel sort webcam feed.

## Requirements for running

* [Ruby](https://www.ruby-lang.org/en/)
* [RubyGems](http://rubygems.org/)
* [pxlsrt](https://github.com/EVA-01/pxlsrt) ([on RubyGems.org](http://rubygems.org/gems/pxlsrt))
* Developed in [NetBeans](https://netbeans.org/)

### Dependencies included in library

* [Webcam Capture](http://webcam-capture.sarxos.pl/)
  * [SLF4J](http://www.slf4j.org/)
  * [BridJ](https://code.google.com/p/bridj/)

## Variables

* `seconds` — Interval in seconds between webcam capture.
* `maxSeconds` — Maximum number of seconds to run the program. Can remove the `while` loop below and replace with `while(true) {` to run indefinitely.
