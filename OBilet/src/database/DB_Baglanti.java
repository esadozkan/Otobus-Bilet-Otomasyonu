package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DB_Baglanti {
    // veritabanı bilgileri
    // url: localhost:3306 - localde 3306 ıncı porttan bağlıyoz
    // test_db - shema adımız (Test için bunu yaptım)
    // NOT: Yorumların orijinal kalsa da aşağıdaki adres artık Bulut Sunucudur.
    public static final String db_url = "jdbc:mysql://sql7.freesqldatabase.com:3306/sql7815886?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Europe/Istanbul&useSSL=false";
    public  static final String db_user = "sql7815886";  // mysql de belirlediğimiz user ile passwordu buraya yazmamaız gerek

    public  static final String db_password = "VYWCN5B5iH";

    //yukarda static final kullandım çünkü bu bilgilerin ilerde değişmemmesi gerkeiyor ve her yerden doğru erişmemiz gerek ondan

    public static Connection baglan() {
        Connection baglanti = null;
        try {
            baglanti = DriverManager.getConnection(db_url, db_user, db_password);      // lib de ki jar dosyamızı kullanarak fiziksel olarak db ye bağlanmaya çalışıyorum
            System.out.println("Veritabani baglantisi başarili gerçekleşti");
        } catch (SQLException e) {              // eger bağlanmazsa diye try catch ile kontrol sağlıyorum ve mesajı terminale basıyorum
            System.err.println("Baglanti hatasi = " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Veritabanına bağlanılmadı\n Terminali kontrol et hata mesajı basıldı \n Aşağıdaki adımları da kontrol et \n MySQL Server açık mı ? \n Şifren doğru mu (DB şifresi)\n", "Bağlantı Hatası",JOptionPane.ERROR_MESSAGE);
        }
        return baglanti;
    }

    // bağlantı kapatma methodu
    public static void kapatan(Connection baglanti) {
        if (baglanti != null) {
            try {
                baglanti.close();
            } catch (SQLException ex) {
                System.err.println("Bağlantı kapatma hatası = " + ex.getMessage());
            }
        }
    }
}