package bank;


import java.util.*;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.time.*;

public class Customers {
    String CustomerName;
    LocalDate DoB;
    String PANno;
    String phoneNumber;
    String MailAddress;
    String Address;
    String CustomerID;
    String AccountNumber;
    String Passwd;
    double AccountBalance;
    double LoanBalance;
    double AvgBalance;
    boolean isCurrentAc;
    boolean Active;
    LocalDate OpeningDate;
    String AccountType;
    public DebitCard debitCard;
    Loan Loaninfo = new Loan();

    public void loginATM(String cusID) throws Exception {
        String filePath = "Customers/" + cusID + ".txt";
        login(filePath);
    }

    public void logoutATM() throws Exception {
        // replace all existing values of this file.txt
        String filePath = "Customers/" + this.CustomerID + ".txt";
        logout(filePath);
    }

    public void loginBank(String cusID) throws Exception {
        String filePath = "Customers/" + cusID + ".txt";
        login(filePath);
    }

    public void logoutBank() throws Exception {
        String filePath = "Customers/" + this.CustomerID + ".txt";
        logout(filePath);
    }

    void login(String filePath) throws Exception {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            if (line != null) {
                String[] values = line.split("~");
                this.CustomerName = values[0];
                this.DoB = LocalDate.parse(values[1]);
                this.PANno = values[2];
                this.phoneNumber = values[3];
                this.MailAddress = values[4];
                this.Address = values[5];
                this.CustomerID = values[6];
                this.Passwd = values[7];
                String[] balStrings = values[8].substring(1, values[8].length() - 1).split(",");
                double[] balances = new double[3];
                for (int i = 0; i < 3; i++) {
                    balances[i] = Double.parseDouble(balStrings[i]);
                }
                this.AccountBalance = balances[0];
                this.LoanBalance = balances[1];
                this.AvgBalance = balances[2];

                this.isCurrentAc = (values[9].equals("0") ? false : true);
                this.Active = (values[10].equals("0") ? false : true);
                this.OpeningDate = LocalDate.parse(values[11]);

                if (isCurrentAc) {
                    this.AccountType = "Current A/c";
                } else
                    this.AccountType = "Savings A/c";

                String[] tempCard = values[12].substring(1, values[12].length() - 1).split(",");
                String[] tempExp = tempCard[1].split("/");
                this.debitCard = new DebitCard(tempCard[0], Integer.parseInt(tempExp[1]), Integer.parseInt(tempExp[0]),
                        Integer.parseInt(tempCard[2]), Integer.parseInt(tempCard[3]),
                        (tempCard[4].equals("1")) ? true : false, Double.parseDouble(tempCard[5]));
                this.Loaninfo.LoanObtained = Double.parseDouble(values[13]);
                this.Loaninfo.loanType = values[14];
                this.AccountNumber = values[15];
                this.Loaninfo.monthlyInterest = this.Loaninfo.LoanObtained * (0.0075 * Math.pow(1.0075, 36))
                        / (Math.pow(1.0075, 36) - 1);

            } else {
                System.out.println("User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
    void logout(String filePath) throws Exception {
        // replace all existing values of this file.txt
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedString = CustomerName + "~" +
                DoB.format(formatter) + "~" +
                PANno + "~" +
                phoneNumber + "~" +
                MailAddress + "~" +
                Address + "~" +
                CustomerID + "~" +
                Passwd + "~" +
                "[" + AccountBalance + "," + LoanBalance + "," + AvgBalance + "]" + "~" +
                (isCurrentAc ? "1" : "0") + "~" +
                (Active ? "1" : "0") + "~" +
                OpeningDate.format(formatter) + "~" +
                "[" + debitCard.getCardNumber() + "," +
                debitCard.getExpMonth() + "/" + debitCard.getExpYear() + "," +
                debitCard.getCVV() + "," +
                debitCard.getPIN() + "," +
                (debitCard.isActive() ? "1" : "0") + "," +
                debitCard.getLimit() + "]" + "~" +
                this.Loaninfo.LoanObtained + "~" +
                this.Loaninfo.loanType + "~" +
                this.AccountNumber;

        // System.out.println(formattedString);
        FileWriter userData = new FileWriter(filePath);

        userData.write(formattedString);
        userData.close();
    }

    void getInterest() {
        this.AccountBalance *= (Bank.interestRate / 1200);
        // create a log
    }

    void Transfer(double amt, Customers receipient) throws Exception {
        // cusID must be valid
        if (amt > 0 && receipient.Active) {
            this.AccountBalance -= amt;
            receipient.AccountBalance += amt;
            
            this.appendTransactionLog("Tranfer to " + receipient.CustomerName, false, amt, this.AccountBalance);
            receipient.appendTransactionLog("Recieved from " + this.CustomerName, true, amt, receipient.AccountBalance);

            receipient.logoutBank();
            this.logoutBank();

        }

    }

    void DisplayPersonalDetails() throws Exception {
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("My Profile: ");
        Thread.sleep(200);
        System.out.printf("\n\tLogin Info: \n");
        Thread.sleep(200);
        System.out.printf("\t\t%-20s%-1s\n", "Customer ID:", this.CustomerID);
        Thread.sleep(200);
        System.out.println("\n\tPersonal Info: ");
        Thread.sleep(200);
        System.out.printf("\t\t%-20s%-1s\n", "Customer Name:", this.CustomerName);
        Thread.sleep(200);
        System.out.printf("\t\t%-20s%-1s\n", "D.O.B:", this.DoB);
        Thread.sleep(200);
        System.out.printf("\t\t%-20s%-1s\n", "PAN Details:", this.PANno);
        Thread.sleep(200);
        System.out.println("\n\tContact Info: ");
        Thread.sleep(200);
        System.out.printf("\t\t%-20s%-1s\n", "Address:", this.Address);
        Thread.sleep(200);
        System.out.printf("\t\t%-20s%-1s\n", "Phone Number:", this.phoneNumber);
        Thread.sleep(200);
        System.out.printf("\t\t%-20s%-1s\n", "Email Address:", this.MailAddress);
        Thread.sleep(200);
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    void DisplayAccInfo() throws Exception {
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        Thread.sleep(200);
        System.out.printf("\nAccount Info: ");
        Thread.sleep(200);
        System.out.println("\n\tBasic Details");
        Thread.sleep(200);
        System.out.printf("\t\t%-20s%-1s\n", "Account Number:", this.AccountNumber);
        Thread.sleep(200);
        System.out.printf("\t\t%-20s%-1s\n", "Customer ID:", this.CustomerID);
        Thread.sleep(200);
        System.out.printf("\t\t%-20s%-1s\n", "Branch:", Bank.Branch);
        Thread.sleep(200);
        System.out.printf("\t\t%-20s%-1s\n", "IFSC Code:", Bank.IFSC);
        Thread.sleep(150);
        System.out.printf("\t\t%-20s%-1s\n", "Account Status:", (this.Active ? "Active" : "Freeze"));
        Thread.sleep(150);
        System.out.printf("\t\t%-20s%-1s\n", "Opening Date:", this.OpeningDate);
        Thread.sleep(150);
        System.out.printf("\t\t%-20s%-1s\n", "Account Type:", this.AccountType);
        Thread.sleep(150);
        System.out.println("\n\tBalance Info : \n");
        System.out.printf("\t\t%-20s%-1s\n", "Account Balance : ", String.format("₹ %.2f", this.AccountBalance));
        Thread.sleep(150);
        if (this.isCurrentAc && (this.AccountBalance < 0)) {
            System.out.printf("\t\t%-20s%-1s\n", "OverDraft : ",
                    String.format("₹ %.2f", Math.abs(this.AccountBalance)));
            Thread.sleep(100);
        }
        // System.out.printf("\t\t%-20s%-1s\n", "Average Balance:", String.format("₹
        // %.2f", this.AvgBalance));
        Thread.sleep(100);

        System.out.println("\n\tLoan Details: \n");
        Thread.sleep(100);
        System.out.printf("\t\t%-20s%-1s\n", "Loan Obtained:", String.format("₹ %.2f", this.Loaninfo.LoanObtained));
        Thread.sleep(100);
        System.out.printf("\t\t%-20s%-1s\n", "Loan Type:", this.Loaninfo.loanType);
        Thread.sleep(100);
        System.out.printf("\t\t%-20s%-1s\n", "Monthly Interest:",
                String.format("₹ %.2f", this.Loaninfo.monthlyInterest));
        Thread.sleep(100);
        System.out.printf("\t\t%-20s%-1s\n", "Outstanding Loan :",
                String.format("₹ %.2f", +this.Loaninfo.LoanObtained));
        Thread.sleep(100);

        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    void init() {
        // init method calls
    }

    public void appendTransactionLog(String description, boolean isCredit, double amount, double closingBalance) throws Exception{
        LocalDateTime transactionTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String logEntry = transactionTime.format(formatter) + "~" + description + "~" +
                (isCredit ? "1" : "0") + "~" + amount + "~" + closingBalance;
        
        try (PrintWriter out = new PrintWriter(
                new BufferedWriter(new FileWriter("Customers/" + this.CustomerID + ".log", true)))) {
            out.println(logEntry);
        } catch (IOException e) {
            System.err.println("Error appending log entry: " + e.getMessage());
        }
    }

    public void getLastNTransactions(int n) throws Exception{
        List<String> lastNTransactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("Customers/" + this.CustomerID + ".log"))) {
            String line;
            LinkedList<String> linkedList = new LinkedList<>();

            while ((line = reader.readLine()) != null) {
                linkedList.addLast(line);
                if (linkedList.size() > n) {
                    linkedList.removeFirst();
                }
            }

            lastNTransactions.addAll(linkedList);
            Collections.reverse(lastNTransactions);

            System.out.println("--------------------------------------------------------------------------------------------------");
            Thread.sleep(200);
            System.out.printf("| %-20s | %-30s | %-8s | %-10s | %-14s |%n",
                    "Transaction Time", "Description", "Type", "Amount", "Balance");
            Thread.sleep(200);

            System.out.println(
                    "|----------------------|--------------------------------|----------|------------|----------------|");
            Thread.sleep(200);

            
            for (String transaction : lastNTransactions) {
                String[] transactionDetails = transaction.split("~");
                LocalDateTime transactionTime = LocalDateTime.parse(transactionDetails[0],
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String description = transactionDetails[1];
                String type = (transactionDetails[2].equals("1")) ? "Credit" : "Debit";
                double amount = Double.parseDouble(transactionDetails[3]);
                double closingBalance = Double.parseDouble(transactionDetails[4]);

                System.out.printf("| %-20s | %-30s | %-8s | %-10.2f | %-14.2f |%n",
                        transactionDetails[0], description, type, amount, closingBalance);
                Thread.sleep(200);
            }
            System.out.println("--------------------------------------------------------------------------------------------------");

        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
        }

    }
}

class DebitCard {
    String CardNumber;
    LocalDate ExpDate;
    int CVV;
    int PinNumber;
    double CardLimit;
    boolean Active;

    static Scanner scanner = new Scanner(System.in);

    DebitCard(String cardno, int expYYYY, int expMM, int cvv, int pin, boolean active) {
        this.CardNumber = cardno;
        this.ExpDate = LocalDate.of(expYYYY, expMM, 1);
        this.CVV = cvv;
        this.PinNumber = pin;
        this.Active = active;
    }

    DebitCard(String cardno, int expYYYY, int expMM, int cvv, int pin, boolean active, double cardlimit) {
        this(cardno, expYYYY, expMM, cvv, pin, active);
        this.CardLimit = cardlimit;
    }

    void setLimit(double amt) {
        this.CardLimit = amt;
    }

    void ChangePIN(int PINnew) {
        this.PinNumber = PINnew;
    }

    void BlockCard() {
        this.Active = false;
    }

    boolean verifyExp() {
        LocalDate Today = LocalDate.now();
        if (Today.isBefore(ExpDate)) {
            return true;
        }
        return false;
    }

    String getCardNumber() {
        return this.CardNumber;
    }

    int getExpMonth() {
        return this.ExpDate.getDayOfMonth();
    }

    int getExpYear() {
        return this.ExpDate.getYear();
    }

    int getCVV() {
        return this.CVV;
    }

    int getPIN() {
        return this.PinNumber;
    }

    double getLimit() {
        return this.CardLimit;
    }

    boolean isActive() {
        return this.Active;
    }

    void Manage(Customers current) throws Exception {

        int choice;
        do {
            System.out.println("Manage Your Card: ");
            System.out.println("\t1. Show Card Details");
            System.out.println("\t2. Pin Generate or Change ");
            System.out.println("\t3. Set Card Limit");
            System.out.println("\t4. Block Your Card");
            System.out.println("\t5. Back");

            System.out.print("\nEnter Your Choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n\nDebit Card Details");
                    Thread.sleep(200);
                    System.out.printf("\t%-20s%-1s\n", "Card Number:",
                            "XXXXXXXX" + this.CardNumber.substring(9));
                    Thread.sleep(200);
                    System.out.printf("\t%-20s%-1s\n", "Expire on:", this.ExpDate);
                    Thread.sleep(200);
                    System.out.printf("\t%-20s%-1s\n", "Status:",
                            (this.PinNumber == 0) ? "InActive" : this.Active ? "Active" : "Blocked");
                    Thread.sleep(200);
                    System.out.printf("\t%-20s%-1s\n", "Card Limit:", this.CardLimit);
                    System.out.println("\n\n");
                    break;

                case 2:
                    scanner.nextLine();
                    System.out.println("\n\nGenerate or Change PIN");
                    System.out.print("\nEnter Your DebitCard Number: ");
                    String crdNum = scanner.nextLine();
                    if (!this.CardNumber.equals(crdNum)) {
                        do {
                            System.out.println("Invalid Numbers!");
                            System.out.print("\nEnter Again: ");
                            crdNum = scanner.nextLine();
                        } while (!this.CardNumber.equals(crdNum));
                    }


                    System.out.print("\nSet a PIN: ");
                    String tempPin = Bank.getpasswd();

                    if (Bank.isValidPIN(Integer.parseInt(tempPin))) {
                        System.out.print("\nConfirm PIN: ");
                        if (tempPin.equals(Bank.getpasswd())) {
                            this.ChangePIN(choice);
                            System.out.println("\nPIN Set Successfully!\n\n");
                        }else{
                            System.out.println("PIN Mismatch!");
                            break;
                        }
                    }
                    else {
                        System.out.println("Invalid PIN!");
                    }
                    break;

                case 3:
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    Thread.sleep(200);
                    System.out.printf("\t%-20s%-1s\n", "Card Limit:", this.CardLimit);
                    Thread.sleep(200);
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    Thread.sleep(200);
                    scanner.nextLine();
                    System.out.print("Want to change limit (y/n) : ");
                    String yesorNO = scanner.nextLine();

                    if (yesorNO.equals("y")) {
                        System.out.print("\nEnter Limit [0 - 50000]: ");
                        Double newLimit = scanner.nextDouble();
                        if ((newLimit < 50000) && (newLimit > 0)) {
                            this.CardLimit = newLimit;
                            System.out.println("Updated Successfully!");
                        } else {
                            System.out.println("Invalid limit!");
                        }
                    }
                    break;

                case 4:
                    if (this.Active) {
                        scanner.nextLine();
                        System.out.print("\nPress 'y' to Block your Card: ");
                        String decision = scanner.nextLine();
                        if (decision.equals("y")) {
                            this.Active = false;
                            System.out.println("Your Card is Blocked!");
                        }
                    } else {
                        System.out.println("Your card is already Inactive..");
                        System.out.println("\n\n");
                    }

                    break;

                case 5:
                    System.out.println("<-- Back");
                    break;
                default:
                    break;
            }

        } while (choice < 5);
    }

    void newDebitCard(Customers currentUser) throws Exception {
        System.out.println("\nApply for new Debit Card: ");

        if (this.Active) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("You already have an Active debit card!");
            System.out.println("You can Apply after block the old one!");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            return;
        }

        scanner.nextLine();
        System.out.print("\nEnter your customer ID: ");
        String tempCusID = scanner.nextLine();
        System.out.println("\nMobile number: ");
        String tempPh = scanner.nextLine();
        boolean wrong = ((currentUser.CustomerID.equals(tempCusID) && currentUser.phoneNumber.equals(tempPh)));
        if (wrong) {
            for (int i = 3; (wrong && i > 0); i--) {
                System.out.println("Wrong details! Try again..");
                System.out.print("\nEnter your customer ID: ");
                tempCusID = scanner.nextLine();
                System.out.println("\nMobile number: ");
                tempPh = scanner.nextLine();
                if (i==1) {
                    return;
                }
            }
            
        }

        Bank.newCardIssue(currentUser);

        System.out.println("New Card Issued Successfully");

    }

}

class Loan {
    double LoanObtained;
    String loanType;
    double monthlyInterest;

    void payInterest(Customers obj) {
        obj.LoanBalance -= this.monthlyInterest;
    }

    void rePayLoan(Customers obj, double amt) {
        obj.LoanBalance -= amt;
        obj.AccountBalance -= amt;
    }

    void calcMonthlyInterest(Customers obj) {
        this.monthlyInterest = (obj.LoanBalance * (0.0075 * Math.pow(1.0075, 36)) / (Math.pow(1.0075, 36) - 1));
    }
}
