

import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

// ----- Entity Class -----
@Entity
@Table(name = "student")
class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int age;

    public Student() {}
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }

    @Override
    public String toString() {
        return id + ": " + name + ", Age: " + age;
    }
}

// ----- Main Class with Hibernate CRUD -----
public class HibernateCRUDExample {
    public static void main(String[] args) {
        // Step 1: Setup Hibernate SessionFactory
        SessionFactory factory = new Configuration()
                .configure() // Looks for hibernate.cfg.xml
                .addAnnotatedClass(Student.class)
                .buildSessionFactory();

        // Step 2: Perform CRUD operations
        try (Session session = factory.getCurrentSession()) {
            // ----- CREATE -----
            Student s1 = new Student("Debangshu", 21);
            Student s2 = new Student("Ananya", 22);

            session.beginTransaction();
            session.save(s1);
            session.save(s2);
            session.getTransaction().commit();
            System.out.println("Students created!");

            // ----- READ -----
            Session readSession = factory.getCurrentSession();
            readSession.beginTransaction();
            List<Student> students = readSession.createQuery("from Student", Student.class).getResultList();
            System.out.println("All Students:");
            students.forEach(System.out::println);
            readSession.getTransaction().commit();

            // ----- UPDATE -----
            Session updateSession = factory.getCurrentSession();
            updateSession.beginTransaction();
            Student updateStudent = updateSession.get(Student.class, s1.getId());
            if (updateStudent != null) {
                updateStudent.setAge(25);
            }
            updateSession.getTransaction().commit();
            System.out.println("Student updated!");

            // ----- DELETE -----
            Session deleteSession = factory.getCurrentSession();
            deleteSession.beginTransaction();
            Student deleteStudent = deleteSession.get(Student.class, s2.getId());
            if (deleteStudent != null) {
                deleteSession.delete(deleteStudent);
            }
            deleteSession.getTransaction().commit();
            System.out.println("Student deleted!");

        } finally {
            factory.close();
        }
    }
}
