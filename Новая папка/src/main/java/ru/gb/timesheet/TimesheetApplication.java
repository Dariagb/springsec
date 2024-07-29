package ru.gb.timesheet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.gb.timesheet.model.*;
import ru.gb.timesheet.repository.ProjectRepository;
import ru.gb.timesheet.repository.TimesheetRepository;
import ru.gb.timesheet.repository.UserRepository;
import ru.gb.timesheet.repository.UserRoleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
public class TimesheetApplication {
  public static void main(String[] args) {
    ApplicationContext ctx = SpringApplication.run(TimesheetApplication.class, args);
    UserRepository userRepository = ctx.getBean(UserRepository.class);
    UserRoleRepository userRoleRepository = ctx.getBean(UserRoleRepository.class);
    ProjectRepository projectRepo = ctx.getBean(ProjectRepository.class);
    TimesheetRepository timesheetRepo = ctx.getBean(TimesheetRepository.class);

    User admin = createUser(userRepository, "admin", "$2a$12$LbAPCsHn8ZN5MUDqDmIX7e9n1YlDkCxEt0lW3Q2WuW0M1vteo8jvG");
    User user = createUser(userRepository, "user", "$2a$12$.dlnBAYq6sOUumn3jtG.AepxdSwGxJ8xA2iAPoCHSH61Vjl.JbIfq");
    User anonymous = createUser(userRepository, "anon", "$2a$12$tPkyYzWCYUEePUFXUh3scesGuPum1fvFYwm/9UpmWNa52xEeUToRu");

    assignRoles(userRoleRepository, admin.getId(), Role.ADMIN, Role.USER);
    assignRoles(userRoleRepository, user.getId(), Role.USER);

    for (int i = 1; i <= 5; i++) {
      Project project = new Project();
      project.setName("Project #" + i);
      projectRepo.save(project);
    }

    LocalDate createdAt = LocalDate.now();
    for (int i = 1; i <= 10; i++) {
      createdAt = createdAt.plusDays(1);
      Timesheet timesheet = new Timesheet();
      timesheet.setProjectId(ThreadLocalRandom.current().nextLong(1, 6));
      timesheet.setCreatedAt(createdAt);
      timesheet.setMinutes(ThreadLocalRandom.current().nextInt(100, 1000));
      timesheetRepo.save(timesheet);
    }
  }

  private static User createUser(UserRepository userRepository, String login, String password) {
    User user = new User();
    user.setLogin(login);
    user.setPassword(password);
    return userRepository.save(user);
  }

  private static void assignRoles(UserRoleRepository userRoleRepository, Long userId, String... roleNames) {
    for (String roleName : roleNames) {
      UserRole userRole = new UserRole();
      userRole.setUserId(userId);
      userRole.setRoleName(roleName);
      userRoleRepository.save(userRole);
    }
  }
}
