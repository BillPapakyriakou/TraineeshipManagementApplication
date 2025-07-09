package traineeship_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import traineeship_app.domainmodel.*;
import traineeship_app.services.CommitteeService;
import traineeship_app.services.CompanyService;
import traineeship_app.services.ProfessorService;
import traineeship_app.services.StudentService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/committees")
public class CommitteeController {


    private final CommitteeService committeeService;

    private final CompanyService companyService;

    private final ProfessorService professorService;


    @Autowired
    public CommitteeController(CommitteeService committeeService, CompanyService companyService, ProfessorService professorService){
        this.committeeService = committeeService;
        this.companyService = companyService;
        this.professorService = professorService;
    }

    @GetMapping("/home")
    public String commiteeHome(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("role", "ROLE_COMMITTEE");
        return "committees/home";  // This maps to templates/committee/home.html
    }

    @PostMapping("/register")
    public String registerCommittee(
            @ModelAttribute("committee_member") Committee committee,
            BindingResult result,
            RedirectAttributes redirectAttributes) {


        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.committee", result);
            redirectAttributes.addFlashAttribute("committee_member", committee);
            return "redirect:/users/register?role=COMMITTEE_MEMBER";
        }

        try {
            committeeService.SaveProfile(committee);
            redirectAttributes.addFlashAttribute("success", "COMMITTEE registration successful!");
            return "redirect:/users/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("committee_member", committee);
            return "redirect:/users/register?role=COMMITTEE_MEMBER";
        }
    }

    @GetMapping("/applicants-list")
    public String showApplicants(Model model) {
        List<Student> applicants = committeeService.getAllStudentsLookingForTraineeship();
        model.addAttribute("applicants", applicants);
        return "committees/applicants-list";
    }

    @GetMapping("/applicants-list/find-traineeships/{id}")
    public String findTraineeship(
            @PathVariable("id") Long studentId,
            @RequestParam(value = "strategy", defaultValue = "both") String strategy,
            Model model) {

        // Fetch the student by ID
        Student student = committeeService.getStudentById(studentId);

        // Use CommitteeService to get positions for the selected student
        List<TraineeshipPosition> matchingPositions = committeeService.getCandidatePositions(strategy, student.getUsername());

        // Print out search results for debugging purposes
        //System.out.println("Strategy: " + strategy);
        //System.out.println("Found " + matchingPositions.size() + " positions:");
        for (TraineeshipPosition position : matchingPositions) {
            System.out.println("- " + position.getTitle() + " at " + position.getCompany().getCompanyName());
        }

        // Add the student and positions to the model to display on the page
        model.addAttribute("student", student);
        model.addAttribute("positions", matchingPositions);
        model.addAttribute("strategy", strategy);

        return "committees/find-traineeships";
    }

    @PostMapping("/assign-position")
    public String assignPosition(@RequestParam("positionId") int positionId,
                                 @RequestParam("studentUsername") String studentUsername,
                                 @RequestParam("studentId") Long studentId,
                                 @RequestParam(value = "strategy", defaultValue = "both") String strategy,
                                 RedirectAttributes redirectAttributes) {
        committeeService.assignPosition(positionId, studentUsername);

        redirectAttributes.addFlashAttribute("message", "Traineeship assigned successfully!");

        return "redirect:/committees/applicants-list";

    }

    @GetMapping("/in-progress")
    public String listAssignedTraineeships(Model model) {
        List<TraineeshipPosition> assignedPositions = committeeService.listAssignedTraineeships();
        model.addAttribute("position", assignedPositions);
        return "committees/in-progress";
    }

    @GetMapping("/unsupervised-positions")
    public String listUnsupervisedPositions(Model model) {
        List<TraineeshipPosition> unsupervisedPositions = committeeService.listUnsupervisedPositions();
        model.addAttribute("positions", unsupervisedPositions);
        return "committees/unsupervised-positions";
    }

    @GetMapping("/unsupervised-positions/find-supervisors/{id}")
    public String findSupervisor(
            @PathVariable("id") Long positionId,
            @RequestParam(value = "strategy", defaultValue = "load") String strategy,
            Model model) {

        // Get the traineeship position by ID
        TraineeshipPosition position = committeeService.getPositionById(positionId);

        // Use committee service to get candidate professors based on strategy and position
        List<Professor> candidateProfessors = committeeService.getCandidateProfessors(strategy, position);

        Map<Long, Integer> workloadMap = committeeService.getWorkloadForProfessors(candidateProfessors);


        model.addAttribute("workloadMap", workloadMap);
        model.addAttribute("position", position);
        model.addAttribute("professors", candidateProfessors);
        model.addAttribute("strategy", strategy);

        return "committees/find-supervisors";
    }

    @PostMapping("/assign-supervisor")
    public String assignSupervisor(@RequestParam("positionId") int positionId,
                                   @RequestParam("professorId") Long professorId,
                                   RedirectAttributes redirectAttributes) {

        committeeService.assignSupervisorToPosition(positionId, professorId);

        redirectAttributes.addFlashAttribute("message", "Supervisor assigned successfully!");

        // Redirects back to the supervisor select page
        return "redirect:/committees/unsupervised-positions/find-supervisors/" + positionId;
    }


    @GetMapping("/show-evaluations/{positionId}")
    public String showEvaluations(@PathVariable int positionId, Model model) {
        TraineeshipPosition position = companyService.getPositionById(positionId);

        Optional<Evaluation> professorEval = professorService.findProfessorEvaluationByPosition(position);
        Optional<Evaluation> companyEval = companyService.findCompanyEvaluationByPosition(position);

        model.addAttribute("position", position);
        model.addAttribute("professorEval", professorEval.orElse(null));
        model.addAttribute("companyEval", companyEval.orElse(null));

        return "committees/show-evaluations";
    }


    @PostMapping("/pass/{id}")
    public String pass(@PathVariable Long id) {
        TraineeshipPosition traineeship = committeeService.getPositionById(id);
        if (traineeship != null) {
            traineeship.setAssigned(false);
            traineeship.setPassFailGrade(true);
            traineeship.setCompleted(true);
            traineeship.setSupervisor(null);  // Drop the professor so that AssignBasedOnLoad
                                                // as intended after a traineeship is completed

            Student student = traineeship.getStudent();
            if (student != null) {
                student.setAssignedTraineeship(null);
                committeeService.saveStudent(student);
            }

            committeeService.savePosition(traineeship);
        }
        return "redirect:/committees/show-evaluations/" + id + "?continue";
    }

    @PostMapping("/fail/{id}")
    public String fail(@PathVariable Long id) {
        TraineeshipPosition traineeship = committeeService.getPositionById(id);
        if (traineeship != null) {
            traineeship.setAssigned(false);
            traineeship.setPassFailGrade(false);
            traineeship.setCompleted(true);
            traineeship.setSupervisor(null);

            Student student = traineeship.getStudent();
            if (student != null) {
                student.setAssignedTraineeship(null);
                committeeService.saveStudent(student);
            }

            committeeService.savePosition(traineeship);
        }
        return "redirect:/committees/show-evaluations/" + id + "?continue";
    }

}
