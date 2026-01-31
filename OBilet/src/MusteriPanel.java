import database.DB_Baglanti;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class MusteriPanel extends JPanel {

    // tablo
    private JTable tablo;
    private DefaultTableModel model;
    private JTextField txt_ara;
    private TableRowSorter<DefaultTableModel> sorter;

    // form elemanları
    private JTextField txt_ad_soyad;
    private JTextField txt_tc;
    private JTextField txt_tel;
    private JComboBox<String> cmb_cinsiyet;

    // seçili kayıt bilgileri için
    private int seciliMusteriID = -1;
    private int seciliUserID = -1; // Silme işlemi için Login hesabını da bilmemiz gerek

    // renkler - PREMIUM PALET
    private final Color COLOR_TABLE_HEADER = new Color(44, 62, 80);
    private final Color COLOR_TABLE_ROW_1 = new Color(52, 73, 94);
    private final Color COLOR_TABLE_ROW_2 = new Color(60, 80, 100);
    private final Color COLOR_TEXT = new Color(236, 240, 241);

    private final Color COLOR_BTN_YESIL = new Color(39, 174, 96);
    private final Color COLOR_BTN_MAVI = new Color(41, 128, 185);
    private final Color COLOR_BTN_KIRMIZI = new Color(192, 57, 43);

    // GHOSTING ÇÖZÜMÜ İÇİN EKLENEN MAT RENK
    private final Color COLOR_INPUT_BG = new Color(60, 70, 80);

    public MusteriPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false); // Arka plan şeffaf (Gradient görünsün)
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // solpanel-arama
        JPanel sol_panel = new JPanel(new BorderLayout(0, 10));
        sol_panel.setOpaque(false); // Şeffaf

        // FIX: Manuel boyanan arama paneli
        JPanel arama_paneli = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 40, 50, 200)); // Yarı saydam koyu
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        arama_paneli.setOpaque(false); // Standart boyama kapalı
        arama_paneli.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lbl_ara = new JLabel("Müşteri Ara:  ");
        lbl_ara.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_ara.setForeground(COLOR_TEXT);

        // FIX: Yeni text field oluşturucu kullanıldı
        txt_ara = createStyledTextField();
        txt_ara.putClientProperty("JTextField.placeholderText", "TC Kimlik veya İsim giriniz...");

        arama_paneli.add(lbl_ara, BorderLayout.WEST);
        arama_paneli.add(txt_ara, BorderLayout.CENTER);

        sol_panel.add(arama_paneli, BorderLayout.NORTH);

        // tablo
        JPanel tablo_paneli = new JPanel(new BorderLayout());
        tablo_paneli.setOpaque(false);
        // Modern ince çerçeve
        tablo_paneli.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255,255,255,50)),
                "Müşteri Listesi",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                COLOR_TEXT));

        String[] kolonlar = {"ID", "User ID", "Ad Soyad", "TC No", "Telefon", "Cinsiyet"};
        model = new DefaultTableModel(null, kolonlar) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablo = new JTable(model);

        tablo.getColumnModel().getColumn(1).setMinWidth(0);
        tablo.getColumnModel().getColumn(1).setMaxWidth(0);
        tablo.getColumnModel().getColumn(1).setWidth(0);
        //arama
        sorter = new TableRowSorter<>(model);
        tablo.setRowSorter(sorter);

        txt_ara.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = txt_ara.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else try { sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); } catch (Exception ex) {}
            }
        });

        styleTable(tablo);

        // tıklama aksiyonu
        tablo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablo.getSelectedRow();
                if (row != -1) {

                    int modelRow = tablo.convertRowIndexToModel(row);

                    seciliMusteriID = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
                    seciliUserID = Integer.parseInt(model.getValueAt(modelRow, 1).toString()); // gizli kolon

                    txt_ad_soyad.setText(model.getValueAt(modelRow, 2).toString());
                    txt_tc.setText(model.getValueAt(modelRow, 3).toString());
                    txt_tel.setText(model.getValueAt(modelRow, 4).toString());
                    cmb_cinsiyet.setSelectedItem(model.getValueAt(modelRow, 5).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablo);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(COLOR_TABLE_ROW_1);
        scrollPane.setOpaque(false);
        tablo_paneli.add(scrollPane, BorderLayout.CENTER);
        sol_panel.add(tablo_paneli, BorderLayout.CENTER);
        add(sol_panel, BorderLayout.CENTER);


        // sağpanel
        // FIX: Sağ panel manuel boyama ile güncellendi
        JPanel sag_panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 40, 50, 200)); // Yarı saydam koyu
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        sag_panel.setOpaque(false); // Standart boyama kapalı
        sag_panel.setPreferredSize(new Dimension(340, 0));
        sag_panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; int row = 0;

        JLabel lbl_form_baslik = new JLabel(" Müşteri Kartı");
        lbl_form_baslik.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl_form_baslik.setForeground(COLOR_TEXT);
        lbl_form_baslik.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/costumer.png"));
            lbl_form_baslik.setIcon(new ImageIcon(icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}

        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 30, 0);
        sag_panel.add(lbl_form_baslik, gbc);
        gbc.insets = new Insets(5, 0, 5, 0);

        gbc.gridy = row++; sag_panel.add(createLabel("Ad Soyad"), gbc);
        // FIX: Özel Text Field
        txt_ad_soyad = createStyledTextField();
        gbc.gridy = row++; sag_panel.add(txt_ad_soyad, gbc);

        gbc.gridy = row++; sag_panel.add(createLabel("TC Kimlik No"), gbc);
        // FIX: Özel Text Field
        txt_tc = createStyledTextField();
        gbc.gridy = row++; sag_panel.add(txt_tc, gbc);

        gbc.gridy = row++; sag_panel.add(createLabel("Telefon"), gbc);
        // FIX: Özel Text Field
        txt_tel = createStyledTextField();
        gbc.gridy = row++; sag_panel.add(txt_tel, gbc);

        gbc.gridy = row++; sag_panel.add(createLabel("Cinsiyet"), gbc);
        cmb_cinsiyet = new JComboBox<>(new String[]{"Erkek", "Kadın"});
        cmb_cinsiyet.setPreferredSize(new Dimension(0, 35));
        cmb_cinsiyet.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // ComboBox MAT Boyama
        cmb_cinsiyet.setBackground(COLOR_INPUT_BG);
        cmb_cinsiyet.setForeground(Color.WHITE);
        ((JComponent) cmb_cinsiyet.getRenderer()).setOpaque(true);

        gbc.gridy = row++; sag_panel.add(cmb_cinsiyet, gbc);

        // butonlar
        JPanel buton_panel = new JPanel(new GridLayout(1, 3, 10, 0));
        buton_panel.setOpaque(false);

        JButton btn_kaydet = createButton("Ekle", COLOR_BTN_YESIL);
        JButton btn_guncelle = createButton("Güncelle", COLOR_BTN_MAVI);
        JButton btn_sil = createButton("Sil", COLOR_BTN_KIRMIZI);

        // aksiyonalr
        btn_kaydet.addActionListener(e -> musteriEkle());
        btn_guncelle.addActionListener(e -> musteriGuncelle());
        btn_sil.addActionListener(e -> musteriSil());

        buton_panel.add(btn_kaydet);
        buton_panel.add(btn_guncelle);
        buton_panel.add(btn_sil);

        JButton btn_temizle = new JButton("Formu Temizle");
        btn_temizle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn_temizle.setForeground(new Color(180, 180, 180));
        btn_temizle.setContentAreaFilled(false);
        btn_temizle.setBorderPainted(false);
        btn_temizle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_temizle.addActionListener(e -> formuTemizle());

        gbc.gridy = row++; gbc.insets = new Insets(30, 0, 0, 0);
        sag_panel.add(buton_panel, gbc);
        gbc.gridy = row++; gbc.insets = new Insets(10, 0, 0, 0);
        sag_panel.add(btn_temizle, gbc);

        gbc.gridy = row++; gbc.weighty = 1.0;
        sag_panel.add(Box.createGlue(), gbc);
        add(sag_panel, BorderLayout.EAST);

        musterileriListele();
    }

    private void musterileriListele() {
        model.setRowCount(0);
        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();
            String sql = "SELECT * FROM musteriler";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("user_id"),   //gizli olcak
                        rs.getString("ad_soyad"),
                        rs.getString("tc_kimlik"),
                        rs.getString("telefon"),
                        rs.getString("cinsiyet")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DB_Baglanti.kapatan(conn);
        }
    }

    private void musteriEkle() {
        String adSoyad = txt_ad_soyad.getText();
        String tc = txt_tc.getText();
        String tel = txt_tel.getText();
        String cinsiyet = (String) cmb_cinsiyet.getSelectedItem();

        if (tc.length() != 11) {
            JOptionPane.showMessageDialog(this, "TC 11 hane olmalı!");
            return;
        }

        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();

            String sqlUser = "INSERT INTO kullanicilar (kullanici_adi, sifre, rol) VALUES (?, '1234', 'musteri')";
            PreparedStatement psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, tc);
            psUser.executeUpdate();

            ResultSet rs = psUser.getGeneratedKeys();
            int yeniUserId = -1;
            if(rs.next()) yeniUserId = rs.getInt(1);

            //profil oluşturma
            String sqlMusteri = "INSERT INTO musteriler (user_id, ad_soyad, tc_kimlik, telefon, cinsiyet) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psMusteri = conn.prepareStatement(sqlMusteri);
            psMusteri.setInt(1, yeniUserId);
            psMusteri.setString(2, adSoyad);
            psMusteri.setString(3, tc);
            psMusteri.setString(4, tel);
            psMusteri.setString(5, cinsiyet);
            psMusteri.executeUpdate();

            JOptionPane.showMessageDialog(this, "Müşteri ve Hesabı (Şifre: 1234) Oluşturuldu!");
            musterileriListele();
            formuTemizle();

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Bu TC Kimlik / Kullanıcı zaten kayıtlı!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
        } finally {
            DB_Baglanti.kapatan(conn);
        }
    }

    private void musteriGuncelle() {
        if(seciliMusteriID == -1) return;

        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();
            String sql = "UPDATE musteriler SET ad_soyad=?, tc_kimlik=?, telefon=?, cinsiyet=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txt_ad_soyad.getText());
            ps.setString(2, txt_tc.getText());
            ps.setString(3, txt_tel.getText());
            ps.setString(4, (String) cmb_cinsiyet.getSelectedItem());
            ps.setInt(5, seciliMusteriID);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Müşteri Güncellendi!");
            musterileriListele();
            formuTemizle();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
        } finally {
            DB_Baglanti.kapatan(conn);
        }
    }

    private void musteriSil() {
        if(seciliMusteriID == -1 || seciliUserID == -1) {
            JOptionPane.showMessageDialog(this, "Silinecek müşteriyi seçiniz!");
            return;
        }

        int onay = JOptionPane.showConfirmDialog(this,
                "Bu müşteriyi silerseniz Hesabı ve Biletleri de silinir!\nEmin misiniz?",
                "Kritik Uyarı", JOptionPane.YES_NO_OPTION);

        if(onay == JOptionPane.YES_OPTION) {
            Connection conn = null;
            try {
                conn = DB_Baglanti.baglan();

                String sql = "DELETE FROM kullanicilar WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, seciliUserID);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Müşteri ve Tüm Verileri Silindi!");
                musterileriListele();
                formuTemizle();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
            } finally {
                DB_Baglanti.kapatan(conn);
            }
        }
    }

    private void formuTemizle() {
        txt_ad_soyad.setText("");
        txt_tc.setText("");
        txt_tel.setText("");
        seciliMusteriID = -1;
        seciliUserID = -1;
        tablo.clearSelection();
    }

    //methodlar (Premium Stil)
    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(COLOR_TABLE_ROW_1);
        table.setForeground(COLOR_TEXT);
        table.setGridColor(new Color(80, 90, 100));
        table.setShowVerticalLines(false); // Modern görünüm

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COLOR_TABLE_HEADER);
        header.setForeground(new Color(230, 230, 230));
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(0, 40));

        // Özel satır renklendirme
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? COLOR_TABLE_ROW_1 : COLOR_TABLE_ROW_2);
                } else {
                    c.setBackground(COLOR_BTN_MAVI); // Seçili satır
                }
                c.setForeground(COLOR_TEXT);
                return c;
            }
        });
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(COLOR_TEXT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    // YENİ EKLENEN METHOD: Ghosting Çözen Text Field
    private JTextField createStyledTextField() {
        JTextField txt = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Arka planı MAT renk ile boya (Silgi görevi görür)
                g2.setColor(COLOR_INPUT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                // Çerçeve
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
        txt.setBackground(COLOR_INPUT_BG);
        txt.setOpaque(false); // Custom painting için false, ama içi dolu.
        txt.setBorder(new EmptyBorder(0, 10, 0, 10));

        return txt;
    }

    private JButton createButton(String text, Color bg) {
        // Yuvarlak ve modern buton
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 40));
        return btn;
    }
}