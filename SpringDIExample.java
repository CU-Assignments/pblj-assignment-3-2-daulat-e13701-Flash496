
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

// ----- Course Class -----
class Course {
    private String courseName;
    private int duration;

    public Course(String courseName, int duration) {
        this.courseName = courseName;
        this.duration = duration;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return courseName + " (" + duration + " months)";
    }
}

// ----- Student Class -----
class Student {
    private String name;
    private Course course;

    public Student(String name, Course course) {
        this.name = name;
        this.course = course;
    }

    public void showDetails() {
        System.out.println("Student Name: " + name);
        System.out.println("Enrolled Course: " + course);
    }
}

// ----- Spring Configuration -----
@Configuration
class AppConfig {
    @Bean
    public Course course() {
        return new Course("Spring Boot", 3);
    }

    @Bean
    public Student student() {
        return new Student("Debangshu", course());
    }
}

// ----- Main Method -----
public class SpringDIExample {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Student student = context.getBean(Student.class);
        student.showDetails();
    }
}
