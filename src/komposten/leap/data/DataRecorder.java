/*
 * Copyright (c) 2014 Jakob Hjelm 
 */
package komposten.leap.data;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import komposten.utilities.tools.LogUtils;
import komposten.utilities.tools.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;

/**
 * A utility program to record and save hand and finger information from a 
 * Leap Motion Controller in JSON format. The following data will be saved:
 * <ul>
 * <li>The frame id</li>
 * <li>Hand information (id, direction, palm position, palm normal, palm velocity, basis, left or right hand)</li>
 * <li>Finger information (id, direction, tip position, tip velocity, bones)</li>
 * <li>Bone information (type, length, width, direction, center, next joint, previous joint, basis)</li>
 * </ul>
 * <br />Since bone information requires a lot of space, it can be excluded from the save file.
 * @author Komposten (aka Jakob Hjelm)
 * @version 1.2.2
 * <br />Latest additions:
 * <br />- Event handling for controller (dis)connected and service started/stopped.
 * <br />- Additional hand information
 * <br />- Improved the look of the GUI (space between the buttons, separator
 * between the buttons and text area).
 * <br />- Added bones as well as the possibility to exclude bones from saves.
 * <br />- Serialises the list straight to a file, instead of via String instances.
 * <br />- Implementation of Logger to catch and log exceptions.
 */
public class DataRecorder extends JFrame implements ActionListener
{
  private static final String DIRECTORY = "data";
  private static final Color  GRAY      = new Color(230, 230, 230);
  
  private JButton   buttonStart_;
  private JButton   buttonStop_;
  private JButton   buttonSave_;
  private JButton   buttonSave2_;
  private JButton   buttonOpen_;
  private JTextArea areaInfo_;
  
  private Controller            controller_;
  private LinkedList<FrameData> frameData_;
  private boolean               collectData_;
  
  public DataRecorder()
  {
    super("LeapRecorder");

    buttonStart_ = createButton("Start/Reset");
    buttonStop_  = createButton("Stop");
    buttonSave_  = createButton("Save All");
    buttonSave2_ = createButton("Save w/o bones");
    buttonOpen_  = createButton("Open directory");
    areaInfo_    = createTextArea();
    
    JPanel buttons = new JPanel(new GridLayout(5, 1, 2, 2));
    buttons.setBackground(GRAY);
    buttons.setFocusable(false);
    buttons.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0, 0, 0, 1, Color.GRAY), new EmptyBorder(2, 2, 2, 2)));
    buttons.add(buttonStart_);
    buttons.add(buttonStop_);
    buttons.add(buttonSave_);
    buttons.add(buttonSave2_);
    buttons.add(buttonOpen_);
    
    setLayout(new GridLayout(1, 2));
    add(buttons);
    add(areaInfo_);
    pack();
    
    createController();
    
    getContentPane().setBackground(GRAY);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setAlwaysOnTop(true);
    setVisible(true);
  }
  
  
  
  private JButton createButton(String label)
  {
    JButton button = new JButton(label);
    
    button.addActionListener(this);
    button.setFocusable(false);
    
    return button;
  }
  
  
  
  private JTextArea createTextArea()
  {
    JTextArea area = new JTextArea();
    
    area.setEditable(false);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setMargin(new Insets(3, 5, 3, 3));
    area.setBackground(GRAY);
    
    return area;
  }
  
  
  
  private void createController()
  {
    controller_ = new Controller();
    frameData_  = new LinkedList<FrameData>();
    
    areaInfo_.setText("Connecting to Leap Motion Controller...");
    controller_.addListener(leapListener_);
    
    if (controller_.isConnected())
      leapListener_.onConnect(controller_);
  }



  @Override
  public void actionPerformed(ActionEvent event)
  {
    if (event.getSource() == buttonStart_)
    {
      collectData_ = true;
      frameData_.clear();
      areaInfo_ .setText("Recording...");
    }
    else if (event.getSource() == buttonStop_)
    {
      if (collectData_)
      {
        collectData_ = false;
        areaInfo_ .setText("Stopped recording.\nFrames: " + frameData_.size());
      }
    }
    else if (event.getSource() == buttonSave_)
    {
      areaInfo_ .setText("Saving data...");
      saveData(false);
      areaInfo_ .append("\nData saved.");
    }
    else if (event.getSource() == buttonSave2_)
    {
      areaInfo_ .setText("Saving data...");
      saveData(true);
      areaInfo_ .append("\nData saved.");
    }
    else if (event.getSource() == buttonOpen_)
    {
      try
      {
        Desktop.getDesktop().open(new File(DIRECTORY));
      }
      catch (IOException e)
      {
        JOptionPane.showMessageDialog(this, e.getMessage(), "An exception occured!", JOptionPane.ERROR_MESSAGE);
        LogUtils.log(Logger.WRITEERROR, "DataRecorder", "Unable to open the directory!", e, false);
      }
    }
  }
  
  
  
  private Listener leapListener_ = new Listener()
  {
    @Override
    public void onConnect(Controller controller)
    {
      areaInfo_.setText("Leap Motion Controller connected!");
    };

    @Override
    public void onDisconnect(Controller controller)
    {
      areaInfo_.setText("Controller disconnected!");
    };
    
    @Override
    public void onServiceConnect(Controller arg0)
    {
      areaInfo_.setText("Leap Service started!");
    }
    
    @Override
    public void onServiceDisconnect(Controller arg0)
    {
      areaInfo_.setText("Leap Service stopped!");
    }
    
    @Override
    public void onFrame(Controller controller)
    {
      try
      {
        if (collectData_)
        {
          areaInfo_.setText("Recording...\nFrames: " + frameData_.size());
          frameData_.add(new FrameData(controller.frame()));
        }
      }
      catch (Exception e)
      {
        String msg = "An exception occured while processing a frame, terminating program!";
        JOptionPane.showMessageDialog(DataRecorder.this, msg, "An exception occured!", JOptionPane.ERROR_MESSAGE);
        LogUtils.log("EXCEPTION", "DataRecorder", msg, e, false);
        
        System.exit(1);
      }
    };
  };
  
  
  
  private void saveData(boolean excludeBones)
  {
    Calendar c = Calendar.getInstance();
  
    c.setTime(new Date(System.currentTimeMillis()));
  
    String time = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + 
            c.get(Calendar.DATE) + " " + c.get(Calendar.HOUR_OF_DAY) + "." + 
            c.get(Calendar.MINUTE) + "." + c.get(Calendar.SECOND);
    
    File       file   = new File(DIRECTORY + "/leapdata" + time + ".json");
    Gson       gson   = new GsonBuilder().setExclusionStrategies(new BoneExcludor()).create();
    FileWriter writer = null;
    
    BoneExcludor.shouldExclude = excludeBones;
    
    try
    {
      if (!file.exists())
      {
        if (file.getParentFile() != null)
          file.getParentFile().mkdirs();
        file.createNewFile();
      }
      writer = new FileWriter(file, true);
      
      gson.toJson(frameData_, new TypeToken<LinkedList<FrameData>>(){}.getType(), writer);
    }
    catch (IOException e)
    {
      JOptionPane.showMessageDialog(this, e.getMessage(), "An exception occured!", JOptionPane.ERROR_MESSAGE);
      LogUtils.log(Logger.WRITEERROR, "DataRecorder", "An exception occured when saving!", e, false);
    }
    
    try
    {
      if (writer != null)
        writer.close();
    }
    catch (IOException e)
    {
      LogUtils.log("ERROR", "DataRecorder", "Could not close the writer!", e, false);
    }
  }
  
  

  public static void main(String[] args)
  {
    DataRecorder recorder = null;
    try
    {
      recorder = new DataRecorder();
    }
    catch (Exception e)
    {
      String msg = "An exception occured, terminating program!";
      JOptionPane.showMessageDialog(recorder, msg, "An exception occured!", JOptionPane.ERROR_MESSAGE);
      LogUtils.log("EXCEPTION", "DataRecorder", msg, e, false);
      
      System.exit(1);
    }
  }
}
