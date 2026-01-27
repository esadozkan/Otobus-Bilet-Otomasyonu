import database.DB_Baglanti;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdminPanel extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    private KaptanPanel pnlKaptan;
    private BiletAlPanel pnlGise;

    // Dashboard elemanları
    private JLabel lblValCiro;
    private JLabel lblValMusteri;
    private JLabel lblValBilet;
    private JPanel pnlChartArea; // Grafikler buraya gelecek

    private List<JButton> sidebarButtons = new ArrayList<>();
    private JButton currentActiveButton = null;

    // --- RENK PALETİ ---
    private final Color COLOR_SIDEBAR_BG = new Color(30, 40, 50, 240); // Yarı saydam
    private final Color COLOR_BTN_NORMAL = new Color(30, 40, 50, 0);   // Tam şeffaf
    private final Color COLOR_BTN_HOVER = new Color(52, 73, 94);
    private final Color COLOR_BTN_ACTIVE = new Color(41, 128, 185);

    // Kart Renkleri
    private final Color COLOR_CARD_BLUE = new Color(41, 128, 185);
    private final Color COLOR_CARD_GREEN = new Color(39, 174, 96);
    private final Color COLOR_CARD_ORANGE = new Color(230, 126, 34);

    public AdminPanel() {
        initUI();
    }

    private void initUI() {
        setTitle("Yönetim Paneli - Çağdaş Güven");
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ana kapsayıcı (Gradient Arka Plan)
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // --- SIDEBAR (SOL MENÜ) - GÖLGELENMEYİ ÇÖZEN KISIM BURASI ---
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Standart boyama yerine, yarı saydam rengi biz boyuyoruz
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false); // Swing'in standart boyamasını kapattık (Ghosting biter)
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        // Admin Profil Kısmı
        JLabel lbl_icon = new JLabel();
        lbl_icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/user.png"));
            Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            lbl_icon.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lbl_icon.setText("ADMIN");
            lbl_icon.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lbl_icon.setForeground(Color.WHITE);
        }

        JLabel lbl_admin = new JLabel("Yönetici Paneli");
        lbl_admin.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl_admin.setForeground(Color.WHITE);
        lbl_admin.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl_rol = new JLabel("Sistem Yöneticisi");
        lbl_rol.setFont(new Font("Segoe UI Light", Font.ITALIC, 14));
        lbl_rol.setForeground(new Color(200, 200, 200));
        lbl_rol.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(lbl_icon);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(lbl_admin);
        sidebar.add(lbl_rol);
        sidebar.add(Box.createVerticalStrut(40));

        // --- MENÜ BUTONLARI ---
        JButton btn_dash = createMenuButton("Gösterge Paneli", "/icons/dashboard.png");
        btn_dash.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "dashboard");
            updateDashboard();
            setActiveButton(btn_dash);
        });

        JButton btn_kaptan = createMenuButton("Kaptan Yönetimi", "/icons/captain.png");
        btn_kaptan.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "kaptan");
            if (pnlKaptan != null) pnlKaptan.kaptanlariListele();
            setActiveButton(btn_kaptan);
        });

        JButton btn_sefer = createMenuButton("Sefer Planlama", "/icons/travel.png");
        btn_sefer.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "sefer");
            setActiveButton(btn_sefer);
        });

        JButton btn_gise = createMenuButton("Gişe İşlemleri", "/icons/ticket.png");
        btn_gise.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "gise_satis");
            if (pnlGise != null) pnlGise.seferleriGetir();
            setActiveButton(btn_gise);
        });

        JButton btn_musteri = createMenuButton("Müşteri Listesi", "/icons/costumer.png");
        btn_musteri.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "musteri");
            setActiveButton(btn_musteri);
        });

        JButton btn_rezerv = createMenuButton("Rezervasyonlar", "/icons/booking.png");
        btn_rezerv.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "rezerv");
            setActiveButton(btn_rezerv);
        });

        sidebar.add(btn_dash); sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btn_sefer); sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btn_kaptan); sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btn_gise); sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btn_musteri); sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btn_rezerv);

        sidebar.add(Box.createVerticalGlue());

        // --- GÜVENLİ ÇIKIŞ BUTONU ---
        JButton btn_cikis = new JButton("Güvenli Çıkış");
        btn_cikis.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn_cikis.setHorizontalAlignment(SwingConstants.CENTER);
        btn_cikis.setFont(new Font("Segoe UI", Font.BOLD, 15));

        // Varsayılan Kırmızı Yazı
        btn_cikis.setForeground(new Color(231, 76, 60));
        btn_cikis.setBackground(COLOR_BTN_NORMAL);
        btn_cikis.setBorder(new EmptyBorder(10, 10, 10, 10));
        btn_cikis.setFocusPainted(false);
        btn_cikis.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_cikis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        btn_cikis.setContentAreaFilled(false); // Şeffaf olması için
        btn_cikis.setOpaque(false);

        // İkonu ekle
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/logout.png"));
            Image img = icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
            btn_cikis.setIcon(new ImageIcon(img));
            btn_cikis.setIconTextGap(10);
        } catch (Exception e) {}

        // Kırmızı Hover Efekti (Özel boyama)
        btn_cikis.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                if (c.getBackground().getAlpha() > 0) { // Sadece renk atanmışsa boya
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(c.getBackground());
                    g2.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 20, 20));
                    g2.dispose();
                }
                super.paint(g, c);
            }
        });

        btn_cikis.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_cikis.setBackground(new Color(192, 57, 43)); // Kırmızı Hover
                btn_cikis.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_cikis.setBackground(COLOR_BTN_NORMAL); // Şeffaf
                btn_cikis.setForeground(new Color(231, 76, 60));
            }
        });

        btn_cikis.addActionListener(e -> {
            this.dispose();
            new Login().setVisible(true);
        });
        sidebar.add(btn_cikis);

        // --- ANA İÇERİK PANELİ ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setOpaque(false);

        mainContentPanel.add(createDashboardPanel(), "dashboard");

        pnlKaptan = new KaptanPanel();
        mainContentPanel.add(pnlKaptan, "kaptan");

        mainContentPanel.add(new SeferPanel(), "sefer");
        mainContentPanel.add(new MusteriPanel(), "musteri");
        mainContentPanel.add(new RezervasyonPanel(), "rezerv");

        pnlGise = new BiletAlPanel(true);
        mainContentPanel.add(pnlGise, "gise_satis");

        add(sidebar, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        updateDashboard();
        setActiveButton(btn_dash);
    }

    // --- DASHBOARD OLUŞTURMA ---
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(30, 30));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel lblHeader = new JLabel("Genel Durum Özeti");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblHeader.setForeground(Color.WHITE);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setPreferredSize(new Dimension(0, 160));

        lblValCiro = new JLabel("0 TL");
        lblValMusteri = new JLabel("0");
        lblValBilet = new JLabel("0");

        cardsPanel.add(createStatCard("Toplam Ciro", lblValCiro, COLOR_CARD_GREEN, "/icons/money.png"));
        cardsPanel.add(createStatCard("Kayıtlı Müşteri", lblValMusteri, COLOR_CARD_BLUE, "/icons/costumer.png"));
        cardsPanel.add(createStatCard("Satılan Bilet", lblValBilet, COLOR_CARD_ORANGE, "/icons/ticket.png"));

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(lblHeader, BorderLayout.NORTH);
        topContainer.add(Box.createVerticalStrut(30), BorderLayout.CENTER);
        topContainer.add(cardsPanel, BorderLayout.SOUTH);

        // --- GRAFİK ALANI ---
        pnlChartArea = new JPanel(new GridLayout(1, 2, 30, 0));
        pnlChartArea.setOpaque(false);
        pnlChartArea.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(pnlChartArea, BorderLayout.CENTER);

        return panel;
    }

    // --- BUTON OLUŞTURMA (GÖLGELENME SORUNU ÇÖZÜLDÜ) ---
    private JButton createMenuButton(String text, String iconPath) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // Kaliteli görüntü
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Eğer butonun arka plan rengi şeffaf değilse (Alpha > 0) boya
                if (getBackground().getAlpha() > 0) {
                    g2.setColor(getBackground());
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20)); // Yuvarlak
                }

                g2.dispose();
                super.paintComponent(g); // Yazıyı ve ikonu çiz
            }
        };

        // --- ORTALAMA VE BOYUT AYARLARI ---
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        // --- STİL AYARLARI ---
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(new Color(220, 220, 220));
        btn.setBackground(COLOR_BTN_NORMAL);

        btn.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- KRİTİK ŞEFFAFLIK AYARLARI ---
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        try {
            if (getClass().getResource(iconPath) != null) {
                ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                Image img = icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setIconTextGap(10);
            }
        } catch (Exception e) {}

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn != currentActiveButton) {
                    btn.setBackground(COLOR_BTN_HOVER);
                    btn.repaint();
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn != currentActiveButton) {
                    btn.setBackground(COLOR_BTN_NORMAL);
                    btn.repaint();
                }
            }
        });

        sidebarButtons.add(btn);
        return btn;
    }

    private void setActiveButton(JButton activeBtn) {
        currentActiveButton = activeBtn;
        for (JButton btn : sidebarButtons) {
            btn.setBackground(COLOR_BTN_NORMAL);
            btn.setForeground(new Color(220, 220, 220));
        }
        activeBtn.setBackground(COLOR_BTN_ACTIVE);
        activeBtn.setForeground(Color.WHITE);
    }

    private JPanel createStatCard(String title, JLabel lblValue, Color color, String iconPath) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTitle.setForeground(new Color(255, 255, 255, 220));

        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));

        JPanel txtPanel = new JPanel(new GridLayout(2, 1));
        txtPanel.setOpaque(false);
        txtPanel.add(lblTitle);
        txtPanel.add(lblValue);

        JLabel lblIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            lblIcon.setIcon(new ImageIcon(img));
        } catch (Exception e) {}
        lblIcon.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(txtPanel, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }

    private void updateDashboard() {
        new Thread(() -> {
            int musteri = 0;
            int bilet = 0;
            double ciro = 0;

            try (Connection conn = DB_Baglanti.baglan()) {
                if(conn != null) {
                    PreparedStatement ps1 = conn.prepareStatement("SELECT COUNT(*) FROM musteriler");
                    ResultSet rs1 = ps1.executeQuery();
                    if(rs1.next()) musteri = rs1.getInt(1);

                    PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) FROM biletler");
                    ResultSet rs2 = ps2.executeQuery();
                    if(rs2.next()) bilet = rs2.getInt(1);

                    PreparedStatement ps3 = conn.prepareStatement("SELECT SUM(tutar) FROM biletler WHERE durum = 'Aktif'");
                    ResultSet rs3 = ps3.executeQuery();
                    if(rs3.next()) ciro = rs3.getDouble(1);
                }
            } catch (Exception e) { e.printStackTrace(); }

            int finalMusteri = musteri;
            int finalBilet = bilet;
            double finalCiro = ciro;

            SwingUtilities.invokeLater(() -> {
                lblValMusteri.setText(String.valueOf(finalMusteri));
                lblValBilet.setText(String.valueOf(finalBilet));
                lblValCiro.setText(new DecimalFormat("#,### TL").format(finalCiro));

                if(pnlChartArea != null) {
                    pnlChartArea.removeAll();
                    try {
                        GrafikPanel grafikci = new GrafikPanel();
                        pnlChartArea.add(grafikci.getPopulerRotaPanel());
                        pnlChartArea.add(grafikci.getAylikKarPanel());
                    } catch (Exception e) {
                    }
                    pnlChartArea.revalidate();
                    pnlChartArea.repaint();
                }
            });
        }).start();
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