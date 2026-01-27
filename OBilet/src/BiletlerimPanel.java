import database.DB_Baglanti;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BiletlerimPanel extends JPanel {

    private JPanel listContainer;
    private int currentUserId;

    // renkler
    private final Color COLOR_BG = new Color(52, 73, 94);
    private final Color COLOR_BTN_RED = new Color(231, 76, 60);
    private final Color COLOR_BTN_DISABLED = new Color(149, 165, 166);

    public BiletlerimPanel(int userId) {
        this.currentUserId = userId;
        initUI();
        biletleriGetir();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel lblHeader = new JLabel("Biletlerim");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setBorder(new EmptyBorder(0, 40, 20, 0)); // Soldan hizalı
        add(lblHeader, BorderLayout.NORTH);

        // liste konteyneri
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(COLOR_BG);

        // scroll
        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(COLOR_BG);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void biletleriGetir() {
        listContainer.removeAll();

        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();
            String sql = "SELECT b.pnr_kod, b.yolcu_ad, b.koltuk_no, b.tutar, b.durum, " +
                    "s.nereden, s.nereye, s.tarih, s.saat, " +
                    "a.plaka " +
                    "FROM biletler b " +
                    "JOIN seferler s ON b.sefer_id = s.id " +
                    "LEFT JOIN araclar a ON s.arac_id = a.id " +
                    "WHERE b.musteri_id = ? " +
                    "ORDER BY b.id DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();

            boolean biletVarMi = false;
            while (rs.next()) {
                biletVarMi = true;
                // Verileri çek
                String nereden = rs.getString("nereden");
                String nereye = rs.getString("nereye");
                String tarih = rs.getString("tarih");
                String saat = rs.getString("saat");
                String koltuk = String.valueOf(rs.getInt("koltuk_no"));
                String tutar = rs.getInt("tutar") + " TL";
                String pnr = rs.getString("pnr_kod");
                String yolcuAd = rs.getString("yolcu_ad");
                String plaka = (rs.getString("plaka") != null) ? rs.getString("plaka") : "-";
                String durum = rs.getString("durum");

                addTicketCard(nereden, nereye, tarih, saat, koltuk, pnr, tutar, yolcuAd, plaka, durum);

                listContainer.add(Box.createVerticalStrut(30));
            }

            if (!biletVarMi) {
                JLabel lblBos = new JLabel("Henüz biletiniz bulunmuyor.");
                lblBos.setForeground(Color.LIGHT_GRAY);
                lblBos.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                lblBos.setAlignmentX(Component.CENTER_ALIGNMENT);
                listContainer.add(Box.createVerticalStrut(50));
                listContainer.add(lblBos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DB_Baglanti.kapatan(conn);
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    private void addTicketCard(String kalkis, String varis, String tarih, String saat, String koltuk, String pnr, String tutar, String yolcu, String plaka, String durum) {

        // BoxLayout Y_AXIS kullanarak bileti ve butonu alt alta dizer
        JPanel cardWrapper = new JPanel();
        cardWrapper.setLayout(new BoxLayout(cardWrapper, BoxLayout.Y_AXIS));
        cardWrapper.setOpaque(false);
        cardWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardWrapper.setMaximumSize(new Dimension(600, 270));
        cardWrapper.setPreferredSize(new Dimension(600, 270));

        BiletDetayPanel biletGorseli = new BiletDetayPanel(kalkis, varis, tarih, saat, koltuk, pnr, tutar, yolcu, plaka);

        biletGorseli.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardWrapper.add(biletGorseli);
        cardWrapper.add(Box.createVerticalStrut(5));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(600, 35));
        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnIptal = new JButton("Bileti İptal Et");
        btnIptal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnIptal.setForeground(Color.WHITE);
        btnIptal.setBackground(COLOR_BTN_RED);
        btnIptal.setFocusPainted(false);
        btnIptal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIptal.setPreferredSize(new Dimension(140, 30));

        // buton durum kontrolü
        if ("İptal".equalsIgnoreCase(durum)) {
            btnIptal.setText("İptal Edildi");
            btnIptal.setEnabled(false);
            btnIptal.setBackground(COLOR_BTN_DISABLED);
        }
        else if (isSeferGecmisMi(tarih, saat)) {
            btnIptal.setText("Sefer Tamamlandı");
            btnIptal.setEnabled(false);
            btnIptal.setBackground(COLOR_BTN_DISABLED);
        }
        else {
            btnIptal.addActionListener(e -> iptalIsleminiBaslat(pnr, tutar));
        }

        btnPanel.add(btnIptal);
        cardWrapper.add(btnPanel);

        listContainer.add(cardWrapper);
    }

    private void iptalIsleminiBaslat(String pnr, String tutarStr) {
        double biletFiyati = 0;
        try {
            String temiz = tutarStr.replace(" TL", "").trim();
            biletFiyati = Double.parseDouble(temiz);
        } catch (Exception e) {}

        double iade = biletFiyati * 0.80;
        double kesinti = biletFiyati * 0.20;

        String msg = String.format(
                "Bilet Tutarı: %.2f TL\nKesinti (%%20): -%.2f TL\n\nİADE EDİLECEK: %.2f TL\n\nOnaylıyor musunuz?",
                biletFiyati, kesinti, iade
        );

        int secim = JOptionPane.showConfirmDialog(this, msg, "İptal Onayı", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (secim == JOptionPane.YES_OPTION) {
            veritabanindaIptalEt(pnr);
        }
    }

    private void veritabanindaIptalEt(String pnr) {
        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();
            String sql = "UPDATE biletler SET durum = 'İptal' WHERE pnr_kod = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, pnr);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Bilet iptal edildi.");
            biletleriGetir();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DB_Baglanti.kapatan(conn);
        }
    }

    private boolean isSeferGecmisMi(String tarih, String saat) {
        try {
            String birlesik = tarih + " " + saat;
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime seferTarihi = LocalDateTime.parse(birlesik, f);
            return LocalDateTime.now().isAfter(seferTarihi);
        } catch (Exception e) { return false; }
    }
}