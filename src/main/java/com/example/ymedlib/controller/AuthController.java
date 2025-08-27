package com.example.ymedlib.controller;

import com.example.ymedlib.model.User;
import com.example.ymedlib.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Value("${jwt.secret:ymedlibSecretymedlibSecretymedlibSecretymedlibSecret}")
    private String jwtSecret;

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User loginUser) {
        Optional<User> userOpt = userService.findByUsername(loginUser.getUsername());
        Map<String, Object> result = new HashMap<>();
        if (userOpt.isPresent() && passwordEncoder.matches(loginUser.getPassword(), userOpt.get().getPassword())) {
            // 生成安全的256位密钥
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            String token = Jwts.builder()
                    .setSubject(userOpt.get().getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            result.put("token", token);
            result.put("username", userOpt.get().getUsername());
            result.put("role", userOpt.get().getRole());
        } else {
            result.put("error", "用户名或密码错误");
        }
        return result;
    }
} 