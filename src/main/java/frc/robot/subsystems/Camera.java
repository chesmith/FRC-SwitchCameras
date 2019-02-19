/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.cscore.HttpCamera;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.cscore.VideoSource;
import edu.wpi.cscore.HttpCamera.HttpCameraKind;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Camera system to allow toggling between a front-facing camera and a back-facing camera
 */
public class Camera extends Subsystem {
  HttpCamera frontCamera;
  UsbCamera backCamera;
  VideoSink videoSink;

  boolean showFrontCamera = true;

  public Camera() {
    //NOTE: There is a bug in CameraServer where cameras constructed before the first  getInstance() call aren't published,
    //      so it's VERY IMPORTANT to call getInstance() prior to constructing the first camera
    //      https://www.chiefdelphi.com/t/stream-from-jetson-to-rio/343525/2
    CameraServer cs = CameraServer.getInstance();

    ///////////////////////////
    // *** Following can be used for one Axis camera in front and one USB camera in back
    frontCamera = new HttpCamera("frontCamera", "http://10.31.3.12/mjpg/video.mjpg", HttpCameraKind.kMJPGStreamer);
    frontCamera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);

    backCamera = cs.startAutomaticCapture("backCamera", 0);
    backCamera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);

    videoSink = cs.addSwitchedCamera("Switched camera");

    //TODO: If we want to override the video mode of a USB camera, it must be done after the addSwitchedCamera call, which defaults to 160x120 @ 30 fps
    //backCamera.setVideoMode(VideoMode.PixelFormat.kMJPEG, 320, 240, 15);
    ////////////////////////////

    /////////////////////////////
    // *** Following cam be used for Rapsberry Pi cameras (both as HttpCamera)
    // frontCamera = new HttpCamera("frontCamera", "http://frcvision.local:1181/stream.mjpg", HttpCameraKind.kMJPGStreamer);
    // frontCamera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);

    // backCamera = new HttpCamera("backCamera", "http://frcvision.local:1182/stream.mjpg", HttpCameraKind.kMJPGStreamer);
    // backCamera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);

    // videoSink = cs.addSwitchedCamera("Switched camera");
    ////////////////////////////////

    videoSink.setSource(frontCamera); // ensure we show the front camera when we start
  }

  public void toggle() {
    showFrontCamera = !showFrontCamera;
    if(showFrontCamera) {
      videoSink.setSource(frontCamera);
      SmartDashboard.putString("camera","front");
    }
    else {
      videoSink.setSource(backCamera);
      SmartDashboard.putString("camera","back");
    }
  }
  
  @Override
  public void initDefaultCommand() {
    // no default command
  }
}
