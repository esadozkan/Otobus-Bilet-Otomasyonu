import database.DB_Baglanti;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OtobusSemaPanel extends JPanel {

    public ArrayList<String> secilenKoltuklar = new ArrayList<>();
    public Map<String, String> secilenCinsiyetler = new HashMap<>();
    private Map<Integer, String> veritabanindanGelenDoluKoltuklar = new HashMap<>();

    private JPanel koltukGridPaneli;
    private ImageIcon iconBos, iconSecili, iconDoluErkek, iconDoluKadin, iconDireksiyon;
    private ImageIcon iconKadinKucuk, iconErkekKucuk;

    private boolean isAdminMode;
    private int biletFiyati = 0;
    private int currentSeferId = -1;

    public OtobusSemaPanel(boolean isAdminMode) {
        this.isAdminMode = isAdminMode;
        ikonlariYukle();
        initUI();
    }

    private void ikonlariYukle() {
        int size = 58;
        iconBos = safeIconLoad("/icons/koltuk_bos.png", size);
        iconSecili = safeIconLoad("/icons/koltuk_secili.png", size);
        iconDoluErkek = safeIconLoad("/icons/koltuk_dolu_erkek.png", size);
        iconDoluKadin = safeIconLoad("/icons/koltuk_dolu_k.png", size);
        iconDireksiyon = safeIconLoad("/icons/dii.png", 60);
        iconKadinKucuk = safeIconLoad("/icons/woman.png", 24);
        iconErkekKucuk = safeIconLoad("/icons/man.png", 24);
    }

    private ImageIcon safeIconLoad(String path, int size) {
        URL url = getClass().getResource(path);
        if (url == null) return null;
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(230, 230, 230));

        koltukGridPaneli = new JPanel(new GridBagLayout());
        koltukGridPaneli.setBackground(new Color(230, 230, 230));
        koltukGridPaneli.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(koltukGridPaneli);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
        otobusuCiz(0, -1);
    }

    public void otobusuCiz(int fiyat, int seferId) {
        this.biletFiyati = fiyat;
        this.currentSeferId = seferId;
        doluKoltuklariGetir();

        koltukGridPaneli.removeAll();
        secilenKoltuklar.clear();
        secilenCinsiyetler.clear();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        koltukGridPaneli.add(new JLabel(iconDireksiyon), gbc);

        int koltukNo = 1;
        int toplamSira = 16;
        int ortaKapiSirasi = 8;

        for (int row = 1; row <= toplamSira; row++) {
            gbc.gridy = row;

            //koltuklar
            gbc.gridx = 0;
            if (koltukNo <= 40) koltukGridPaneli.add(createKoltukButton(koltukNo++), gbc);

            //koridor
            gbc.gridx = 1; koltukGridPaneli.add(Box.createHorizontalStrut(40), gbc);

            // ikililer
            if (row == ortaKapiSirasi) {
                gbc.gridx = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
                koltukGridPaneli.add(new MerdivenPanel(), gbc);
                gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
            } else {

                gbc.gridx = 2; if (koltukNo <= 40) koltukGridPaneli.add(createKoltukButton(koltukNo++), gbc);

                gbc.gridx = 3; if (koltukNo <= 40) koltukGridPaneli.add(createKoltukButton(koltukNo++), gbc);
            }
        }
        koltukGridPaneli.revalidate();
        koltukGridPaneli.repaint();
    }

    private void doluKoltuklariGetir() {
        veritabanindanGelenDoluKoltuklar.clear();
        if (currentSeferId == -1) return;

        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();
            String sql = "SELECT koltuk_no, cinsiyet FROM biletler WHERE sefer_id = ? AND durum = 'Aktif'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentSeferId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                veritabanindanGelenDoluKoltuklar.put(rs.getInt("koltuk_no"), rs.getString("cinsiyet"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { DB_Baglanti.kapatan(conn); }
    }

    private int yanKoltuguBul(int no) {
        if (no % 3 == 1) return -1;

        if (no % 3 == 2) return no + 1; // 2 -> 3, 5 -> 6
        if (no % 3 == 0) return no - 1; // 3 -> 2, 6 -> 5

        return -1;
    }

    //cinsiyet kontrol
    private boolean cinsiyetIzniVarMi(String koltukNoStr, String secilenCinsiyet) {

        if (isAdminMode) return true;

        int koltukNo = Integer.parseInt(koltukNoStr);
        int yanKoltukNo = yanKoltuguBul(koltukNo);

        if (yanKoltukNo == -1) return true;

        // yan koltuk sepette mi
        if (secilenKoltuklar.contains(String.valueOf(yanKoltukNo))) return true;

        //yan koltuk doluluk kontrolü db
        if (veritabanindanGelenDoluKoltuklar.containsKey(yanKoltukNo)) {
            String yanKoltukCinsiyet = veritabanindanGelenDoluKoltuklar.get(yanKoltukNo);

            if (!yanKoltukCinsiyet.equalsIgnoreCase(secilenCinsiyet)) {
                return false;
            }
        }
        return true;
    }

    private JToggleButton createKoltukButton(int no) {
        String numaraStr = String.valueOf(no);
        JToggleButton btn = new JToggleButton(numaraStr);
        btn.setPreferredSize(new Dimension(50, 50));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.DARK_GRAY);
        if(iconBos != null) btn.setIcon(iconBos);

        if (veritabanindanGelenDoluKoltuklar.containsKey(no)) {
            btn.setEnabled(false);
            String cinsiyet = veritabanindanGelenDoluKoltuklar.get(no);
            if ("Erkek".equalsIgnoreCase(cinsiyet)) {
                if(iconDoluErkek != null) btn.setDisabledIcon(iconDoluErkek);
            } else {
                if(iconDoluKadin != null) btn.setDisabledIcon(iconDoluKadin);
            }
            UIManager.put("Button.disabledText", Color.WHITE);
        } else {
            btn.addActionListener((ActionEvent e) -> {
                if (btn.isSelected()) {
                    showCinsiyetPopup(btn, numaraStr);
                } else {
                    iptalEt(btn, numaraStr);
                }
            });
        }
        return btn;
    }

    private void showCinsiyetPopup(JToggleButton btn, String koltukNo) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        popup.setBackground(Color.WHITE);
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JButton btnKadin = new JButton("Kadın", iconKadinKucuk);
        stylePopupButton(btnKadin, new Color(233, 30, 99));
        btnKadin.addActionListener(e -> {

            if (cinsiyetIzniVarMi(koltukNo, "Kadın")) {
                secimYap(btn, koltukNo, "Kadın", iconDoluKadin, Color.WHITE);
                popup.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Yan koltukta Erkek yolcu bulunmaktadır!", "Bayan Yanı Hatası", JOptionPane.WARNING_MESSAGE);
                btn.setSelected(false);
                popup.setVisible(false);
            }
        });

        JButton btnErkek = new JButton("Erkek", iconErkekKucuk);
        stylePopupButton(btnErkek, new Color(33, 150, 243));
        btnErkek.addActionListener(e -> {

            if (cinsiyetIzniVarMi(koltukNo, "Erkek")) {
                secimYap(btn, koltukNo, "Erkek", iconDoluErkek, Color.WHITE);
                popup.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Yan koltukta Kadın yolcu bulunmaktadır!", "Bayan Yanı Hatası", JOptionPane.WARNING_MESSAGE);
                btn.setSelected(false);
                popup.setVisible(false);
            }
        });

        panel.add(btnKadin); panel.add(btnErkek); popup.add(panel);
        popup.show(btn, 0, btn.getHeight());

        popup.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) { if (!secilenKoltuklar.contains(koltukNo)) btn.setSelected(false); }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) { }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) { }
        });
    }

    private void stylePopupButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorder(new EmptyBorder(5, 10, 5, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalTextPosition(SwingConstants.CENTER); btn.setVerticalTextPosition(SwingConstants.BOTTOM);
    }

    private void secimYap(JToggleButton btn, String koltukNo, String cinsiyet, ImageIcon ikon, Color yaziRengi) {
        btn.setSelected(true); btn.setIcon(ikon); btn.setForeground(yaziRengi);
        if (!secilenKoltuklar.contains(koltukNo)) secilenKoltuklar.add(koltukNo);
        secilenCinsiyetler.put(koltukNo, cinsiyet);
    }

    private void iptalEt(JToggleButton btn, String koltukNo) {
        btn.setSelected(false); btn.setIcon(iconBos); btn.setForeground(Color.DARK_GRAY);
        secilenKoltuklar.remove(koltukNo); secilenCinsiyetler.remove(koltukNo);
    }

    public String getSecilenKoltuklarStr() { return String.join(", ", secilenKoltuklar); }
    public int getToplamTutar() { return secilenKoltuklar.size() * biletFiyati; }

    private class MerdivenPanel extends JPanel {
        public MerdivenPanel() { setOpaque(false); setPreferredSize(new Dimension(100, 50)); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.LIGHT_GRAY); g2d.setStroke(new BasicStroke(3));
            for (int i = 0; i < getHeight(); i += 10) g2d.drawLine(0, i, getWidth(), i);
            g2d.setColor(Color.GRAY); g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String text = "ORTA KAPI";
            int x = (getWidth() - g2d.getFontMetrics().stringWidth(text)) / 2;
            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.fillRect(x - 5, (getHeight()/2) - 15, g2d.getFontMetrics().stringWidth(text) + 10, 20);
            g2d.setColor(Color.DARK_GRAY); g2d.drawString(text, x, (getHeight()/2) + g2d.getFontMetrics().getAscent()/2);
        }
    }
}