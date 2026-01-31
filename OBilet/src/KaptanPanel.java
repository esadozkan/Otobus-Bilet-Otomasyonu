import database.DB_Baglanti;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KaptanPanel extends JPanel {

    // Tablo
    private JTable tablo;
    private DefaultTableModel model;

    // Form Elemanları
    private JTextField txt_ad_soyad;
    private JTextField txt_tel;
    private JComboBox<String> cmb_ehliyet;

    // Seçili kaptan ID
    private int seciliKaptanID = -1;

    // --- RENK PALETİ (PREMIUM) ---
    private final Color COLOR_TABLE_HEADER = new Color(44, 62, 80);
    private final Color COLOR_TABLE_ROW_1 = new Color(52, 73, 94);
    private final Color COLOR_TABLE_ROW_2 = new Color(60, 80, 100);
    private final Color COLOR_TEXT = new Color(236, 240, 241);

    private final Color COLOR_BTN_YESIL = new Color(39, 174, 96);
    private final Color COLOR_BTN_MAVI = new Color(41, 128, 185);
    private final Color COLOR_BTN_KIRMIZI = new Color(231, 76, 60);

    // EKLENDİ: GÖLGELENMEYİ ENGELLEYEN MAT RENK
    private final Color COLOR_INPUT_BG = new Color(60, 70, 80);

    public KaptanPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false); // Arka plandaki gradient görünsün
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- SOL PANEL (LİSTE) ---
        JPanel sol_panel = new JPanel(new BorderLayout());
        sol_panel.setOpaque(false);
        // Modern Çerçeve
        sol_panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255,255,255,50)),
                "Kayıtlı Kaptanlar",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                COLOR_TEXT));

        // Tablo Yapısı
        String[] kolonlar = {"ID", "Ad Soyad", "Telefon", "Ehliyet", "Durum"};
        model = new DefaultTableModel(null, kolonlar) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablo = new JTable(model);
        styleTable(tablo);

        // Tıklama Olayı
        tablo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablo.getSelectedRow();
                if (row != -1) {
                    seciliKaptanID = Integer.parseInt(model.getValueAt(row, 0).toString());
                    txt_ad_soyad.setText(model.getValueAt(row, 1).toString());
                    txt_tel.setText(model.getValueAt(row, 2).toString());
                    cmb_ehliyet.setSelectedItem(model.getValueAt(row, 3).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablo);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(COLOR_TABLE_ROW_1);
        scrollPane.setOpaque(false); // Scrollpane şeffaf
        sol_panel.add(scrollPane, BorderLayout.CENTER);

        add(sol_panel, BorderLayout.CENTER);

        // --- SAĞ PANEL (FORM) ---
        // DÜZELTME: Standart JPanel yerine paintComponent override edildi.
        // Bu sayede arka plan her frame'de temizlenir, ghosting olmaz.
        JPanel sag_panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // Önceki boyamayı hazırla
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 40, 50, 200)); // Yarı saydam koyu
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Köşeleri yuvarlat
                g2.dispose();
            }
        };
        sag_panel.setOpaque(false); // Önemli: Şeffaflık için false olmalı
        sag_panel.setPreferredSize(new Dimension(320, 0));
        sag_panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        int row = 0;

        // Başlık
        JLabel lbl_form_baslik = new JLabel("Kaptan İşlemleri");
        lbl_form_baslik.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl_form_baslik.setForeground(COLOR_TEXT);
        lbl_form_baslik.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/captain.png"));
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            lbl_form_baslik.setIcon(new ImageIcon(img));
        } catch (Exception e) {}

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 30, 0);
        sag_panel.add(lbl_form_baslik, gbc);
        gbc.insets = new Insets(5, 0, 5, 0);

        // Form Alanları
        gbc.gridy = row++; sag_panel.add(createLabel("Ad Soyad"), gbc);
        txt_ad_soyad = createStyledTextField();
        gbc.gridy = row++; sag_panel.add(txt_ad_soyad, gbc);

        gbc.gridy = row++; sag_panel.add(createLabel("Telefon"), gbc);
        txt_tel = createStyledTextField();
        gbc.gridy = row++; sag_panel.add(txt_tel, gbc);

        gbc.gridy = row++; sag_panel.add(createLabel("Ehliyet Sınıfı"), gbc);
        String[] ehliyetler = {"E Sınıfı ", "D Sınıfı ", "DE Sınıfı"};
        cmb_ehliyet = new JComboBox<>(ehliyetler);
        cmb_ehliyet.setPreferredSize(new Dimension(0, 40));
        cmb_ehliyet.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // ComboBox MAT arka plan ayarı
        cmb_ehliyet.setBackground(COLOR_INPUT_BG);
        cmb_ehliyet.setForeground(Color.WHITE);
        ((JComponent) cmb_ehliyet.getRenderer()).setOpaque(true);

        gbc.gridy = row++; sag_panel.add(cmb_ehliyet, gbc);

        // Butonlar
        JPanel buton_panel = new JPanel(new GridLayout(1, 3, 10, 0));
        buton_panel.setOpaque(false);

        JButton btn_kaydet = createStyledButton("Ekle", COLOR_BTN_YESIL);
        JButton btn_guncelle = createStyledButton("Güncelle", COLOR_BTN_MAVI);
        JButton btn_sil = createStyledButton("Sil", COLOR_BTN_KIRMIZI);

        // Aksiyonlar
        btn_kaydet.addActionListener(e -> kaptanEkle());
        btn_guncelle.addActionListener(e -> kaptanGuncelle());
        btn_sil.addActionListener(e -> kaptanSil());

        buton_panel.add(btn_kaydet);
        buton_panel.add(btn_guncelle);
        buton_panel.add(btn_sil);

        // Temizle Butonu
        JButton btn_temizle = new JButton("Seçimi Temizle");
        btn_temizle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn_temizle.setForeground(new Color(180, 180, 180));
        btn_temizle.setContentAreaFilled(false);
        btn_temizle.setBorderPainted(false);
        btn_temizle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_temizle.addActionListener(e -> formuTemizle());

        gbc.gridy = row++;
        gbc.insets = new Insets(30, 0, 0, 0);
        sag_panel.add(buton_panel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 0, 0);
        sag_panel.add(btn_temizle, gbc);

        gbc.gridy = row++; gbc.weighty = 1.0;
        sag_panel.add(Box.createGlue(), gbc);
        add(sag_panel, BorderLayout.EAST);

        // Listeyi Doldur
        kaptanlariListele();
    }

    // --- LOGIC KISIMLARI (SQL SORGULARI AYNEN KORUNDU) ---
    public void kaptanlariListele() {
        model.setRowCount(0);
        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();

            // SQL sorgusu aynen korundu: Günlük sefer kontrolü
            String sql = "SELECT k.id, k.ad_soyad, k.telefon, k.ehliyet, " +
                    "(SELECT COUNT(*) FROM seferler s " +
                    " WHERE s.kaptan_id = k.id AND STR_TO_DATE(s.tarih, '%d.%m.%Y') = CURDATE()) as gunluk_sefer_sayisi " +
                    "FROM kaptanlar k";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int seferSayisi = rs.getInt("gunluk_sefer_sayisi");
                String anlikDurum = (seferSayisi > 0) ? "SEFERDE" : "MÜSAİT";

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("ad_soyad"),
                        rs.getString("telefon"),
                        rs.getString("ehliyet"),
                        anlikDurum
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DB_Baglanti.kapatan(conn);
        }
    }

    private void kaptanEkle() {
        if (txt_ad_soyad.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ad Soyad boş olamaz!");
            return;
        }

        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();
            String sql = "INSERT INTO kaptanlar (ad_soyad, telefon, ehliyet, durum) VALUES (?, ?, ?, 'Müsait')";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txt_ad_soyad.getText());
            ps.setString(2, txt_tel.getText());
            ps.setString(3, (String) cmb_ehliyet.getSelectedItem());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kaptan Başarıyla Eklendi!");
            kaptanlariListele();
            formuTemizle();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
        } finally {
            DB_Baglanti.kapatan(conn);
        }
    }

    private void kaptanGuncelle() {
        if (seciliKaptanID == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen tablodan bir kaptan seçiniz!");
            return;
        }

        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();
            String sql = "UPDATE kaptanlar SET ad_soyad=?, telefon=?, ehliyet=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txt_ad_soyad.getText());
            ps.setString(2, txt_tel.getText());
            ps.setString(3, (String) cmb_ehliyet.getSelectedItem());
            ps.setInt(4, seciliKaptanID);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kaptan Güncellendi!");
            kaptanlariListele();
            formuTemizle();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
        } finally {
            DB_Baglanti.kapatan(conn);
        }
    }

    private void kaptanSil() {
        if (seciliKaptanID == -1) {
            JOptionPane.showMessageDialog(this, "Silinecek kaptanı seçiniz!");
            return;
        }

        int onay = JOptionPane.showConfirmDialog(this, "Bu kaptanı silmek istediğinize emin misiniz?", "Silme Onayı", JOptionPane.YES_NO_OPTION);
        if (onay == JOptionPane.YES_OPTION) {
            Connection conn = null;
            try {
                conn = DB_Baglanti.baglan();
                String sql = "DELETE FROM kaptanlar WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, seciliKaptanID);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Kaptan Silindi!");
                kaptanlariListele();
                formuTemizle();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Hata: Bu kaptan şu an bir seferde görevli olabilir!\n" + e.getMessage());
            } finally {
                DB_Baglanti.kapatan(conn);
            }
        }
    }

    private void formuTemizle() {
        txt_ad_soyad.setText("");
        txt_tel.setText("");
        cmb_ehliyet.setSelectedIndex(0);
        seciliKaptanID = -1;
        tablo.clearSelection();
    }

    // --- PREMIUM TASARIM METOTLARI ---
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(200, 200, 200));
        return lbl;
    }

    // GÖLGELENME (GHOSTING) ÇÖZÜMÜ BURADA:
    private JTextField createStyledTextField() {
        JTextField txt = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // DÜZELTME: Artık arka plan ŞEFFAF DEĞİL, MAT KOYU RENK
                // Bu sayede eski yazıların üstü kapanır, ghosting biter.
                g2.setColor(COLOR_INPUT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                // Çerçeveyi çiz
                g2.setColor(new Color(255, 255, 255, 50));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        txt.setPreferredSize(new Dimension(0, 35));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);

        // Arka planı kodla boyadığımız için buraya gerek yok ama tutarlılık için:
        txt.setBackground(COLOR_INPUT_BG);

        txt.setOpaque(false); // Köşelerin yuvarlak kalması için false, ama içi dolu boyanıyor.
        txt.setBorder(new EmptyBorder(0, 10, 0, 10));

        return txt;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20)); // Yuvarlak buton
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 35));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(COLOR_TABLE_ROW_1);
        table.setForeground(COLOR_TEXT);
        table.setGridColor(new Color(80, 90, 100));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COLOR_TABLE_HEADER);
        header.setForeground(new Color(230, 230, 230));
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(0, 40));

        // Satır renklendirme ve Durum Renklendirme
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? COLOR_TABLE_ROW_1 : COLOR_TABLE_ROW_2);
                } else {
                    c.setBackground(COLOR_BTN_MAVI); // Seçili satır rengi
                }

                // Durum Kolonu Renklendirme (4. indeks)
                if (column == 4) {
                    String status = (String) value;
                    if ("MÜSAİT".equals(status)) {
                        setForeground(COLOR_BTN_YESIL);
                    } else {
                        setForeground(new Color(255, 100, 100)); // Kırmızı ton
                    }
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    setForeground(COLOR_TEXT);
                    setFont(new Font("Segoe UI", Font.PLAIN, 14));
                }
                return c;
            }
        });
    }
}