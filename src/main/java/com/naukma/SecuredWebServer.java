package com.naukma;

import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;
import javax.validation.Valid;

import com.naukma.models.Transaction;
import com.naukma.models.TransactionForm;
import com.naukma.models.TransactionStatus;
import com.naukma.models.User;
import com.naukma.services.AuthService;
import com.naukma.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@Controller

public class SecuredWebServer implements WebMvcConfigurer {

    @Autowired
    AuthService authService;

    @Autowired
    TransactionService transactionService;

	@GetMapping("/")
	public String home(Map<String, Object> model) {
        if(auth().isAuthenticated() && auth().getAuthorities().stream().anyMatch(x -> !x.getAuthority().equals("ROLE_ANONYMOUS"))) {
            return "redirect:/dashboard";
        }

		return "login";
	}

	@GetMapping("/admin/only")
    public String test() {
	    return "adminOnly";
    }

    @GetMapping("/register")
    public String register(Map<String, Object> model) {
	    model.put("userForm", new User());
	    return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userForm") User userForm, BindingResult bindingResult, Map<String, Object> model) {
        if(authService.emailIsUsed(userForm.getEmail())) {
            bindingResult.addError(new ObjectError("email", "E-mail is already used"));
        }
	    if(bindingResult.hasErrors()) {
            model.put("userForm", userForm);
            return "register";
        }
        authService.register(userForm);
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(Map<String, Object> model) {
        if(auth().getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN"))) {
            return "dashboardAdmin";
        } else {
            model.put("transactionCount", transactionService.countTransactionsByUser(currentUser()));
            return "dashboardUser";
        }
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "logout", required = false) String logout) {
        if(auth().isAuthenticated() && auth().getAuthorities().stream().noneMatch(x -> x.getAuthority().equals("ROLE_ANONYMOUS"))) {
            if(logout != null) {
                auth().setAuthenticated(false);
                return "login";
            } else {
                return "redirect:/dashboard";
            }
        } else {
            return "login";
        }
    }

    @GetMapping("/create")
    public String create(Map<String, Object> model) {
	    model.put("transactionForm", new TransactionForm());
	    return "create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("transactionForm")TransactionForm transactionForm, BindingResult bindingResult, Map<String, Object> model) {
        model.put("transactionForm", transactionForm);
        if(!authService.emailIsUsed(transactionForm.getAnotherUserEmail())) {
            bindingResult.addError(new ObjectError("anotherUserEmail", "User does not exist"));
            return "create";
        }
        User currentUser = currentUser();
        User anotherUser = authService.findByEmail(transactionForm.getAnotherUserEmail());
        if(currentUser.getEmail().equals(transactionForm.getAnotherUserEmail())) {
            bindingResult.addError(new ObjectError("anotherUserEmail", "Another user's email cannot be yours"));
            return "create";
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionForm.getAmount());
        transaction.setCryptocurrency(transactionForm.getCryptocurrency());
        transaction.setStatus(TransactionStatus.WaitingForTermAgreement);
        transaction.setTermsOfAgreement(transactionForm.getTermsOfAgreement());
        if(transactionForm.getIsSender() > 0) {
            transaction.setSenderId(currentUser.getId());
            transaction.setRecipientId(anotherUser.getId());
        } else {
            transaction.setSenderId(anotherUser.getId());
            transaction.setRecipientId(currentUser.getId());
        }
        transactionService.createTransaction(transaction);

        return "redirect:/dashboard";
    }

    @GetMapping("/viewAll")
    public String viewAll(Map<String, Object> model) {
	    model.put("currentUser", currentUser());
	    if(isAdmin()) {
            model.put("transactions", transactionService.getAllTransactions());
	        return "viewAllAdmin";
        } else {
	        model.put("transactions", transactionService.getTransactionsByUser(currentUser()));
	        return "viewAllUser";
        }
    }

    @GetMapping("/view/:transactionId")
    public String viewSingle(@PathVariable(name="transactionId") Integer transactionId, Map<String, Object> model) {

	    return "viewSingleUser";
    }


    private boolean isAdmin() {
        return auth().getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN"));
    }

    private User currentUser() {
        String email = ((org.springframework.security.core.userdetails.User)auth().getPrincipal()).getUsername();
        return authService.findByEmail(email);
    }

    private Authentication auth() {
	    return SecurityContextHolder.getContext().getAuthentication();
    }

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
	}

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
    }

    public static void main(String[] args) {
		new SpringApplicationBuilder(SecuredWebServer.class).run(args);
	}

	@Configuration
    @PropertySource(value={"classpath:database.properties"})
	protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Value("${spring.queries.users-query}")
        private String usersQuery;

        @Value("${spring.queries.roles-query}")
        private String rolesQuery;

        @Qualifier("dataSource")
        @Autowired
        private DataSource dataSource;

        @Override
        protected void configure(AuthenticationManagerBuilder auth)
                throws Exception {
            auth.
                    jdbcAuthentication()
                    .usersByUsernameQuery(usersQuery)
                    .authoritiesByUsernameQuery(rolesQuery)
                    .dataSource(dataSource)
                    .passwordEncoder(new BCryptPasswordEncoder());
        }

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
                    .antMatchers("/css/**").permitAll()
                    .antMatchers("/images/**").permitAll()
                    .antMatchers("/register").permitAll()
                    .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                    .anyRequest() .fullyAuthenticated()
                    .and()
                    .formLogin().loginPage("/login")
                    .usernameParameter("email").passwordParameter("password")
					.failureUrl("/login?error").permitAll().and().logout().permitAll();
		}


	}

}
