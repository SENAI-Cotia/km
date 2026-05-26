package br.com.notelab.backend.Controllers;

import br.com.notelab.backend.Model.Caderno;
import br.com.notelab.backend.Model.Matter;
import br.com.notelab.backend.Model.User;
import br.com.notelab.backend.Services.AuthenticatedUserService;
import br.com.notelab.backend.Services.CadernoService;
import br.com.notelab.backend.Services.MatterService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PageController {

    private final AuthenticatedUserService authenticatedUserService;
    private final MatterService matterService;
    private final CadernoService cadernoService;

    public PageController(
            AuthenticatedUserService authenticatedUserService,
            MatterService matterService,
            CadernoService cadernoService
    ) {
        this.authenticatedUserService = authenticatedUserService;
        this.matterService = matterService;
        this.cadernoService = cadernoService;
    }

    @GetMapping("/matter/{id}")
    public String matterPage(@PathVariable Long id, Authentication authentication, Model model) {
        Long userId = authenticatedUserService.getAuthenticatedUserId(authentication);
        User user = authenticatedUserService.getAuthenticatedUser(authentication);
        Matter matter = matterService.getMatterByIdAndUserId(id, userId);
        List<Caderno> cadernos = cadernoService.listByMatterAndUser(id, userId);

        model.addAttribute("username", user.getName());
        model.addAttribute("matter", matter);
        model.addAttribute("cadernos", cadernos);
        return "matter";
    }

    @GetMapping("/caderno/{id}")
    public String cadernoPage(@PathVariable Long id, Authentication authentication, Model model) {
        Long userId = authenticatedUserService.getAuthenticatedUserId(authentication);
        User user = authenticatedUserService.getAuthenticatedUser(authentication);
        Caderno caderno = cadernoService.getCadernoByIdAndUserId(id, userId);
        Matter matter = matterService.getMatterByIdAndUserId(caderno.getMatterId(), userId);

        model.addAttribute("username", user.getName());
        model.addAttribute("caderno", caderno);
        model.addAttribute("matter", matter);
        return "cadernoNotas";
    }
}
