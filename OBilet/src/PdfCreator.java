import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfCreator {

    private static final Color COLOR_HEADER = new Color(52, 152, 219);
    private static final Color COLOR_TEXT_DARK = new Color(44, 62, 80);
    private static final Color COLOR_BG = new Color(245, 248, 250);

    // DİKKAT: 'String plaka' parametresini buradan kaldırdık
    public static void biletOlustur(String dosyaYolu, String kalkis, String varis,
                                    String tarih, String saat, String koltuk,
                                    String pnr, String tutar, String yolcu) {

        Document document = new Document(PageSize.A4);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dosyaYolu));
            document.open();

            PdfContentByte canvas = writer.getDirectContent();
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, "Cp1254", BaseFont.NOT_EMBEDDED);
            BaseFont bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, "Cp1254", BaseFont.NOT_EMBEDDED);

            float x = 40; float y = 600; float w = 520; float h = 200;

            // Arka Plan
            canvas.setColorFill(COLOR_BG);
            canvas.roundRectangle(x, y, w, h, 20);
            canvas.fill();

            // Mavi Başlık
            float headerW = w * 0.7f;
            canvas.setColorFill(COLOR_HEADER);
            canvas.roundRectangle(x, y + h - 50, headerW, 50, 20);
            canvas.fill();
            canvas.rectangle(x, y + h - 25, headerW, 25);
            canvas.fill();

            canvas.beginText();
            canvas.setFontAndSize(bfBold, 24);
            canvas.setColorFill(Color.WHITE);
            canvas.showTextAligned(Element.ALIGN_LEFT, "OTOBÜS BİLETİ", x + 20, y + h - 35, 0);
            canvas.endText();

            // Kesik Çizgi
            canvas.saveState();
            canvas.setColorStroke(Color.GRAY);
            canvas.setLineDash(3, 3);
            canvas.moveTo(x + headerW, y + 10);
            canvas.lineTo(x + headerW, y + h - 10);
            canvas.stroke();
            canvas.restoreState();

            // İkon
            try {
                java.net.URL imgUrl = PdfCreator.class.getResource("/icons/otobus_icon.png");
                if (imgUrl != null) {
                    Image img = Image.getInstance(imgUrl);
                    img.scaleToFit(50, 50);
                    img.setAbsolutePosition(x + 30, y + h - 120);
                    canvas.addImage(img);
                }
            } catch (Exception e) { }

            // Bilgiler
            yazdirEtiketDeger(canvas, bf, bfBold, "NEREDEN", kalkis.toUpperCase(), x + 120, y + h - 90);
            yazdirEtiketDeger(canvas, bf, bfBold, "NEREYE", varis.toUpperCase(), x + 120, y + h - 140);

            canvas.saveState();
            canvas.setColorFill(Color.LIGHT_GRAY);
            canvas.rectangle(x + 122, y + h - 115, 2, 15);
            canvas.fill();
            canvas.restoreState();

            yazdirEtiketDeger(canvas, bf, bfBold, "TARİH", tarih, x + 280, y + h - 90);
            yazdirEtiketDeger(canvas, bf, bfBold, "SAAT", saat, x + 280, y + h - 140);

            // Alt Kısım (Araç bilgisi kalktı, Koltuk No kaldı)
            canvas.beginText();
            canvas.setColorFill(COLOR_HEADER);
            canvas.setFontAndSize(bfBold, 22);
            // Koltuk numarasını biraz daha sola çekebiliriz artık yer açıldı
            canvas.showTextAligned(Element.ALIGN_LEFT, "NO: " + koltuk, x + 120, y + 20, 0);
            canvas.endText();

            // Sağ Taraf
            float rightX = x + headerW + 20;
            yazdirEtiketDeger(canvas, bf, bfBold, "YOLCU ADI", yolcu.toUpperCase(), rightX, y + h - 60);

            canvas.beginText();
            canvas.setFontAndSize(bf, 10);
            canvas.setColorFill(Color.GRAY);
            canvas.showTextAligned(Element.ALIGN_LEFT, "PNR KODU", rightX, y + h - 100, 0);

            canvas.setFontAndSize(bfBold, 16);
            canvas.setColorFill(COLOR_HEADER);
            canvas.showTextAligned(Element.ALIGN_LEFT, pnr, rightX, y + h - 125, 0);

            canvas.setFontAndSize(bfBold, 14);
            canvas.setColorFill(COLOR_TEXT_DARK);
            canvas.showTextAligned(Element.ALIGN_LEFT, tutar + " TL", rightX, y + 20, 0);

            canvas.setFontAndSize(bf, 8);
            canvas.setColorFill(Color.GRAY);
            canvas.showTextAligned(Element.ALIGN_CENTER, "Çağdaş Güven Turizm", x + w/2, y - 15, 0);
            canvas.endText();

            document.close();
            System.out.println("PDF kaydedildi: " + dosyaYolu);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void yazdirEtiketDeger(PdfContentByte canvas, BaseFont bf, BaseFont bfBold,
                                          String etiket, String deger, float x, float y) {
        canvas.beginText();
        canvas.setFontAndSize(bf, 10);
        canvas.setColorFill(Color.GRAY);
        canvas.showTextAligned(Element.ALIGN_LEFT, etiket, x, y + 12, 0);
        canvas.setFontAndSize(bfBold, 14);
        canvas.setColorFill(COLOR_TEXT_DARK);
        canvas.showTextAligned(Element.ALIGN_LEFT, deger, x, y - 5, 0);
        canvas.endText();
    }
}