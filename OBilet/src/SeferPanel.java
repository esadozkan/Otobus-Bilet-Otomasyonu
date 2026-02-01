import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
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
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SeferPanel extends JPanel {

    // Tablo
    private JTable tablo;
    private DefaultTableModel model;

    // Filtreleme
    private JComboBox<String> cmb_filtre;
    private JLabel lbl_tablo_baslik;

    // Form Elemanları
    private JComboBox<String> cmb_nereden;
    private JComboBox<String> cmb_nereye;

    // --- TARİH ARTIK DATEPICKER ---
    private DatePicker datePicker;

    private JSpinner spinner_saat;
    private JComboBox<ComboItem> cmb_arac;
    private JComboBox<ComboItem> cmb_kaptan;

    // Fiyat
    private JTextField txt_fiyat_input;
    private JLabel lbl_fiyat;

    // Seçili sefer ID
    private int seciliSeferID = -1;

    // --- RENK PALETİ (MAT & KOYU) ---
    private final Color COLOR_TABLE_HEADER = new Color(44, 62, 80);
    private final Color COLOR_TABLE_ROW_1 = new Color(52, 73, 94);
    private final Color COLOR_TABLE_ROW_2 = new Color(60, 80, 100);
    private final Color COLOR_TEXT = new Color(236, 240, 241);
    private final Color COLOR_BTN_YESIL = new Color(39, 174, 96);
    private final Color COLOR_BTN_KIRMIZI = new Color(231, 76, 60);
    private final Color COLOR_INPUT_BG = new Color(60, 70, 80); // Koyu Mat Arka Plan
    private final Color COLOR_BTN_ORANGE = new Color(241, 82, 6);

    public SeferPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false); // Arka plandaki gradient görünsün
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- SOL PANEL (LİSTE) ---
        JPanel sol_panel = new JPanel(new BorderLayout());
        sol_panel.setOpaque(false);
        sol_panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255,255,255,50)),
                "Sefer Listesi",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                COLOR_TEXT));

        // Filtre Bar
        JPanel pnl_header = new JPanel(new BorderLayout());
        pnl_header.setOpaque(false);
        pnl_header.setBorder(new EmptyBorder(5, 10, 10, 10));

        lbl_tablo_baslik = new JLabel("  Gelecek Seferler");
        lbl_tablo_baslik.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl_tablo_baslik.setForeground(COLOR_TEXT);

        String[] filtreler = {"Gelecek Seferler", "Geçmiş Seferler", "Tüm Seferler"};
        cmb_filtre = new JComboBox<>(filtreler);
        cmb_filtre.setPreferredSize(new Dimension(150, 35));

        // ComboBox Stili (Mat arka plan)
        cmb_filtre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cmb_filtre.setBackground(new Color(236, 240, 241));
        ((JComponent) cmb_filtre.getRenderer()).setOpaque(true);

        cmb_filtre.addActionListener(e -> seferleriListele());

        pnl_header.add(lbl_tablo_baslik, BorderLayout.WEST);
        pnl_header.add(cmb_filtre, BorderLayout.EAST);
        sol_panel.add(pnl_header, BorderLayout.NORTH);

        // Tablo Yapısı
        String[] kolonlar = {"ID", "Güzergah", "Tarih", "Saat", "Araç", "Kaptan", "Fiyat"};
        model = new DefaultTableModel(null, kolonlar) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablo = new JTable(model);
        styleTable(tablo);

        tablo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablo.getSelectedRow();
                if(row != -1) seciliSeferID = Integer.parseInt(model.getValueAt(row, 0).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablo);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(COLOR_TABLE_ROW_1);
        scrollPane.setOpaque(false);
        sol_panel.add(scrollPane, BorderLayout.CENTER);

        add(sol_panel, BorderLayout.CENTER);

        // --- SAĞ PANEL (PLANLAMA FORMU) ---
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
        sag_panel.setPreferredSize(new Dimension(340, 0));
        sag_panel.setOpaque(false);
        sag_panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 2, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; int row = 0;

        // Başlık
        JLabel lbl_form_baslik = new JLabel("Sefer Planla");
        lbl_form_baslik.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl_form_baslik.setForeground(COLOR_TEXT);
        lbl_form_baslik.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/travel.png"));
            lbl_form_baslik.setIcon(new ImageIcon(icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}

        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 20, 0);
        sag_panel.add(lbl_form_baslik, gbc);

        // Form
        gbc.insets = new Insets(5, 0, 5, 0);

        gbc.gridy = row++; sag_panel.add(createLabel("Nereden"), gbc);
        cmb_nereden = new JComboBox<>(getSehirler()); styleCombo(cmb_nereden);
        gbc.gridy = row++; sag_panel.add(cmb_nereden, gbc);

        gbc.gridy = row++; sag_panel.add(createLabel("Nereye"), gbc);
        cmb_nereye = new JComboBox<>(getSehirler()); styleCombo(cmb_nereye);
        gbc.gridy = row++; sag_panel.add(cmb_nereye, gbc);

        // --- TARİH KUTUSU (LGoodDatePicker) ---
        gbc.gridy = row++; sag_panel.add(createLabel("Tarih"), gbc);

        datePicker = createDarkDatePicker(); // DÜZELTİLMİŞ METOT
        gbc.gridy = row++; sag_panel.add(datePicker, gbc);

        // Saat
        gbc.gridy = row++; sag_panel.add(createLabel("Saat"), gbc);
        spinner_saat = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spinner_saat, "HH:mm");
        spinner_saat.setEditor(timeEditor);
        spinner_saat.setPreferredSize(new Dimension(0, 35));
        spinner_saat.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JFormattedTextField tf = timeEditor.getTextField();
        tf.setBackground(COLOR_INPUT_BG);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);

        gbc.gridy = row++; sag_panel.add(spinner_saat, gbc);

        gbc.gridy = row++; sag_panel.add(createLabel("Araç Seç"), gbc);
        cmb_arac = new JComboBox<>(); styleCombo(cmb_arac);
        gbc.gridy = row++; sag_panel.add(cmb_arac, gbc);

        gbc.gridy = row++; sag_panel.add(createLabel("Kaptan Seç"), gbc);
        cmb_kaptan = new JComboBox<>(); styleCombo(cmb_kaptan);
        gbc.gridy = row++; sag_panel.add(cmb_kaptan, gbc);

        // --- FİYAT ALANI ---
        gbc.gridy = row++; sag_panel.add(createLabel("Bilet Fiyatı"), gbc);
        txt_fiyat_input = createStyledTextField();
        txt_fiyat_input.setText("500");
        txt_fiyat_input.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridy = row++; sag_panel.add(txt_fiyat_input, gbc);

        // Dinamik Fiyat Etiketi
        lbl_fiyat = new JLabel("500.00 TL");
        lbl_fiyat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl_fiyat.setForeground(COLOR_BTN_YESIL);
        lbl_fiyat.setHorizontalAlignment(SwingConstants.CENTER);
        lbl_fiyat.setBorder(new LineBorder(new Color(255,255,255,30)));
        lbl_fiyat.setPreferredSize(new Dimension(0, 35));

        txt_fiyat_input.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePrice(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePrice(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePrice(); }
        });

        gbc.gridy = row++; sag_panel.add(lbl_fiyat, gbc);

        // Butonlar
        JPanel buton_panel = new JPanel(new GridLayout(1, 2, 10, 0));
        buton_panel.setOpaque(false);
        JButton btn_kaydet = createStyledButton("Planla", COLOR_BTN_YESIL);
        JButton btn_sil = createStyledButton("İptal Et", COLOR_BTN_KIRMIZI);

        btn_kaydet.addActionListener(e -> seferEkle());
        btn_sil.addActionListener(e -> seferSil());

        buton_panel.add(btn_kaydet);
        buton_panel.add(btn_sil);

        gbc.gridy = row++; gbc.insets = new Insets(20, 0, 0, 0);
        sag_panel.add(buton_panel, gbc);

        JButton btn_temizle = new JButton("Formu Temizle");
        btn_temizle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn_temizle.setForeground(new Color(180, 180, 180));
        btn_temizle.setContentAreaFilled(false);
        btn_temizle.setBorderPainted(false);
        btn_temizle.addActionListener(e -> formuTemizle());

        gbc.gridy = row++; sag_panel.add(btn_temizle, gbc);
        gbc.gridy = row++; gbc.weighty = 1.0; sag_panel.add(Box.createGlue(), gbc);

        add(sag_panel, BorderLayout.EAST);

        // Verileri Yükle
        araclariGetir(); kaptanlariGetir(); seferleriListele();
    }

    // --- LOGIC KISMI ---
    private void seferleriListele() {
        model.setRowCount(0);
        String secim = (String) cmb_filtre.getSelectedItem();
        if (secim == null) secim = "Gelecek Seferler";
        lbl_tablo_baslik.setText("  " + secim);
        String sqlWhere = "";
        if ("Gelecek Seferler".equals(secim)) sqlWhere = "WHERE STR_TO_DATE(s.tarih, '%d.%m.%Y') >= CURDATE() ";
        else if ("Geçmiş Seferler".equals(secim)) sqlWhere = "WHERE STR_TO_DATE(s.tarih, '%d.%m.%Y') < CURDATE() ";

        try (Connection conn = DB_Baglanti.baglan()) {
            String sql = "SELECT s.id, s.nereden, s.nereye, s.tarih, s.saat, s.fiyat, " +
                    "k.ad_soyad, a.plaka, a.marka_model FROM seferler s " +
                    "LEFT JOIN kaptanlar k ON s.kaptan_id = k.id " +
                    "LEFT JOIN araclar a ON s.arac_id = a.id " + sqlWhere +
                    "ORDER BY STR_TO_DATE(s.tarih, '%d.%m.%Y') ASC, s.saat ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("nereden") + " -> " + rs.getString("nereye"),
                        rs.getString("tarih"), rs.getString("saat"),
                        rs.getString("plaka") + " (" + rs.getString("marka_model") + ")",
                        rs.getString("ad_soyad"), rs.getString("fiyat") + " TL"
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void seferEkle() {
        String nereden = (String) cmb_nereden.getSelectedItem();
        String nereye = (String) cmb_nereye.getSelectedItem();
        if (nereden.equals(nereye)) { JOptionPane.showMessageDialog(this, "Başlangıç ve Bitiş aynı olamaz!"); return; }

        int girilenFiyat = 0;
        try { girilenFiyat = Integer.parseInt(txt_fiyat_input.getText().trim()); } catch (Exception e) { return; }
        if (girilenFiyat < 250) { JOptionPane.showMessageDialog(this, "Minimum 250 TL olmalı!"); return; }

        // --- TARİH ALMA (LGoodDatePicker) ---
        java.time.LocalDate selectedDate = datePicker.getDate();
        if(selectedDate == null) { JOptionPane.showMessageDialog(this, "Tarih seçiniz!"); return; }
        String tarihStr = selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");
        String saatStr = timeSdf.format((Date) spinner_saat.getValue());

        ComboItem secilenArac = (ComboItem) cmb_arac.getSelectedItem();
        ComboItem secilenKaptan = (ComboItem) cmb_kaptan.getSelectedItem();

        if (secilenArac == null || secilenKaptan == null) return;

        try (Connection conn = DB_Baglanti.baglan()) {
            PreparedStatement psK = conn.prepareStatement("SELECT COUNT(*) FROM seferler WHERE kaptan_id = ? AND tarih = ?");
            psK.setInt(1, secilenKaptan.getValue()); psK.setString(2, tarihStr);
            ResultSet rsK = psK.executeQuery();
            if(rsK.next() && rsK.getInt(1) > 0) { JOptionPane.showMessageDialog(this, "Bu kaptan o tarihte dolu!"); return; }

            PreparedStatement psA = conn.prepareStatement("SELECT COUNT(*) FROM seferler WHERE arac_id = ? AND tarih = ?");
            psA.setInt(1, secilenArac.getValue()); psA.setString(2, tarihStr);
            ResultSet rsA = psA.executeQuery();
            if(rsA.next() && rsA.getInt(1) > 0) { JOptionPane.showMessageDialog(this, "Bu araç o tarihte dolu!"); return; }

            PreparedStatement ps = conn.prepareStatement("INSERT INTO seferler (nereden, nereye, tarih, saat, arac_id, kaptan_id, fiyat) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, nereden); ps.setString(2, nereye); ps.setString(3, tarihStr);
            ps.setString(4, saatStr); ps.setInt(5, secilenArac.getValue());
            ps.setInt(6, secilenKaptan.getValue()); ps.setInt(7, girilenFiyat);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sefer Planlandı!");
            seferleriListele(); formuTemizle();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void seferSil() {
        if(seciliSeferID == -1) return;
        if(JOptionPane.showConfirmDialog(this, "İptal edilsin mi?", "Onay", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DB_Baglanti.baglan()) {
                conn.prepareStatement("DELETE FROM seferler WHERE id=" + seciliSeferID).executeUpdate();
                seferleriListele(); formuTemizle();
            } catch (Exception e) {}
        }
    }

    private void araclariGetir() {
        cmb_arac.removeAllItems();
        try (Connection conn = DB_Baglanti.baglan()) {
            ResultSet rs = conn.prepareStatement("SELECT id, plaka, marka_model FROM araclar").executeQuery();
            while(rs.next()) cmb_arac.addItem(new ComboItem(rs.getString("plaka") + " (" + rs.getString("marka_model") + ")", rs.getInt("id")));
        } catch(Exception e) {}
    }
    private void kaptanlariGetir() {
        cmb_kaptan.removeAllItems();
        try (Connection conn = DB_Baglanti.baglan()) {
            ResultSet rs = conn.prepareStatement("SELECT id, ad_soyad FROM kaptanlar").executeQuery();
            while(rs.next()) cmb_kaptan.addItem(new ComboItem(rs.getString("ad_soyad"), rs.getInt("id")));
        } catch(Exception e) {}
    }
    private void formuTemizle() { txt_fiyat_input.setText("500"); seciliSeferID = -1; tablo.clearSelection(); datePicker.setDateToToday(); }
    private void updatePrice() { try { lbl_fiyat.setText(txt_fiyat_input.getText() + ".00 TL"); } catch(Exception e){} }

    class ComboItem {
        private String key; private int value;
        public ComboItem(String k, int v) { key=k; value=v; }
        public String toString() { return key; }
        public int getValue() { return value; }
    }
    private String[] getSehirler() { return new String[] {"Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin", "Aydın", "Balıkesir",
            "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli",
            "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari",
            "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir", "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir",
            "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir",
            "Niğde", "Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat",
            "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman",
            "Kırıkkale", "Batman", "Şırnak", "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye", "Düzce"}; }

    // --- STYLING (MAT KOYU) ---
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(200, 200, 200));
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField txt = new JTextField(20);
        txt.setPreferredSize(new Dimension(0, 35));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        txt.setBackground(COLOR_INPUT_BG);
        txt.setOpaque(true);
        txt.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(255, 255, 255, 50)), new EmptyBorder(0, 10, 0, 10)));
        return txt;
    }

    private void styleCombo(JComboBox box) {
        box.setPreferredSize(new Dimension(0, 35));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBackground(COLOR_INPUT_BG);
        box.setForeground(Color.WHITE);
        ((JComponent) box.getRenderer()).setOpaque(true);
    }

    private JButton createStyledButton(String text, Color bg) {
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
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 40));
        return btn;
    }

    // --- DATEPICKER OLUŞTURUCU (UserPanel ile AYNI - HATASIZ) ---
    private DatePicker createDarkDatePicker() {
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra("dd.MM.yyyy");
        settings.setAllowKeyboardEditing(false);

        Color darkInputBg = COLOR_INPUT_BG;
        Color darkCalendarBg = new Color(52, 73, 94);
        Color orangeAccent = COLOR_BTN_ORANGE;
        Color whiteText = Color.WHITE;

        // Input Alanı
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundValidDate, darkInputBg);
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundInvalidDate, darkInputBg);
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundVetoedDate, darkInputBg);

        // Takvim Genel
        settings.setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel, darkCalendarBg);
        settings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearNavigationButtons, new Color(44, 62, 80));

        // Seçim
        settings.setColor(DatePickerSettings.DateArea.CalendarBackgroundSelectedDate, orangeAccent);
        settings.setColor(DatePickerSettings.DateArea.CalendarBorderSelectedDate, orangeAccent);

        // Hata veren satırlar kaldırıldı (CalendarTextNormal vb.)

        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        settings.setFontValidDate(font);
        settings.setFontCalendarDateLabels(font);

        DatePicker dp = new DatePicker(settings);
        dp.setPreferredSize(new Dimension(150, 40));
        dp.getComponentDateTextField().setBackground(darkInputBg);
        dp.getComponentDateTextField().setForeground(whiteText);
        dp.getComponentDateTextField().setCaretColor(whiteText);
        dp.getComponentDateTextField().setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        dp.setDateToToday();

        JButton btn = dp.getComponentToggleCalendarButton();
        btn.setText("📅");
        btn.setBackground(new Color(44, 62, 80));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);

        return dp;
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
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? COLOR_TABLE_ROW_1 : COLOR_TABLE_ROW_2);
                else c.setBackground(new Color(41, 128, 185));
                return c;
            }
        });
    }
}