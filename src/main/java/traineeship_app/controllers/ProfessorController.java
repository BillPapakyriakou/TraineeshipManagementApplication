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

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/professors")
public class ProfessorController{

    private final ProfessorService professorService;

    private final CompanyService companyService;

    private final CommitteeService committeeService;

    @Autowired
    public ProfessorController(ProfessorService professorService, CompanyService companyService, CommitteeService committeeService) {
        this.professorService = professorService;
        this.companyService = companyService;

        this.committeeService = committeeService;
    }

    @GetMapping("/home")
    public String professorHome(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("role", "ROLE_PROFESSOR");
        return "professors/home";  // This maps to templates/professors/home.html
    }

    @PostMapping("/register")
    public String registerProfessor(
            @ModelAttribute("professor") Professor professor,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.professor", result);
            redirectAttributes.addFlashAttribute("professor", professor);
            return "redirect:/users/register?role=PROFESSOR";
        }

        try {
            professorService.SaveProfile(professor);
            redirectAttributes.addFlashAttribute("success", "Professor registration successful!");
            return "redirect:/users/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("professor", professor);
            return "redirect:/users/register?role=PROFESSOR";
        }

    }

    @GetMapping("/profile")
    public String retrieveProfile(@RequestParam("username") String professorUsername, Model model) {

        Professor professor = professorService.retrieveProfile(professorUsername);

        // Add professor to the model
        model.addAttribute("professor", professor);

        return "professors/profile"; // Returns the profile view
    }

    @PostMapping("/profile/save")
    public String updateProfile(@ModelAttribute("professor") Professor professor, Model theModel) {
        professorService.UpdateProfile(professor);
        theModel.addAttribute("professor", professor);
        return "redirect:/professors/profile?username=" + professor.getUsername();
    }

    @GetMapping("/supervised-positions")
    public String getSupervisedPositions(@RequestParam String username, Model model) {
        Professor professor = professorService.findByUsername(username);

        // Fetch the positions supervised by this professor
        List<TraineeshipPosition> supervisedPositions = committeeService.getPositionsBySupervisor(professor);

        model.addAttribute("professor", professor);
        model.addAttribute("positions", supervisedPositions);
        return "professors/supervised-positions";
    }

    @GetMapping("/evaluation/{positionId}")
    public String showEvaluationForm(@PathVariable int positionId, Model model) {
        TraineeshipPosition position = companyService.getPositionById(positionId);

        // Try to fetch existing evaluation
        Optional<Evaluation> existingEval = professorService
                .findProfessorEvaluationByPosition(position);

        Evaluation evaluation = existingEval.orElseGet(() -> {
            Evaluation newEval = new Evaluation();
            newEval.setEvaluationType(Evaluation.EvaluationType.PROFESSOR_EVALUATION);
            newEval.setTraineeshipPosition(position);
            newEval.setProfessor(position.getSupervisor());
            return newEval;
        });

        Professor supervisor = position.getSupervisor();

        model.addAttribute("evaluation", evaluation);
        model.addAttribute("position", position);
        model.addAttribute("professorName", supervisor.getProfessorName());
        model.addAttribute("professorUsername", supervisor.getUsername());
        return "professors/evaluation";
    }


    @PostMapping("/evaluation/save")
    public String saveEvaluation(@ModelAttribute("evaluation") Evaluation evaluation,
                                 Model model) {

        Optional<Evaluation> existingEvalOpt = professorService
                .findByEvaluationTypeAndPosition(evaluation.getEvaluationType(), evaluation.getTraineeshipPosition());

        if (existingEvalOpt.isPresent()) {
            // Update the evaluation that already exists
            professorService.updateEvaluation(evaluation);
        } else {
            // Save the new evaluation
            professorService.saveEvaluation(evaluation);
        }

        Evaluation savedEval = professorService
                .findByEvaluationTypeAndPosition(evaluation.getEvaluationType(), evaluation.getTraineeshipPosition())
                .orElseThrow(() -> new RuntimeException("Error saving evaluation"));

        model.addAttribute("evaluation", savedEval);

        int positionId = evaluation.getTraineeshipPosition().getId();
        return "redirect:/professors/evaluation/" + positionId + "?success";
    }

}
