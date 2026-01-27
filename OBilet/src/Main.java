import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // 1. Temayı yüklüyorum ve kontrol ediyorum
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.out.println("Tema hatası! Kütüphane eklenmedi.");
        }

        // splash screen
        SplashScreen splash = new SplashScreen();
        splash.oynat();

        // başlangıç yerimiz
        SwingUtilities.invokeLater(() -> {
            AnaMenu anaEkran = new AnaMenu();
            anaEkran.setVisible(true);
        });
    }
}