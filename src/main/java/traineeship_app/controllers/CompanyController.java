package traineeship_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import traineeship_app.domainmodel.Company;
import traineeship_app.domainmodel.Evaluation;
import traineeship_app.domainmodel.TraineeshipPosition;
import traineeship_app.mappers.EvaluationMapper;
import traineeship_app.services.CompanyService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    private final EvaluationMapper evaluationMapper;


    @Autowired
    public CompanyController(CompanyService companyService, EvaluationMapper evaluationMapper) {
        this.companyService = companyService;
        this.evaluationMapper = evaluationMapper;
    }


    @GetMapping("/home")
    public String companyHome(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("role", "ROLE_COMPANY");
        return "companies/home";  // This maps to templates/companies/home.html
    }

    @PostMapping("/register")
    public String registerCompany(
            @ModelAttribute("company") Company company,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.company", result);
            redirectAttributes.addFlashAttribute("company", company);
            return "redirect:/users/register?role=COMPANY";
        }


        try {
            companyService.SaveProfile(company);
            redirectAttributes.addFlashAttribute("success", "Company user registration successful!");
            return "redirect:/users/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("company",company);
            return "redirect:/users/register?role=COMPANY";
        }

    }

    @GetMapping("/profile")
    public String retrieveProfile(@RequestParam("username") String companyUsername, Model model) {
        // Fetch company profile using its username
        Company company = companyService.retrieveProfile(companyUsername);

        model.addAttribute("company", company);

        return "companies/profile";
    }

    @PostMapping("/profile/save")  // tells spring that this method should handle post-requests sent to the correct URL
    public String updateProfile(@ModelAttribute("company") Company company, Model theModel) {
        companyService.UpdateProfile(company);
        theModel.addAttribute("company", company);
        return "redirect:/companies/profile?username=" + company.getUsername();  // redirects back to the user's profile
    }

    @GetMapping("/traineeship-management")
    public String listAvailablePositions(@RequestParam("username") String companyUsername, Model model) {
        List<TraineeshipPosition> positions = companyService.retrieveAvailablePositions(companyUsername);
        model.addAttribute("username", companyUsername);
        model.addAttribute("positions", positions);

        return "companies/traineeship-management";
    }

    @GetMapping("/traineeship-management/announce-traineeship")
    public String addPositionForm(@RequestParam("username") String companyUsername, Model model) {
        model.addAttribute("traineeship", new TraineeshipPosition());  // Empty position object
        model.addAttribute("username", companyUsername);  // Adds username to the model to make it available to the view
        return "companies/announce-traineeship";
    }


    @PostMapping("/traineeship-management/announce-traineeship/save")
    public String saveTraineeship(@ModelAttribute("traineeship") TraineeshipPosition position,
                @RequestParam("username") String username, Model model) {

        Company company = companyService.retrieveProfile(username);

        companyService.addPosition(username, position);
        model.addAttribute("username", username);
        model.addAttribute("successMessage", "Traineeship announced successfully.");
        return "redirect:/companies/traineeship-management/announce-traineeship?username=" + company.getUsername() + "&success";

    }

    @PostMapping("/traineeship-management/delete/{id}")
    public String deletePosition(@PathVariable("id") int positionId,
                                 @RequestParam("username") String companyUsername) {
        companyService.deletePosition(positionId); // Deletes the traineeship position
        return "redirect:/companies/traineeship-management?username=" + companyUsername;
    }

    @GetMapping("assigned-traineeships")
    public String listAssignedPositions(@RequestParam("username") String companyUsername, Model model) {
        List<TraineeshipPosition> positions = companyService.retrieveAssignedPositions(companyUsername);

        model.addAttribute("positions", positions);

        return "companies/assigned-traineeships";
    }

    @GetMapping("/evaluation/{positionId}")
    public String showEvaluationForm(@PathVariable int positionId, Model model) {
        TraineeshipPosition position = companyService.getPositionById(positionId);

        // Try to fetch existing evaluation
        Optional<Evaluation> existingEval = companyService
                .findCompanyEvaluationByPosition(position);

        Evaluation evaluation = existingEval.orElseGet(() -> {
            Evaluation newEval = new Evaluation();
            newEval.setEvaluationType(Evaluation.EvaluationType.COMPANY_EVALUATION);
            newEval.setTraineeshipPosition(position);
            newEval.setCompany(position.getCompany());
            return newEval;
        });

        model.addAttribute("evaluation", evaluation);
        model.addAttribute("position", position);
        model.addAttribute("companyUsername", position.getCompany().getUsername());
        return "companies/evaluation";
    }


    @PostMapping("/evaluation/save")
    public String saveEvaluation(@ModelAttribute("evaluation") Evaluation evaluation,
                                 Model model) {

        Optional<Evaluation> existingEvalOpt = companyService
                .findByEvaluationTypeAndPosition(evaluation.getEvaluationType(), evaluation.getTraineeshipPosition());

        if (existingEvalOpt.isPresent()) {
            // Update the evaluation that already exists
            companyService.updateEvaluation(evaluation);
        } else {
            // Save new evaluation
            companyService.saveEvaluation(evaluation);
        }

        Evaluation savedEval = companyService
                .findByEvaluationTypeAndPosition(evaluation.getEvaluationType(), evaluation.getTraineeshipPosition())
                .orElseThrow(() -> new RuntimeException("Error saving evaluation"));

        model.addAttribute("evaluation", savedEval);

        model.addAttribute("evaluation", savedEval);

        int positionId = evaluation.getTraineeshipPosition().getId();

        return "redirect:/companies/evaluation/" + positionId + "?success";
    }


}
