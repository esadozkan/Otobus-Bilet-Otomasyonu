import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class AnaMenu extends JFrame {

    public AnaMenu() {
        initUI();
    }

    private void initUI() {
        //pencere ayarlrı
        setTitle("Çağdaş Güven Firması - Hoşgeldiniz");
        setSize(1000, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        MainPanel mainPanel = new MainPanel();  //anapanel
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 0, 0, 0));  // üstten 40px
        setContentPane(mainPanel);

        JLabel lbl_banner = new JLabel();
        lbl_banner.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/otobus_logo.png"));
            Image img = icon.getImage().getScaledInstance(750, -1, Image.SCALE_SMOOTH); // ölçekelme ayarı
            lbl_banner.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lbl_banner.setText("görsel yüklenmedi");
            lbl_banner.setForeground(Color.RED);
        }

        JLabel lbl_subtitle = new JLabel("Keyifli Yolculuğun Tek Adresi");
        lbl_subtitle.setFont(new Font("Segoe UI Light", Font.PLAIN, 24));
        lbl_subtitle.setForeground(new Color(220, 230, 240));
        lbl_subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(1200, 100));

        JButton btn_giris = createStyledButton("Giriş Yap", new Color(41, 128, 185), new Color(52, 152, 219));
        JButton btn_kayit = createStyledButton("Kayıt Ol", new Color(39, 174, 96), new Color(46, 204, 113));

        // aksiyonlar
        btn_giris.addActionListener(e -> {
            new Login().setVisible(true);
            this.dispose();
        });

        btn_kayit.addActionListener(e -> {
            new Kayit().setVisible(true);
            this.dispose();
        });

        buttonPanel.add(btn_giris);
        buttonPanel.add(btn_kayit);

        // sırayla yerleştirme
        mainPanel.add(lbl_banner);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(lbl_subtitle);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalGlue());
    }

    //methodlar
    private JButton createStyledButton(String text, Color colorNormal, Color colorHover) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(colorHover);
                } else {
                    g2.setColor(colorNormal);
                }

                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30)); // 30px yuvarlaklaştırma(butonları)
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Dimension d = new Dimension(220, 60);
        btn.setPreferredSize(d);

        return btn;
    }

    private class MainPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            GradientPaint gp = new GradientPaint(0, 0, new Color(30, 45, 60), w, h, new Color(5, 5, 10));   // gradyanlı arkaplan
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);

            // çiizmler için
            g2d.setColor(new Color(255, 255, 255, 8));
            g2d.setStroke(new BasicStroke(2));

            g2d.drawOval(-150, -150, 500, 500);
            g2d.drawOval(w - 400, h - 400, 800, 800);

            g2d.setColor(new Color(52, 152, 219, 20));
            g2d.drawLine(0, h/2 + 100, w, h/2 - 50);
        }
    }
}