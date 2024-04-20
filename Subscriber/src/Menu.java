import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Menu {
   public static int mainThing() {
        List<String> list = new ArrayList<>();
        list.add("Выберите действие:");
        list.add("1 - Добавить нового читателя");
        list.add("2 - Выдать книгу");
        list.add("3 - Продлить выданную книгу");
        list.add("4 - Сдать книгу");
        list.add("5 - Должны сегодня прийти");
        list.add("0 - Выход");
        list.add("Введите цифру нужного пункта - ");
        return printlList(list,0,5);
    }

    public static int printlList(final List<String> list, int start, int end){
        do {
            for (int i = 0; i < list.size()-1; i++) {
                System.out.println(list.get(i));
            }
            System.out.print(list.getLast());
            try {
                return Input.number(start, end);
            } catch (Exception e) {
                System.out.println("Действия с таким номером не существует");
            }
        } while (true);
    }

    public static String dataEntryText(String text){
       try {
           System.out.print("Введите " + text + " - ");
           return Input.string();
       }
       catch (Exception ex){
           System.out.println(ex.getMessage());
           System.exit(1);
       }
        return "";
    }

    public static void lastday(ResultSet resultSet) {
        List<String> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String stroka = "";
                String tepm0 = resultSet.getString("nameR");
                String tepm1 = resultSet.getString("SurnameS");
                String tepm2 = resultSet.getString("patronymicR");
                stroka = lastNameAndInitials(tepm0,tepm1,tepm2) + " взял книгу - ";
                tepm0 = resultSet.getString("nameA");
                tepm1 = resultSet.getString("SurnameA");
                tepm2 = resultSet.getString("patronymicA");
                stroka = stroka + lastNameAndInitials(tepm0,tepm1,tepm2);
                stroka = stroka + resultSet.getString("TitleBook");
                list.add(stroka);
            }
            System.out.println("Список читателей и взятие ими книги у которых сегодня прийти сдать или продлить книги");
            for (String s : list) {
                System.out.println(s);
            }
            System.out.println("Для возрата в меню нажмите Enter");
            Input.string();
        }catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

    }

    public static String lastNameAndInitials(String name, String Surname, String patronymic){
        return Surname + " " + name.charAt(0) + ". " + patronymic.charAt(0) + ". ";
    }

    public static boolean isIssue(int bookItem){
        try (SqLite db = new SqLite()) {
            ResultSet resultSet = db.readerMovementBooks(bookItem);
            while (resultSet.next()) {
                return false;
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return true;
    }

    public static int isBook(int bookItem){
        List<String> list = new ArrayList<>();
        list.add("Вы хотите выдать книгу ");
        list.add(infoBook(bookItem));
        if (list.getLast() == "") return -1;
        list.add("1 - Да ");
        list.add("2 - нет ");
        list.add("Введите цифру нужного пункта - ");
        return printlList(list,1,2);
    }

    public static String infoBook(int bookItem){
        try (SqLite db = new SqLite()) {
            ResultSet resultSet = db.readerBooks(bookItem);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String surname = resultSet.getString("Surname");
                String patronymic = resultSet.getString("patronymic");
                return lastNameAndInitials(name,surname,patronymic) + resultSet.getString("TitleBook");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    public static int isReader(int readerItem) {
        List<String> list = new ArrayList<>();
        list.add("Вы хотите начать работать с ");
        list.add(getFIOIsdb(readerItem));
        if (list.getLast() == "") return -1;
        list.add("1 - Да ");
        list.add("2 - нет ");
        list.add("Введите цифру нужного пункта - ");
        return printlList(list,1,2);
    }

    public static String getFIOIsdb(int readerItem){
        try (SqLite db = new SqLite()) {
            ResultSet resultSet = db.readerChoice(readerItem);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String surname = resultSet.getString("Surname");
                String patronymic = resultSet.getString("patronymic");
                return surname + " " + name + " " + patronymic;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    public static int readerChoice(){
        List<String> list = new ArrayList<>();
        list.add("Выберите как будет осуществлятся поиск читателя:");
        list.add("1 - через ввод номера четальского билета");
        list.add("2 - Назад");
        list.add("0 - Выход");
        list.add("Введите цифру нужного пункта - ");
        return printlList(list,0,2);
    }

    public static int dataEntryNumber(String text){
        try {
            System.out.print("Введите " + text + " - ");
            return Input.number();
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        return -1;
    }
}