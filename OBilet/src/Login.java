import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import database.DB_Baglanti;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame {

    public Login() {
        initUI();
    }

    private void initUI() {
        setTitle("Giriş Yap - Çağdaş Güven");
        setSize(450, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        MainPanel mainPanel = new MainPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 40, 40, 40));
        setContentPane(mainPanel);

        JPanel pnl_ust = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl_ust.setOpaque(false);
        pnl_ust.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pnl_ust.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn_geri = new JButton("← Ana Menü");
        btn_geri.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_geri.setForeground(new Color(200, 200, 200));
        btn_geri.setContentAreaFilled(false);
        btn_geri.setBorderPainted(false);
        btn_geri.setFocusPainted(false);
        btn_geri.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn_geri.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn_geri.setForeground(Color.WHITE); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn_geri.setForeground(new Color(200, 200, 200)); }
        });

        btn_geri.addActionListener(e -> {
            new AnaMenu().setVisible(true);
            this.dispose();
        });

        pnl_ust.add(btn_geri);

        //logo kısmı
        JLabel lbl_logo = new JLabel();
        lbl_logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/otobus_logo.png"));
            Image img = icon.getImage().getScaledInstance(280, -1, Image.SCALE_SMOOTH);
            lbl_logo.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lbl_logo.setText("ÇAĞDAŞ GÜVEN");
            lbl_logo.setForeground(Color.WHITE);
            lbl_logo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        }

        JLabel lbl_baslik = new JLabel("Hoşgeldiniz");
        lbl_baslik.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lbl_baslik.setForeground(Color.WHITE);
        lbl_baslik.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl_slogan = new JLabel("Lütfen hesabınıza giriş yapın");
        lbl_slogan.setFont((new Font("Segoe UI Light", Font.PLAIN, 15)));
        lbl_slogan.setForeground(new Color(180, 190, 200));
        lbl_slogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txt_kadi = createStyledTextField();  //input
        JPasswordField txt_sifre = createStyledPasswordField();

        JButton btn_giris = createStyledButton("GİRİŞ YAP", new Color(41, 128, 185), new Color(52, 152, 219));
        btn_giris.setAlignmentX(Component.CENTER_ALIGNMENT);

        getRootPane().setDefaultButton(btn_giris);

        btn_giris.addActionListener(e -> {
            girisKontrol(txt_kadi.getText(), new String(txt_sifre.getPassword()));
        });

        // sonradan eklenen kayıt ol butonu
        JPanel pnl_alt_link = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnl_alt_link.setOpaque(false);
        pnl_alt_link.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnl_alt_link.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pnl_alt_link.setPreferredSize(new Dimension(450, 40));

        JLabel lbl_kayit = new JLabel("<html><span style='color:#cccccc;'>Hesabın yok mu?</span> <span style='color:#3498db;'><b>Kayıt Ol</b></span></html>");
        lbl_kayit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbl_kayit.setHorizontalAlignment(SwingConstants.CENTER);

        lbl_kayit.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new Kayit().setVisible(true);
                dispose();
            }
        });
        pnl_alt_link.add(lbl_kayit);

        mainPanel.add(pnl_ust);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(lbl_logo);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(lbl_baslik);
        mainPanel.add(lbl_slogan);
        mainPanel.add(Box.createVerticalStrut(40));

        mainPanel.add(createInputGroup("Kullanıcı Adı", txt_kadi));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createInputGroup("Şifre", txt_sifre));

        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(btn_giris);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(pnl_alt_link);
        mainPanel.add(Box.createVerticalGlue());
    }

    //methodlar
    private JPanel createInputGroup(String title, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(350, 70));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel l = new JLabel(title);
        l.setForeground(new Color(220, 220, 220));
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(l);
        p.add(Box.createVerticalStrut(5));
        p.add(field);
        return p;
    }
    //yazı stili
    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(350, 40));
        tf.setMaximumSize(new Dimension(350, 40));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setForeground(Color.WHITE);
        tf.setBackground(new Color(255, 255, 255, 30));
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 50), 1),
                new EmptyBorder(0, 10, 0, 10)
        ));
        return tf;
    }
    //şifre için
    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setPreferredSize(new Dimension(350, 40));
        pf.setMaximumSize(new Dimension(350, 40));
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setForeground(Color.WHITE);
        pf.setBackground(new Color(255, 255, 255, 30));
        pf.setCaretColor(Color.WHITE);
        pf.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(255, 255, 255, 50), 1), new EmptyBorder(0, 10, 0, 10)));
        pf.putClientProperty("JPasswordField.showRevealButton", true);
        return pf;
    }
    //buton oluşturmA
    private JButton createStyledButton(String text, Color colorNormal, Color colorHover) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) { g2.setColor(colorHover); }
                else { g2.setColor(colorNormal); }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(280, 50));
        btn.setMaximumSize(new Dimension(280, 50));
        return btn;
    }

    private class MainPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, new Color(30, 45, 60), w, h, new Color(5, 5, 10));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
            g2d.setColor(new Color(255, 255, 255, 8));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(-100, -100, 300, 300);
            g2d.drawOval(w - 200, h - 200, 400, 400);
        }
    }

    //veritabanı
    private void girisKontrol(String kadi, String sifre) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT id, rol FROM kullanicilar WHERE kullanici_adi = ? AND sifre = ?";

        try {
            conn = DB_Baglanti.baglan();
            if (conn == null) return;

            ps = conn.prepareStatement(sql);
            ps.setString(1, kadi);
            ps.setString(2, sifre);
            rs = ps.executeQuery();

            if (rs.next()) {
                int loginTableId = rs.getInt("id");
                String rol = rs.getString("rol");

                if ("admin".equalsIgnoreCase(rol)) {
                    new AdminPanel().setVisible(true);
                }
                else if ("musteri".equalsIgnoreCase(rol)) {
                    String musteriSql = "SELECT id FROM musteriler WHERE user_id = ?";
                    PreparedStatement psMusteri = conn.prepareStatement(musteriSql);
                    psMusteri.setInt(1, loginTableId);
                    ResultSet rsMusteri = psMusteri.executeQuery();

                    if (rsMusteri.next()) {
                        int gercekMusteriId = rsMusteri.getInt("id");
                        new UserPanel(gercekMusteriId).setVisible(true);
                    }
                    rsMusteri.close();
                    psMusteri.close();
                }
                this.dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Hatalı kullanıcı adı veya şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQL Hatası: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                DB_Baglanti.kapatan(conn);
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}