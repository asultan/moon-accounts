package accounts.controller;

//import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

    public static final String PATH_HOME = "/swagger-ui.html";

    @GetMapping("/")
//    @ApiOperation(value = "${HomeController.root}")
    public String root() {
        log.debug("Handling a request on the root path. Redirecting to {}", PATH_HOME);
        return "redirect:" + PATH_HOME;
    }
}

