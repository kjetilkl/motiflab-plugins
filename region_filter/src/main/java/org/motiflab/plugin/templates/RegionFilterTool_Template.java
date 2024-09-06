package org.motiflab.plugin.templates;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import motiflab.engine.DataListener;
import motiflab.engine.ExecutionError;
import motiflab.engine.MotifLabClient;
import motiflab.engine.MotifLabEngine;
import motiflab.engine.Plugin;
import motiflab.engine.SystemError;
import motiflab.engine.data.Data;
import motiflab.engine.data.Region;
import motiflab.engine.data.RegionDataset;
import motiflab.gui.DataTrackVisualizer;
import motiflab.gui.MotifLabGUI;
import motiflab.gui.RegionVisualizationFilter;
import motiflab.gui.VisualizationSettings;

/**
 *
 * @author kjetikl
 */
public abstract class RegionFilterTool_Template implements Plugin, RegionVisualizationFilter, DataListener {
  
    private FilterDialog dialog = null;     
    private MotifLabGUI gui = null;
    private MotifLabEngine engine = null;
    private RegionDataset targetDataset = null;
    
    
    public RegionFilterTool_Template() {
        
    }
    
    @Override
    public void initializePlugin(MotifLabEngine engine) throws ExecutionError, SystemError {
       this.engine = engine;
    }
    
    @Override
    public void initializePluginFromClient(MotifLabClient client) throws ExecutionError, SystemError {
        if (client instanceof MotifLabGUI) {
            this.gui = (MotifLabGUI)client;  
            JMenuItem item = new JMenuItem(getPluginName());
            if (!gui.addToToolsMenu(item)) throw new ExecutionError("Unable to add '"+getPluginName()+"' to Tools menu");
            Object iconProperty = (String)engine.getPluginProperty(getPluginName(), "icon");
            String iconFile = (iconProperty!=null)?iconProperty.toString():"icon.png";
            Image image = getImage(iconFile);
            if (image!=null) {
                item.setIcon( new ImageIcon(image) );
            } else {
                URL imageUrl = getClass().getResource(iconFile);
                ImageIcon icon = (imageUrl!=null)?new ImageIcon(imageUrl):null;
                if (icon!=null) item.setIcon(icon);
            }
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showDialog();
                }
            });            
        }
    }
    
    @Override
    public void uninstallPlugin(MotifLabEngine engine) {
       engine.removeDataListener(this);         
       if (dialog!=null) {
           dialog.setVisible(false);
           dialog.dispose();
           dialog = null;
       }
       // remove the tool menu item from the Tools menu       
       if (gui instanceof MotifLabGUI) {
           gui.removeFromMenu("Tools", getPluginName());
           gui.removeRegionVisualizationFilter(this);
       }      
       gui = null;
       engine = null;
    }    
                                 

    @Override
    public void shutdownPlugin() { // shut down the plugin 
        engine.removeDataListener(this);
        if (dialog!=null) {
           dialog.setVisible(false);
           dialog.dispose();
           dialog = null;
        }  
       if (gui instanceof MotifLabGUI) {
           gui.removeFromMenu("Tools", getPluginName());
           gui.removeRegionVisualizationFilter(this);
       }            
        engine.removeDataListener(engine);
    }

    @Override
    public void shutdown() { // shut down the filter  
        engine.removeDataListener(this);
        if (dialog!=null) {
           dialog.setVisible(false);
           dialog.dispose();
           dialog = null;
        }        
    }

    @Override
    public String getRegionVisualizationFilterName() {
        return getPluginName();
    }
       
    public MotifLabEngine getEngine() {
        return engine;
    }
    
    public MotifLabGUI getGUI() {
        return gui;
    }
    
    public void filterUpdated() {
        gui.redraw();
    }
    
    public abstract boolean hasSingleTarget();
    public abstract boolean isPersistent();
    public abstract int getFilterPriority();
    public abstract void targetChanged(RegionDataset target);    
    public abstract void setupDialog(JPanel panel);
    
    
    
    public RegionDataset getTargetDataset() {
        return targetDataset;
    }
    
    @Override
    public String[] getTargets() {
        if (hasSingleTarget()) {
            if (targetDataset!=null) return new String[]{targetDataset.getName()};
            else return new String[0]; // no target for the filter
        } else return null; // applies to all tracks
    }
    
    @Override
    public boolean appliesToTrack(String featureName) {
        if (hasSingleTarget()) {
            if (targetDataset!=null) return targetDataset.getName().equals(featureName);
            else return false;
        } else return true;       
    }

    @Override
    public void drawOverlay(Region region, Graphics2D g, Rectangle rect, VisualizationSettings settings, DataTrackVisualizer visualizer) {
    }

    @Override
    public boolean drawsOverlay() {
        return false;
    }

    @Override
    public int getPriority() {
        return getFilterPriority();
    }


    @Override
    public void sequenceRepainted(String sequenceName, String featureName) {

    }   
    
     
    private void showDialog() {
        if (dialog == null) dialog = new FilterDialog(gui);
        int height = dialog.getHeight();
        int width = dialog.getWidth();
        java.awt.Dimension size = gui.getGUIFrame().getSize();
        dialog.setLocation((int)((size.width-width)/2),(int)((size.height-height)/2));
        dialog.setVisible(true);   
    }    
    
     private class FilterDialog extends javax.swing.JDialog {
        private JPanel topPanel;
        private JPanel mainPanel;
        private JPanel bottomPanel;
        private JPanel controlsPanel;
        private JComboBox<String> tracksCombobox;
        private JButton closeButton;
        private JCheckBox removeOnClose;

        public FilterDialog(MotifLabGUI guiclient) {
            super(guiclient.getFrame(), getPluginName(), false);
            this.setResizable(true);
            initComponents();
            engine.addDataListener(RegionFilterTool_Template.this);
            gui.addRegionVisualizationFilter(RegionFilterTool_Template.this);    
        }
        
        /** Returns ComboboxModels containing all NumericVariables and MotifNumericMaps as well as a default constant integer */
        private DefaultComboBoxModel getDataTracks() {
            ArrayList<String>candidateNames=new ArrayList<String>();
            for (Data item:engine.getAllDataItemsOfType(RegionDataset.class)) {
                candidateNames.add(item.getName());
            } 
            Collections.sort(candidateNames);
            String[] entries=new String[candidateNames.size()];
            entries=candidateNames.toArray(entries);
            DefaultComboBoxModel model=new DefaultComboBoxModel(entries);
            return model;       
        }



        private void closeButtonPressed(java.awt.event.ActionEvent evt) {                                        
            setVisible(false);
            if (!isPersistent() || (removeOnClose!=null && removeOnClose.isSelected())) {
                gui.removeRegionVisualizationFilter(RegionFilterTool_Template.this);
                gui.redraw();
                this.dispose();
            }
        }     
                 

        @SuppressWarnings("unchecked")                   
        private void initComponents() {        
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            topPanel = new JPanel();     
            mainPanel = new JPanel();
            bottomPanel = new JPanel();
            controlsPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());

            controlsPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createCompoundBorder(
                       BorderFactory.createEmptyBorder(2, 2, 2, 2),
                       BorderFactory.createEtchedBorder()) ,
                     BorderFactory.createEmptyBorder(6, 6, 6, 6)
            ));
            controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
            setupDialog(controlsPanel);
            mainPanel.add(controlsPanel,BorderLayout.CENTER);  
           
            closeButton = new JButton("Close");
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    closeButtonPressed(evt);
                }
            });
            
            if (isPersistent()) {
                bottomPanel.setLayout(new BorderLayout());
                removeOnClose = new JCheckBox("Remove on close   ", false);
                bottomPanel.add(removeOnClose, BorderLayout.WEST); 
                bottomPanel.add(closeButton, BorderLayout.EAST);
            } else {
                bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                bottomPanel.add(closeButton);
            }
                       
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(3,6,3,6));
            mainPanel.add(bottomPanel,BorderLayout.SOUTH);
            
            if (hasSingleTarget()) {
                DefaultComboBoxModel model = getDataTracks();
                tracksCombobox = new JComboBox<>(model);
                tracksCombobox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String trackName = (String)tracksCombobox.getSelectedItem();
                        targetDataset = (RegionDataset)engine.getDataItem(trackName, RegionDataset.class);
                        targetChanged(targetDataset);
                    }                    
                });
                if (model.getSize()>0) tracksCombobox.setSelectedIndex(0);
                topPanel.add(tracksCombobox);
                mainPanel.add(topPanel,BorderLayout.NORTH);
            }            
            getContentPane().add(mainPanel);
            pack();
        }                      
    }
     
    private void updateRegionDatasetSelection() {
        if (dialog!=null && dialog.tracksCombobox!=null) {
            String selected = (String)dialog.tracksCombobox.getSelectedItem();
            DefaultComboBoxModel model = dialog.getDataTracks();
            dialog.tracksCombobox.setModel(model);
            if (model.getSize()>0) {
                if (model.getIndexOf(selected)>=0) dialog.tracksCombobox.setSelectedItem(selected);
                else dialog.tracksCombobox.setSelectedIndex(0);
            }
            engine.logMessage("updateRegionDatasetSelection");
        }
    }

    @Override
    public void dataAdded(Data data) {
        engine.logMessage("Data added: "+data);
        if (data instanceof RegionDataset && hasSingleTarget()) {
           updateRegionDatasetSelection();
        }
    }

    @Override
    public void dataRemoved(Data data) {
        if (data == targetDataset) {
            targetDataset = null;
        }        
        if (data instanceof RegionDataset && hasSingleTarget()) {
            updateRegionDatasetSelection();
        }
    }

    @Override
    public void dataAddedToSet(Data parentDataset, Data child) {}
    
    @Override
    public void dataRemovedFromSet(Data parentDataset, Data child) {}

    @Override
    public void dataUpdated(Data data) {
        if (data == targetDataset) filterUpdated();
    }

    @Override
    public void dataUpdate(Data oldvalue, Data newvalue) {
        if (oldvalue == targetDataset) filterUpdated();
    }
    
    /**
     * Loads an image found in the "images" directory bundled with the JAR.
     * NB! This function cannot be called before the plugin has been property initialized!
     * @param filename
     * @return 
     */
    public Image getImage(String filename) {
        String pluginDir = engine.getPluginDirectory(this);
        if (pluginDir==null) return null;
        String fullpath=pluginDir+File.separator+"images"+File.separator+filename;              
        try (InputStream is = new BufferedInputStream(new FileInputStream(fullpath))) {
            if (is == null) return null;
            BufferedImage image = ImageIO.read(is);
            return image;
        } catch (IOException e) {

        }   
        return null;
    } 
     
}
