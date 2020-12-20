package com.redit.clone.service;

import com.redit.clone.dto.AuthenticationResponse;
import com.redit.clone.dto.LoginRequest;
import com.redit.clone.dto.RegisterRequest;
import com.redit.clone.exceptions.SpringRedditException;
import com.redit.clone.model.NotificationEmail;
import com.redit.clone.model.User;
import com.redit.clone.model.VerificationToken;
import com.redit.clone.repository.UserRepository;
import com.redit.clone.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
@AllArgsConstructor
public class AuthService {

    //this is a feild injection....its always better to use the constructor injection
    /*@Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;*/

    //constructor based injection-taken care by @AllArgsConstructor
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;


    @Transactional
    public void signup(RegisterRequest registerRequest){
        User user= new User();
        user.setUsername(registerRequest.getUsername());
        //storing the passowrd in clear text is unsafe
        user.setEmail(registerRequest.getEmail());//encoding the password using the encoder
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);
        String token = generateVerificationToken(user);
        NotificationEmail notificationEmail= new NotificationEmail("Please, Activate your account",user.getEmail(),"Thank you for signing up for Reddit Clone.Please click on the below link to activate your account."
                +"http://localhost:8080/api/auth/accountVerification/"+token);
        mailService.sendEmail(notificationEmail);

    }
    private String generateVerificationToken(User user){
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken= new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return token;
    }


    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(()->new SpringRedditException("Invalid Token"));
        fetchUserAndEnable(verificationToken.get());

    }

    @Transactional
    public void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(()->new SpringRedditException("User is not found with username = "+username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(token, loginRequest.getUsername());
    }
}
