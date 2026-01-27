import database.DB_Baglanti;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class BiletAlPanel extends JPanel {

    private OtobusSemaPanel otobusSema;
    private boolean isAdminMode;

    // müşteri ID'sini tutacak değişken
    private int currentMusteriId = -1;

    private JTable table;
    private DefaultTableModel model;
    private JLabel lbl_secilen_koltuklar;
    private JLabel lbl_toplam_tutar;
    private JButton btn_satin_al;
    private int biletFiyati = 0;

    // seçilen seferin bilgileri
    private int secilenSeferId = -1;
    private String secilenGuzergah = "";
    private String secilenTarihSaat = "";

    // renkler - PREMIUM PALET
    private final Color COLOR_TABLE_HEADER = new Color(44, 62, 80);
    private final Color COLOR_TABLE_ROW_1 = new Color(52, 73, 94);
    private final Color COLOR_TABLE_ROW_2 = new Color(60, 80, 100);
    private final Color COLOR_TEXT = new Color(236, 240, 241);
    private final Color COLOR_ACCENT = new Color(52, 152, 219); // Mavi
    private final Color COLOR_BTN_ORANGE = new Color(241, 82, 6);

    public BiletAlPanel(boolean isAdminMode) {
        this(isAdminMode, -1);
    }

    public BiletAlPanel(boolean isAdminMode, int musteriId) {
        this.isAdminMode = isAdminMode;
        this.currentMusteriId = musteriId;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false); // Arka plan şeffaf olsun ki gradient görünsün
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // solpanel
        JPanel sol_panel = new JPanel(new BorderLayout(0, 15));
        sol_panel.setOpaque(false); // Şeffaf

        // filtre paneli
        JPanel pnl_filtre = new JPanel(new GridLayout(2, 2, 10, 10));
        pnl_filtre.setBackground(new Color(30, 40, 50, 200)); // Yarı saydam koyu
        pnl_filtre.setBorder(new EmptyBorder(15, 15, 15, 15));

        // nereden
        final JComboBox<String> cmb_nereden = new JComboBox<>(getSehirler());
        styleComboBox(cmb_nereden, "Nereden");

        // nereye
        final JComboBox<String> cmb_nereye = new JComboBox<>(getSehirler());
        styleComboBox(cmb_nereye, "Nereye");

        // tarih
        final JSpinner spinner_tarih = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner_tarih, "dd.MM.yyyy");
        spinner_tarih.setEditor(editor);
        // Spinner Stil
        spinner_tarih.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 50)), "Tarih",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12), Color.WHITE));
        spinner_tarih.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Spinner içindeki text alanını boyayalım
        JComponent editorComponent = spinner_tarih.getEditor();
        if (editorComponent instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editorComponent).getTextField().setForeground(Color.BLACK);
        }

        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy");
        } catch(Exception e){}

        // buton
        JButton btn_listele = createStyledButton("Seferleri Getir", COLOR_ACCENT);

        btn_listele.addActionListener(e -> {
            String nereden = (String) cmb_nereden.getSelectedItem();
            String nereye = (String) cmb_nereye.getSelectedItem();

            java.util.Date tarihDate = (java.util.Date) spinner_tarih.getValue();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy");
            String tarihStr = sdf.format(tarihDate);

            if(nereden.equals(nereye)) {
                JOptionPane.showMessageDialog(this, "Nereden ve Nereye aynı olamaz!");
                return;
            }

            //filtreleme
            seferleriFiltrele(nereden, nereye, tarihStr);
        });

        pnl_filtre.add(cmb_nereden);
        pnl_filtre.add(cmb_nereye);
        pnl_filtre.add(spinner_tarih);
        pnl_filtre.add(btn_listele);

        sol_panel.add(pnl_filtre, BorderLayout.NORTH);

        //tablo
        String[] kolonlar = {"ID", "Tarih", "Saat", "Güzergah", "Araç", "Fiyat"};
        model = new DefaultTableModel(null, kolonlar) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        styleTable(table);

        seferleriGetir();

        // tıklama aksiyonu tabloya
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    secilenSeferId = Integer.parseInt(model.getValueAt(row, 0).toString());
                    secilenTarihSaat = model.getValueAt(row, 1).toString() + " " + model.getValueAt(row, 2).toString();
                    secilenGuzergah = model.getValueAt(row, 3).toString();

                    String fiyatStr = model.getValueAt(row, 5).toString().replace(" TL", "");
                    biletFiyati = Integer.parseInt(fiyatStr);

                    otobusSema.otobusuCiz(biletFiyati, secilenSeferId);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_TABLE_ROW_1); // Tablo arka planı
        scroll.setOpaque(false);
        sol_panel.add(scroll, BorderLayout.CENTER);
        add(sol_panel, BorderLayout.CENTER);


        //sağpanel
        JPanel sag_panel = new JPanel(new BorderLayout());
        sag_panel.setBackground(new Color(30, 40, 50, 200)); // Yarı saydam
        sag_panel.setBorder(new EmptyBorder(0,0,0,0));
        sag_panel.setPreferredSize(new Dimension(380, 0));

        String baslikText = isAdminMode ? "Gişe Satış Ekranı" : "Koltuk Seçimi";
        JLabel lbl_baslik = new JLabel(baslikText, SwingConstants.CENTER);
        lbl_baslik.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl_baslik.setForeground(COLOR_TEXT);
        lbl_baslik.setPreferredSize(new Dimension(0, 50));
        sag_panel.add(lbl_baslik, BorderLayout.NORTH);

        otobusSema = new OtobusSemaPanel(isAdminMode);
        // Otobüs şemasını da şeffaf yapalım (Eğer panel destekliyorsa)
        otobusSema.setOpaque(false);
        sag_panel.add(otobusSema, BorderLayout.CENTER);

        JPanel pnl_odeme = new JPanel(new BorderLayout(10, 10));
        pnl_odeme.setOpaque(false);
        pnl_odeme.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel pnl_bilgi = new JPanel(new GridLayout(2, 1));
        pnl_bilgi.setOpaque(false);

        lbl_secilen_koltuklar = new JLabel("Lütfen Sefer Seçiniz");
        lbl_secilen_koltuklar.setForeground(new Color(200, 200, 200));
        lbl_secilen_koltuklar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        lbl_toplam_tutar = new JLabel("Toplam: 0 TL");
        lbl_toplam_tutar.setForeground(COLOR_ACCENT); // Mavi ton
        lbl_toplam_tutar.setFont(new Font("Segoe UI", Font.BOLD, 22));

        pnl_bilgi.add(lbl_secilen_koltuklar);
        pnl_bilgi.add(lbl_toplam_tutar);

        btn_satin_al = createStyledButton(isAdminMode ? "SATIŞI TAMAMLA" : "SATIN AL", COLOR_BTN_ORANGE);

        btn_satin_al.addActionListener(e -> satisIsleminiBaslat());

        pnl_odeme.add(pnl_bilgi, BorderLayout.CENTER);
        pnl_odeme.add(btn_satin_al, BorderLayout.EAST);
        sag_panel.add(pnl_odeme, BorderLayout.SOUTH);
        add(sag_panel, BorderLayout.EAST);

        new Timer(500, e -> {
            int tutar = otobusSema.getToplamTutar();
            if(tutar > 0) {
                lbl_secilen_koltuklar.setText("Koltuklar: " + otobusSema.getSecilenKoltuklarStr());
                lbl_toplam_tutar.setText("Toplam: " + tutar + " TL");
                btn_satin_al.setEnabled(true);
            } else {
                lbl_toplam_tutar.setText("Toplam: 0 TL");
                btn_satin_al.setEnabled(false);
            }
        }).start();
    }

    public void seferleriGetir() {
        model.setRowCount(0);
        try {
            Connection conn = DB_Baglanti.baglan();
            String sql = "SELECT s.id, s.tarih, s.saat, s.nereden, s.nereye, s.fiyat, a.plaka, a.marka_model " +
                    "FROM seferler s " +
                    "LEFT JOIN araclar a ON s.arac_id = a.id " +
                    "WHERE STR_TO_DATE(CONCAT(s.tarih, ' ', s.saat), '%d.%m.%Y %H:%i') > NOW() " +
                    "ORDER BY s.id DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int id = rs.getInt("id");
                String guzergah = rs.getString("nereden") + " > " + rs.getString("nereye");

                String arac = "Atanmamış";
                if(rs.getString("plaka") != null) {
                    arac = rs.getString("plaka") + " (" + rs.getString("marka_model") + ")";
                }

                model.addRow(new Object[]{
                        id,
                        rs.getString("tarih"),
                        rs.getString("saat"),
                        guzergah,
                        arac,
                        rs.getString("fiyat") + " TL"
                });
            }
            conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void satisIsleminiBaslat() {
        ArrayList<String> koltuklar = otobusSema.secilenKoltuklar;
        Map<String, String> cinsiyetler = otobusSema.secilenCinsiyetler;

        if (koltuklar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen koltuk seçiniz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ArrayList<String[]> satisListesi = new ArrayList<>();

        for (String koltukNo : koltuklar) {
            String cinsiyet = cinsiyetler.get(koltukNo);

            JTextField txtAd = new JTextField();
            JTextField txtTc = new JTextField();

            Object[] message = {
                    "Sefer: " + secilenGuzergah,
                    "Koltuk: " + koltukNo + " (" + cinsiyet + ")",
                    "Yolcu Adı Soyadı:", txtAd,
                    "TC Kimlik No:", txtTc
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Yolcu Bilgileri", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                if(!txtAd.getText().isEmpty() && txtTc.getText().length() == 11) {
                    satisListesi.add(new String[]{koltukNo, cinsiyet, txtAd.getText(), txtTc.getText()});
                } else {
                    JOptionPane.showMessageDialog(this, "Eksik veya hatalı bilgi!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                return;
            }
        }

        int onay = JOptionPane.showConfirmDialog(this, satisListesi.size() + " bilet kesilecek. Onaylıyor musunuz?", "Onay", JOptionPane.YES_NO_OPTION);
        if (onay == JOptionPane.YES_OPTION) {
            dbKayitYap(satisListesi);
        }
    }

    // db
    private void dbKayitYap(ArrayList<String[]> satisListesi) {
        Connection conn = null;
        PreparedStatement ps = null;
        System.out.println("DEBUG: Şu anki Müşteri ID: " + currentMusteriId);
        try {
            conn = DB_Baglanti.baglan();
            String sql = "INSERT INTO biletler (pnr_kod, sefer_id, musteri_id, koltuk_no, cinsiyet, yolcu_ad, yolcu_tc, tutar, durum) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql);

            for (String[] veri : satisListesi) {
                String pnr = "PNR" + (new Random().nextInt(9000) + 1000) + veri[0];

                ps.setString(1, pnr);
                ps.setInt(2, secilenSeferId);

                if (currentMusteriId != -1) {
                    ps.setInt(3, currentMusteriId);
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }

                ps.setInt(4, Integer.parseInt(veri[0]));
                ps.setString(5, veri[1]);
                ps.setString(6, veri[2]);
                ps.setString(7, veri[3]);
                ps.setInt(8, biletFiyati);
                ps.setString(9, "Aktif");

                ps.addBatch();
            }

            ps.executeBatch();
            JOptionPane.showMessageDialog(this, "Satış Başarılı!!! \nİyi yolculuklar dileriz :D");

            otobusSema.otobusuCiz(biletFiyati, secilenSeferId);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı Hatası: " + e.getMessage());
        } finally {
            try { if(ps!=null) ps.close(); DB_Baglanti.kapatan(conn); } catch(Exception e){}
        }
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

        // Özel Satır Renklendirme
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? COLOR_TABLE_ROW_1 : COLOR_TABLE_ROW_2);
                } else {
                    c.setBackground(COLOR_ACCENT); // Seçili mavi
                }
                c.setForeground(COLOR_TEXT);
                return c;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(40);

        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(220);
        table.getColumnModel().getColumn(4).setPreferredWidth(250);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? bg : Color.GRAY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20)); // Yuvarlak
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 40));
        return btn;
    }

    private void styleComboBox(JComboBox box, String title) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 50)),
                title,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                Color.WHITE));
    }

    public void seferleriFiltrele(String nereden, String nereye, String tarih) {
        model.setRowCount(0);
        boolean kayitBulundu = false;

        try {
            Connection conn = DB_Baglanti.baglan();
            String sql = "SELECT s.id, s.tarih, s.saat, s.nereden, s.nereye, s.fiyat, a.plaka, a.marka_model " +
                    "FROM seferler s " +
                    "LEFT JOIN araclar a ON s.arac_id = a.id " +
                    "WHERE s.nereden = ? AND s.nereye = ? AND s.tarih = ? " +
                    "AND STR_TO_DATE(s.tarih, '%d.%m.%Y') >= CURDATE() " +
                    "ORDER BY s.saat ASC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nereden);
            ps.setString(2, nereye);
            ps.setString(3, tarih);

            java.sql.ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                kayitBulundu = true;
                int id = rs.getInt("id");
                String guzergah = rs.getString("nereden") + " > " + rs.getString("nereye");
                String arac = (rs.getString("plaka") != null) ? rs.getString("plaka") + " (" + rs.getString("marka_model") + ")" : "-";

                model.addRow(new Object[]{
                        id,
                        rs.getString("tarih"),
                        rs.getString("saat"),
                        guzergah,
                        arac,
                        rs.getString("fiyat") + " TL"
                });
            }
            conn.close();

            if (!kayitBulundu) {
                JOptionPane.showMessageDialog(this, "Aradığınız kriterlere uygun güncel sefer bulunamadı.", "Sonuç Yok", JOptionPane.INFORMATION_MESSAGE);
                seferleriGetir();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
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
}