public class ListUsers {

    static User userTestDefault = new User("default_1@mail.ru", "19625", "Dekab_1");
    static User userTestWithoutEmail = new User("", "123", "test10");
    static User userTestWithoutPassword = new User("test085@test.ru", "", "test10");
    static User userTestWithoutName = new User("test085@test.ru", "123", "");
    static User userTestNotReal = new User("left@mail.ru", "2864", "leftUser");
    static User userTestForEdit = new User("mail3@mail.ru", "0102", "littleUser4");
    static User userDataUpd = new User("update32@mail.ru", "", "BIGUser");
    static User userDataUpdWithExistsEmail = new User("up_timur1@asd.ru", "2343221", "BIGUser1");
    static User existsEmail = new User("", "", "up_timur@asd.ru");

}
