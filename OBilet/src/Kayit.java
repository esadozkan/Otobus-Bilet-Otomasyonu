import database.DB_Baglanti;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class Kayit extends JFrame {

    public Kayit() {
        initUI();
    }

    private void initUI() {
        setTitle("Kayıt Ol - Çağdaş Güven");
        setSize(480, 800);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        MainPanel mainPanel = new MainPanel();  //panel
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 40, 40, 40));
        setContentPane(mainPanel);

        JPanel pnl_ust = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl_ust.setOpaque(false);
        pnl_ust.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pnl_ust.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn_geri = new JButton("← Giriş Ekranı");
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
            new Login().setVisible(true);
            this.dispose();
        });
        pnl_ust.add(btn_geri);

        JLabel lbl_logo = new JLabel(); //logo
        lbl_logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/otobus_logo.png"));
            Image img = icon.getImage().getScaledInstance(180, -1, Image.SCALE_SMOOTH);
            lbl_logo.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lbl_logo.setText("KAYIT OL");
            lbl_logo.setForeground(Color.WHITE);
            lbl_logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        }

        JLabel lbl_baslik = new JLabel("Aramıza Katılın");
        lbl_baslik.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl_baslik.setForeground(new Color(220, 230, 240));
        lbl_baslik.setAlignmentX(Component.CENTER_ALIGNMENT);

        //input alanları için
        JTextField txt_adSoyad = createStyledTextField("Adınız ve Soyadınız");
        JTextField txt_tc = createStyledTextField("11 haneli TC kimlik numaranız");
        JTextField txt_tel = createStyledTextField("05XX XXX XX XX");
        JTextField txt_kadi = createStyledTextField("Kullanıcı adı belirleyin");
        JPasswordField txt_pass = createStyledPasswordField("Güçlü bir şifre oluşturun");

        String[] cinsiyetler = {"Seçiniz...", "Erkek", "Kadın"};
        JComboBox<String> cmb_cinsiyet = new JComboBox<>(cinsiyetler);
        styleComboBox(cmb_cinsiyet);

        JButton btn_kayit = createStyledButton("Kaydı Tamamla", new Color(39, 174, 96), new Color(46, 204, 113));
        btn_kayit.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn_kayit.addActionListener(e -> {
            kayitIslemiYap(txt_adSoyad, txt_tc, txt_tel, txt_kadi, txt_pass, cmb_cinsiyet);
        });

        mainPanel.add(pnl_ust);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(lbl_logo);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(lbl_baslik);
        mainPanel.add(Box.createVerticalStrut(25));

        mainPanel.add(createInputGroup("Ad Soyad", txt_adSoyad));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createInputGroup("TC Kimlik No", txt_tc));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createInputGroup("Telefon Numarası", txt_tel));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createInputGroup("Cinsiyet", cmb_cinsiyet));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createInputGroup("Kullanıcı Adı", txt_kadi));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createInputGroup("Şifre", txt_pass));

        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(btn_kayit);
        mainPanel.add(Box.createVerticalGlue());
    }

    //methodlar
    private void kayitIslemiYap(JTextField txt_adSoyad, JTextField txt_tc, JTextField txt_tel, JTextField txt_kadi, JPasswordField txt_pass, JComboBox<String> cmb_cinsiyet) {
        String adSoyad = txt_adSoyad.getText().trim();
        String tc = txt_tc.getText().trim();
        String tel = txt_tel.getText().trim();
        String kadi = txt_kadi.getText().trim();
        String psw = new String(txt_pass.getPassword()).trim();
        String cinsiyet = (String) cmb_cinsiyet.getSelectedItem();

        if (adSoyad.isEmpty() || tc.isEmpty() || tel.isEmpty() || kadi.isEmpty() || psw.isEmpty() || "Seçiniz...".equals(cinsiyet)) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları eksiksiz doldurun!", "Eksik Bilgi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!tc.matches("\\d+") || tc.length() != 11) { // tc kontrolü
            JOptionPane.showMessageDialog(this, "TC Kimlik No 11 haneli olmalı ve sadece rakam içermelidir!", "Hatalı TC", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psMusteri = null;
        ResultSet rs = null;

        try {
            conn = DB_Baglanti.baglan();
            if(conn == null) return;

            String sqlUser = "INSERT INTO kullanicilar (kullanici_adi, sifre, rol) VALUES (?, ?, 'musteri')";
            psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, kadi);
            psUser.setString(2, psw);
            int etkilenen = psUser.executeUpdate();

            if (etkilenen > 0) {
                rs = psUser.getGeneratedKeys();
                int yeniUserId = -1;
                if (rs.next()) { yeniUserId = rs.getInt(1); }

                String sqlMusteri = "INSERT INTO musteriler (user_id, ad_soyad, tc_kimlik, telefon, cinsiyet) VALUES (?, ?, ?, ?, ?)";
                psMusteri = conn.prepareStatement(sqlMusteri);
                psMusteri.setInt(1, yeniUserId);
                psMusteri.setString(2, adSoyad);
                psMusteri.setString(3, tc);
                psMusteri.setString(4, tel);
                psMusteri.setString(5, cinsiyet);
                psMusteri.executeUpdate();

                new Login().setVisible(true);   //logine atıyo
                this.dispose();
            }

        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Bu Kullanıcı Adı veya TC zaten sistemde kayıtlı!", "Hata", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı Hatası: " + ex.getMessage());
        } finally {
            try {
                if(rs != null) rs.close();
                if(psUser != null) psUser.close();
                if(psMusteri != null) psMusteri.close();
                DB_Baglanti.kapatan(conn);
            } catch (Exception ex) {}
        }
    }
    // input alanları için
    private JPanel createInputGroup(String title, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(380, 65));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel l = new JLabel(title);
        l.setForeground(new Color(220, 220, 220));
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(3));
        p.add(field);
        return p;
    }
    // yazı için
    private JTextField createStyledTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(380, 38));
        tf.setMaximumSize(new Dimension(380, 38));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setForeground(Color.WHITE);
        tf.setBackground(new Color(255, 255, 255, 30));
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 50), 1),
                new EmptyBorder(0, 10, 0, 10)
        ));
        tf.putClientProperty("JTextField.placeholderText", placeholder);
        return tf;
    }
    //şifre
    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setPreferredSize(new Dimension(380, 38));
        pf.setMaximumSize(new Dimension(380, 38));
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setForeground(Color.WHITE);
        pf.setBackground(new Color(255, 255, 255, 30));
        pf.setCaretColor(Color.WHITE);
        pf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 50), 1),
                new EmptyBorder(0, 10, 0, 10)
        ));
        pf.putClientProperty("JTextField.placeholderText", placeholder);
        pf.putClientProperty("JPasswordField.showRevealButton", true);
        return pf;
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setPreferredSize(new Dimension(380, 38));
        box.setMaximumSize(new Dimension(380, 38));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
    // buton oluşturma
    private JButton createStyledButton(String text, Color colorNormal, Color colorHover) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) { g2.setColor(colorHover); } else { g2.setColor(colorNormal); }
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
}