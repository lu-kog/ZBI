
// package ATM;
import java.util.*;
import java.io.*;
import java.time.*;
import org.mindrot.jbcrypt.*; // for hashing the passwd
import java.util.regex.Pattern;

// import Bank.*;

// Class representing an ATM
public class MainATM {

    
    static Scanner cin = new Scanner(System.in);
    Customers currentUser = new Customers();
    ArrayList<String> customerIDList = new ArrayList<>();
    ArrayList<String> customersCardNums = new ArrayList<>();
    ArrayList<String> customerAccList = new ArrayList<>();
    

    public static void main(String[] args) throws Exception {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        MainATM person = new MainATM();

        person.initializeCustomers();
        person.printNameOfATM();
        System.out.print("\n\nEnter your Card Number : ");
        String inputCardNum = cin.next();
        String islogged = person.isCardPresent(inputCardNum);

        if (!islogged.equals("false")) {
            person.currentUser.loginATM(islogged);
            if (!person.currentUser.debitCard.Active) {
                System.out.println("Your card is Blocked!!");
                return;
            }
            if (!person.currentUser.Active) {
                System.out.println("Your Account is Blocked!!");
                return;
            }
            if (person.currentUser.debitCard.ExpDate.isBefore(LocalDate.now())) {
                System.out.println("Your card is Expired");
                return;
            }
            if (person.currentUser.debitCard.PinNumber == 0) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println("Your Card doesn't have a Pin");
                System.out.println("Kindly, generate your Pin!");
                cin.nextLine();
                person.generatePin();
                System.out.println("Pin set successful");
            }
            person.process();
        } else {
            System.out.println("Card Not Found!!");
            return;
        }
    }

    void initializeCustomers() throws Exception {
        File[] files;
        String filePathForCustomers = "Customers";
        File folder = new File(filePathForCustomers);
        if (folder.exists() && folder.isDirectory()) {
            files = folder.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    Customers temp = new Customers();
                    temp.loginATM(file.getName().substring(0, file.getName().length() - 4));
                    customerIDList.add(temp.CustomerID);
                    customersCardNums.add(temp.debitCard.CardNumber);
                    customerAccList.add(temp.AccountNumber);
                    // temp.logoutATM();
                }
            }
            // System.out.println("The values of the list is : " + customerIDList);
            //System.out.println("The card numbers are      : " + customersCardNums);
        }
    }

    void printingChoices() {
        System.out.println("\n1. Check Balance");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Transfer");
        System.out.println("5. Display Personal Details");
        System.out.println("6. Display Account Info");
        System.out.println("7. Mini statement");
        System.out.println("8. Change PIN");

        System.out.println("0. Cancel");
        System.out.print("Enter your choice: ");
    }

    int getPIN() {
        System.out.print("Enter your PIN : ");
        return cin.nextInt();
    }

    void process() throws Exception {
        int choice;
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("👋 Hi " + currentUser.CustomerName);
        int PINnum;
        do {
            printingChoices();
            choice = cin.nextInt();
            switch (choice) {
                case 1:
                    do {
                        PINnum = getPIN();
                        if (PINnum == currentUser.debitCard.PinNumber) {
                            checkBalance();
                            currentUser.logoutATM();

                        } else {
                            System.out.println("Enter the correct PIN ");
                        }
                    } while (PINnum != currentUser.debitCard.PinNumber);
                    break;

                case 2:
                    do {
                        PINnum = getPIN();
                        if (PINnum == currentUser.debitCard.PinNumber) {
                            withDraw();
                            currentUser.logoutATM();
                            System.out.println(currentUser.CustomerName + ", your current Balance : "
                                    + currentUser.AccountBalance + "\n\n\n");
                        } else {
                            System.out.println("Enter the correct PIN ");
                        }
                    } while (PINnum != currentUser.debitCard.PinNumber);
                    break;
                case 3:
                    do {
                        PINnum = getPIN();
                        if (PINnum == currentUser.debitCard.PinNumber) {
                            depositeMoney();
                            currentUser.logoutATM();
                        } else {
                            System.out.println("Enter the correct PIN ");
                        }
                    } while (PINnum != currentUser.debitCard.PinNumber);
                    break;
                case 4:
                    do {
                        PINnum = getPIN();
                        if (PINnum == currentUser.debitCard.PinNumber) {
                            transferATM();
                            currentUser.logoutATM();
                        } else {
                            System.out.println("Enter the correct PIN ");
                        }
                    } while (PINnum != currentUser.debitCard.PinNumber);
                    break;
                case 5:
                    do {
                        PINnum = getPIN();
                        if (PINnum == currentUser.debitCard.PinNumber) {
                            System.out.print("\033[H\033[2J");
                            System.out.flush();
                            currentUser.DisplayPersonalDetails();
                        } else {
                            System.out.println("Enter the correct PIN ");
                        }
                    } while (PINnum != currentUser.debitCard.PinNumber);
                    break;
                case 6:
                    do {
                        PINnum = getPIN();
                        if (PINnum == currentUser.debitCard.PinNumber) {
                            System.out.print("\033[H\033[2J");
                            System.out.flush();
                            currentUser.DisplayAccInfo();
                        } else {
                            System.out.println("Enter the correct PIN ");
                        }
                    } while (PINnum != currentUser.debitCard.PinNumber);
                    break;
                case 7:
                    do {
                        PINnum = getPIN();
                        if (PINnum == currentUser.debitCard.PinNumber) {
                            System.out.print("\033[H\033[2J");
                            System.out.flush();
                            currentUser.getLastNTransactions(6);
                        } else {
                            System.out.println("Enter the correct PIN ");
                        }
                    } while (PINnum != currentUser.debitCard.PinNumber);
                    break;
                case 8:
                    changePinATM();
                    currentUser.logoutATM();
                    break;
                case 0:
                    System.out.println("Thank you for using the ATM. Goodbye!");
                    break;
                case 99:
                    Bank ZBI = new Bank();
                    ArrayList<String> temp = new ArrayList<String>();

                    System.out.println("\nCreate new Account\n");
                    cin.nextLine();
                    System.out.println("Enter Your Name: ");
                    temp.add(cin.nextLine());

                    System.out.println("Mobile Number: ");
                    temp.add(cin.nextLine());

                    System.out.println("Email ID: ");
                    temp.add(cin.nextLine());

                    System.out.println("Date of Birth: [2023-12-23]");
                    temp.add(cin.nextLine());
                    LocalDate tempDate = LocalDate.parse(temp.get(3));
                    if (((LocalDate.now().getYear()) - (tempDate.getYear())) <= 18) {
                        System.out.println("Sorry, You're under age!");
                        break;
                    }

                    System.out.println("Address: ");
                    temp.add(cin.nextLine());

                    System.out.println("PAN Number: ");
                    temp.add(cin.nextLine());

                    System.out.println("");

                    if (ZBI.areValidDetails(temp)) {
                        ZBI.CreateAcnt(temp);
                    } else {
                        System.out.println("Your details are not valid!");
                        break;
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (choice != 0);
    }

    void checkBalance() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(
                currentUser.CustomerName + ", your current Balance : " + currentUser.AccountBalance + "\n\n\n");
    }

    void withDraw() throws Exception {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.print("Enter amount to withdraw: ₹");
        double withdrawAmount = cin.nextDouble();
        if (withdrawAmount <= 0) {
            if (currentUser.isCurrentAc) {
                currentUser.AccountBalance -= withdrawAmount;
                currentUser.appendTransactionLog("ATM Withdrawal", false, withdrawAmount, currentUser.AccountBalance);
            } else {
                System.out.println("Invalid Amount");
                return;
            }
        }

        else if (withdrawAmount <= currentUser.AccountBalance) {
            if (withdrawAmount <= currentUser.debitCard.CardLimit) {
                currentUser.AccountBalance = currentUser.AccountBalance - withdrawAmount;
                currentUser.appendTransactionLog("ATM Withdrawal", false, withdrawAmount, currentUser.AccountBalance);
            } else {
                System.out.println("Card Limit Exeeded!");
            }
        } else {
            System.out.println("Insufficient balance!");
        }

    }

    void depositeMoney() throws Exception {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        double depositeAmount;
        System.out.println("1. This Account");
        System.out.println("2. Another Account");
        int depositeChoice;
        do {
            depositeChoice = cin.nextInt();
            int totalProgress = 100;
            switch (depositeChoice) {
                case 1:
                    System.out.print("Enter the money : ");
                    depositeAmount = cin.nextDouble();
                    if (depositeAmount <= 0) {
                        System.out.println("Invalid Amount");
                    } else if (depositeAmount <= 50000) {
                        currentUser.AccountBalance += depositeAmount;
                        currentUser.appendTransactionLog("ATM Deposit", true, depositeAmount,
                                currentUser.AccountBalance);

                        for (int i = 0; i <= totalProgress; i++) {
                            Bank.printLoadingBar(i, totalProgress);
                            Thread.sleep(50);
                        }

                        System.out.println("\n\nDeposit completed!");

                    } else {
                        System.out.println("Can't deposit more than 50,000 through ATM!");
                    }
                    break;

                case 2:
                    System.out.print("Enter the \"Account Number\" : ");
                    String depositeAccount = cin.next();
                    String depositeAccFound = isAccountPresent(depositeAccount);
                    if (!depositeAccFound.equals("false")) {
                        Customers depositeAmountUser = new Customers();
                        depositeAmountUser.loginATM(depositeAccFound);
                        System.out.print("Enter the money : ");
                        depositeAmount = cin.nextDouble();
                        if (depositeAmount <= 0) {
                            System.out.println("Invalid Amount");
                        } else {
                            depositeAmountUser.AccountBalance += depositeAmount;
                            depositeAmountUser.appendTransactionLog("ATM Deposit", true, depositeAmount,
                                    depositeAmountUser.AccountBalance);
                        }
                    } else {
                        System.out.println("User Not found !");
                        return;
                    }
                    for (int i = 0; i <= totalProgress; i++) {
                        Bank.printLoadingBar(i, totalProgress);
                        Thread.sleep(50);
                    }

                    System.out.println("\n\nDeposit completed!");
                    break;
                default:
                    System.out.println("Enter a valid option");
                    break;
            }
        } while (depositeChoice != 1 && depositeChoice != 2);
    }

    void transferATM() throws Exception {
        System.out.print("Enter the other Account number : ");
        String transferATMOtherAcc = cin.next();
        String istransferATMOtherAccFound = isAccountPresent(transferATMOtherAcc);
        if (!istransferATMOtherAccFound.equals("false")) {
            Customers transferAmountUser = new Customers();
            transferAmountUser.loginATM(istransferATMOtherAccFound);
            System.out.print("Enter the money : ");
            double transferAmount = cin.nextDouble();
            if (transferAmount <= 0) {
                System.out.println("Invalid Amount");
            } else {
                if (currentUser.AccountBalance >= transferAmount) {
                    transferAmountUser.AccountBalance += transferAmount;
                    transferAmountUser.appendTransactionLog("Recieved From : " + currentUser.CustomerName, true,
                            transferAmount, transferAmountUser.AccountBalance);
                    transferAmountUser.logoutATM();
                    currentUser.AccountBalance -= transferAmount;
                    currentUser.appendTransactionLog("Transfer to : " + transferAmountUser.CustomerName, false,
                            transferAmount, currentUser.AccountBalance);
                } else {
                    System.out.println("Insufficient balance!");
                }
            }
        } else {
            System.out.println("User Not found !");
            return;
        }
        int totalProgress = 100; // I need 100 times loop within 5s

        for (int i = 0; i <= totalProgress; i++) {
            Bank.printLoadingBar(i, totalProgress);
            Thread.sleep(50);
        }

        System.out.println("\nTransaction completed!");

    }

    public String isCardPresent(String cardNumber) throws Exception {
        for (int i = 0; i < customersCardNums.size(); i++) {
            if (customersCardNums.get(i).equals(cardNumber)) {
                return customerIDList.get(i); // Card number found in the list
            }
        }
        return "false"; // Card number not found in the list
    }

    public String isAccountPresent(String AccNumber) throws Exception {
        for (int i = 0; i < customerAccList.size(); i++) {
            if (customerAccList.get(i).equals(AccNumber)) {
                return customerIDList.get(i);
            }
        }
        return "false";
    }

    void printNameOfATM() {
        System.out.println(
                "\n\n\t\t\t\t\t  ███████╗██████╗░██╗\n\t\t\t\t\t  ╚════██║██╔══██╗██║\n\t\t\t\t\t  ░░███╔═╝██████╦╝██║\n\t\t\t\t\t  ██╔══╝░░██╔══██╗██║\n\t\t\t\t\t  ███████╗██████╦╝██║\n\t\t\t\t\t  ╚══════╝╚═════╝░╚═╝");
    }

    void changePinATM() throws Exception {
        cin.nextLine();
        System.out.print("Please enter your Account Number for verification : ");
        String accNoForPin = cin.nextLine();
        if (accNoForPin.equals(currentUser.AccountNumber)) {
            System.out.print("Please enter your Mobile Number : ");
            String mobNumForPin = cin.nextLine();
            if (mobNumForPin.equals(currentUser.phoneNumber)) {
                System.out.println("Access Granted ");
                System.out.print("Enter a PIN : ");
                int genPIN = cin.nextInt();
                while (genPIN == currentUser.debitCard.PinNumber) {
                    System.out.println("You can not set same PIN");
                    System.out.print("Enter a PIN : ");
                    genPIN = cin.nextInt();
                }
                while (!Bank.isValidPIN(genPIN)) {
                    System.out.println("Avoid using consecutive numbers in your PIN!");
                    System.out.print("Enter a different PIN : ");
                    genPIN = cin.nextInt();
                }
                System.out.print("Conform your PIN : ");
                int conformPIN = cin.nextInt();
                if (conformPIN != genPIN) {
                    do {
                        System.out.println("Mismatch PIN!");
                        System.out.print("Conform your PIN : ");
                        conformPIN = cin.nextInt();
                    } while (conformPIN != genPIN);
                }
                currentUser.debitCard.PinNumber = genPIN;

                int totalProgress = 100; // I need 100 times loop within 5s

                for (int i = 0; i <= totalProgress; i++) {
                    Bank.printLoadingBar(i, totalProgress);
                    Thread.sleep(50);
                }

                System.out.println("\nPIN set successfully!");
                return;
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println("Please enter your correct Mobile Number!");
                changePinATM();
                return;
            }
        } else {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("Please enter your correct Account Number!");
            changePinATM();
            return;
        }
    }

    void generatePin() throws Exception {
        System.out.print("Please enter your Account Number for verification : ");
        String accNoForPin = cin.nextLine();
        if (accNoForPin.equals(currentUser.AccountNumber)) {
            System.out.print("Please enter your Mobile Number : ");
            String mobNumForPin = cin.nextLine();
            if (mobNumForPin.equals(currentUser.phoneNumber)) {
                System.out.println("Access Granted ");
                System.out.print("Enter a PIN : ");
                int genPIN = cin.nextInt();
                while (!Bank.isValidPIN(genPIN)) {
                    System.out.println("Avoid using consecutive numbers in your PIN!");
                    System.out.print("Enter a different PIN : ");
                    genPIN = cin.nextInt();
                }
                System.out.print("Conform your PIN : ");
                int conformPIN = cin.nextInt();
                if (conformPIN != genPIN) {
                    do {
                        System.out.println("Mismatch Pin!");
                        System.out.print("Conform your PIN : ");
                        conformPIN = cin.nextInt();
                    } while (conformPIN != genPIN);
                }
                currentUser.debitCard.PinNumber = genPIN;
                return;
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                System.out.println("Your Card doesn't have a Pin");
                System.out.println("Kindly, generate your Pin!");
                System.out.println("Please enter your correct Mobile Number!");
                generatePin();
                return;
            }
        } else {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("Your Card doesn't have a Pin");
            System.out.println("Kindly, generate your Pin!");
            System.out.println("Please enter your correct Account Number!");
            generatePin();
            return;
        }
    }

    static boolean hasConsecutiveNumbers(String pin) {
        // Define a regular expression pattern to match consecutive digits
        String consecutivePattern = "(\\d)\\1\\1\\1|0123|1234|2345|3456|4567|5678|6789|9876|8765|7654|6543|5432|4321|3210";

        // Check if the PIN contains any of the consecutive number patterns
        return Pattern.compile(consecutivePattern).matcher(pin).find();
    }

}
