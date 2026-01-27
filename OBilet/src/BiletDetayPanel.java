import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.Rectangle;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BiletDetayPanel extends JPanel {

    private String kalkis, varis, tarih, saat, koltuk, pnr, tutar, yolcu;

    private  String plaka; // plaka
    private Image imgOtobus;

    // renkler
    private final Color COLOR_HEADER = new Color(52, 152, 219);
    private final Color COLOR_BG = new Color(245, 248, 250);
    private final Color COLOR_TEXT_DARK = new Color(44, 62, 80);

    public BiletDetayPanel(String kalkis, String varis, String tarih, String saat, String koltuk, String pnr, String tutar, String yolcu, String plaka) {
        this.kalkis = kalkis;
        this.varis = varis;
        this.tarih = tarih;
        this.saat = saat;
        this.koltuk = koltuk;
        this.pnr = pnr;
        this.tutar = tutar;
        this.yolcu = yolcu;
        this.plaka = plaka;

        setOpaque(false);
        setPreferredSize(new Dimension(600, 220));
        setLayout(null);
        imgOtobus = resimYukle("/icons/otobus_icon.png", 60, 60);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int splitX = (int) (w * 0.7);

        //zaman kontrolü
        boolean seferGecmis = isSeferGecmisMi();

        int kagitGenisligi = seferGecmis ? splitX : w;

        Shape outer = new RoundRectangle2D.Float(0, 0, kagitGenisligi, h, 20, 20);
        Area area = new Area(outer);

        int holeSize = 20;
        Area topHole = new Area(new Ellipse2D.Float(splitX - holeSize / 2, -holeSize / 2, holeSize, holeSize));
        Area bottomHole = new Area(new Ellipse2D.Float(splitX - holeSize / 2, h - holeSize / 2, holeSize, holeSize));

        area.subtract(topHole);
        area.subtract(bottomHole);

        g2d.setColor(COLOR_BG);
        g2d.fill(area);

        Shape headerShape = new RoundRectangle2D.Float(0, 0, splitX, 50, 20, 20);
        Area headerArea = new Area(headerShape);

        Area rectFix = new Area(new Rectangle(20, 0, splitX - 20, 50));
        headerArea.add(rectFix);

        headerArea.subtract(topHole);

        g2d.setColor(COLOR_HEADER);
        g2d.fill(headerArea);


        if (!isSeferGecmisMi()) {

            // A) Kesik Çizgi
            Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
            g2d.setStroke(dashed);
            g2d.setColor(Color.GRAY);
            g2d.drawLine(splitX, 10, splitX, h - 10);

            // B) Sağ Taraf Yazıları
            int rightMargin = splitX + 20;

            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2d.drawString("YOLCU ADI", rightMargin, 30);

            g2d.setColor(COLOR_TEXT_DARK);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2d.drawString(yolcu.toUpperCase(), rightMargin, 55);

            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2d.drawString("PNR KODU", rightMargin, 90);

            g2d.setColor(COLOR_HEADER);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 20));
            g2d.drawString(pnr, rightMargin, 115);


        }
        else {

        }
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g2d.drawString("OTOBÜS BİLETİ", 20, 35);

        //otobüs
        if (imgOtobus != null) {
            g2d.drawImage(imgOtobus, 30, 80, this); // 'null' yerine 'this' yaz ki panel hazır olunca çizsin
        }

        // sol detaylar
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2d.drawString("NEREDEN", 120, 80);
        g2d.drawString("NEREYE", 120, 140);

        g2d.setColor(COLOR_TEXT_DARK);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2d.drawString(kalkis.toUpperCase(), 120, 105);
        g2d.drawString(varis.toUpperCase(), 120, 165);

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(122, 115, 2, 20);

        // tarih ve saat
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2d.drawString("TARİH", 280, 80);
        g2d.drawString("SAAT", 280, 140);

        g2d.setColor(COLOR_TEXT_DARK);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2d.drawString(tarih, 280, 105);
        g2d.drawString(saat, 280, 165);

        g2d.setColor(COLOR_TEXT_DARK);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.drawString("ARAÇ: " + plaka, 120, 200);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g2d.drawString("NO: " + koltuk, 280, 200);

    }

    private Image resimYukle(String yol, int genislik, int yukseklik) {
        if (getClass().getResource(yol) == null) return null;
        return new ImageIcon(getClass().getResource(yol)).getImage()
                .getScaledInstance(genislik, yukseklik, Image.SCALE_SMOOTH);
    }
    private boolean isSeferGecmisMi() {
        try {
            String birlesikZaman = tarih + " " + saat;
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

            LocalDateTime seferZamani = LocalDateTime.parse(birlesikZaman, format);
            LocalDateTime suAn = LocalDateTime.now();

            return suAn.isAfter(seferZamani);
        } catch (Exception e) {
            return false;
        }
    }
}
