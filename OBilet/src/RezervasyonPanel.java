import database.DB_Baglanti;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RezervasyonPanel extends JPanel {

    // tablo
    private JTable tablo;
    private DefaultTableModel model;
    private JTextField txt_ara;
    private TableRowSorter<DefaultTableModel> sorter;

    // alt biletdetay kısmı
    private JPanel biletContainer;
    private JLabel lblBos;

    private int seciliBiletID = -1;
    private String seciliBiletDurumu = "";

    // --- RENK PALETİ ---
    private final Color COLOR_BG = new Color(52, 73, 94);
    private final Color COLOR_PANEL_BG = new Color(44, 62, 80);
    private final Color COLOR_TEXT = new Color(236, 240, 241);

    // Tablo ve Buton Renkleri
    private final Color COLOR_TABLE_HEADER = new Color(44, 62, 80);
    private final Color COLOR_TABLE_ROW_1 = new Color(52, 73, 94);
    private final Color COLOR_TABLE_ROW_2 = new Color(60, 80, 100);
    private final Color COLOR_BTN_MAVI = new Color(52, 152, 219);
    private final Color COLOR_BTN_KIRMIZI = new Color(192, 57, 43);

    // Renkler-İptal
    private final Color COLOR_IPTAL_BG = new Color(80, 30, 30);
    private final Color COLOR_IPTAL_FG = new Color(255, 100, 100);

    public RezervasyonPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 20));
        setOpaque(false); // Ana panel şeffaf kalabilir, sorun inputlarda
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel ust_panel = new JPanel(new BorderLayout(0, 10));
        ust_panel.setOpaque(false);

        JPanel arama_paneli = new JPanel(new BorderLayout());
        arama_paneli.setBackground(COLOR_PANEL_BG); // Burayı mat yaptık
        arama_paneli.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lbl_ara = new JLabel("Bilet Ara (PNR/İsim):  ");
        lbl_ara.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_ara.setForeground(COLOR_TEXT);

        // TEXTFIELD ARTIK ŞEFFAF DEĞİL, MAT. SORUN ÇÖZÜLDÜ.
        txt_ara = new JTextField();
        styleTextField(txt_ara);

        arama_paneli.add(lbl_ara, BorderLayout.WEST);
        arama_paneli.add(txt_ara, BorderLayout.CENTER);
        ust_panel.add(arama_paneli, BorderLayout.NORTH);

        // tablo
        JPanel tablo_paneli = new JPanel(new BorderLayout());
        tablo_paneli.setOpaque(false);
        tablo_paneli.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255,255,255,50)),
                "Rezervasyon Listesi",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                COLOR_TEXT));

        String[] kolonlar = {"ID", "PNR", "Yolcu", "Güzergah", "Tarih", "Araç", "Koltuk", "Tutar", "Durum"};

        model = new DefaultTableModel(null, kolonlar) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        // RENDERER MANTIĞI KORUNDU
        tablo = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    int modelRow = convertRowIndexToModel(row);
                    String durum = (String) model.getValueAt(modelRow, 8);

                    if ("İptal".equalsIgnoreCase(durum)) {
                        c.setBackground(COLOR_IPTAL_BG);
                        c.setForeground(COLOR_IPTAL_FG);
                    } else {
                        c.setBackground(row % 2 == 0 ? COLOR_TABLE_ROW_1 : COLOR_TABLE_ROW_2);
                        c.setForeground(Color.WHITE);
                    }
                } else {
                    c.setBackground(COLOR_BTN_MAVI);
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        };

        styleTable(tablo);

        tablo.getColumnModel().getColumn(0).setMinWidth(0);
        tablo.getColumnModel().getColumn(0).setMaxWidth(0);
        tablo.getColumnModel().getColumn(0).setWidth(0);

        sorter = new TableRowSorter<>(model);
        tablo.setRowSorter(sorter);
        txt_ara.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = txt_ara.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else try { sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); } catch(Exception ex){}
            }
        });

        // tıklama aksiyonu
        tablo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablo.getSelectedRow();
                if (row != -1) {
                    int modelRow = tablo.convertRowIndexToModel(row);

                    seciliBiletID = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
                    String pnr = model.getValueAt(modelRow, 1).toString();
                    String yolcu = model.getValueAt(modelRow, 2).toString();
                    String guzergah = model.getValueAt(modelRow, 3).toString();
                    String tarihSaat = model.getValueAt(modelRow, 4).toString();
                    String arac = model.getValueAt(modelRow, 5).toString();
                    String koltuk = model.getValueAt(modelRow, 6).toString();
                    String tutar = model.getValueAt(modelRow, 7).toString();
                    seciliBiletDurumu = model.getValueAt(modelRow, 8).toString();

                    guncelleBiletGosterimi(pnr, yolcu, guzergah, tarihSaat, arac, koltuk, tutar);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablo);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(COLOR_TABLE_ROW_1);
        scrollPane.setOpaque(false);
        tablo_paneli.add(scrollPane, BorderLayout.CENTER);

        ust_panel.add(tablo_paneli, BorderLayout.CENTER);
        add(ust_panel, BorderLayout.CENTER);

        //altpanel
        JPanel alt_panel = new JPanel(new BorderLayout());
        alt_panel.setPreferredSize(new Dimension(0, 300));
        alt_panel.setOpaque(false);
        alt_panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        biletContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        biletContainer.setOpaque(false);

        lblBos = new JLabel("<html><center>Listeden bir bilet seçin<br>Detaylar burada görünecek</center></html>");
        lblBos.setForeground(Color.GRAY);
        lblBos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        biletContainer.add(lblBos);

        alt_panel.add(biletContainer, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        JButton btn_yenile = createButton("LİSTEYİ YENİLE", COLOR_BTN_MAVI);
        btn_yenile.addActionListener(e -> biletleriListele());

        JButton btn_iptal = createButton("BİLETİ İPTAL ET / İADE", COLOR_BTN_KIRMIZI);
        btn_iptal.setPreferredSize(new Dimension(200, 45));
        btn_iptal.addActionListener(e -> biletIptalEt());

        btnPanel.add(btn_yenile);
        btnPanel.add(btn_iptal);
        alt_panel.add(btnPanel, BorderLayout.SOUTH);

        add(alt_panel, BorderLayout.SOUTH);

        biletleriListele();
    }

    private void biletleriListele() {
        model.setRowCount(0);
        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();
            String sql = "SELECT b.id, b.pnr_kod, b.yolcu_ad, " +
                    "s.nereden, s.nereye, s.tarih, s.saat, " +
                    "a.plaka, b.koltuk_no, b.tutar, b.durum " +
                    "FROM biletler b " +
                    "JOIN seferler s ON b.sefer_id = s.id " +
                    "LEFT JOIN araclar a ON s.arac_id = a.id " +
                    "ORDER BY b.id DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                String guzergah = rs.getString("nereden") + " > " + rs.getString("nereye");
                String tarihSaat = rs.getString("tarih") + " " + rs.getString("saat");
                String plaka = (rs.getString("plaka") != null) ? rs.getString("plaka") : "Belirsiz";

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("pnr_kod"),
                        rs.getString("yolcu_ad"),
                        guzergah,
                        tarihSaat,
                        plaka,
                        rs.getInt("koltuk_no"),
                        rs.getInt("tutar") + " TL",
                        rs.getString("durum")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DB_Baglanti.kapatan(conn);
        }
    }

    private void biletIptalEt() {
        if (seciliBiletID == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen işlem yapılacak bileti seçiniz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("İptal".equalsIgnoreCase(seciliBiletDurumu)) {
            JOptionPane.showMessageDialog(this, "Bu bilet zaten iptal edilmiş!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int secim = JOptionPane.showConfirmDialog(this,
                "Bu bileti İPTAL ETMEK istediğinize emin misiniz?\nDurumu 'İptal' olarak güncellenecektir.",
                "İptal Onayı", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (secim == JOptionPane.YES_OPTION) {
            Connection conn = null;
            try {
                conn = DB_Baglanti.baglan();
                String sql = "UPDATE biletler SET durum = 'İptal' WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, seciliBiletID);

                int etkilenen = ps.executeUpdate();
                if (etkilenen > 0) {
                    JOptionPane.showMessageDialog(this, "Bilet Durumu 'İptal' Olarak Güncellendi.");
                    biletleriListele();

                    biletContainer.removeAll();
                    biletContainer.add(lblBos);
                    biletContainer.repaint();
                    biletContainer.revalidate();

                    seciliBiletID = -1;
                    seciliBiletDurumu = "";
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
            } finally {
                DB_Baglanti.kapatan(conn);
            }
        }
    }

    private void guncelleBiletGosterimi(String pnr, String yolcu, String guzergah, String tarihSaat, String arac, String koltuk, String tutar) {
        biletContainer.removeAll();

        String tarih = "", saat = "";
        try {
            String[] ts = tarihSaat.split(" ");
            tarih = ts[0];
            saat = (ts.length > 1) ? ts[1] : "";
        } catch (Exception e) { tarih = tarihSaat; }

        String nereden = "", nereye = "";
        try {
            String[] gz = guzergah.split(" > ");
            nereden = gz[0];
            nereye = (gz.length > 1) ? gz[1] : "";
        } catch (Exception e) { nereden = guzergah; }

        BiletDetayPanel bilet = new BiletDetayPanel(nereden, nereye, tarih, saat, koltuk, pnr, tutar, yolcu, arac);
        biletContainer.add(bilet);
        biletContainer.revalidate();
        biletContainer.repaint();
    }

    // --- STYLING METOTLARI (GÖLGELENME ÇÖZÜLDÜ) ---

    // TextField artık ŞEFFAF DEĞİL (Mat renk) -> Gölgelenme olmaz
    private void styleTextField(JTextField txt) {
        txt.setPreferredSize(new Dimension(0, 35));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setForeground(Color.WHITE); // Beyaz yazı
        txt.setCaretColor(Color.WHITE); // Beyaz imleç

        // Şeffaflığı KAPATIP mat renk veriyoruz.
        txt.setOpaque(true);
        txt.setBackground(new Color(60, 70, 80)); // Koyu gri arka plan

        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 50)),
                new EmptyBorder(0, 10, 0, 10)
        ));
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        // Buton şeffaflığını da kapatıyoruz ki temiz görünsün
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 45));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(COLOR_TABLE_ROW_1);
        table.setForeground(COLOR_TEXT);
        table.setGridColor(new Color(80, 90, 100));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(COLOR_TABLE_HEADER);
        header.setForeground(new Color(230, 230, 230));
        header.setOpaque(true);
    }
}