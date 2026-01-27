import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    private JProgressBar progressBar;
    private JLabel lblStatus;

    public SplashScreen() {
        // Çerçevesiz ekran boyutu
        setSize(600, 380);
        setLocationRelativeTo(null); // Ortala

        // Senin AnaMenu'deki arka plan yapısının aynısı
        MainPanel contentPanel = new MainPanel();
        contentPanel.setLayout(new BorderLayout());
        setContentPane(contentPanel);

        // --- ORTA KISIM (LOGO VE BAŞLIK) ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false); // Arka plan görünsün

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Logo (AnaMenü'deki mantığın aynısı)
        JLabel lblLogo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/otobus_logo.png"));
            // Logoyu biraz daha küçük (120px) yapıyoruz ki sığsın
            Image img = icon.getImage().getScaledInstance(120, -1, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // Logo yoksa metin göster
            lblLogo.setText("ÇAĞDAŞ GÜVEN");
            lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 30));
            lblLogo.setForeground(Color.WHITE);
        }
        centerPanel.add(lblLogo, gbc);

        // Başlık
        gbc.gridy = 1;
        gbc.insets = new Insets(15, 0, 5, 0); // Boşluk
        JLabel lblTitle = new JLabel("ÇAĞDAŞ GÜVEN TURİZM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        centerPanel.add(lblTitle, gbc);

        // Alt Başlık
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel lblSub = new JLabel("Otomasyon Sistemi v1.0");
        lblSub.setFont(new Font("Segoe UI Light", Font.ITALIC, 14));
        lblSub.setForeground(new Color(200, 200, 200));
        centerPanel.add(lblSub, gbc);

        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // --- ALT KISIM (LOADING BAR) ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 30, 40)); // Kenarlardan boşluk

        // Durum yazısı
        lblStatus = new JLabel("Sistem başlatılıyor...", SwingConstants.LEFT);
        lblStatus.setForeground(new Color(220, 230, 240));
        lblStatus.setFont(new Font("Consolas", Font.PLAIN, 12));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        // Progress Bar (Senin mavi buton rengini kullandım: 52, 152, 219)
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false); // Yüzde yazmasın, sade olsun
        progressBar.setForeground(new Color(52, 152, 219)); // Mavi Dolgu
        progressBar.setBackground(new Color(30, 45, 60));   // Koyu Zemin
        progressBar.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 1));
        progressBar.setPreferredSize(new Dimension(0, 6)); // İnce çubuk

        bottomPanel.add(lblStatus, BorderLayout.NORTH);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    // Yükleme Animasyonunu Başlatan Metot
    public void oynat() {
        setVisible(true);
        try {
            for (int i = 0; i <= 100; i++) {
                Thread.sleep(30); // Hız ayarı (toplam 3 saniye)
                progressBar.setValue(i);

                // Yapay yükleme mesajları
                if (i == 10) lblStatus.setText("Modüller yükleniyor...");
                if (i == 30) lblStatus.setText("Veritabanı bağlantısı kontrol ediliyor...");
                if (i == 50) lblStatus.setText("Güvenlik protokolleri başlatılıyor...");
                if (i == 70) lblStatus.setText("Arayüz hazırlanıyor...");
                if (i == 90) lblStatus.setText("Uygulama açılıyor...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dispose(); // Ekranı kapat
    }

    // Senin AnaMenu'deki arka plan çizim kodun (Birebir kopyaladım)
    private class MainPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            // Kalite ayarları
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Gradyan Arka Plan
            GradientPaint gp = new GradientPaint(0, 0, new Color(30, 45, 60), w, h, new Color(5, 5, 10));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);

            // Arka plandaki yuvarlak çizimler
            g2d.setColor(new Color(255, 255, 255, 8));
            g2d.setStroke(new BasicStroke(2));

            g2d.drawOval(-100, -100, 300, 300); // Biraz küçülttüm splash screen için
            g2d.drawOval(w - 200, h - 200, 400, 400);

            // Mavi çizgi detayı
            g2d.setColor(new Color(52, 152, 219, 20));
            g2d.drawLine(0, h/2 + 50, w, h/2 - 20);

            // Splash screen etrafına ince bir çerçeve (Daha şık durur)
            g2d.setColor(new Color(52, 152, 219));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(0, 0, w-1, h-1);
        }
    }
}