package traineeship_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import traineeship_app.domainmodel.Student;
import traineeship_app.domainmodel.TraineeshipPosition;
import traineeship_app.mappers.StudentMapper;
import traineeship_app.mappers.TraineeshipPositionsMapper;
import traineeship_app.services.StudentService;

@Controller
@RequestMapping("/students")
public class StudentController {


    private final StudentService studentService;

    private final StudentMapper studentMapper;

    private final TraineeshipPositionsMapper traineeshipPositionsMapper;

    @Autowired
    public StudentController(StudentService studentService, StudentMapper studentMapper, TraineeshipPositionsMapper traineeshipPositionsMapper) {
        this.studentService = studentService;
        this.studentMapper = studentMapper;
        this.traineeshipPositionsMapper = traineeshipPositionsMapper;
    }


    @GetMapping("/home")
    public String studentHome(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("role", "ROLE_STUDENT");
        return "students/home";  // This maps to templates/students/home.html
    }

    @PostMapping("/register")
    public String registerStudent(
            @ModelAttribute("student") Student student,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.student", result);
            redirectAttributes.addFlashAttribute("student", student);
            return "redirect:/users/register?role=STUDENT";
        }

        try {
            studentService.SaveProfile(student);
            redirectAttributes.addFlashAttribute("success", "Student registration successful!");
            return "redirect:/users/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("student", student);
            return "redirect:/users/register?role=STUDENT";
        }
    }


    @GetMapping("/dashboard")
    public String getStudentDashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        return "students/dashboard";
    }

    @GetMapping("/profile")
    public String retrieveProfile(@RequestParam("username") String studentUsername, Model model) {
        // RequestParam binds the query parameter "username" to the method parameter "studentUsername"

        // Fetch student profile using the username
        Student student = studentService.retrieveProfile(studentUsername);

        // Add student to the model
        model.addAttribute("student", student);

        return "students/profile"; // Returns the profile view
    }

    @PostMapping("/profile/save")
    public String updateProfile(@ModelAttribute("student") Student student, Model theModel) {
        studentService.UpdateProfile(student);
        theModel.addAttribute("student", student);
        return "redirect:/students/profile?username=" + student.getUsername();
    }

    @GetMapping("/logbook")
    public String fillLogbook(@RequestParam("username") String username, Model model, RedirectAttributes redirectAttributes) {
        Student student = studentMapper.findByUsername(username);
        TraineeshipPosition position = traineeshipPositionsMapper.findByStudentUsernameAndIsCompletedFalse(student.getUsername()).orElse(null);

        if (position == null) {
            redirectAttributes.addFlashAttribute("logbookError", "You cannot access the logbook because you have not been assigned a traineeship.");
            return "redirect:/students/home";
        }

        model.addAttribute("position", position);
        model.addAttribute("username", username);
        return "/students/logbook";
    }


    @PostMapping("/logbook/save")
    public String saveLogbook(@RequestParam("username") String username,
                              @ModelAttribute("student") Student student,
                              @ModelAttribute("position") TraineeshipPosition position, Model model) {
        // Fetch student by username
        student = studentMapper.findByUsername(username);

        if (student == null) {
            // Handle case where student isnt found
            throw new IllegalArgumentException("Student not found with username: " + username);
        }

        TraineeshipPosition studentPosition = traineeshipPositionsMapper
                .findByStudentUsernameAndIsCompletedFalse(student.getUsername())
                .orElse(null);

        if (studentPosition != null) {
            studentPosition.setStudentLogbook(position.getStudentLogbook());
            traineeshipPositionsMapper.save(studentPosition);
        }

        studentService.saveLogBook(position, username); // Save logbook entry
        model.addAttribute("student", student);
        model.addAttribute("position", student);
        return "redirect:/students/logbook?username=" + student.getUsername();
    }


}
