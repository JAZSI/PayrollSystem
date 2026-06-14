package com.com253.payrollsystem.shared.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/** Forwards client-side (React Router) routes to the SPA shell so deep links / refresh work. */
@Controller
public class SpaController {

    /** Single-segment paths without a file extension (e.g. /dashboard, /employees). */
    @RequestMapping(value = {"/", "/{path:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}
