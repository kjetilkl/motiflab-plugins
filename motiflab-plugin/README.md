# Template: RegionVisualizationFilter Tool

*RegionVisualizationFilters* can be added to MotifLab to dynamically decide whether a particular region in a Region Dataset should be hidden from view, or to change the colors of regions that are shown. They can also draw additional graphics on top of the regions to convey more information. This makes it possible to draw focus away from less interesting regions and instead highlight regions with particular properties.   

This plugin template can be used to create an interactive tool that will add such a filter to MotifLab. 
The tool will be added to the Tools menu when it is installed, and a pop-up dialog will be displayed when the tool is selected.
The dialog can be customized to add user interface elements that can be manipulated to potentially change the behaviour of the filter.

To create a new plugin based on this template, follow these steps:

## pom.xml
The [pom.xml](pom.xml) file is used by the [Maven](https://maven.apache.org/) build tool to describe how the plugin should be compiled and packaged.
Open the `pom.xml` file in a text editor and change the "[artifactId](pom.xml#L6)" name to something more sensible. You can change the "[groupID](pom.xml#L5)" as well, if you want to.

The POM-file also describes which external JAVA package dependencies the plugin requires. The [MotifLab package](pom.xml#L42-L48) is already listed here to allow your plugin to reference MotifLab code.
MotifLab relies on many other packages as well, and these can be directly used by your plugin without listing them. 
However, if your plugin depends on other packages that are not used by MotifLab itself, these must be explicitly added to the POM-file.

## plugin.conf
Open the [plugin.conf](plugin.conf) file in an editor and set the "[name](plugin.conf#L4)" property to a name of your choice. This is the name that will be displayed for the tool in the Tools menu.

You can specify additional meta-data for the plugin as well, such as a short description of the tool or your own contact information.
The "name" property is the only one that is required, but a few other properties, such as "[version](plugin.conf#L5)", "[type](plugin.conf#L6)" and "[description](plugin.conf#L16)", are recognized by MotifLab and will be
shown in the Configure Plugins dialog. You can add any other meta-data properties that you like, such as your [contact information](plugin.conf#L11-L13)
All the meta-data properties will be displayed if you select a plugin in the Configure Plugins dialog and click the "info" button, and the plugin code can also access properties defined in the plugin.conf file if it needs to.

## Java code
Rename the file [MyPlugin.java](src/main/java/org/motiflab/plugin/MyPlugin.java) in the [src/main/java/org/motiflab/plugin/](src/main/java/org/motiflab/plugin/) directory to something suiteable for your tool. Then edit this file and change the [class name in line 13](src/main/java/org/motiflab/plugin/MyPlugin.java#L13) to the same name.
This name will not be displayed anywhere and it does not really matter what the name is, but it should be unique to your plugin.

Next, change the [pluginName](src/main/java/org/motiflab/plugin/MyPlugin.java#L15) String in line 15 to the *exact same name* that you specified in the [plugin.conf](plugin.conf#L4) file. It is very important that these two names are identical, or else MotifLab may behave strangely.

For instructions on how to code the rest of the plugin, see below.

## Tool icon
You can add an optional icon that will be displayed before the name of the tool in the Tools menu. It should be a 16x16 pixel image in PNG format and placed in the "images" directory.
By default, the template assumes that the name of this image file will be "icon.png", but you can use a different name if you specify this with the [icon](plugin.conf#L9)  property in the `plugin.conf` file.

## Documentation
You can edit the [docs/index.html](docs/index.html) file to provide HTML-formatted documentation on how to use the plugin.

# Coding the plugin
The main code for the plugin should go in the [MyPlugin.java](src/main/java/org/motiflab/plugin/MyPlugin.java)  file (or whatever you have renamed that file to), but you can split the code across multiple class files if you need to. 
Remember to set the "[pluginName](src/main/java/org/motiflab/plugin/MyPlugin.java#L15)" to the exact same name that you specified in the [plugin.conf](plugin.conf#L4) file.

# Building the plugin
To compile and package the plugin, you need the [Maven](https://maven.apache.org/) build tool. When you have finished all the steps described above, run the following command.

```bash
mvn package
```

This will create a ZIP-file under the `target` directory which contains the Java JAR-file (also in the target-directory) along with the `plugin.conf` file and other resources needed by the plugin.
If you like, you can rename the ZIP-file to have an `mlp` file suffix rather than `zip`, since "mlp" is the preferred suffix for MotifLab Plugins. (But ZIP can be used as well).
The MLP or ZIP-file can then be installed into MotifLab by selecting "Plugins..." from the "Configure" menu, pressing the "Add" button in the lower-left corner of the Plugins dialog and then selecting your file in the file browser. (If the file still has a ZIP-suffix, you have to select either "ZIP-files" or "All Files" from the "Files of Type" menu to see it.)

