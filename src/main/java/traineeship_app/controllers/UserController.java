package traineeship_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import traineeship_app.domainmodel.Committee;
import traineeship_app.domainmodel.Company;
import traineeship_app.domainmodel.Professor;
import traineeship_app.domainmodel.Student;
import traineeship_app.services.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }


    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "users/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(@RequestParam(required = false) String role, Model model) {
        model.addAttribute("selectedRole", role != null ? role : "");

        if ("STUDENT".equals(role)) {
            model.addAttribute("student", new Student());
        } else if ("PROFESSOR".equals(role)) {
            model.addAttribute("professor", new Professor());
        } else if ("COMPANY".equals(role)) {
            model.addAttribute("company", new Company());
        }else if("COMMITTEE_MEMBER".equals(role)){
            model.addAttribute("committee_member", new Committee());
        }
        return "users/register";
    }

    @PostMapping("/register/select-role")
    public String selectRole(@RequestParam String role) {
        return "redirect:/users/register?role=" + role;
    }
    @GetMapping("/home")
    public String home(Authentication authentication, Model model){
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .orElseThrow(()->new IllegalStateException("User has no roles"))
                .getAuthority();
        model.addAttribute("username", username);
        model.addAttribute("role", role);

        switch (role) {
            case "ROLE_STUDENT":
                return "redirect:/students/home";
            case "ROLE_PROFESSOR":
                return "redirect:/professors/home";
            case "ROLE_COMPANY":
                return "redirect:/companies/home";
            case "ROLE_COMMITTEE_MEMBER":
                return "redirect:/committees/home";
            default:
                throw new IllegalStateException("Unknown role: " + role);
        }

    }


    @GetMapping("/logbook")
    public String showLogbookPage() {
        return "student/logbook";  // maps to templates/student/logbook.html
    }

    @GetMapping("/dashboard")
    public String showDashboardPage() {
        return "students/dashboard";  // maps to templates/student/dashboard.html
    }

    @GetMapping("/profile")
    public String showProfilePage() {
        return "students/profile";  // maps to templates/student/profile.html
    }


}