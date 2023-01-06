# Apereo News Reader Portlet

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet/NewsReaderPortlet/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet/NewsReaderPortlet)
[![build status](https://github.com/uPortal-Project/NewsReaderPortlet/workflows/CI/badge.svg?branch=master)](https://github.com/uPortal-Project/NewsReaderPortlet/actions)

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

### Allowing Doctypes in RSS Feeds

For backwards compatibility at a slight security risk, you may opt to allow doctypes in RSS feeds.
This is disabled by default. To allow this, add the following to a `.properties file`:

```properties
newsreader.synfeedinput.xmlreader.allowDoctypes=true
```

[Anthony Colebourne]: https://github.com/acolebourne
[Jen Bourey]: https://github.com/bourey

[Sponsored Portlet]: https://wiki.jasig.org/display/PLT/Jasig+Sponsored+Portlets
[NewsReaderPortlet in Confluence]: https://wiki.jasig.org/display/PLT/NewsReaderPortlet
[CalendarPortlet]: https://github.com/Jasig/CalendarPortlet

[NewsReaderPortlet contributors]: https://github.com/Jasig/NewsReaderPortlet/graphs/contributors
