# Spring Security JWT (Spring Boot 3 / Security 6)

Aşağıdaki proje; **login**, **JWT üretimi**, **filter ile doğrulama**, **rol bazlı yetki** (USER/ADMIN) ve **örnek endpointler** içerir.

---

## 1) Proje Yapısı

```
spring-jwt-demo/
├─ pom.xml
├─ src/main/java/com/example/jwtdemo/
│  ├─ JwtDemoApplication.java
│  ├─ config/
│  │  ├─ SecurityConfig.java
│  │  └─ JwtAuthenticationFilter.java
│  ├─ security/
│  │  ├─ JwtService.java
│  │  └─ CustomUserDetailsService.java
│  ├─ web/
│  │  ├─ AuthController.java
│  │  ├─ HelloController.java
│  │  └─ GlobalExceptionHandler.java
│  └─ model/
│     ├─ LoginRequest.java
│     └─ TokenResponse.java
└─ src/main/resources/
   └─ application.yml
```

> İstersen `CustomUserDetailsService` yerine JPA ile gerçek kullanıcı tablosu bağlayabilirsin. Bu örnekte **in-memory kullanıcılar** kullanıyoruz.

---

## 2) `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>spring-jwt-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-jwt-demo</name>
    <description>Spring Security + JWT Demo</description>
    <properties>
        <java.version>17</java.version>
        <spring-boot.version>3.3.2</spring-boot.version>
        <jjwt.version>0.11.5</jjwt.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- (Opsiyonel) Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 3) `application.yml`

```yaml
server:
  port: 8080

app:
  jwt:
    # 256-bit gizli anahtar: en az 32 byte önerilir.
    secret: "change-this-secret-to-a-very-long-256bit-key-change-me-please"
    expiration: 3600000 # 1 saat (ms)
```

> Gerçekte `secret` için **ENV** değişkeni kullanın; repoya koymayın.

---

## 4) `JwtDemoApplication.java`

```java
package com.example.jwtdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JwtDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(JwtDemoApplication.class, args);
    }
}
```

---

## 5) `security/JwtService.java` (Token üretme & doğrulama)

```java
package com.example.jwtdemo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Key key;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long expiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expiration;
    }

    public String generateToken(String username, Map<String, Object> claims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public String extractUsername(String token) {
        return parse(token).getBody().getSubject();
    }
}
```

---

## 6) `security/CustomUserDetailsService.java` (Kullanıcı sağlayıcısı)

```java
package com.example.jwtdemo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.withUsername("user")
                .password(encoder.encode("1234"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("1234"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## 7) `config/JwtAuthenticationFilter.java` (OncePerRequestFilter)

```java
package com.example.jwtdemo.config;

import com.example.jwtdemo.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtService.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Token geçerliyse authentication yerleştir
                    jwtService.parse(token); // parse + signature check

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception ex) {
                // Hatalı token vb. durumlarda 401 dön
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

---

## 8) `config/SecurityConfig.java`

```java
package com.example.jwtdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
```

---

## 9) `model/LoginRequest.java` ve `model/TokenResponse.java`

```java
package com.example.jwtdemo.model;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
```

```java
package com.example.jwtdemo.model;

public class TokenResponse {
    private String token;
    private String tokenType = "Bearer";

    public TokenResponse(String token) { this.token = token; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
}
```

---

## 10) `web/AuthController.java` (Login)

```java
package com.example.jwtdemo.web;

import com.example.jwtdemo.model.LoginRequest;
import com.example.jwtdemo.model.TokenResponse;
import com.example.jwtdemo.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String username = auth.getName();
            String roles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", roles);

            String token = jwtService.generateToken(username, claims);
            return new TokenResponse(token);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
```

---

## 11) `web/HelloController.java` (Örnek endpointler)

```java
package com.example.jwtdemo.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/public/hello")
    public String publicHello() {
        return "Hello Public";
    }

    @GetMapping("/user/hello")
    public String userHello() {
        return "Hello Authenticated User";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/hello")
    public String adminHello() {
        return "Hello Admin";
    }
}
```

---

## 12) `web/GlobalExceptionHandler.java` (Opsiyonel)

```java
package com.example.jwtdemo.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Validation failed");
        body.put("details", ex.getBindingResult().getFieldErrors());
        return ResponseEntity.badRequest().body(body);
    }
}
```

---

## 13) Çalıştırma & Test

### Uygulamayı başlat

```bash
mvn spring-boot:run
```

### Login isteği (örnek)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"1234"}'
```

Cevap:

```json
{
  "token": "<JWT>",
  "tokenType": "Bearer"
}
```

### Token ile korunan endpoint

```bash
curl http://localhost:8080/api/user/hello \
  -H "Authorization: Bearer <JWT>"
```

### Admin endpoint (ADMIN rolü gerekir)

```bash
curl http://localhost:8080/api/admin/hello \
  -H "Authorization: Bearer <JWT>"
```

---

## 14) Notlar & Geliştirme Önerileri

- **Gerçek kullanıcı verisi**: `UserDetailsService` yerine JPA + DB kullanıp kullanıcı/rol tablosu bağla.
- **Refresh Token**: Ayrı bir refresh token stratejisi ekle (kısa ömürlü access, uzun ömürlü refresh).
- **CORS**: Frontend ile çalışacaksan `HttpSecurity.cors()` ve `CorsConfigurationSource` ekle.
- **Logout/Blacklist**: İsteğe bağlı olarak token blacklisting veya kısa expiration + refresh yaklaşımı.
- **Key Rotasyonu**: Çok kullanıcıda production için anahtar rotasyonu (kid header vs.).

---
