import database.DB_Baglanti;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrafikPanel {

    // renkler
    private final Color COLOR_TEXT = new Color(236, 240, 241);
    private final Color COLOR_AXIS = new Color(149, 165, 166);

    private final Color[] BAR_COLORS = {
            new Color(52, 152, 219),
            new Color(46, 204, 113),
            new Color(155, 89, 182),
            new Color(241, 196, 15),
            new Color(231, 76, 60)
    };

    // linechart
    private final Color LINE_COLOR = new Color(46, 204, 113); //ana çizgi
    private final Color LINE_FILL_START = new Color(46, 204, 113, 100); //gradyan başlangıç
    private final Color LINE_FILL_END = new Color(46, 204, 113, 0);     //gradyan bitişi
    private final Color HOVER_GUIDE_COLOR = new Color(255, 255, 255, 150); //hover

    public GrafikPanel() { }

    // sol grafik popüler rotalarım
    public JPanel getPopulerRotaPanel() {
        Map<String, Integer> data = new HashMap<>();
        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();
            String sql = "SELECT s.nereye, COUNT(*) as sayi FROM biletler b JOIN seferler s ON b.sefer_id = s.id WHERE b.durum = 'Aktif' GROUP BY s.nereye ORDER BY sayi DESC LIMIT 5";
            ResultSet rs = conn.prepareStatement(sql).executeQuery();
            while(rs.next()) data.put(rs.getString("nereye"), rs.getInt("sayi"));
        } catch (Exception e) { e.printStackTrace(); } finally { DB_Baglanti.kapatan(conn); }
        return new CustomBarChart("En Popüler 5 Rota", data);
    }

    // kar grsfiği linechart
    public JPanel getAylikKarPanel() {
        ArrayList<Double> vList = new ArrayList<>(); ArrayList<String> lList = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DB_Baglanti.baglan();
            String sql = "SELECT s.tarih, SUM(b.tutar) as ciro FROM biletler b JOIN seferler s ON b.sefer_id = s.id WHERE b.durum = 'Aktif' GROUP BY s.tarih ORDER BY STR_TO_DATE(s.tarih, '%d.%m.%Y') DESC LIMIT 30";
            ResultSet rs = conn.prepareStatement(sql).executeQuery();
            while(rs.next()) { lList.add(rs.getString("tarih").substring(0, 5)); vList.add(rs.getDouble("ciro")); }
            Collections.reverse(lList); Collections.reverse(vList);
        } catch (Exception e) { e.printStackTrace(); } finally { DB_Baglanti.kapatan(conn); }
        return new CustomLineChart("Son 30 Günlük Kâr Analizi", vList.stream().mapToDouble(d->d).toArray(), lList.toArray(new String[0]));
    }
    // barlar
    private class CustomBarChart extends JPanel {
        private String title; Map<String, Integer> data;
        public CustomBarChart(String title, Map<String, Integer> data) {
            this.title = title; this.data = data;
            setOpaque(false);
            setBorder(BorderFactory.createLineBorder(new Color(255,255,255,15), 1));
        }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight(), p=40;
            g2.setColor(COLOR_TEXT); g2.setFont(new Font("Segoe UI", Font.BOLD, 18)); g2.drawString(title, p, 30);
            g2.setColor(COLOR_AXIS); g2.drawLine(p, h-p, w-p, h-p); g2.drawLine(p, h-p, p, p+20);
            if(data.isEmpty()) { g2.drawString("Veri Yok", w/2, h/2); return; }
            int bw=(w-(2*p))/data.size()-30; int max=data.values().stream().max(Integer::compare).orElse(100); if(max==0)max=100;
            int x=p+20; int i=0;
            for(Map.Entry<String,Integer> e:data.entrySet()){
                int v=e.getValue(); int bh=(int)((double)v/max*(h-2*p-40));
                g2.setColor(BAR_COLORS[i % BAR_COLORS.length]);
                g2.fillRoundRect(x, h-p-bh, bw, bh, 12, 12);
                g2.setColor(COLOR_TEXT); g2.setFont(new Font("Segoe UI",Font.BOLD,12)); g2.drawString(String.valueOf(v), x+(bw-g2.getFontMetrics().stringWidth(String.valueOf(v)))/2, h-p-bh-5);
                g2.setColor(COLOR_AXIS); g2.setFont(new Font("Segoe UI",Font.PLAIN,11)); String k=e.getKey(); if(k.length()>8)k=k.substring(0,6)+".."; g2.drawString(k, x+(bw-g2.getFontMetrics().stringWidth(k))/2, h-p+15);
                x+=bw+30; i++;
            }
        }
    }

    // imlece duyarlı chart
    private class CustomLineChart extends JPanel {
        private String title; double[] v; String[] l;
        private List<Point> graphPoints = new ArrayList<>();
        private int hoveredIndex = -1;

        public CustomLineChart(String title, double[] v, String[] l) {
            this.title = title; this.v = v; this.l = l;
            setOpaque(false);
            setBorder(BorderFactory.createLineBorder(new Color(255,255,255,15), 1));

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    int mouseX = e.getX(); int bestIndex = -1; int minDist = 30;
                    for(int i=0; i<graphPoints.size(); i++) {
                        if(Math.abs(mouseX - graphPoints.get(i).x) < minDist) { bestIndex = i; break; }
                    }
                    if(hoveredIndex != bestIndex) { hoveredIndex = bestIndex; repaint(); }
                }
            });
        }

        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight(), p=50; graphPoints.clear();

            g2.setColor(COLOR_TEXT); g2.setFont(new Font("Segoe UI", Font.BOLD, 18)); g2.drawString(title, p, 30);
            g2.setColor(COLOR_AXIS); g2.drawLine(p, h-p, w-p, h-p); g2.drawLine(p, h-p, p, p+20);

            if(v.length<2) { g2.setColor(COLOR_AXIS); g2.drawString("Yeterli Veri Yok", w/2, h/2); return; }

            double max=0; for(double val:v)if(val>max)max=val; if(max==0)max=1000; max*=1.1;
            int xs=(w-2*p)/(v.length-1);
            Path2D.Double path=new Path2D.Double();

            for(int i=0;i<v.length;i++){
                int x=p+(i*xs); int y=h-p-(int)((v[i]/max)*(h-2*p-40));
                graphPoints.add(new Point(x, y));
                if(i==0)path.moveTo(x,y); else path.lineTo(x,y);
                if(v.length>15?(i%5==0):(true)) { g2.setColor(COLOR_AXIS); g2.setFont(new Font("Segoe UI",Font.PLAIN,10)); g2.drawString(l[i], x-10, h-p+20); }
            }
            path.lineTo(graphPoints.get(graphPoints.size()-1).x, h-p); path.lineTo(graphPoints.get(0).x, h-p); path.closePath();

            //alan boyaması
            GradientPaint gp=new GradientPaint(0, p, LINE_FILL_START, 0, h-p, LINE_FILL_END); g2.setPaint(gp); g2.fill(path);
            //çizgi
            g2.setColor(LINE_COLOR); g2.setStroke(new BasicStroke(2.5f));
            Path2D.Double lp=new Path2D.Double(); lp.moveTo(graphPoints.get(0).x, graphPoints.get(0).y); for(int i=1;i<graphPoints.size();i++)lp.lineTo(graphPoints.get(i).x, graphPoints.get(i).y); g2.draw(lp);

            //hover için
            if(hoveredIndex != -1 && hoveredIndex < graphPoints.size()) {
                Point pt = graphPoints.get(hoveredIndex);

                g2.setColor(HOVER_GUIDE_COLOR); g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f}, 0.0f));
                g2.drawLine(pt.x, p, pt.x, h-p);
                //nokta
                g2.setStroke(new BasicStroke(1f)); g2.setColor(LINE_COLOR); g2.fillOval(pt.x-6, pt.y-6, 12, 12); g2.setColor(Color.WHITE); g2.drawOval(pt.x-6, pt.y-6, 12, 12);

                //bilgi için
                String txtDate = "Tarih: " + l[hoveredIndex];
                String txtVal = "Kâr: " + new DecimalFormat("#,###").format(v[hoveredIndex]) + " TL";
                int boxW=120, boxH=50, boxX=pt.x-boxW/2, boxY=pt.y-boxH-15;
                if(boxX<p) boxX=p; if(boxX+boxW>w-p) boxX=w-p-boxW; if(boxY<p) boxY=pt.y+15;

                g2.setColor(new Color(44, 62, 80, 230)); g2.fill(new RoundRectangle2D.Float(boxX, boxY, boxW, boxH, 10, 10));
                g2.setColor(COLOR_TEXT); g2.setFont(new Font("Segoe UI", Font.PLAIN, 11)); g2.drawString(txtDate, boxX+10, boxY+20);
                g2.setColor(LINE_COLOR); g2.setFont(new Font("Segoe UI", Font.BOLD, 13)); g2.drawString(txtVal, boxX+10, boxY+40);
            }
        }
    }
}