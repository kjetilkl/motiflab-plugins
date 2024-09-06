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

The template contains two main parts: a section pertaining to the [dialog](src/main/java/org/motiflab/plugin/MyPlugin.java#L23-L38) that will be displayed when the tool is selected and the specification of the [filter](src/main/java/org/motiflab/plugin/MyPlugin.java#L39-L72) itself.
In addition, the header contains a few Boolean [settings](src/main/java/org/motiflab/plugin/MyPlugin.java#L17-L19) that control some main behavioural aspects of the tool:

- [singleTarget](src/main/java/org/motiflab/plugin/MyPlugin.java#L17) : This setting controls whether the filter will be applied to a single Region Dataset (if set to TRUE) or all the Region Datasets (FALSE). If TRUE, the dialog will include a drop-down menu which allows the user to select exactly which dataset to apply the filter to.
- [persistent](src/main/java/org/motiflab/plugin/MyPlugin.java#L18) : This setting controls whether the filter should always be discarded when the dialog is closed (if set to FALSE) or whether it should potentially persist even after the user closes the dialog (if set to TRUE). If set to TRUE, the dialog will include a checkbox that allows the user to select whether or not to discard the filter.
- [overlay](src/main/java/org/motiflab/plugin/MyPlugin.java#L19) : This must be set to TRUE if you want to draw additional stuff on top of the regions with the filter. It is set to FALSE by default for optimization reasons.

It is possible to have multiple filters installed at the same time. If any of these filter wants a region to be hidden, it will not be displayed. If all the filters agree that the region should be shown, they may suggest alternative colors for the region itself, its border, label or motif logo colors. The filters will be ordered according to *priority*, and the first filter to suggest an alternative color for the region will be obeyed. Hence, filters with [higher priority](src/main/java/org/motiflab/plugin/MyPlugin.java#L21) are more likely to get their will.

## The tool dialog
The template automatically creates a dialog that will be displayed when the tool is selected from MotifLab's Tools menu. This dialog contains a CLOSE button at the bottom, and, if the [singleTarget](src/main/java/org/motiflab/plugin/MyPlugin.java#L17) setting is set to TRUE, it will also include a drop-down menu at the top to select the target dataset. The plugin creator can add more GUI elements to the middle part of the dialog by adding components to the JPanel provided as an argument to the [setupDialog](src/main/java/org/motiflab/plugin/MyPlugin.java#L27-L29) method. The JPanel has a vertical BoxLayout by default, so the components will be displayed beneath each other (but this can be changed, of course).

```java
    @Override
    public void setupDialog(JPanel panel) {
        panel.add(new JLabel("Move this slider"));
        panel.add(new JSlider(0,100));
    }
```
> NOTE: Whenever the conditions underlying the filter have changed, making it necessary to reapply the filter in order to properly update the visualization, you should call the `filterUpdated()` method.

The second method in the dialog section of the template is [targetChanged](src/main/java/org/motiflab/plugin/MyPlugin.java#L32-L36). This method will called every time the target dataset is changed (if the tool is used in "singleTarget" mode). This will happen when the user selects a new dataset to target with the drop-down menu, or it can be forced to happen when the current target dataset is deleted (in which case a new target will be selected automatically). The method already includes a call to `filterUpdated()` in order to reapply the filter, but the plugin creator can add additional code to respond to such events, if necessary.

## The filter
The [filter section](src/main/java/org/motiflab/plugin/MyPlugin.java#L39-L72) of the template contains 6 callback methods that are always called for every installed filter before a Region is drawn. The methods are provided with the Region as an input parameter and can inspect the properties of the region before making a choice on how the Region should be drawn. 

- [shouldVisualizeRegion(Region region)](src/main/java/org/motiflab/plugin/MyPlugin.java#L42-L44) : The filter should return TRUE if it wants the region to be shown or FALSE if it wants the region to be hidden. Note that if any of the installed filters wants the region to be hidden, it will not be shown.
- [getDynamicRegionColor(Region region)](src/main/java/org/motiflab/plugin/MyPlugin.java#L47-L49) : The filter can return a specific Color to use when drawing the region or return `null` if it does not care what color the region should have. If multiple filters are installed, the color returned by the filter with the highest priority will be used. If all filters return `null`, the region's default color will be used (based on either the color of the track or the type of the region).
- [getDynamicRegionBorderColor(Region region)](src/main/java/org/motiflab/plugin/MyPlugin.java#L53-L55) : The filter can return a specific Color to use when drawing the border for the region or return `null` if it does not care what color the border should have. If multiple filters are installed, the color returned by the filter with the highest priority will be used. If all filters return `null`, the region's default border color will be used.
- [getDynamicRegionLabelColor(Region region)](src/main/java/org/motiflab/plugin/MyPlugin.java#L58-L60) : The filter can return a specific Color to use when drawing the label for the region or return `null` if it does not care what color the label should have. If multiple filters are installed, the color returned by the filter with the highest priority will be used. If all filters return `null`, the region's default label color will be used.
- [getDynamicMotifLogoColor(Region region)](src/main/java/org/motiflab/plugin/MyPlugin.java#L63-L65) : The method is called when the region represents a motif site and the motif match logo is drawn on top of the region (as shown e.g. [here](https://tare.medisin.ntnu.no/motiflab/screenshots/viz4.png)). The return value should be a Color[] array with 8 colors. The first four colors determine which colors to use for A, C, G and T, respectively, when the base letter from the motif matches the base from the DNA sequence in that position. The last 4 colors determine which colors to use for the same bases when there is a mismatch between the motif and the sequence (usually the color gray). If the filter does not care what color to use, it can return a `null` value. If multiple filters are installed, the colors returned by the filter with the highest priority will be used.

### Drawing overlays
The last callback method, [drawOverlay](src/main/java/org/motiflab/plugin/MyPlugin.java#L68-L71), does not return a value but can be used to draw additional graphics on top of the region after it has been drawn in the regular way. The method is provided with a Graphics2D object which offers various painting tools and a Rectangle defining the area occupied by the Region on screen. The other input parameters are references to the VisualizationSettings object (a giant lookup-table containing all visualization-related settings in MotifLab) and the DataTrackVisualizer object that is responsible for drawing the entire Region Dataset track. 
In order to optimize the rendering process, the `drawOverlay` method will only be called if the [overlay](src/main/java/org/motiflab/plugin/MyPlugin.java#L19) setting is set to TRUE.

The example below will draw an image (defined elsewhere) covering the full area if the region and then draw a filled oval on top of that again. The oval is half the size of the region and drawn in the region's default color. 
```java
    @Override
    public void drawOverlay(Region region, Graphics2D g, Rectangle rect, VisualizationSettings settings, DataTrackVisualizer visualizer) {
        g.drawImage(someimage, rect.x, rect.y, rect.width, rect.height, null);
        g.setColor(settings.getFeatureColor(region.getType()));
        g.fillOval(rect.x+(int)(rect.width/4), rect.y+(int)(rect.height/4), (int)(rect.width/2), (int)(rect.height/2));
    } 
```

## Additional tips

### Data updates
The [parent class of the template](src/main/java/org/motiflab/plugin/templates/RegionFilterTool_Template.java) implements a DataListener interface to monitor changes to the target dataset and reapply the filter when necessary. It also detects if new Region Datasets are added or existing datasets are deleted and updates the options availbale in the target dataset drop-down menu accordingly. If the plugin creator needs to, they can [override these callback methods](src/main/java/org/motiflab/plugin/templates/RegionFilterTool_Template.java#L320-L351) to respond to such events. Just remember to include a call to `super()` at the beginning of each overrided method.

### Loading images
Image files placed in the [images/](images) directory can easily be loaded with a call to the `getImage(<filename>)` method which returns a java.awt.Image. 
Note, however, that this method can only be used after the plugin has been initialized by MotifLab, which in effect means that it must be called from within one of the template's methods, such as [setupDialog](src/main/java/org/motiflab/plugin/MyPlugin.java#L27-L29).

## Example

The example below implements a simple filter tool that hides regions whose *score* property is lower than a threshold selected by the user with the help of a slider.
The range of the slider is from 0 to 100, so it is assumed that the score values of the regions have been normalized to this same range.
Only the two methods that need to be changed are shown.
```java
// =======================================   DIALOG   ===============================================

    JSlider slider;

    @Override
    public void setupDialog(JPanel panel) {
        slider = new JSlider(0,100,0); // create a slider widget with minimum value = 0 and maximum value = 100
        slider.addChangeListener(changeEvent -> filterUpdated()); // reapply the filter whenever the user moves the slider 
        panel.add(slider);  // add the slider to the tool dialog
    }
// =======================================   FILTER   ===============================================

    @Override
    public boolean shouldVisualizeRegion(Region region) {
        return region.getScore() > slider.getValue(); 
    }

```

# Building the plugin
To compile and package the plugin, you need the [Maven](https://maven.apache.org/) build tool. When you have finished all the steps described above, run the following command.

```bash
mvn package
```

This will create a ZIP-file under the `target` directory which contains the Java JAR-file (also in the target-directory) along with the `plugin.conf` file and other resources needed by the plugin.
If you like, you can rename the ZIP-file to have an `mlp` file suffix rather than `zip`, since "mlp" is the preferred suffix for MotifLab Plugins. (But ZIP can be used as well).
The MLP or ZIP-file can then be installed into MotifLab by selecting "Plugins..." from the "Configure" menu, pressing the "Add" button in the lower-left corner of the Plugins dialog and then selecting your file in the file browser. (If the file still has a ZIP-suffix, you have to select either "ZIP-files" or "All Files" from the "Files of Type" menu to see it.)

