import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class StudentCourseRegistrationSystem {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                StudentCourseRegistrationSystem window = new StudentCourseRegistrationSystem();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private JFrame frame;
    private JTextField studentIdField;
    private JTextField studentNameField;
    private JComboBox<String> courseComboBox;
    private JTextArea courseListTextArea;
    private JTextArea registeredCoursesTextArea;

    private HashMap<String, Course> courseDatabase = new HashMap<>();
    private HashMap<String, Student> studentDatabase = new HashMap<>();

    public StudentCourseRegistrationSystem() {
        initializeCourses();
        initialize();
    }

    private void initializeCourses() {
        courseDatabase.put("CSE101", new Course("CSE101", "Intro to Computer Science", "Basics of CS", 30, "Mon-Wed-Fri 10:00-11:00"));
        courseDatabase.put("MAT201", new Course("MAT201", "Calculus I", "Differential Calculus", 25, "Tue-Thu 14:00-15:30"));
        courseDatabase.put("PHY101", new Course("PHY101", "Physics I", "Mechanics", 20, "Mon-Wed 09:00-10:30"));
        courseDatabase.put("ENG101", new Course("ENG101", "English Literature", "Introduction to Literature", 35, "Fri 13:00-15:00"));
    }

    private void initialize() {
        frame = new JFrame("Student Course Registration System");
        frame.setBounds(100, 100, 600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
        inputPanel.setLayout(new GridLayout(3, 2));

        JLabel lblStudentId = new JLabel("Student ID:");
        inputPanel.add(lblStudentId);

        studentIdField = new JTextField();
        inputPanel.add(studentIdField);
        studentIdField.setColumns(10);

        JLabel lblStudentName = new JLabel("Student Name:");
        inputPanel.add(lblStudentName);

        studentNameField = new JTextField();
        inputPanel.add(studentNameField);
        studentNameField.setColumns(10);

        JLabel lblCourse = new JLabel("Select Course:");
        inputPanel.add(lblCourse);

        courseComboBox = new JComboBox<>();
        for (Course course : courseDatabase.values()) {
            courseComboBox.addItem(course.getCourseCode() + " - " + course.getTitle());
        }
        inputPanel.add(courseComboBox);

        JPanel buttonPanel = new JPanel();
        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);
        buttonPanel.setLayout(new GridLayout(1, 2));

        JButton btnRegisterCourse = new JButton("Register Course");
        btnRegisterCourse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerCourse();
            }
        });
        buttonPanel.add(btnRegisterCourse);

        JButton btnDropCourse = new JButton("Drop Course");
        btnDropCourse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dropCourse();
            }
        });
        buttonPanel.add(btnDropCourse);

        JPanel outputPanel = new JPanel();
        frame.getContentPane().add(outputPanel, BorderLayout.SOUTH);
        outputPanel.setLayout(new GridLayout(2, 1));

        courseListTextArea = new JTextArea();
        courseListTextArea.setEditable(false);
        outputPanel.add(new JScrollPane(courseListTextArea));

        registeredCoursesTextArea = new JTextArea();
        registeredCoursesTextArea.setEditable(false);
        outputPanel.add(new JScrollPane(registeredCoursesTextArea));

        updateCourseList();
    }

    private void updateCourseList() {
        StringBuilder courseList = new StringBuilder("Available Courses:\n");
        for (Course course : courseDatabase.values()) {
            courseList.append(course).append("\n");
        }
        courseListTextArea.setText(courseList.toString());
    }

    private void registerCourse() {
        String studentId = studentIdField.getText().trim();
        String studentName = studentNameField.getText().trim();
        String selectedCourse = (String) courseComboBox.getSelectedItem();

        if (studentId.isEmpty() || studentName.isEmpty() || selectedCourse == null) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String courseCode = selectedCourse.split(" - ")[0];
        Course course = courseDatabase.get(courseCode);
        if (course == null || course.getAvailableSlots() <= 0) {
            JOptionPane.showMessageDialog(frame, "Course is full or does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student student = studentDatabase.getOrDefault(studentId, new Student(studentId, studentName));
        if (student.registerCourse(course)) {
            studentDatabase.put(studentId, student);
            JOptionPane.showMessageDialog(frame, "Course registered successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Already registered for this course or capacity reached.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateCourseList();
        updateRegisteredCourses(student);
    }

    private void dropCourse() {
        String studentId = studentIdField.getText().trim();
        String selectedCourse = (String) courseComboBox.getSelectedItem();

        if (studentId.isEmpty() || selectedCourse == null) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String courseCode = selectedCourse.split(" - ")[0];
        Course course = courseDatabase.get(courseCode);
        if (course == null) {
            JOptionPane.showMessageDialog(frame, "Course does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student student = studentDatabase.get(studentId);
        if (student != null && student.dropCourse(course)) {
            JOptionPane.showMessageDialog(frame, "Course dropped successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Not registered for this course.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateCourseList();
        updateRegisteredCourses(student);
    }

    private void updateRegisteredCourses(Student student) {
        if (student != null) {
            registeredCoursesTextArea.setText("Registered Courses for " + student.getName() + ":\n" + student.getRegisteredCourses());
        }
    }

    class Course {
        private String courseCode;
        private String title;
        private String description;
        private int capacity;
        private int registeredStudents;
        private String schedule;

        public Course(String courseCode, String title, String description, int capacity, String schedule) {
            this.courseCode = courseCode;
            this.title = title;
            this.description = description;
            this.capacity = capacity;
            this.schedule = schedule;
            this.registeredStudents = 0;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int getCapacity() {
            return capacity;
        }

        public int getAvailableSlots() {
            return capacity - registeredStudents;
        }

        public String getSchedule() {
            return schedule;
        }

        public boolean registerStudent() {
            if (registeredStudents < capacity) {
                registeredStudents++;
                return true;
            }
            return false;
        }

        public boolean dropStudent() {
            if (registeredStudents > 0) {
                registeredStudents--;
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return courseCode + " - " + title + " (" + getAvailableSlots() + " slots available)";
        }
    }

    class Student {
        private String studentId;
        private String name;
        private ArrayList<Course> registeredCourses;

        public Student(String studentId, String name) {
            this.studentId = studentId;
            this.name = name;
            this.registeredCourses = new ArrayList<>();
        }

        public String getStudentId() {
            return studentId;
        }

        public String getName() {
            return name;
        }

        public String getRegisteredCourses() {
            StringBuilder courses = new StringBuilder();
            for (Course course : registeredCourses) {
                courses.append(course.getCourseCode()).append(" - ").append(course.getTitle()).append("\n");
            }
            return courses.toString();
        }

        public boolean registerCourse(Course course) {
            if (registeredCourses.contains(course)) {
                return false;
            }
            if (course.registerStudent()) {
                registeredCourses.add(course);
                return true;
            }
            return false;
        }

        public boolean dropCourse(Course course) {
            if (registeredCourses.contains(course)) {
                registeredCourses.remove(course);
                course.dropStudent();
                return true;
            }
            return false;
        }
    }
}
