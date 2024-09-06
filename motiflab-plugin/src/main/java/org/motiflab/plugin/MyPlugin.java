package org.motiflab.plugin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.*;
import javax.swing.event.*;
import motiflab.engine.data.Region;
import motiflab.engine.data.RegionDataset;
import motiflab.gui.DataTrackVisualizer;
import motiflab.gui.VisualizationSettings;

public class MyPlugin extends org.motiflab.plugin.templates.RegionFilterTool_Template {
    
    private final String pluginName    = "My plugin"; // IMPORTANT: this name must be identical to the name used in the "plugin.conf" file !!!
    
    private final boolean singleTarget = true;  // apply filter to a single chosen region track (true) or to all region tracks (false)
    private final boolean persistant   = false; // keep the filter after closing the dialog (true) or remove the filter when closing (false)
    private final boolean overlay      = false;  // set to TRUE if you want to draw stuff on top of the regions with the drawOverlay method below  
    
    private final int filterPriority   = FILTER_PRIORITY_HIGH;
       
// =======================================   DIALOG   ===============================================      

    
    @Override
    public void setupDialog(JPanel panel) {
        // use this method to add GUI components to the filter dialog
    }

    @Override
    public void targetChanged(RegionDataset target) {
        // Respond to the user selecting a new 
        // dataset to target with this filter
        filterUpdated(); // this function must be called every time the filter is updated in order to reapply it
    }


// =======================================   FILTER   ===============================================

    @Override
    public boolean shouldVisualizeRegion(Region region) {
        return true;
    }

    @Override
    public Color getDynamicRegionColor(Region region) {
        return null; // Returning NULL means that the filter does not care what color to use
    }

    
    @Override
    public Color getDynamicRegionBorderColor(Region region) {
        return null; // Returning NULL means that the filter does not care what color to use
    }    

    @Override
    public Color getDynamicRegionLabelColor(Region region) {
        return null; // Returning NULL means that the filter does not care what color to use
    }

    @Override
    public Color[] getDynamicMotifLogoColors(Region region) {
        return null; // Returning NULL means that the filter does not care what color to use
    }
    
    @Override
    public void drawOverlay(Region region, Graphics2D g, Rectangle rect, VisualizationSettings settings, DataTrackVisualizer visualizer) {
        // Remember to set the "overlay" setting to TRUE at the top of the page if you want to draw stuff here!
        
    }    
    
// =======================================   DON'T CHANGE ANYTHING BELOW HERE   ===============================================  
    
    @Override
    public String getPluginName() {
        return pluginName;
    }
    
    @Override
    public boolean hasSingleTarget() {
        return singleTarget;
    } 
    
    @Override
    public boolean isPersistant() {
        return persistant;
    }       
    
    @Override
    public int getFilterPriority() {
        return filterPriority;
    }    
    
    @Override
    public boolean drawsOverlay() {
        return overlay;
    }        
     
    
}
