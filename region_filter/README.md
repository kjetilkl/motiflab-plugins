# Template: RegionVisualizationFilter Tool

*RegionVisualizationFilters* [RVF](https://github.com/kjetilkl/motiflab/blob/master/src/main/java/motiflab/gui/RegionVisualizationFilter.java) can be added to MotifLab to dynamically decide whether a particular region in a Region Dataset should be hidden from view, or to change the colors of regions that are shown. They can also draw additional graphics on top of the regions to convey more information. This makes it possible to draw focus away from less interesting regions and instead highlight regions with particular properties.   

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

Next, change the value of the [pluginName](src/main/java/org/motiflab/plugin/MyPlugin.java#L15C43-C51) String in line 15 to the *exact same name* that you specified in the [plugin.conf](plugin.conf#L4) file. It is very important that these two names are identical, or else MotifLab may behave strangely.

For instructions on how to code the rest of the plugin, [see below](#coding-the-plugin).

## Tool icon
You can add an optional icon that will be displayed before the name of the tool in the Tools menu. It should be a 16x16 pixel image in PNG format and placed in the "images" directory.
By default, the template assumes that the name of this image file will be "icon.png", but you can use a different name if you specify this with the [icon](plugin.conf#L9)  property in the `plugin.conf` file.

## Documentation
You can edit the [docs/index.html](docs/index.html) file to provide HTML-formatted documentation on how to use the plugin.

# Coding the plugin
The main code for the plugin should go in the [MyPlugin.java](src/main/java/org/motiflab/plugin/MyPlugin.java) file (or whatever you have renamed that file to), but you can split the code across multiple class files if you need to. 
Remember to set the "[pluginName](src/main/java/org/motiflab/plugin/MyPlugin.java#L15C43-C51)" to the exact same name that you specified in the [plugin.conf](plugin.conf#L4) file.

The template contains two main parts: a section pertaining to the [dialog](#L23-L38) that will be displayed when the tool is selected and the specification of the [filter](#L39-L72) itself.
In addition, the header contains a few Boolean [settings](#L17-L19) that control some main behavioural aspects of the tool:

- [singleTarget](#L17) : This setting controls whether the filter will be applied to a single Region Dataset (if set to TRUE) or all the Region Datasets (FALSE). If TRUE, the dialog will include a drop-down menu which allows the user to select exactly which dataset to apply the filter to.
- [persistant](#L18) : This setting controls whether the filter should always be discarded when the dialog is closed (if set to FALSE) or whether it should potentially persist even after the user closes the dialog (if set to TRUE). If set to TRUE, the dialog will include a checkbox that allows the user to select whether or not to discard the filter.
- [overlay](#L19) : This must be set to TRUE if you want to draw additional stuff on top of the regions with the filter. It is set to FALSE by default for optimization reasons.

It is possible to have multiple filters installed at the same time. If any of these filter wants a region to be hidden, it will not be displayed. If all the filters agree that the region should be shown, they may suggest alternative colors for the region itself, its border, label or motif logo colors. The filters will be ordered according to *priority*, and the first filter to suggest an alternative color for the region will be obeyed. Hence, filters with [higher priority](#L21) are more likely to get their preferences respected.

## The tool dialog
The template automatically creates a dialog that will be displayed when the tool is selected from MotifLab's Tools menu. This dialog contains a CLOSE button at the bottom, and, if the [singleTarget](#L17) setting is set to TRUE, it will also include a drop-down menu at the top to select the target dataset. The plugin creator can add more GUI elements to the middle part of the dialog by adding components to the JPanel provided as an argument to the [setupDialog](#L27-L29) method. The JPanel has a vertical BoxLayout by default, so the components that are added will be displayed beneath each other (but this can be changed, of course).

```java
    @Override
    public void setupDialog(JPanel panel) {
        panel.add(new JLabel("Move this slider"));
        panel.add(new JSlider(0,100));
    }
```



## The filter
The template automatically creates a dialog containing a CLOSE button that will be displayed when the tool is selected from MotifLab's Tools menu. In addition, the dialog will include a drop-down menu 

## Additional 

### Data updates
Lorem ipsum

### Loading images
Lorem ipsum


# Building the plugin
To compile and package the plugin, you need the [Maven](https://maven.apache.org/) build tool. When you have finished all the steps described above, run the following command.

```bash
mvn package
```

This will create a ZIP-file under the `target` directory which contains the Java JAR-file (also in the target-directory) along with the `plugin.conf` file and other resources needed by the plugin.
If you like, you can rename the ZIP-file to have an `mlp` file suffix rather than `zip`, since "mlp" is the preferred suffix for MotifLab Plugins. (But ZIP can be used as well).
The MLP or ZIP-file can then be installed into MotifLab by selecting "Plugins..." from the "Configure" menu, pressing the "Add" button in the lower-left corner of the Plugins dialog and then selecting your file in the file browser. (If the file still has a ZIP-suffix, you have to select either "ZIP-files" or "All Files" from the "Files of Type" menu to see it.)

