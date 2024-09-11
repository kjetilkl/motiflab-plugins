# MotifLab Plugins Templates

This repository contains various templates that can be used as a starting point to easily create new plugins of different types for [MotifLab](https://www.motiflab.org).

To begin, clone this repository to your local computer and change into the directory for the plugin type you want to create. 
Follow the instructions in the README file of that directory.

## Templates

- [Region Visualization Filter tool](region_filter/README.md)

## Prerequisites
MotifLab and its plugins are written in Java. To build plugins from Java source code you will need:

* [Java JDK 8](https://www.java.com) - programming language
* [Maven](https://maven.apache.org/) - build and dependency manager

The MotifLab core package is published on [GitHub Packages](https://github.com/kjetilkl/motiflab/packages/), and to access it you have to configure your GitHub credentials in a [server](https://maven.apache.org/settings.html#servers) block in your personal Maven [settings](https://maven.apache.org/settings.html) file,
which is typically found in `~/.m2/settings.xml`

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">

   <servers>
       <server>
           <id>github</id>
           <username>your_github_user_name</username>
           <password>your_github_token</password>
       </server>
   </servers>

</settings>
```
