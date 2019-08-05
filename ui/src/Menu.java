import java.util.Scanner;

public class Menu {
    private final int k_MinValue = 0;
    private final int k_MaxValue = 13;
    private final String k_Menu =
            "Please choose a number from the list below:" + System.lineSeparator() +
                    "1. Update user name" + System.lineSeparator() +
                    "2. Read repository details from XML file" + System.lineSeparator() +
                    "3. Create New repository" + System.lineSeparator() +
                    "4. Change repository" + System.lineSeparator() +
                    "5. Show details of current commit" + System.lineSeparator() +
                    "6. Show status" + System.lineSeparator() +
                    "7. Create New Commit" + System.lineSeparator() +
                    "8. Show all branches" + System.lineSeparator() +
                    "9. Create new branch" + System.lineSeparator() +
                    "10. Delete branch" + System.lineSeparator() +
                    "11. Checkout" + System.lineSeparator() +
                    "12. Show commit history of current branch" + System.lineSeparator() +
                    "13. Exit" + System.lineSeparator();

    public void printMenu() {
        System.out.println(k_Menu);
    }

    public int getUserChoice() {
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();
        while (!inRange(choice, k_MinValue, k_MaxValue)) {
            System.out.println("please enter number in range of" + k_MinValue + "to" + k_MaxValue);
            choice = in.nextInt();
        }
        return choice;
    }

    private boolean inRange(int i_UserChoice, int i_MinValue, int i_MaxValue) {
        return i_UserChoice >= i_MinValue && i_UserChoice <= i_MaxValue;
    }
}
