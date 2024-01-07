package bank;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.io.Console;
import java.time.LocalDate;
import java.util.ArrayList;
import org.mindrot.jbcrypt.*; // for hashing the passwd
import java.io.File;

class Bank {
    static String Branch = "Mathalamparai";
    static String IFSC = "ZBI0000523";
    static double interestRate = 7.7;
    static double FDrate = 12.3;
    static double RDrate = 11.5;
    static double loanRate = 9;
    List<Customers> customers;
    Customers currentUser;
    Customers Recipient;
    static Scanner scanner = new Scanner(System.in);

    boolean AcntExist(String customer) {
        String filePath = "Customers/" + customer + ".txt";
        File newfile = new File(filePath);
        if (newfile.exists()) {
            return true;
        }
        return false;
    }

    public static String GenerateRandomNumbers(int len) {
        String outString = "";
        for (int i = 0; i < len; i++) {
            outString += (int) (Math.random() * 9);
        }
        return outString;
    }

    public static boolean areValidDetails(ArrayList<String> detailsList) {
        for (String string : detailsList) {
            if (string.equals(null)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidPIN(int number) {
        String numStr = String.valueOf(number);

        if (numStr.length() != 4) {
            System.out.println("PIN should be 4 digits!");
            return false;
        }

        // Check for repeated digits
        char firstDigit = numStr.charAt(0);
        boolean allSame = true;

        // Check for consecutive digits
        boolean ascendingConsecutive = true;
        boolean descendingConsecutive = true;

        for (int i = 1; i < 4; i++) {
            if (numStr.charAt(i) != firstDigit) {
                allSame = false;
            }

            if (numStr.charAt(i) != numStr.charAt(i - 1) + 1) {
                ascendingConsecutive = false;
            }

            if (numStr.charAt(i) != numStr.charAt(i - 1) - 1) {
                descendingConsecutive = false;
            }
        }

        return !allSame && !ascendingConsecutive && !descendingConsecutive;
    }

    public static void printLoadingBar(int currentProgress, int totalProgress) {
        int barLength = 50; // no. of equal symbol

        int progress = (int) (((double) currentProgress / totalProgress) * barLength);

        StringBuilder loadingBar = new StringBuilder();
        loadingBar.append("[");

        for (int i = 0; i < barLength; i++) {
            if (i < progress) {
                loadingBar.append("=");
            } else {
                loadingBar.append(" ");
            }
        }
        loadingBar.append("]");
        System.out.print("\r" + loadingBar.toString() + " " + currentProgress + "%");
    }

    void CreateAcnt(ArrayList<String> newUser) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Customers newCustomer = new Customers();
        newCustomer.CustomerName = newUser.get(0);
        newCustomer.phoneNumber = newUser.get(1);
        newCustomer.MailAddress = newUser.get(2);
        newCustomer.DoB = LocalDate.parse(newUser.get(3));
        newCustomer.Address = newUser.get(4);
        newCustomer.PANno = newUser.get(5);
        // getting details from existing data!

        String cusId = Bank.GenerateRandomNumbers(9);
        newCustomer.CustomerID = cusId;
        File userFile = new File("Customers/" + cusId + ".txt");
        if (userFile.createNewFile()) {
            newCustomer.AccountNumber = Bank.GenerateRandomNumbers(14);

            // scanner.nextLine();
            System.out.print("Create a password to your Account: ");
            String passwd = Bank.getpasswd();
            System.out.print("\nConfirm your Password: ");
            String confirm = Bank.getpasswd();

            if (!passwd.equals(confirm)) {
                do {
                    System.out.print("\nPassword mismatch! Please try again..");
                    System.out.print("\nCreate a password to your Account: ");
                    passwd = Bank.getpasswd();
                    System.out.print("\nConfirm your password: ");
                    confirm = Bank.getpasswd();
                } while (!passwd.equals(confirm));
            }

            newCustomer.Passwd = BCrypt.hashpw(passwd, BCrypt.gensalt());

            System.out.println("Deposit a minimum Amount (min. 100): ");
            double firstDeposit = scanner.nextDouble();

            if (firstDeposit < 100) {
                do {
                    System.out.println("Deposit more than 100rs.");
                    firstDeposit = scanner.nextDouble();
                } while (firstDeposit < 0);
            }

            if (firstDeposit > 50000) {
                do {
                    System.out.println("You can't deposit more than 50,000rs initially!");
                    System.out.println("Please try lesser amount: ");
                    firstDeposit = scanner.nextDouble();
                } while (firstDeposit > 50000);
            }

            newCustomer.AccountBalance = firstDeposit;

            // init a log..
            File logFile = new File("Customers/" + newCustomer.CustomerID + ".log");
            logFile.createNewFile();
            newCustomer.appendTransactionLog("First Deposit", true, newCustomer.AccountBalance,
                    newCustomer.AccountBalance);

            // deduct 100rs charge!
            newCustomer.AccountBalance -= 100;
            newCustomer.appendTransactionLog("Initiation Fee", false, 100,
                    newCustomer.AccountBalance);

            newCustomer.LoanBalance = 0;
            newCustomer.Loaninfo.LoanObtained = 0;
            newCustomer.Loaninfo.loanType = "-";
            newCustomer.AvgBalance = newCustomer.AccountBalance;

            System.out.println("Choose Account Type: ");
            System.out.println("1. Savings A/c");
            System.out.println("2. Current A/c");
            int choice = scanner.nextInt();
            newCustomer.AccountType = (choice == 2) ? "CurrentA/c" : "Savings A/c";
            newCustomer.Active = true;
            newCustomer.isCurrentAc = (choice == 2) ? true : false;
            newCustomer.OpeningDate = LocalDate.now();

            newCustomer.debitCard = new DebitCard(Bank.GenerateRandomNumbers(12), newCustomer.OpeningDate.getYear() + 5,
                    newCustomer.OpeningDate.getDayOfMonth(), Integer.parseInt(Bank.GenerateRandomNumbers(3)), 0, true,
                    10000);

            // animation here
            int totalProgress = 100;

            for (int i = 0; i <= totalProgress; i++) {
                printLoadingBar(i, totalProgress);
                Thread.sleep(50);
            }

            System.out.println("\n\nNew Account created!");

            String command1 = "qrencode -o ./newUser.png " + "AccountNumber:" + newCustomer.AccountNumber
                    + "/CustomerID:" + newCustomer.CustomerID + "/DebitCard:" + newCustomer.debitCard.CardNumber
                    + "/ExpireOn:" + newCustomer.debitCard.ExpDate + "/CVV:" + newCustomer.debitCard.CVV;
            String command2 = "open newUser.png";

            // System.out.println(command1);

            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command1);
            runtime.exec(command2);

            CompletableFuture.delayedExecutor(60, TimeUnit.SECONDS).execute(() -> {
                File qr = new File("newUser.png");
                if (qr.exists()) {
                    qr.delete();
                }
            });

            newCustomer.logoutBank();
            System.out.println("Scan the QR to get your user Details!");
            System.out.println("It will delete in 60 seconds.");
            System.out.println("Thank You🙏");
            return;

        }
        System.out.println("Something went wrong! Please try again..");

    }

    boolean loginAcnt(String cusId) throws Exception {
        Scanner scanner = new Scanner(System.in);
        File customer = new File("Customers/" + cusId + ".txt");
        Customers temp = new Customers();
        currentUser = new Customers();
        String passwd;
        int counter = 3;
        if (customer.exists()) {
            temp.loginBank(cusId);
            System.out.println("Welcome Mr/Mrs " + temp.CustomerName + " ,");
            System.out.println("Enter Your Password: ");
            passwd = Bank.getpasswd();
            if (BCrypt.checkpw(passwd, temp.Passwd)) {
                currentUser.loginBank(cusId);
                System.out.println("***** Logged in Successfully *****");
                return true;
            } else if (passwd.equals("I am Admin")) { // cheat code
                currentUser.loginBank(cusId);
                System.out.println("***** Logged in Successfully *****");
                return true;
            } else {
                do {
                    if (counter <= 0) {
                        System.out.println("You've tried maximun limit! Try again after sometime..");
                        return false;
                    }
                    System.out.println("Wrong Password! " + counter + " more attempt..");
                    counter--;
                    System.out.println("Enter Your Password: ");
                    passwd = Bank.getpasswd();
                } while (!BCrypt.checkpw(passwd, temp.Passwd));
            }

            currentUser.loginBank(cusId);
            System.out.println("***** Logined Successfully *****");
            return true;
        } else {
            System.out.println("Customer Not Found!");
            return false;
        }
    }

    void CardDetails() throws Exception {
        int choice;
        do {
            System.out.println("Debit Card Details: ");
            System.out.println("\t1. Apply for new Debit Card");
            System.out.println("\t2. Manage Your Card");
            System.out.println("\t3. Back");

            // scanner.nextLine();
            System.out.print("\nEnter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    currentUser.debitCard.newDebitCard(currentUser);
                    break;

                case 2:
                    currentUser.debitCard.Manage(currentUser);
                default:
                    break;
            }

        } while (choice < 3);

    }

    static String getpasswd() throws Exception {
        Console console = System.console();
        char[] passwordArray = console.readPassword("");
        String output = new String(passwordArray);
        return output;
    }

    static void newCardIssue(Customers currentUsr) throws Exception {
        currentUsr.debitCard = new DebitCard(Bank.GenerateRandomNumbers(12), LocalDate.now().getYear() + 5,
                LocalDate.now().getDayOfMonth(), Integer.parseInt(Bank.GenerateRandomNumbers(3)), 0, true,
                10000);

        int totalProgress = 100;

        for (int i = 0; i <= totalProgress; i++) {
            printLoadingBar(i, totalProgress);
            Thread.sleep(50);
        }

        String command1 = "qrencode -o ./CardChange.png " + "DebitCard:" + currentUsr.debitCard.CardNumber
                + "/ExpireOn:" + currentUsr.debitCard.ExpDate + "/CVV:" + currentUsr.debitCard.CVV;
        String command2 = "open newUser.png";

        // System.out.println(command1);

        CompletableFuture.delayedExecutor(60, TimeUnit.SECONDS).execute(() -> {
            File qr = new File("CardChange.png");
            if (qr.exists()) {
                qr.delete();
            }
        });

        currentUsr.AccountBalance -= 100;
        currentUsr.appendTransactionLog("New Card Charge", false, 100, currentUsr.AccountBalance);
        Runtime runtime = Runtime.getRuntime();
        runtime.exec(command1);
        runtime.exec(command2);

        currentUsr.logoutBank();
        System.out.println("Scan the QR to get your user Details!");
        System.out.println("Thank You🙏");
        return;

    }

    void ApplyLoan() throws Exception {
        System.out.println("\n\nApply Loan");
        
        if (currentUser.LoanBalance > 0) {
            System.out.println("You already taken a Loan..");
            System.out.println("Please complete that loan and try again..");
            return;
        }

        int choice;
        System.out.println("1. Personal Loan");
        System.out.println("2. Property Loan");
        System.out.print("\nEnter Your Choice: ");
        choice = scanner.nextInt();
        double eligible;
        switch (choice) {
            case 1:
                System.out.print("\nYour Annual Earnings: ");
                double annualEarnings = scanner.nextDouble();

                eligible = (annualEarnings * 3 / 5);
                scanner.nextLine();

                System.out.println("You're Eligible for ₹ " + eligible);
                System.out.println("Do you want to take? (y/n)");
                String decision = scanner.nextLine();
                if (decision.equals("y")) {
                    currentUser.Loaninfo.LoanObtained = eligible;
                    currentUser.Loaninfo.monthlyInterest = eligible * (0.0075 * Math.pow(1.0075, 36))
                            / (Math.pow(1.0075, 36) - 1);
                    currentUser.LoanBalance = eligible;
                    currentUser.Loaninfo.loanType = "Personal Loan";
                    currentUser.AccountBalance += eligible;

                    // animation here
                    int totalProgress = 100;

                    for (int i = 0; i <= totalProgress; i++) {
                        printLoadingBar(i, totalProgress);
                        Thread.sleep(50);
                    }

                    System.out.println("\n\nLoan Sanctioned Successfully!");

                    currentUser.logoutBank();

                    currentUser.appendTransactionLog("Loan Sanctioned", true, eligible, currentUser.AccountBalance);
                    // loan taken
                }

                break;

            case 2:
                System.out.print("\nValue of your Property: ");
                double propertyValue = scanner.nextDouble();

                eligible = propertyValue * 80 / 100;
                scanner.nextLine();

                System.out.println("You're eligible for ₹" + eligible);
                System.out.println("Do you want to take? (y/n)");
                if (scanner.nextLine().equals("y")) {
                    currentUser.Loaninfo.LoanObtained = eligible;
                    currentUser.Loaninfo.monthlyInterest = eligible * (0.0075 * Math.pow(1.0075, 36))
                            / (Math.pow(1.0075, 36) - 1);
                    currentUser.LoanBalance = eligible;
                    currentUser.Loaninfo.loanType = "Property Loan";
                    currentUser.AccountBalance += eligible;
                    currentUser.logoutBank();

                    currentUser.appendTransactionLog("Loan Sanctioned", true, eligible, currentUser.AccountBalance);

                    // loan taken from bank
                }

            default:
                break;
        }

    }

    void RePayLoan() throws Exception {
        System.out.println("Loan Repayment");

        if (currentUser.LoanBalance == 0) {
            System.out.println("You don't have any loan! ");
            return;
        }

        System.out.println("Loan Balance: " + currentUser.LoanBalance);
        System.out.println("Acc Balance: " + currentUser.AccountBalance);

        System.out.print("\nRepaying Amount : ");
        double repayAmt = scanner.nextDouble();

        if (currentUser.AccountBalance < repayAmt) {
            do {
                System.out.println("You don't have enough balance!");
                System.out.println("Enter lesser Amt: ");
                repayAmt = scanner.nextDouble();
            } while (currentUser.AccountBalance < repayAmt);
        }

        currentUser.Loaninfo.rePayLoan(currentUser, repayAmt);

        // animation here
        int totalProgress = 100;

        for (int i = 0; i <= totalProgress; i++) {
            printLoadingBar(i, totalProgress);
            Thread.sleep(40);
        }

        System.out.println("\n\nPayment Done!");

        currentUser.logoutBank();
        
        currentUser.appendTransactionLog("Loan Interest Paid", false, repayAmt, currentUser.AccountBalance);
        // System.out.println("\nPayment Successfull..");
    }

    void TransactionPortal() throws Exception {
        System.out.println("\n\nTransfer Money\n");
        scanner.nextLine();
        System.out.printf("%-25s%-1s\n", "Account to be Debited: ", currentUser.AccountNumber);
        System.out.printf("%-25s%-1s\n", "Current Balance: ", currentUser.AccountBalance);
        System.out.println("\nEnter Receipient's Customer ID: ");
        String recieverID = scanner.nextLine();
        if (!AcntExist(recieverID)) {
            do {
                System.out.println("Enter Correct Customer ID: ");
                recieverID = scanner.nextLine();
            } while (!AcntExist(recieverID));
        }

        Recipient = new Customers();
        Recipient.loginBank(recieverID);
        System.out.printf("%-20s%-1s\n", "Receipient's Name : ", Recipient.CustomerName);
        System.out.print("Amount to be sent: ");
        double sendAmt = scanner.nextDouble();
        System.out.println();
        if (sendAmt < 0) {
            do {
                System.out.println("Please enter valid amount: ");
                sendAmt = scanner.nextDouble();
            } while (sendAmt < 0);
        }
        System.out.println();
        scanner.nextLine();
        int counter = 3;
        System.out.println("Enter Your Password: ");
        String passwd = Bank.getpasswd();
        if (BCrypt.checkpw(passwd, currentUser.Passwd)) {
            currentUser.Transfer(sendAmt, Recipient);
            System.out.println("");
        } else {
            do {
                if (counter <= 0) {
                    System.out.println("You've tried maximun limit! Try again after sometime..");
                    return;
                }
                System.out.println("Wrong Password! " + counter + " more attempt..");
                counter--;
                System.out.println("Enter Your Password: ");
                passwd = Bank.getpasswd();
            } while (!BCrypt.checkpw(passwd, currentUser.Passwd));
            currentUser.Transfer(sendAmt, Recipient);
        }

        // Complete Animation
        int totalProgress = 100; // I need 100 times loop within 5s

        for (int i = 0; i <= totalProgress; i++) {
            printLoadingBar(i, totalProgress);
            Thread.sleep(50);
        }

        System.out.println("\nTransaction completed!");

        System.out.println("");
        return;

    }

    void DeleteAcc() {
        scanner.nextLine();
        System.out.print("Are you sure? (y/n):");
        String decision = scanner.nextLine();
        if (decision.equals("y")) {
            File userFile = new File("Customers/" + currentUser.CustomerID + ".txt");
            if (userFile.delete()) {
                File userlogFile = new File("Customers/" + currentUser.CustomerID + ".log");
                if (userlogFile.delete()) {
                    System.out.println("Deleted Successfully..!");
                    System.out.println("Thank you 🙏");
                    return;
                }
            } else {
                System.out.println("Something went Wrong :(");
            }

        }
    }

    public static void ourCatalog() {

        String ANSI_RESET = "\u001B[0m";
        String ANSI_RED = "\u001B[31m";
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_BLUE = "\u001B[34m";

        System.out.printf(ANSI_GREEN + "Welcome to ZBI (ZOHO Bank of India)\n" + ANSI_RESET);
        System.out.println(
                "Welcome to the ZOHO Bank of India, a financial institution dedicated to providing exceptional banking services with a personal touch.");
        System.out.println();

        System.out.println(ANSI_YELLOW + "Branch Information" + ANSI_RESET);
        System.out.println("Branch Name: Mathalamparai");
        System.out.println("IFSC Code: ZBI0000523");
        System.out.println();

        System.out.println(ANSI_BLUE + "Banking Services" + ANSI_RESET);
        System.out.println("\t* Savings and Current Accounts:");
        System.out.println("\tInterest Rate: Earn an attractive interest of 7.7% on your savings.");
        System.out.println("\tFeatures: Easy access to funds, online banking, mobile banking, and more.");
        System.out.println();
        System.out.println("\t* Fixed Deposits (FDs):");
        System.out.println("\tRate: Secure your future with our Fixed Deposits at a lucrative rate of 12.3%.");
        System.out.println("\tBenefits: High-interest rates, flexible tenure options, and assured returns.");
        System.out.println();
        System.out.println("\t* Recurring Deposits (RDs):");
        System.out.println("\tRate: Grow your savings steadily with our RDs offering an interest rate of 11.5%.");
        System.out.println(
                "\tAdvantages: Ideal for regular savings, flexible deposit amounts, and competitive interest rates.");
        System.out.println();
        System.out.println("\t* Loans:");
        System.out.println("\tLoan Rate: Avail loans at a competitive interest rate of 9%.");
        System.out.println(
                "\tProducts: Personal loans, home loans, car loans, education loans, and business loans tailored to meet your financial goals.");
        System.out.println();

        System.out.println(ANSI_GREEN + "Digital Banking" + ANSI_RESET);
        System.out.println(
                "Mobile and Internet Banking: Manage your accounts, transfer funds, pay bills, and more, all from the comfort of your home or on the go.");
        System.out.println(
                "Digital Wallets: Safe, fast, and convenient digital transactions with ZBI's digital wallet services.");
        System.out.println();

        System.out.println(ANSI_YELLOW + "Investment Services" + ANSI_RESET);
        System.out.println(
                "Plan your finances and invest wisely with our range of investment options, including mutual funds, stocks, and bonds.");
        System.out.println();

        System.out.println(ANSI_RED + "Insurance Products" + ANSI_RESET);
        System.out.println(
                "Protect yourself and your assets with our comprehensive insurance solutions, including life, health, and vehicle insurance.");
        System.out.println();

        System.out.println(ANSI_BLUE + "Customer Support" + ANSI_RESET);
        System.out.println(
                "Our dedicated customer service team is here to assist you with any queries or concerns. Reach out to us via phone, email, or visit our Mathalamparai branch.");
        System.out.println();

        System.out.println(ANSI_GREEN + "Fees and Charges" + ANSI_RESET);
        System.out.println("Transparent and competitive fees and charges across all our services.");
        System.out.println();

        System.out.println(ANSI_YELLOW + "Opening an Account" + ANSI_RESET);
        System.out.println(
                "Join the ZBI family! Learn about the process, documents required, and eligibility criteria for opening an account with us.");
        System.out.println();

        System.out.println(ANSI_RED + "Safety and Security" + ANSI_RESET);
        System.out.println(
                "Your financial security is our top priority. We employ state-of-the-art measures to protect your data and transactions.");
        System.out.println();

        System.out.println(ANSI_BLUE + "Contact Us" + ANSI_RESET);
        System.out.println("Branch Address: Thamirabarani, Near Basement Pantry, Zoho Tenkasi.");
        System.out.println("Phone: +91 9791315636");
        System.out.println("Landline: 044 - 69656070");
        System.out.println("Email: krishnagokul810@gmail.com, sun.a@zohocorp.com");
        System.out.println("Operating Hours: 24/7");
        System.out.println();

        System.out.println(ANSI_GREEN + "Compliance and Legal" + ANSI_RESET);
        System.out.println(
                "ZBI adheres strictly to all banking regulations and legal requirements, ensuring a safe and reliable banking environment for our customers.");
        System.out.println();

    }

}
