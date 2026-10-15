package busbooking;

import busbooking.ui.AdminLoginFrame;
import busbooking.ui.ManageRouteFrame;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        // ตั้งค่า Font สำหรับภาษาไทย

        UIManager.put(
                "OptionPane.messageFont",
                new Font("Tahoma", Font.PLAIN, 16)
        );

        UIManager.put(
                "OptionPane.buttonFont",
                new Font("Tahoma", Font.PLAIN, 14)
        );

        UIManager.put(
                "OptionPane.titleFont",
                new Font("Tahoma", Font.BOLD, 16)
        );

        // ตั้งค่า Font สำหรับหน้าต่าง Swing
     
        UIManager.put(
                "Label.font",
                new Font("Tahoma", Font.PLAIN, 16)
        );

        UIManager.put(
                "Button.font",
                new Font("Tahoma", Font.PLAIN, 16)
        );

        UIManager.put(
                "TextField.font",
                new Font("Tahoma", Font.PLAIN, 16)
        );

        UIManager.put(
                "PasswordField.font",
                new Font("Tahoma", Font.PLAIN, 16)
        );
          //สีปุ่มok
          UIManager.put(
                "Button.focus",
                new Color(0,0,0,0)
        );

        // เปิดหน้า Admin Login
      
        SwingUtilities.invokeLater(() -> {
            new AdminLoginFrame();
           
        });
    }
}