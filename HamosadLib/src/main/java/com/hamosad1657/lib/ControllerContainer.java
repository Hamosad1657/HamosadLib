package com.hamosad1657.lib;

import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj.PS4Controller.Button;

public class ControllerContainer {
    public static PS4Controller controller = new PS4Controller(RobotConstants.ControllerConstants.controllerID);
    public static JoystickButton leftTrigger = new JoystickButton(controller, Button.kL2.value);
    public static JoystickButton rightTrigger = new JoystickButton(controller, Button.kR2.value);
    public static JoystickButton triangleButton = new JoystickButton(controller, Button.kTriangle.value);
    public static JoystickButton circlebButton = new JoystickButton(controller, Button.kCircle.value);
    public static JoystickButton crossButton = new JoystickButton(controller, Button.kCross.value);
    public static JoystickButton squarebButton = new JoystickButton(controller, Button.kSquare.value);
}
