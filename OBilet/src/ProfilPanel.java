import database.DB_Baglanti;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfilPanel extends JPanel {

    // form elemanları
    private JTextField txt_adsoyad;
    private JTextField txt_telefon;
    private JTextField txt_kullanici_adi;
    private JPasswordField txt_yeni_sifre;

    private int currentMusteriId;

    private int linkedUserId = -1;

    // renkler
    private final Color COLOR_BG = new Color(52, 73, 94);
    private final Color COLOR_PANEL_BG = new Color(44, 62, 80);
    private final Color COLOR_TEXT = new Color(236, 240, 241);
    private final Color COLOR_BTN_BLUE = new Color(52, 152, 219);

    public ProfilPanel(int musteriId) {
        this.currentMusteriId = musteriId;
        initUI();
        profilBilgileriniGetir();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel profileCard = new JPanel(new GridBagLayout());
        profileCard.setBackground(COLOR_PANEL_BG);
        profileCard.setPreferredSize(new Dimension(450, 600));
        profileCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(30, 40, 50), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        // avatar
        JLabel lbl_avatar = new JLabel();
        lbl_avatar.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/userrr.png"));
            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            lbl_avatar.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lbl_avatar.setText("👤"); lbl_avatar.setFont(new Font("Segoe UI", Font.PLAIN, 80)); lbl_avatar.setForeground(Color.WHITE);
        }
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 20, 0); profileCard.add(lbl_avatar, gbc);

        JLabel lbl_baslik = new JLabel("Profil Bilgilerim", SwingConstants.CENTER);
        lbl_baslik.setFont(new Font("Segoe UI", Font.BOLD, 22)); lbl_baslik.setForeground(Color.WHITE);
        gbc.gridy = row++; profileCard.add(lbl_baslik, gbc);

        // form alanı
        gbc.insets = new Insets(5, 0, 5, 0);

        gbc.gridy = row++; profileCard.add(createLabel("Ad Soyad"), gbc);
        txt_adsoyad = createTextField("");
        gbc.gridy = row++; profileCard.add(txt_adsoyad, gbc);

        gbc.gridy = row++; profileCard.add(createLabel("Telefon Numarası"), gbc);
        txt_telefon = createTextField("");
        gbc.gridy = row++; profileCard.add(txt_telefon, gbc);

        gbc.gridy = row++; profileCard.add(createLabel("Kullanıcı Adı / TC (Değiştirilemez)"), gbc);
        txt_kullanici_adi = createTextField("");
        txt_kullanici_adi.setEditable(false); txt_kullanici_adi.setForeground(Color.GRAY);
        gbc.gridy = row++; profileCard.add(txt_kullanici_adi, gbc);

        gbc.gridy = row++; gbc.insets = new Insets(20, 0, 5, 0);
        profileCard.add(createLabel("Yeni Şifre (Boş bırakırsanız değişmez)"), gbc);
        txt_yeni_sifre = new JPasswordField(20);
        txt_yeni_sifre.setPreferredSize(new Dimension(0, 40));
        txt_yeni_sifre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt_yeni_sifre.putClientProperty("JPasswordField.showRevealButton", true);
        gbc.gridy = row++; gbc.insets = new Insets(5, 0, 5, 0);
        profileCard.add(txt_yeni_sifre, gbc);

        JButton btn_guncelle = new JButton("BİLGİLERİ GÜNCELLE");
        btn_guncelle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_guncelle.setBackground(COLOR_BTN_BLUE); btn_guncelle.setForeground(Color.WHITE);
        btn_guncelle.setFocusPainted(false);
        btn_guncelle.setPreferredSize(new Dimension(0, 50));
        btn_guncelle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_guncelle.addActionListener(e -> profilGuncelle());

        gbc.gridy = row++; gbc.insets = new Insets(30, 0, 0, 0);
        profileCard.add(btn_guncelle, gbc);

        add(profileCard);
    }

    // db
    private void profilBilgileriniGetir() {
        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();

            String sql = "SELECT m.ad_soyad, m.telefon, m.user_id, k.kullanici_adi " +
                    "FROM musteriler m " +
                    "JOIN kullanicilar k ON m.user_id = k.id " +
                    "WHERE m.id = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentMusteriId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txt_adsoyad.setText(rs.getString("ad_soyad"));
                txt_telefon.setText(rs.getString("telefon"));
                txt_kullanici_adi.setText(rs.getString("kullanici_adi"));

                linkedUserId = rs.getInt("user_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DB_Baglanti.kapatan(conn);
        }
    }

    private void profilGuncelle() {
        String yeniAd = txt_adsoyad.getText().trim();
        String yeniTel = txt_telefon.getText().trim();
        String yeniSifre = new String(txt_yeni_sifre.getPassword()).trim();

        if (yeniAd.isEmpty() || yeniTel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ad Soyad ve Telefon boş olamaz!", "Hata", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();

            // güncelleme
            String sqlMusteri = "UPDATE musteriler SET ad_soyad = ?, telefon = ? WHERE id = ?";
            PreparedStatement ps1 = conn.prepareStatement(sqlMusteri);
            ps1.setString(1, yeniAd);
            ps1.setString(2, yeniTel);
            ps1.setInt(3, currentMusteriId);
            ps1.executeUpdate();

            if (!yeniSifre.isEmpty()) {
                if(linkedUserId != -1) {
                    String sqlSifre = "UPDATE kullanicilar SET sifre = ? WHERE id = ?";
                    PreparedStatement ps2 = conn.prepareStatement(sqlSifre);
                    ps2.setString(1, yeniSifre);
                    ps2.setInt(2, linkedUserId);
                    ps2.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Profil ve Şifre başarıyla güncellendi!");
                } else {
                    JOptionPane.showMessageDialog(this, "Profil güncellendi ama şifre tablosuna erişilemedi!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Profil bilgileri güncellendi (Şifre değişmedi).");
            }

            txt_yeni_sifre.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
        } finally {
            DB_Baglanti.kapatan(conn);
        }
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(COLOR_TEXT);
        return lbl;
    }

    private JTextField createTextField(String text) {
        JTextField txt = new JTextField(text);
        txt.setPreferredSize(new Dimension(0, 40));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return txt;
    }
}