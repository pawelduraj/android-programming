import java.sql.*

fun main() {
    val c: Connection
    c = DriverManager.getConnection("jdbc:sqlite:sample.db")

    val s: Statement = c.createStatement()
    s.executeUpdate("DROP TABLE IF EXISTS test")
    s.executeUpdate("CREATE TABLE test(t1 TEXT, t2 TEXT)")
    s.executeUpdate("INSERT INTO test(t1, t2) VALUES ('Hello', 'World!')")

    val rs: ResultSet = s.executeQuery("SELECT * FROM test")
    while (rs.next()) {
        println(rs.getString("t1") + " " + rs.getString("t2"));
    }
}
