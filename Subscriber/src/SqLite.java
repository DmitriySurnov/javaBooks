import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SqLite implements AutoCloseable {
    private final Connection _dbConnection;

    public SqLite() throws SQLException, ClassNotFoundException {

        Class.forName("org.sqlite.JDBC");
        _dbConnection = DriverManager.getConnection("jdbc:sqlite:db.db");
        try {
            ExecuteWithoutResult("select ID from Readers");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    private void ExecuteWithoutResult(String sqlQuery) throws SQLException {
        var statement = _dbConnection.createStatement();
        statement.execute(sqlQuery);
    }

    public void WriteDBReader(String surname, String name, String patronymic) throws SQLException {

        String sqlQuery = "INSERT INTO 'Readers' ('Surname', 'Name', 'patronymic') VALUES ('" + surname +
                "', '" + name + "','" + patronymic + "'); ";
        ExecuteWithoutResult(sqlQuery);
    }

    public void WriteDBMovementBooks(int ReaderNumber, String DateIssue,
                                     String ApproximateDateDelivery, int BooksID) throws SQLException {

        String sqlQuery = "INSERT INTO 'MovementBooks' ('ReaderNumber', 'DateIssue','DateDelivery', " +
                "'ApproximateDateDelivery', 'BooksID') VALUES (" + ReaderNumber + ", '" + DateIssue + "','','" +
                ApproximateDateDelivery + "', '" + BooksID + "'); ";
        ExecuteWithoutResult(sqlQuery);
    }

    public ResultSet readerChoice(int idReader) throws SQLException {
        var statement = _dbConnection.createStatement();
        String sqlQuery = "select Name, Surname, patronymic from Readers  WHERE ID = " + idReader;
        return statement.executeQuery(sqlQuery);
    }

    public ResultSet readerBooks(int idBooks) throws SQLException {
        var statement = _dbConnection.createStatement();
        String sqlQuery = "select Surname, Name, patronymic, TitleBook " +
                "FROM Books JOIN Author on Books.AuthorID == Author.ID  WHERE Books.ID = " + idBooks;
        return statement.executeQuery(sqlQuery);
    }

    public ResultSet readerMovementBooks(int idBooks) throws SQLException {
        var statement = _dbConnection.createStatement();
        String sqlQuery = "select ID FROM MovementBooks " +
                "WHERE DateDelivery = '' and BooksID = " + idBooks;
        return statement.executeQuery(sqlQuery);
    }

    public ResultSet handOrExtendBook(int idReader) throws SQLException {
        var statement = _dbConnection.createStatement();
        String sqlQuery = "select MovementBooks.ID, TitleBook, Surname, name, patronymic " +
                "FROM MovementBooks " +
                "JOIN Books on MovementBooks.BooksID == Books.ID " +
                "JOIN Author on Books.AuthorID == Author.ID " +
                "WHERE MovementBooks.ReaderNumber = " + idReader + " AND DateDelivery = ''";
        return statement.executeQuery(sqlQuery);
    }

    public void BookExtension(int id, String data) throws SQLException {
        var statement = _dbConnection.createStatement();
        String sqlQuery = "UPDATE MovementBooks SET ApproximateDateDelivery = '"+data+"' "+
                " WHERE  ID = " + id;
        ExecuteWithoutResult(sqlQuery);
    }
    public void BookHand (int id, String data) throws SQLException {
        var statement = _dbConnection.createStatement();
        String sqlQuery = "UPDATE MovementBooks SET DateDelivery = '"+data+"', "+
                " 'ApproximateDateDelivery' = ''" +
                " WHERE  ID = " + id;
        ExecuteWithoutResult(sqlQuery);
    }

    public ResultSet Lastday() throws SQLException {
        var statement = _dbConnection.createStatement();
        LocalDate today = LocalDate.now();
        String sqlQuery = "select Readers.Name  as nameR, Readers.Surname as SurnameS, " +
                "Readers.patronymic as patronymicR, Books.TitleBook, Author.Name as NameA," +
                "Author.Surname as SurnameA, Author.patronymic as patronymicA " +
                "FROM MovementBooks " +
                "JOIN Readers on MovementBooks.ReaderNumber == Readers.ID " +
                "JOIN Books on MovementBooks.BooksID == Books.ID " +
                "JOIN Author on Books.AuthorID == Author.ID " +
                "WHERE DateDelivery = '' and ApproximateDateDelivery = '" + today + "'";
        return statement.executeQuery(sqlQuery);
    }

    @Override
    public void close() throws Exception {
        _dbConnection.close();
    }
}