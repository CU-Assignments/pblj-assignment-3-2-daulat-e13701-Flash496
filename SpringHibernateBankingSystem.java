
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

// ----- Account Entity -----
@Entity
@Table(name = "account")
class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String accountHolder;
    private double balance;

    public Account() {}
    public Account(String accountHolder, double balance) {
        this.accountHolder = accountHolder;
        this.balance = balance;
    }

    public int getId() { return id; }
    public String getAccountHolder() { return accountHolder; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    @Override
    public String toString() {
        return "Account{" + "id=" + id + ", accountHolder='" + accountHolder + "', balance=" + balance + '}';
    }
}

// ----- Transaction Entity -----
@Entity
@Table(name = "transaction")
class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String sender;
    private String receiver;
    private double amount;

    public Transaction() {}
    public Transaction(String sender, String receiver, double amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" + "sender='" + sender + "', receiver='" + receiver + "', amount=" + amount + '}';
    }
}

// ----- Service for Bank Operations -----
@EnableTransactionManagement
class BankService {

    private SessionFactory sessionFactory;

    public BankService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void transferMoney(int senderId, int receiverId, double amount) throws Exception {
        // Start a new session
        Session session = sessionFactory.getCurrentSession();

        // Retrieve sender and receiver accounts
        Account sender = session.get(Account.class, senderId);
        Account receiver = session.get(Account.class, receiverId);

        if (sender == null || receiver == null) {
            throw new Exception("Invalid account(s) for transfer");
        }

        if (sender.getBalance() < amount) {
            throw new Exception("Insufficient funds in sender's account");
        }

        // Process the transaction (transfer money)
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        // Create a transaction record
        Transaction transaction = new Transaction(sender.getAccountHolder(), receiver.getAccountHolder(), amount);
        session.save(transaction);

        // Update accounts
        session.update(sender);
        session.update(receiver);

        System.out.println("Transfer successful!");
    }
}

// ----- Spring Configuration Class -----
@Configuration
class AppConfig {
    public SessionFactory sessionFactory() {
        return new Configuration()
                .configure() // Uses hibernate.cfg.xml
                .addAnnotatedClass(Account.class)
                .addAnnotatedClass(Transaction.class)
                .buildSessionFactory();
    }

    public BankService bankService(SessionFactory sessionFactory) {
        return new BankService(sessionFactory);
    }
}

// ----- Main Method -----
public class SpringHibernateBankingSystem {

    public static void main(String[] args) {
        // Initialize Spring context and session factory
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        BankService bankService = context.getBean(BankService.class);

        // Create accounts and add to the database
        SessionFactory factory = new Configuration().configure().addAnnotatedClass(Account.class).buildSessionFactory();
        Session session = factory.getCurrentSession();
        session.beginTransaction();

        // Create new accounts
        Account acc1 = new Account("John", 1000);
        Account acc2 = new Account("Jane", 1500);
        session.save(acc1);
        session.save(acc2);

        session.getTransaction().commit();

        // Perform a transfer of money
        try {
            bankService.transferMoney(acc1.getId(), acc2.getId(), 500); // Transfer 500 from John to Jane
        } catch (Exception e) {
            System.out.println("Error during transaction: " + e.getMessage());
        }

        // View updated account balances
        session = factory.getCurrentSession();
        session.beginTransaction();

        Account updatedAcc1 = session.get(Account.class, acc1.getId());
        Account updatedAcc2 = session.get(Account.class, acc2.getId());
        System.out.println("Updated Accounts:");
        System.out.println(updatedAcc1);
        System.out.println(updatedAcc2);

        session.getTransaction().commit();
        factory.close();
    }
}
