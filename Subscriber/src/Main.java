import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main implements Runnable {
    public static String surname;
    public static String name;
    public static String patronymic;

    public static void main(String[] args) {
        while (true) {
            int menuItem = Menu.mainThing();
            if (menuItem == 1) addReader();
            else if (menuItem == 2) bookDistribution();
            else if (menuItem == 3) handOrExtendBook(true);
            else if (menuItem == 4) handOrExtendBook(false);
            else if (menuItem == 5) lastday();
            else if (menuItem == 0) break;
        }
    }

    private static void lastday() {

        try (SqLite db = new SqLite()) {
            Menu.lastday(db.Lastday());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void handOrExtendBook(boolean isExtendBook) {
        int readerItem = readerChoice();
        if (readerItem == -1) return;
        try (SqLite db = new SqLite()) {
            ResultSet resultSet = db.handOrExtendBook(readerItem);
            List<String> list = new ArrayList<>();
            List<Integer> listID = new ArrayList<>();
            list.add("Список книг которые взял " + Menu.getFIOIsdb(readerItem));
            while (resultSet.next()) {
                listID.add(resultSet.getInt("ID"));
                String stroka = listID.size() + " - ";
                String tepm0 = resultSet.getString("name");
                String tepm1 = resultSet.getString("Surname");
                String tepm2 = resultSet.getString("patronymic");
                stroka = stroka + Menu.lastNameAndInitials(tepm0, tepm1, tepm2);
                stroka = stroka + resultSet.getString("TitleBook");
                list.add(stroka);
            }
            list.add("-1 - Назад");
            list.add("0 - Выход");
            String stroka = "Введите номер книги которую хотите ";
            if (isExtendBook)
                stroka = stroka + "продлить - ";
            else
                stroka = stroka + "сдать - ";
            list.add(stroka);
            int menuItem = Menu.printlList(list, -1, listID.size());
            if (menuItem == 0) System.exit(0);
            if (menuItem == -1) return;
            LocalDate date = LocalDate.now();
            if (isExtendBook) {
                date = date.plusMonths(1);
                db.BookExtension(listID.get(menuItem-1),date.toString());
            }
            else
                db.BookHand(listID.get(menuItem-1),date.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }


    private synchronized static void addReader() {
        surname = Menu.dataEntryText("фамилию нового читателя");
        name = Menu.dataEntryText("имя нового читателя");
        patronymic = Menu.dataEntryText("отчество нового читателя");
        Thread thread = new Thread( new Main());
        thread.start();
    }

    private static void bookDistribution() {
        int readerItem = readerChoice();
        if (readerItem == -1) return;
        int idBooks = -1;
        while (true) {
            idBooks = Menu.dataEntryNumber("уникальный номер книги ");
            int book = Menu.isBook(idBooks);
            if (book == -1){
                System.out.println("Книги с введенным уникальным номером несуществует");
                clickEnter();
                continue;
            }
            if (book == 1) break;
        }
        if (!Menu.isIssue(idBooks)){
            System.out.println("Книга  - " + Menu.infoBook(idBooks));
            System.out.println("Не может быть выдона так как уже выдана");
            clickEnter();
            return;
        }
        LocalDate dateIssue = LocalDate.now();
        LocalDate approximateDateDelivery = dateIssue.plusMonths(1);
        try (SqLite db = new SqLite()) {
            db.WriteDBMovementBooks(readerItem, dateIssue.toString(), approximateDateDelivery.toString(), idBooks);
            System.out.println("Книга успешно выдона");
            clickEnter();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void clickEnter() {
        System.out.println("Для продолжение нажмите Enter");
        try {
            Input.string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int readerChoice() {
        int menuItem = Menu.readerChoice();
        if (menuItem == 0) System.exit(0);
        else if (menuItem == 2) return -1;
        else if (menuItem == 1) {
            int readerItem = Menu.dataEntryNumber("номер читательского билета ");
            int reader = Menu.isReader(readerItem);
            if (reader == -1) {
                System.out.println("Читателя с таким номером читательского билета несуществует");
                clickEnter();
                return readerChoice();
            }
            if (reader == 2) return readerChoice();
        }
        return menuItem;
    }

    @Override
    public  void run() {
        try (SqLite db = new SqLite()) {
            db.WriteDBReader(surname, name, patronymic);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}