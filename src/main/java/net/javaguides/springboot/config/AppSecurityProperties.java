package net.javaguides.springboot.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.security")
@Validated
public class AppSecurityProperties {

    @NotEmpty(message = "At least one admin credential must be configured")
    @Valid
    private List<UserCredentials> admins = new ArrayList<>();

    @Valid
    private List<UserCredentials> viewers = new ArrayList<>();

    public List<UserCredentials> getAdmins() {
        return admins;
    }

    public void setAdmins(List<UserCredentials> admins) {
        this.admins = admins;
    }

    public List<UserCredentials> getViewers() {
        return viewers;
    }

    public void setViewers(List<UserCredentials> viewers) {
        this.viewers = viewers;
    }

    public static class UserCredentials {
        @NotBlank
        private String username;

        @NotBlank
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
