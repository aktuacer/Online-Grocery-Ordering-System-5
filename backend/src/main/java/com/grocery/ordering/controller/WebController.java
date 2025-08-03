package com.grocery.ordering.controller;

import com.grocery.ordering.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Web Controller for JSP views
 * Handles server-side rendering for admin dashboard
 * 
 * @author Chirag Singhal (chirag127)
 */
@Controller
public class WebController {

    @Autowired
    private AuthService authService;

    /**
     * Home page redirect
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    /**
     * Login page
     */
    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        // Check if user is already authenticated
        HttpSession session = request.getSession(false);
        if (session != null && Boolean.TRUE.equals(session.getAttribute("authenticated"))) {
            String userType = (String) session.getAttribute("userType");
            if ("ADMIN".equals(userType)) {
                return "redirect:/dashboard";
            }
        }
        return "login";
    }

    /**
     * Handle login form submission
     */
    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                             @RequestParam String password,
                             HttpServletRequest request,
                             Model model) {
        try {
            AuthService.AuthResult authResult = authService.login(username, password, "ADMIN");
            
            if (authResult.isSuccess() && "ADMIN".equals(authResult.getUserType())) {
                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("userType", "ADMIN");
                session.setAttribute("authenticated", true);
                session.setAttribute("adminUser", authResult.getAdminUser());
                session.setAttribute("username", authResult.getAdminUser().getUsername());
                
                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Invalid username or password");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return "login";
        }
    }

    /**
     * Admin dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("authenticated"))) {
            return "redirect:/login";
        }

        String userType = (String) session.getAttribute("userType");
        if (!"ADMIN".equals(userType)) {
            return "redirect:/login";
        }

        // Add user info to model
        model.addAttribute("adminUser", session.getAttribute("adminUser"));
        return "dashboard";
    }

    /**
     * Logout
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout=true";
    }

    /**
     * Access denied page
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

    /**
     * Customer registration page (redirect to Angular)
     */
    @GetMapping("/register")
    public String register() {
        return "redirect:http://localhost:4200/register";
    }

    /**
     * Customer portal redirect
     */
    @GetMapping("/customer")
    public String customerPortal() {
        return "redirect:http://localhost:4200";
    }
}
