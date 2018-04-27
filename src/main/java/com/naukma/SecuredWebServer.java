package com.naukma;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.validation.Valid;

import com.naukma.models.*;
import com.naukma.services.AuthService;
import com.naukma.services.DisputeService;
import com.naukma.services.MessageService;
import com.naukma.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.access.AccessDeniedException;
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

    @Autowired
    DisputeService disputeService;

    @Autowired
    MessageService messageService;

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
            return "forward:/viewAll";
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
        transaction.setCreatorId(currentUser.getId());
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

    @GetMapping("/view/{transactionId}")
    public String viewSingle(@PathVariable(name="transactionId") Integer transactionId, Map<String, Object> model) {
        Transaction transaction = transactionService.getById(transactionId);

        boolean isAdmin = isAdmin();
        boolean isSender = transaction.getSenderId().equals(currentUser().getId());
        boolean isRecipient = transaction.getRecipientId().equals(currentUser().getId());
        boolean isCreator = transaction.getCreatorId().equals(currentUser().getId());

        if(!isAdmin && !isSender && !isRecipient) {
            throw new AccessDeniedException("");
        }

        List<Message> messages = messageService.getMessagesByTransaction(transaction);

        model.put("isAdmin", isAdmin);
        model.put("isSender", isSender);
        model.put("isRecipient", isRecipient);
        model.put("isCreator", isCreator);
        model.put("transaction", transaction);
        model.put("dispute", transaction.getDispute());
        model.put("messages", messages);
        model.put("currentUser", currentUser());

	    return "viewSingle";
    }

    @PostMapping("/transaction/{transactionId}/proceed")
    public String proceed(@PathVariable(name="transactionId") Integer transactionId, Map<String, Object> model) {
        Transaction transaction = transactionService.getById(transactionId);

        boolean isAdmin = isAdmin();
        boolean isSender = transaction.getSenderId().equals(currentUser().getId());
        boolean isRecipient = transaction.getRecipientId().equals(currentUser().getId());

        if(!isAdmin && !isSender && !isRecipient) {
            throw new AccessDeniedException("");
        }

        // TODO Add security
        switch(transaction.getStatus()) {
            case WaitingForTermAgreement:
                transactionService.updateTransactionStatus(transaction, TransactionStatus.WaitingForPaymentByMoneySender);
                break;
            case WaitingForPaymentByMoneySender:
                transactionService.updateTransactionStatus(transaction, TransactionStatus.WaitingForItemOrServiceByMoneyRecipient);
                break;
            case WaitingForItemOrServiceByMoneyRecipient:
                transactionService.updateTransactionStatus(transaction, TransactionStatus.CheckingItemOrServiceByMoneySender);
                break;
            case CheckingItemOrServiceByMoneySender:
                transactionService.updateTransactionStatus(transaction, TransactionStatus.FinishedWithoutDispute);
                break;
        }

        return "redirect:/view/" + transaction.getId();
    }

    @PostMapping("/transaction/{transactionId}/dispute/start")
    public String disputeStart(@PathVariable(name="transactionId") Integer transactionId, Map<String, Object> model) {
        Transaction transaction = transactionService.getById(transactionId);

        boolean isSender = transaction.getSenderId().equals(currentUser().getId());

        if(!isSender || transaction.getStatus() != TransactionStatus.CheckingItemOrServiceByMoneySender) {
            throw new AccessDeniedException("");
        }

        transactionService.updateTransactionStatus(transaction, TransactionStatus.DisputeStarted);

        return "redirect:/view/" + transaction.getId();
    }

    @PostMapping("/transaction/{transactionId}/dispute/resolve")
    public String disputeResolve(
            @PathVariable(name="transactionId") Integer transactionId,
            @RequestParam(name="amountRefunded") Double amountRefunded,
            Map<String, Object> model) {
        Transaction transaction = transactionService.getById(transactionId);

        if(!isAdmin() || transaction.getStatus() != TransactionStatus.DisputeStarted) {
            throw new AccessDeniedException("");
        }

        transactionService.updateTransactionStatus(transaction, TransactionStatus.DisputeFinished);

        disputeService.resolveDispute(transaction.getDispute(), BigDecimal.valueOf(amountRefunded));

        return "redirect:/view/" + transaction.getId();
    }

    @PostMapping("/transaction/{transactionId}/setStatus/{status}")
    public String setStatus(
            @PathVariable(name="transactionId") Integer transactionId,
            @PathVariable(name="status") String statusStr,
            Map<String, Object> model) {
        Transaction transaction = transactionService.getById(transactionId);

        TransactionStatus status = TransactionStatus.valueOf(statusStr);

	    if(!isAdmin()) {
	        throw new AccessDeniedException("");
        }

        transactionService.updateTransactionStatus(transaction, status);


        return "redirect:/view/" + transaction.getId();
    }

    @PostMapping("/transaction/{transactionId}/message")
    public String sendMessage(
            @PathVariable(name="transactionId") Integer transactionId,
            @RequestParam(name="message") String messageText
    ) {
        Transaction transaction = transactionService.getById(transactionId);

        Message message = new Message();
        message.setUserId(currentUser().getId());
        message.setTransactionId(transactionId);
        message.setMessage(messageText);
        messageService.createMessage(message);

        transactionService.updateLastTimeUpdated(transaction);

        return "redirect:/view/" + transaction.getId();
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
