import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import database.DB_Baglanti;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class UserPanel extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    private BiletlerimPanel pnlBiletlerim;
    private BiletAlPanel pnlBiletAl;

    private List<JButton> sidebarButtons = new ArrayList<>();
    private JButton currentActiveButton = null;

    private JButton btn_biletal;

    private int currentUserId;
    private String currentUserAdSoyad = "Değerli Yolcumuz";

    // --- RENK PALETİ (PREMIUM) ---
    private final Color COLOR_SIDEBAR_BG = new Color(30, 40, 50, 240);
    private final Color COLOR_BTN_NORMAL = new Color(30, 40, 50, 0);
    private final Color COLOR_BTN_HOVER = new Color(52, 73, 94);
    private final Color COLOR_BTN_ACTIVE = new Color(41, 128, 185);
    private final Color COLOR_TEXT = new Color(236, 240, 241);
    private final Color COLOR_BTN_ORANGE = new Color(241, 82, 6);
    private final Color COLOR_INPUT_BG = new Color(60, 70, 80); // MAT INPUT RENGİ

    public UserPanel(int userId) {
        this.currentUserId = userId;
        kullaniciBilgileriniGetir();
        initUI();
    }

    private void initUI() {
        setTitle("Müşteri Paneli - Çağdaş Güven");
        setSize(1280, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ana kapsayıcı (Gradient Arka Plan)
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        // profil
        JLabel lbl_icon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/userrr.png"));
            Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            lbl_icon.setIcon(new ImageIcon(img));
        } catch (Exception e) {}
        lbl_icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl_user = new JLabel(currentUserAdSoyad);
        lbl_user.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl_user.setForeground(Color.WHITE);
        lbl_user.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(lbl_icon);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(lbl_user);
        sidebar.add(Box.createVerticalStrut(50));

        // butonlar
        JButton btn_dash = createMenuButton(" Gösterge Paneli", "/icons/dashboard.png");
        btn_dash.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "dashboard");
            setButtonActive(btn_dash);
        });

        btn_biletal = createMenuButton(" Bilet Al", "/icons/travel.png");
        btn_biletal.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "biletal");
            setButtonActive(btn_biletal);
        });

        JButton btn_biletlerim = createMenuButton(" Biletlerim", "/icons/booking.png");
        btn_biletlerim.addActionListener(e -> {
            if (pnlBiletlerim != null) {
                pnlBiletlerim.biletleriGetir();
            }
            cardLayout.show(mainContentPanel, "biletlerim");
            setButtonActive(btn_biletlerim);
        });

        JButton btn_profil = createMenuButton(" Profilim", "/icons/costumer.png");
        btn_profil.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "profil");
            setButtonActive(btn_profil);
        });

        sidebar.add(btn_dash); sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btn_biletal); sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btn_biletlerim); sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btn_profil);

        // --- GÜVENLİ ÇIKIŞ BUTONU ---
        sidebar.add(Box.createVerticalGlue());

        JButton btn_cikis = new JButton(" Güvenli Çıkış");
        btn_cikis.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn_cikis.setForeground(new Color(231, 76, 60));
        btn_cikis.setBackground(COLOR_BTN_NORMAL);
        btn_cikis.setBorder(new EmptyBorder(10, 10, 10, 10));
        btn_cikis.setFocusPainted(false);
        btn_cikis.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_cikis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn_cikis.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn_cikis.setContentAreaFilled(true); // Mat
        btn_cikis.setOpaque(true);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/logout.png"));
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            btn_cikis.setIcon(new ImageIcon(img));
            btn_cikis.setIconTextGap(15);
        } catch (Exception e) {}

        btn_cikis.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_cikis.setBackground(new Color(192, 57, 43));
                btn_cikis.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_cikis.setBackground(COLOR_BTN_NORMAL);
                btn_cikis.setForeground(new Color(231, 76, 60));
            }
        });

        btn_cikis.addActionListener(e -> {
            this.dispose();
            new Login().setVisible(true);
        });
        sidebar.add(btn_cikis);

        // sağpanel
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setOpaque(false);

        mainContentPanel.add(createUserDashboard(), "dashboard");

        pnlBiletAl = new BiletAlPanel(false, currentUserId);
        mainContentPanel.add(pnlBiletAl, "biletal");

        pnlBiletlerim = new BiletlerimPanel(currentUserId);
        mainContentPanel.add(pnlBiletlerim, "biletlerim");

        mainContentPanel.add(new ProfilPanel(currentUserId), "profil");

        add(sidebar, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        setButtonActive(btn_dash);
    }

    private void setButtonActive(JButton activeBtn) {
        currentActiveButton = activeBtn;
        for (JButton btn : sidebarButtons) {
            btn.setBackground(COLOR_BTN_NORMAL);
            btn.setForeground(new Color(220, 220, 220));
        }
        activeBtn.setBackground(COLOR_BTN_ACTIVE);
        activeBtn.setForeground(Color.WHITE);
    }

    private void kullaniciBilgileriniGetir() {
        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();
            String sql = "SELECT ad_soyad FROM musteriler WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                currentUserAdSoyad = rs.getString("ad_soyad");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DB_Baglanti.kapatan(conn);
        }
    }

    private JPanel createUserDashboard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        // Karşılama
        JPanel pnl_welcome = createTransparentPanel();
        pnl_welcome.setBorder(new EmptyBorder(20, 30, 20, 30));
        JLabel lbl_welcome = new JLabel("Hoşgeldiniz, " + currentUserAdSoyad);
        lbl_welcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl_welcome.setForeground(COLOR_TEXT);
        pnl_welcome.add(lbl_welcome, BorderLayout.CENTER);
        gbc.gridy = row++;
        panel.add(pnl_welcome, gbc);

        // Arama Paneli
        JPanel pnl_search = createTransparentPanel();
        pnl_search.setLayout(new GridBagLayout());
        pnl_search.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints searchGbc = new GridBagConstraints();
        searchGbc.insets = new Insets(0, 10, 0, 10);
        searchGbc.fill = GridBagConstraints.HORIZONTAL;

        final JComboBox<String> cmb_nereden = new JComboBox<>(getSehirler());
        searchGbc.gridx = 0;
        pnl_search.add(createLabel("Nereden"), searchGbc);
        styleComboBox(cmb_nereden);
        searchGbc.gridy = 1;
        pnl_search.add(cmb_nereden, searchGbc);

        searchGbc.gridx = 1; searchGbc.gridy = 1;
        JLabel lbl_oklar = new JLabel();
        lbl_oklar.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon iconSwap = new ImageIcon(getClass().getResource("/icons/swap.png"));
            Image imgSwap = iconSwap.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            lbl_oklar.setIcon(new ImageIcon(imgSwap));
        } catch (Exception e) {
            lbl_oklar.setText("<->");
            lbl_oklar.setForeground(Color.WHITE);
        }
        pnl_search.add(lbl_oklar, searchGbc);

        final JComboBox<String> cmb_nereye = new JComboBox<>(getSehirler());
        searchGbc.gridx = 2; searchGbc.gridy = 0;
        pnl_search.add(createLabel("Nereye"), searchGbc);
        styleComboBox(cmb_nereye);
        searchGbc.gridy = 1;
        pnl_search.add(cmb_nereye, searchGbc);

        lbl_oklar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Object item1 = cmb_nereden.getSelectedItem();
                Object item2 = cmb_nereye.getSelectedItem();
                cmb_nereden.setSelectedItem(item2);
                cmb_nereye.setSelectedItem(item1);
            }
        });

        // --- TARİH (YENİ DATEPICKER) ---
        searchGbc.gridx = 3; searchGbc.gridy = 0;
        pnl_search.add(createLabel("Tarih"), searchGbc);

        // DatePicker oluşturuluyor
        DatePicker datePicker = createDarkDatePicker();
        searchGbc.gridy = 1;
        pnl_search.add(datePicker, searchGbc);

        // Ara Butonu
        searchGbc.gridx = 4; searchGbc.gridy = 1;
        searchGbc.insets = new Insets(0, 20, 0, 0);

        JButton btn_ara = new JButton("SEFER BUL") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_BTN_ORANGE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn_ara.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_ara.setForeground(Color.WHITE);
        btn_ara.setFocusPainted(false);
        btn_ara.setContentAreaFilled(false);
        btn_ara.setBorderPainted(false);
        btn_ara.setPreferredSize(new Dimension(130, 40));
        btn_ara.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn_ara.addActionListener(e -> {
            String nereden = (String) cmb_nereden.getSelectedItem();
            String nereye = (String) cmb_nereye.getSelectedItem();

            // Tarih Alma Kısmı (LGoodDatePicker)
            java.time.LocalDate date = datePicker.getDate();
            String tarihStr = "";

            if(date != null) {
                tarihStr = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen tarih seçiniz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (nereden.equals(nereye)) {
                JOptionPane.showMessageDialog(this, "Hata: Başlangıç ve Bitiş noktası aynı olamaz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (pnlBiletAl != null) {
                pnlBiletAl.seferleriFiltrele(nereden, nereye, tarihStr);
                cardLayout.show(mainContentPanel, "biletal");

                if(btn_biletal != null) setButtonActive(btn_biletal);
            }
        });

        pnl_search.add(btn_ara, searchGbc);

        gbc.gridy = row++;
        panel.add(pnl_search, gbc);

        JPanel pnl_content = new JPanel(new BorderLayout());
        pnl_content.setOpaque(false);
        pnl_content.setPreferredSize(new Dimension(800, 300));

        JLabel lbl_kampanya = new JLabel();
        lbl_kampanya.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon kampanyaIcon = new ImageIcon(getClass().getResource("/images/kampanya.png"));
            Image img = kampanyaIcon.getImage().getScaledInstance(800, 300, Image.SCALE_SMOOTH);
            lbl_kampanya.setIcon(new ImageIcon(img));
        } catch(Exception e) {}

        pnl_content.add(lbl_kampanya, BorderLayout.CENTER);

        gbc.gridy = row++;
        panel.add(pnl_content, gbc);

        gbc.gridy = row++;
        gbc.weighty = 1.0;
        panel.add(Box.createGlue(), gbc);

        return panel;
    }

    // --- ÖZEL DATEPICKER OLUŞTURUCU (DÜZELTİLMİŞ VERSİYON) ---
    private DatePicker createDarkDatePicker() {
        DatePickerSettings settings = new DatePickerSettings();

        // Format ve Klavye Ayarı
        settings.setFormatForDatesCommonEra("dd.MM.yyyy");
        settings.setAllowKeyboardEditing(false);

        // --- RENK AYARLARI ---
        Color darkInputBg = COLOR_INPUT_BG; // Koyu Gri Input
        Color darkCalendarBg = new Color(52, 73, 94); // Takvim açılınca arka plan
        Color orangeAccent = COLOR_BTN_ORANGE; // Seçili gün
        Color whiteText = Color.WHITE;

        // Input Alanı Renkleri (Bunlar 11.2.1'de kesin var)
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundValidDate, darkInputBg);
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundInvalidDate, darkInputBg);
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundVetoedDate, darkInputBg);

        // Takvim Çerçeve Renkleri
        settings.setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel, darkCalendarBg);
        settings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearNavigationButtons, new Color(44, 62, 80));

        // Seçili Gün Rengi
        settings.setColor(DatePickerSettings.DateArea.CalendarBackgroundSelectedDate, orangeAccent);
        settings.setColor(DatePickerSettings.DateArea.CalendarBorderSelectedDate, orangeAccent);

        // Yazı renkleri için hata veren satırları kaldırdım.
        // Varsayılan siyah/koyu gri yazı, beyaz arka planda okunur olacaktır.

        // Fontlar
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        settings.setFontValidDate(font);
        settings.setFontInvalidDate(font);
        settings.setFontVetoedDate(font);
        settings.setFontCalendarDateLabels(font);
        settings.setFontCalendarWeekdayLabels(new Font("Segoe UI", Font.BOLD, 12));

        DatePicker dp = new DatePicker(settings);
        dp.setPreferredSize(new Dimension(150, 40));

        // Text field kısmını manuel boyayalım (Garanti olsun)
        dp.getComponentDateTextField().setBackground(darkInputBg);
        dp.getComponentDateTextField().setForeground(whiteText);
        dp.getComponentDateTextField().setCaretColor(whiteText);
        dp.getComponentDateTextField().setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // İç boşluk

        // Bugüne ayarla
        dp.setDateToToday();

        // Butonun arka planını da mat yap
        JButton btn = dp.getComponentToggleCalendarButton();
        btn.setText("📅");
        btn.setBackground(new Color(44, 62, 80));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);

        return dp;
    }


    // --- YARDIMCI METOTLAR ---
    private JPanel createTransparentPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 40, 50, 200)); // Yarı saydam koyu
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
    }

    private JButton createMenuButton(String text, String iconPath) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover() || (getBackground().getAlpha() > 0)) {
                    g2.setColor(getBackground());
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(new Color(220, 220, 220));
        btn.setBackground(COLOR_BTN_NORMAL);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBorder(new EmptyBorder(10, 10, 10, 10));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        try {
            if (getClass().getResource(iconPath) != null) {
                ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setIconTextGap(15);
            }
        } catch (Exception e) {}

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if(btn != currentActiveButton) {
                    btn.setBackground(COLOR_BTN_HOVER);
                    btn.repaint();
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if(btn != currentActiveButton) {
                    btn.setBackground(COLOR_BTN_NORMAL);
                    btn.repaint();
                }
            }
        });
        sidebarButtons.add(btn);
        return btn;
    }

    private void styleComboBox(JComboBox box) {
        box.setPreferredSize(new Dimension(180, 40));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBackground(COLOR_INPUT_BG);
        box.setForeground(Color.WHITE);
        ((JComponent) box.getRenderer()).setOpaque(true);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(COLOR_TEXT);
        l.setBorder(new EmptyBorder(0, 0, 5, 0));
        return l;
    }

    private String[] getSehirler() {
        return new String[] {
                "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin", "Aydın", "Balıkesir",
                "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli",
                "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari",
                "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir", "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir",
                "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir",
                "Niğde", "Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat",
                "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman",
                "Kırıkkale", "Batman", "Şırnak", "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye", "Düzce"
        };
    }

    private class GradientPanel extends JPanel {
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

            g2d.setColor(new Color(255, 255, 255, 5));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(-100, -100, 600, 600);
            g2d.drawOval(w - 500, h - 500, 900, 900);
        }
    }
}