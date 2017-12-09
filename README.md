# Apereo News Reader Portlet

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet/NewsReaderPortlet/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet/NewsReaderPortlet)
[![Linux Build Status](https://travis-ci.org/Jasig/NewsReaderPortlet.svg?branch=master)](https://travis-ci.org/Jasig/NewsReaderPortlet)
[![Windows Build status](https://ci.appveyor.com/api/projects/status/2ch9x7y3if4sq8eh/branch/master?svg=true)](https://ci.appveyor.com/project/ChristianMurphy/newsreaderportlet/branch/master)

This Java Portlet is a [Sponsored Portlet][] in the uPortal project.

The original version of NewsReaderPortlet [was written by][NewsReaderPortlet contributors] [Anthony Colebourne][] of University of Manchester and that portlet was heavily based upon the [CalendarPortlet][] written by [Jen Bourey][] of Yale University
(with permission).

## Configuration

See also [documentation in the external wiki][NewsReaderPortlet in Confluence].

### Using Encrypted Property Values

You may optionally provide sensitive configuration items -- such as database passwords -- in encrypted format.  Use the [Jasypt CLI Tools](http://www.jasypt.org/cli.html) to encrypt the sensitive value, then include it in a `.properties` file like this:

```
hibernate.connection.password=ENC(9ffpQXJi/EPih9o+Xshm5g==)
```

Specify the encryption key using the `UP_JASYPT_KEY` environment variable.

[Anthony Colebourne]: https://github.com/acolebourne
[Jen Bourey]: https://github.com/bourey

[Sponsored Portlet]: https://wiki.jasig.org/display/PLT/Jasig+Sponsored+Portlets
[NewsReaderPortlet in Confluence]: https://wiki.jasig.org/display/PLT/NewsReaderPortlet
[CalendarPortlet]: https://github.com/Jasig/CalendarPortlet

[NewsReaderPortlet contributors]: https://github.com/Jasig/NewsReaderPortlet/graphs/contributors
