package bank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class main {
    static void HomeMenu() {
        System.out.println("Enter Your Choice: ");
        System.out.println("1. Login to Your Account");
        System.out.println("2. Create new A/c");
        System.out.println("3. Our Catalog");
        System.out.println("\nEnter Your Choice: ");
    }

    static void loginMenu() {
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        System.out.println("1. My Profile");
        System.out.println("2. My A/c Details");
        System.out.println("3. Card Details");
        System.out.println("4. Transaction History");
        System.out.println("5. Pay & Transfer");
        System.out.println("6. Apply Loan");
        System.out.println("7. Logout");
        System.out.println("\u001B[31m"+"8. Delete Account"+"\u001B[0m");
        System.out.println("\nEnter Your Choice: ");
    }

    public static boolean isValidDate(String date) {
        String regex = "\\d{4}-\\d{2}-\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);

        return matcher.matches();
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int choice;
        int userHomeChoice;
        Bank ZBI = new Bank();
        ArrayList<String> temp = new ArrayList<String>();
        System.out.println("\t\t\tWelcome to Zoho Bank of India 🙏");
        System.out.println("\t\t\t\t\tOur Prime Interest Is You!");

        do {

            System.out.println("\n\n");
            main.HomeMenu();
            choice = scanner.nextInt();
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

            switch (choice) {
                case 1:
                    scanner.nextLine();
                    System.out.println("Enter Your Customer ID: ");
                    String custId = scanner.nextLine();
                    if (ZBI.loginAcnt(custId)) {
                        do {
                            main.loginMenu();
                            userHomeChoice = scanner.nextInt();
                            switch (userHomeChoice) {
                                case 1:
                                    ZBI.currentUser.DisplayPersonalDetails();
                                    break;

                                case 2:
                                    ZBI.currentUser.DisplayAccInfo();
                                    break;

                                case 3:
                                    ZBI.CardDetails();
                                    break;

                                case 4:
                                    System.out.println("Transaction History");
                                    // System.out.print("\nHow many Entries do you want? :");
                                    // int numEntries = scanner.nextInt();
                                    ZBI.currentUser.getLastNTransactions(100);
                                    break;

                                case 5:
                                    System.out.println();
                                    System.out.println("1. Repay Loan");
                                    System.out.println("2. Transfer Money to others");
                                    System.out.print("\nEnter Your choice: ");
                                    int ch = scanner.nextInt();
                                    if (ch == 1) {
                                        ZBI.RePayLoan();
                                    } else if (ch == 2) {
                                        ZBI.TransactionPortal();
                                    }
                                    break;

                                case 6:
                                    // Apply Loan
                                    ZBI.ApplyLoan();
                                    break;

                                case 7:
                                    ZBI.currentUser.logoutBank();
                                    break;

                                case 8:
                                    ZBI.DeleteAcc();
                                    break;
                            }
                        } while (userHomeChoice < 7);

                    }
                    break;

                // Create new account

                case 2:
                    System.out.println("\nCreate new Account\n");
                    scanner.nextLine();
                    System.out.println("Enter Your Name: ");
                    temp.add(scanner.nextLine());

                    System.out.println("Mobile Number: ");
                    temp.add(scanner.nextLine());

                    System.out.println("Email ID: ");
                    temp.add(scanner.nextLine());

                    System.out.println("Date of Birth: [2023-12-23]");
                    String dateStr = scanner.nextLine();
                    if (isValidDate(dateStr)) {
                        temp.add(dateStr);
                    }else{
                        do{
                            System.out.println("Wrong Format!");
                            System.out.print("Please Enter valid date: ");
                            dateStr = scanner.nextLine();
                        }while(!isValidDate(dateStr));
                        temp.add(dateStr);
                    }
                    LocalDate tempDate = LocalDate.parse(temp.get(3));
                    if (((LocalDate.now().getYear()) - (tempDate.getYear())) < 18) {
                        System.out.println("Sorry, You're under age!");
                        break;
                    }

                    System.out.println("Address: ");
                    temp.add(scanner.nextLine());

                    System.out.println("PAN Number: ");
                    temp.add(scanner.nextLine());

                    System.out.println("");

                    if (ZBI.areValidDetails(temp)) {
                        ZBI.CreateAcnt(temp);
                    } else {
                        System.out.println("Your details are not valid!");
                        break;
                    }
                    break;

                case 3:
                    ZBI.ourCatalog();
                    break;

                default:
                    break;

            }
        } while (choice < 4);

    }
}
